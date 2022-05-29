package com.confraternidad.app.confra.modelos_adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.confraternidad.app.confra.MainActivity;
import com.confraternidad.app.confra.R;
import com.confraternidad.app.confra.fragments.ContactanosFragment;

import java.util.List;

public class ContactosMenuAdapter extends RecyclerView.Adapter<ContactosMenuAdapter.ViewHolder>{

    public class ViewHolder extends RecyclerView.ViewHolder{

        private CardView item_conacto_container;
        private TextView uidContainer, titulo, uidLider, bannerRef;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_conacto_container = itemView.findViewById(R.id.item_conacto_container);

            uidContainer = itemView.findViewById(R.id.uid);
            titulo = itemView.findViewById(R.id.titulo);
            uidLider = itemView.findViewById(R.id.uidLider);
            bannerRef = itemView.findViewById(R.id.bannerRef);
        }
    }

    public List<ContactosMenuModelo> itemContactosList;
    public Context context;

    public ContactosMenuAdapter(Context context, List<ContactosMenuModelo> itemContactosList) {
        this.itemContactosList = itemContactosList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contactos,parent,false);
        ContactosMenuAdapter.ViewHolder viewHolder = new ContactosMenuAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final String titulo = itemContactosList.get(position).getTitulo();
        final String uidLider = itemContactosList.get(position).getUidLider();
        final String uidContainer = itemContactosList.get(position).getUid();

        holder.uidContainer.setText(uidContainer);
        holder.titulo.setText(titulo);
        holder.uidLider.setText(uidLider);


        holder.item_conacto_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactanosFragment.getUserInfo(context, uidContainer, uidLider);
            }
        });

        holder.item_conacto_container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(MainActivity.isAdmin){
                    ContactanosFragment.showMenuContacto(context, uidContainer, uidLider);
                }   return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemContactosList.size();
    }


}
