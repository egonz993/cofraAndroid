package com.confraternidad.app.confra.modelos_adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import com.confraternidad.app.confra.MainActivity;
import com.confraternidad.app.confra.R;
import com.confraternidad.app.confra.SplashActivity;
import com.confraternidad.app.confra.fragments.PeticionesFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class PeticionAdapter extends RecyclerView.Adapter<PeticionAdapter.ViewHolder>{
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private CardView peticion_persona_container, peticion_descripcion_container;
        private ImageView peticion_contacto_avatar;
        private TextView peticion_contacto_name, peticion_date, peticion_contacto_id, peticion_destino_id,
                peticion_idContainer, peticion_leido, peticion_titulo, peticion_descripcion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            peticion_persona_container = itemView.findViewById(R.id.peticion_persona_container);
            peticion_descripcion_container = itemView.findViewById(R.id.peticion_descripcion_container);

            peticion_contacto_avatar = itemView.findViewById(R.id.peticion_contacto_avatar);
            peticion_contacto_name = itemView.findViewById(R.id.peticion_contacto_name);
            peticion_date = itemView.findViewById(R.id.peticion_date);
            peticion_contacto_id = itemView.findViewById(R.id.peticion_contacto_id);
            peticion_destino_id = itemView.findViewById(R.id.peticion_destino_id);
            peticion_idContainer = itemView.findViewById(R.id.peticion_idContainer);
            peticion_leido = itemView.findViewById(R.id.peticion_leido);
            peticion_titulo = itemView.findViewById(R.id.peticion_titulo);
            peticion_descripcion = itemView.findViewById(R.id.peticion_descripcion);
        }
    }

    public List<PeticionModelo> itemPeticionesList;
    public Context context;

    public PeticionAdapter(List<PeticionModelo> itemPeticionesList, Context context) {
        this.itemPeticionesList = itemPeticionesList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_peticiones, parent,false);
        return new PeticionAdapter.ViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String uidContainer = itemPeticionesList.get(position).getUidContainer();
        final String uidPersona = itemPeticionesList.get(position).getUidPersona();
        final String uidDestino = itemPeticionesList.get(position).getUidDestino();
        final String nombre = itemPeticionesList.get(position).getNombre();
        final String fecha = itemPeticionesList.get(position).getFecha();
        final String leido = itemPeticionesList.get(position).getLeido();
        final String titulo = itemPeticionesList.get(position).getTitulo();
        final String peticion = itemPeticionesList.get(position).getPeticion();
        final StorageReference avatarRef = itemPeticionesList.get(position).getAvatar();

        holder.peticion_contacto_name.setText(nombre);
        holder.peticion_date.setText(fecha);
        holder.peticion_contacto_id.setText(uidPersona);
        holder.peticion_destino_id.setText(uidDestino);
        holder.peticion_idContainer.setText(uidContainer);
        holder.peticion_leido.setText(leido);
        holder.peticion_titulo.setText(titulo);
        holder.peticion_descripcion.setText(peticion);

        Glide.with(context).load(avatarRef)
                .error(R.drawable.ic_user)
                .circleCrop()
                .into(holder.peticion_contacto_avatar);

        holder.peticion_persona_container.setOnClickListener(v -> {
            PeticionesFragment.getUserInfo(context, uidContainer, uidPersona);
        });

        holder.peticion_persona_container.setOnLongClickListener(v -> {
            AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setIcon(R.drawable.logo_confra_round)
                    .setTitle("¿Desea eliminar este renglón?")
                    .setMessage("Esta acción es irreversible")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        mDatabase.child("peticiones").child(uidContainer).removeValue().addOnCompleteListener(task -> {
                            Toast.makeText(context, "Eliminado", Toast.LENGTH_SHORT).show();
                            MainActivity.navigateTo("peticiones");
                        });
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();

            return false;
        });
    }

    @Override
    public int getItemCount() {
        return itemPeticionesList.size();
    }
}