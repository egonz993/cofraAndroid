package com.confraternidad.app.confra.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.confraternidad.app.confra.MainActivity;
import com.confraternidad.app.confra.R;
import com.confraternidad.app.confra.modelos_adapters.ContactoPersonaAdapter;
import com.confraternidad.app.confra.modelos_adapters.ContactoPersonaModelo;
import com.confraternidad.app.confra.modelos_adapters.ContactosMenuAdapter;
import com.confraternidad.app.confra.modelos_adapters.ContactosMenuModelo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ContactanosFragment extends Fragment{

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference();


    private RecyclerView recyclerView_contactarMiniterio, recyclerView_contactarEspecialista;
    private ContactosMenuAdapter recyclerView_contactosAdapter;
    private List<ContactosMenuModelo> contactosListMinisterios = new ArrayList<>();
    private List<ContactosMenuModelo> contactosListEspecialistas = new ArrayList<>();

    private static List<ContactoPersonaModelo> contactosListPersonas = new ArrayList<>();

    private CardView card_contactanos_pastor, card_miniterios_title, card_asesorias_title;
    private RelativeLayout contactarPastor;

    private ImageView btn_socialFB, btn_socialYT, btn_socialIG;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contactanos, container, false);

        //Contactar pastor
        contactarPastor = view.findViewById(R.id.contactarPastor);
        contactarPastor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPastorInfo("click");
            }
        });
        contactarPastor.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(MainActivity.isAdmin){
                    getPastorInfo("longClick");
                }
                return false;
            }
        });

        card_miniterios_title = view.findViewById(R.id.card_miniterios_title);
        card_miniterios_title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(MainActivity.isAdmin){
                    //agregarContacto("Nuevo Item", "ministerios");
                }
                return false;
            }
        });

        card_asesorias_title = view.findViewById(R.id.card_asesorias_title);
        card_asesorias_title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(MainActivity.isAdmin){
                    agregarContacto("Nuevo Item", "especialistas");
                }
                return false;
            }
        });

        //Contactar ministerios
        recyclerView_contactarMiniterio = view.findViewById(R.id.recyclerView_contactarMiniterio);
        recyclerView_contactarMiniterio.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView_contactosAdapter = new ContactosMenuAdapter(getActivity(), getContactosListMinisterio());
        recyclerView_contactarMiniterio.setAdapter(recyclerView_contactosAdapter);
        recyclerView_contactarMiniterio.setNestedScrollingEnabled(false);

        //Contactar Especialistas
        recyclerView_contactarEspecialista = view.findViewById(R.id.recyclerView_contactarEspecialista);
        recyclerView_contactarEspecialista.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView_contactosAdapter = new ContactosMenuAdapter(getActivity(), getContactosListEspecialista());
        recyclerView_contactarEspecialista.setAdapter(recyclerView_contactosAdapter);
        recyclerView_contactarEspecialista.setNestedScrollingEnabled(false);

        btn_socialFB = view.findViewById(R.id.btn_socialFB);
        btn_socialFB.setOnClickListener(v -> gotoURL("https://www.facebook.com/confraternidad.conproposito"));

        btn_socialYT = view.findViewById(R.id.btn_socialYT);
        btn_socialYT.setOnClickListener(v -> gotoURL("https://www.youtube.com/channel/UCKs4ggGsombPz3UgT5W2dGg"));

        btn_socialIG = view.findViewById(R.id.btn_socialIG);
        btn_socialIG.setOnClickListener(v -> gotoURL("https://instagram.com/confraternidadconproposito?igshid=1m0xn1ds3gcvx"));


        return view;
    }



    //Funciones de usuario

    public static void showPopupContacto(final Context context, final String liderName, final String liderPhone, final String liderAvatar, final String uidContainer){

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View popupContacto = layoutInflater.inflate(R.layout.popup_contacto, null);

        /* SHOW POPUP WINDOW*/

        //Alert Dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setView(popupContacto);

        AlertDialog showContacto = alertDialogBuilder.create();
        showContacto.show();

        //Nombre
        TextView txt_contactoFullName = popupContacto.findViewById(R.id.txt_contactoFullName);
        txt_contactoFullName.setText(liderName);

        //Telefono
        TextView txt_contactoTel = popupContacto.findViewById(R.id.txt_contactoTel);
        txt_contactoTel.setText(liderPhone);

        //Avatar
        StorageReference mStorage = FirebaseStorage.getInstance().getReference();
        StorageReference userImgRef = mStorage.child("media/images/userAvatar/"+liderAvatar+".png");

        ImageView img_contactoAvatar = popupContacto.findViewById(R.id.img_contactoAvatar);
        Glide.with(context).load(userImgRef)
                .error(R.drawable.ic_user)
                .circleCrop()
                .into(img_contactoAvatar);


        //Botones de Accion
        ImageButton btn_contactar_phone, btn_contactar_wapp;

        btn_contactar_phone = popupContacto.findViewById(R.id.btn_contactar_phone);
        btn_contactar_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneAction(context, liderPhone, "call");
            }
        });
        Glide.with(context).load(R.drawable.contact_call)
                .centerInside()
                .into(btn_contactar_phone);

        btn_contactar_wapp = popupContacto.findViewById(R.id.btn_contactar_wapp);
        btn_contactar_wapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneAction(context, liderPhone, "wapp");
            }
        });
        Glide.with(context).load(R.drawable.contat_wapp)
                .centerInside()
                .into(btn_contactar_wapp);



    }

    private List<ContactosMenuModelo> getContactosListMinisterio() {
        mDatabase.child("contactos").orderByChild("posicion").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contactosListMinisterios.clear();

                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    String uid, categoria, posicion, titulo, uidLider;

                    uid = objSnapshot.getKey();
                    categoria = objSnapshot.child("categoria").getValue().toString();
                    titulo = objSnapshot.child("titulo").getValue().toString();
                    uidLider = objSnapshot.child("uidLider").getValue().toString();


                    if(categoria.equals("ministerios")){
                        contactosListMinisterios.add(new ContactosMenuModelo(uid, titulo, uidLider));
                    }
                }
                //Collections.reverse(contactosList);
                recyclerView_contactarMiniterio.setLayoutManager(new LinearLayoutManager(getActivity()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "DatabaseError", Toast.LENGTH_SHORT).show();
            }
        });

        return contactosListMinisterios;
    }

    private List<ContactosMenuModelo> getContactosListEspecialista() {
        mDatabase.child("contactos").orderByChild("posicion").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contactosListEspecialistas.clear();

                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    String uid, categoria, posicion, titulo, uidLider;

                    uid = objSnapshot.getKey();
                    categoria = objSnapshot.child("categoria").getValue().toString();
                    titulo = objSnapshot.child("titulo").getValue().toString();
                    uidLider = objSnapshot.child("uidLider").getValue().toString();

                    if(categoria.equals("especialistas")){
                        contactosListEspecialistas.add(new ContactosMenuModelo(uid, titulo, uidLider));
                    }
                }
                //Collections.reverse(contactosList);
                recyclerView_contactarEspecialista.setLayoutManager(new LinearLayoutManager(getActivity()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "DatabaseError", Toast.LENGTH_SHORT).show();
            }
        });

        return contactosListEspecialistas;
    }

    private static List<ContactoPersonaModelo> getContactosListPersonas(final String inputName) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("usuarios").orderByChild("nombre").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                contactosListPersonas.clear();

                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    String uid, nombre, apellido, avatar;

                    uid = objSnapshot.getKey();
                    nombre = objSnapshot.child("nombre").getValue().toString();
                    apellido = objSnapshot.child("apellido").getValue().toString();
                    avatar = objSnapshot.child("avatar").getValue().toString();

                    String fullName = nombre + " " + apellido;
                    fullName = fullName.toUpperCase();
                    fullName = Normalizer.normalize(fullName, Normalizer.Form.NFD);
                    fullName = fullName.replace("[^//p{ASCII}]","");

                    StorageReference mStorage = FirebaseStorage.getInstance().getReference();
                    StorageReference avatarRef = mStorage.child("media/images/userAvatar/"+avatar+".png");

                    if(fullName.contains(inputName)){
                        contactosListPersonas.add(new ContactoPersonaModelo(uid, avatarRef, fullName));
                    }
                }
                //Collections.reverse(contactosList);
                //recyclerView_.setLayoutManager(new LinearLayoutManager(getActivity()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(getActivity(), "DatabaseError", Toast.LENGTH_SHORT).show();
            }
        });

        return contactosListPersonas;
    }

    private static void phoneAction(Context context, String phone, String action){
        String text = "";

        if(phone.length()==10){

            //Intent Llamada
            Intent call_intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));

            //Intent Whatsapp
            Intent wap_intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://api.whatsapp.com/send?phone=57"+phone+"&"+"text="+text+"&source=&data=&app_absent="));

            if (action.equals("call")) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.CALL_PHONE}, 1000);
                }else{
                    context.startActivity(call_intent);
                }
            }
            else if (action.equals("wapp")){
                context.startActivity(wap_intent);
            }
        }else{
            Toast.makeText(context, "El numero del usuario es invalio, intente contactarlo por otros medios", Toast.LENGTH_SHORT).show();
        }
    }

    private void getPastorInfo(final String action) {
        mDatabase.child("contactos").orderByChild("posicion").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contactosListMinisterios.clear();

                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    String categoria, uidContainer, uidLider;

                    categoria = objSnapshot.child("categoria").getValue().toString();
                    uidContainer = objSnapshot.getKey();
                    uidLider = objSnapshot.child("uidLider").getValue().toString();

                    if(categoria.equals("pastor")){
                        if(action == "click"){
                            getUserInfo(getActivity(), uidContainer, uidLider);
                        }
                        if(action == "longClick"){
                            //cambiarLider(getActivity(), uidContainer);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "DatabaseError", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void getUserInfo(final Context mContext, final String uidContainer, String uidLider){
        DatabaseReference user = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("usuarios")
                .child(uidLider);


        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nombre = dataSnapshot.child("nombre").getValue().toString();
                String apellido = dataSnapshot.child("apellido").getValue().toString();

                String userFullName = nombre + " " + apellido;
                String userPhone = dataSnapshot.child("telefono").getValue().toString();
                String userAvatar = dataSnapshot.child("avatar").getValue().toString();

                showPopupContacto(mContext, userFullName, userPhone, userAvatar, uidContainer);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void gotoURL(String URL){

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(getActivity(), Uri.parse(URL));
    }



    //Funciones de administrador

    public static void showMenuContacto(final Context context, final String uidContainer, final String uidLider){
        final String[] menuContacto = {
                "Cambiar Nombre",
                "Cambiar Lider",
                "Eliminar Contacto"
        };

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        mBuilder.setTitle("Seleccione una opcion");
        mBuilder.setIcon(R.drawable.logo_confra_round);
        mBuilder.setSingleChoiceItems(menuContacto, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        cambiarNombre(context, uidContainer);
                        break;
                    case 1:
                        cambiarLider(context, uidContainer);
                        break;
                    case 2:
                        eliminarContacto(context, uidContainer);
                        break;
                }
                dialog.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    public void agregarContacto(final String name, final String categoria){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        mBuilder.setIcon(R.drawable.logo_confra_round)
                .setTitle("¿Desea Agregar un nuevo contacto?")
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String uidLider = mFirebaseAuth.getUid();
                        String new_uid = UUID.randomUUID().toString();

                        Map<String, Object> map = new HashMap<>();
                        map.put("posicion", "0");
                        map.put("categoria", categoria);
                        map.put("titulo", name);
                        map.put("uidLider", uidLider);

                        DatabaseReference contacto = FirebaseDatabase
                                .getInstance()
                                .getReference()
                                .child("contactos")
                                .child(new_uid);

                        contacto.setValue(map);
                    }
                });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    public static void cambiarNombre(final Context context, final String uidContainer){
        final EditText txt_newName = new EditText(context);
        LinearLayout.LayoutParams txt_container = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        txt_newName.setLayoutParams(txt_container);


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setIcon(R.drawable.logo_confra_round)
                .setTitle("Cambiar nombre")
                .setMessage("Ingrese el nuevo nombre")
                .setView(txt_newName);

        alertDialog.setNegativeButton("Cancelar",
                (dialog, which) -> dialog.cancel());

        alertDialog.setPositiveButton("Continuar",
                (dialog, which) -> {

                    final String newName = txt_newName.getText().toString();
                    DatabaseReference contacto = FirebaseDatabase
                            .getInstance().getReference()
                            .child("contactos").child(uidContainer);

                    contacto.child("titulo").setValue(newName).
                            addOnCompleteListener(task -> Toast.makeText(context, "Actualizado exitosamente", Toast.LENGTH_SHORT).show());
                });

        alertDialog.show();
    }

    public static void cambiarLider(final Context context, final String uidContainer){
        //Toast.makeText(context, "Cambiar Lider", Toast.LENGTH_SHORT).show();

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View popup_contacto_edit = layoutInflater.inflate(R.layout.popup_contacto_edit, null);

        /* EDIT CONTACT*/

        final RecyclerView list_contactos;
        ContactoPersonaAdapter list_contactosAdapter;
        list_contactos = popup_contacto_edit.findViewById(R.id.list_contactos);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setView(popup_contacto_edit);

        list_contactos.setLayoutManager(new LinearLayoutManager(context));
        list_contactosAdapter = new ContactoPersonaAdapter(context, uidContainer, getContactosListPersonas(""));
        list_contactos.setAdapter(list_contactosAdapter);

        EditText txt_contacto_edit_lider;
        txt_contacto_edit_lider = popup_contacto_edit.findViewById(R.id.txt_contacto_edit_lider);
        txt_contacto_edit_lider.requestFocus();

        txt_contacto_edit_lider.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Toast.makeText(context, s, Toast.LENGTH_SHORT).show();

                String searchName = s.toString();

                searchName = searchName.toUpperCase();
                searchName = Normalizer.normalize(searchName, Normalizer.Form.NFD);
                searchName = searchName.replace("[^//p{ASCII}]","");

                getContactosListPersonas(searchName);
                list_contactos.setLayoutManager(new LinearLayoutManager(context));
            }
        });

        AlertDialog showContacto = alertDialogBuilder.create();
        showContacto.show();
    }

    public static void eliminarContacto(final Context context, final String uidContainer){
        //Toast.makeText(context, "Eliminar Contacto", Toast.LENGTH_SHORT).show();

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        mBuilder.setIcon(R.drawable.logo_confra_round)
                .setTitle("¿Desea Eliminar este contacto?")
                .setMessage("Esta accion es irreversible")

                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })

                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DatabaseReference contacto = FirebaseDatabase
                                .getInstance().getReference()
                                .child("contactos").child(uidContainer);

                        contacto.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(context, "Eliminado Exitosamente", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }


}