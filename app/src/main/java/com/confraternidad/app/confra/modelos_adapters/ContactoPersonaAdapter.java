package com.confraternidad.app.confra.modelos_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.confraternidad.app.confra.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ContactoPersonaAdapter  extends RecyclerView.Adapter<ContactoPersonaAdapter.ViewHolder>{

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private CardView item_conacto_container;
        private ImageView contacto_avatar;
        private TextView uidLider, nombre, uidContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_conacto_container = itemView.findViewById(R.id.conacto_persona_container);

            uidContainer = itemView.findViewById(R.id.contacto_idContainer);
            contacto_avatar = itemView.findViewById(R.id.contacto_avatar);
            uidLider = itemView.findViewById(R.id.contacto_id);
            nombre = itemView.findViewById(R.id.contacto_name);
        }
    }

    public List<ContactoPersonaModelo> itemContactosList;
    public Context context;
    public String uidContainer;

    public ContactoPersonaAdapter(Context context, String uidContainer, List<ContactoPersonaModelo> itemContactosList) {
        this.itemContactosList = itemContactosList;
        this.uidContainer = uidContainer;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contacto_persona,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String uidLider = itemContactosList.get(position).getUid();
        final String nombre = itemContactosList.get(position).getNombre();
        final StorageReference avatarRef = itemContactosList.get(position).getAvatar();

        holder.uidLider.setText(uidLider);
        holder.nombre.setText(nombre);

        Glide.with(context).load(avatarRef)
                .error(R.drawable.ic_user)
                .circleCrop()
                .into(holder.contacto_avatar);

        holder.item_conacto_container.setOnClickListener(v -> {
            DatabaseReference contacto = FirebaseDatabase
                    .getInstance().getReference()
                    .child("contactos").child(uidContainer);

            contacto.child("uidLider").setValue(uidLider).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(context, "Lider actualizado\n"+ nombre, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return itemContactosList.size();
    }

}
