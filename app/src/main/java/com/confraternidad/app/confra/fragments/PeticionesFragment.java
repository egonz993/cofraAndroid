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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.confraternidad.app.confra.MainActivity;
import com.confraternidad.app.confra.R;
import com.confraternidad.app.confra.SplashActivity;
import com.confraternidad.app.confra.modelos_adapters.ContactoPersonaModelo;
import com.confraternidad.app.confra.modelos_adapters.ContactosMenuAdapter;
import com.confraternidad.app.confra.modelos_adapters.ContactosMenuModelo;
import com.confraternidad.app.confra.modelos_adapters.PeticionAdapter;
import com.confraternidad.app.confra.modelos_adapters.PeticionModelo;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PeticionesFragment extends Fragment {

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private static StorageReference mStorage = FirebaseStorage.getInstance().getReference();

    private TextView charsConut;
    private EditText txt_nombre, txt_correo, txt_telefono, txt_cedula, txt_titulo_peticion, txt_peticion;
    private CardView btn_peticion;
    private FloatingActionButton fabShowPeticiones;

    private String uidPastor;

    private static RecyclerView recyclerView_peticiones;
    private static PeticionAdapter recyclerViewc_PeticionAdapter;
    private static List<PeticionModelo> peticionesList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_peticiones, container, false);

        if(mFirebaseAuth.getCurrentUser() == null){
            MainActivity.navigateTo("home");

            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                    .setIcon(R.drawable.logo_confra_round)
                    .setTitle("Debe iniciar sesión para enviar peticiones")
                    .setMessage("¿Desea iniciar sesión ahora?")
                    .setPositiveButton("Si", (dialog, which) -> {
                        MainActivity.navigateTo("login");
                        dialog.dismiss();
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        }
        
        else{
            getPastorID();
            txt_titulo_peticion = view.findViewById(R.id.txt_titulo_peticion);
            txt_titulo_peticion.setOnClickListener(v -> {
                final String[] generos = {
                        "Bautismo",
                        "Presentación de niños",
                        "Matrimonio",
                        "Inscripción a discipulados",
                        "Membrecía",
                        "Otros"
                };

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                mBuilder.setTitle("Seleccione una opción");
                mBuilder.setIcon(R.drawable.logo_confra_round);
                mBuilder.setSingleChoiceItems(generos, -1, (dialog, which) -> {
                    if(which == 4){
                        Toast.makeText(getActivity(), "Ser miembro", Toast.LENGTH_SHORT).show();
                    }
                    txt_titulo_peticion.setText(generos[which]);
                    dialog.dismiss();
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            });


            txt_nombre = view.findViewById(R.id.txt_nombre);
            txt_correo = view.findViewById(R.id.txt_correo);
            txt_telefono = view.findViewById(R.id.txt_telefono);
            txt_cedula = view.findViewById(R.id.txt_cedula);

            txt_nombre.setText(mFirebaseAuth.getCurrentUser().getDisplayName());
            txt_correo.setText(mFirebaseAuth.getCurrentUser().getEmail());

            charsConut = view.findViewById(R.id.charsConut);
            txt_peticion = view.findViewById(R.id.txt_peticion);
            txt_peticion.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    charsConut.setText(s.length() + "/500");
                }
            });

            btn_peticion = view.findViewById(R.id.btn_peticion);
            btn_peticion.setOnClickListener(v -> {
                enviarPeticion();
            });



            recyclerView_peticiones = view.findViewById(R.id.recyclerView_peticiones);
            recyclerView_peticiones.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerViewc_PeticionAdapter = new PeticionAdapter(getPeticionesList(), getActivity());
            recyclerView_peticiones.setAdapter(recyclerViewc_PeticionAdapter);
            recyclerView_peticiones.setNestedScrollingEnabled(false);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(mFirebaseAuth.getCurrentUser() != null){
            if(mFirebaseAuth.getCurrentUser().getUid() == uidPastor){
                fabShowPeticiones = view.findViewById(R.id.fabShowPeticiones);
                fabShowPeticiones.animate().translationY(-getResources().getDimension(R.dimen.standard_65));
                fabShowPeticiones.hide();
            }
        }
    }


    private List<PeticionModelo> getPeticionesList() {

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("peticiones").orderByChild("fecha_hora").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                peticionesList.clear();

                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    final String uidContainer = objSnapshot.getKey();
                    final String uidPersona = objSnapshot.child("idUsuario").getValue().toString();
                    final String uidDestino = objSnapshot.child("uidDestino").getValue().toString();
                    final String fecha = objSnapshot.child("fecha_hora").getValue().toString();
                    final String leido = objSnapshot.child("leido").getValue().toString();
                    final String titulo = objSnapshot.child("titulo").getValue().toString();
                    final String peticion = objSnapshot.child("peticion").getValue().toString();

                    final String nombre = objSnapshot.child("nombre").getValue().toString();
                    final String correo = objSnapshot.child("correo").getValue().toString();
                    final String telefono = objSnapshot.child("telefono").getValue().toString();
                    final String cedula = objSnapshot.child("cedula").getValue().toString();

                    mDatabase.child("usuarios").child(uidPersona).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final String nombre = dataSnapshot.child("nombre").getValue().toString();
                            final String apellido = dataSnapshot.child("apellido").getValue().toString();
                            final String avatar = dataSnapshot.child("avatar").getValue().toString();

                            final String fullName = nombre + " " + apellido;

                            StorageReference mStorage = FirebaseStorage.getInstance().getReference();
                            StorageReference avatarRef = mStorage.child("media/images/userAvatar/"+avatar+".png");

                            if(mFirebaseAuth.getCurrentUser().getUid().equals(uidDestino)){
                                peticionesList.add(new
                                        PeticionModelo(uidContainer, uidPersona, uidDestino, fullName,
                                        fecha, leido, titulo, peticion, avatarRef)
                                );


//                                Collections.reverse(peticionesList);
                                recyclerView_peticiones.setLayoutManager(new LinearLayoutManager(getActivity()));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(getActivity(), "DatabaseError", Toast.LENGTH_SHORT).show();
            }
        });

        return peticionesList;
    }




    private void getPastorID() {
        mDatabase.child("contactos").orderByChild("posicion").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    String categoria, uid;
                    categoria = objSnapshot.child("categoria").getValue().toString();
                    uid = objSnapshot.child("uidLider").getValue().toString();

                    if(categoria.equals("pastor")){
                        uidPastor = uid;
                        btn_peticion.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "DatabaseError", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enviarPeticion(){



        if (txt_nombre.getText().toString().length() == 0){
            txt_nombre.setError("Este campo es requerido");
        }else if (txt_correo.getText().toString().length() == 0){
            txt_correo.setError("Este campo es requerido");
        }else if (txt_telefono.getText().toString().length() == 0){
            txt_telefono.setError("Este campo es requerido");
        }else if (txt_cedula.getText().toString().length() == 0){
            txt_cedula.setError("Este campo es requerido");
        }else if (txt_titulo_peticion.getText().toString().length() == 0){
            txt_titulo_peticion.setError("Este campo es requerido");
        }else if(txt_peticion.getText().toString().length() < 25){
            txt_peticion.setError("Debe escribir al menos 25 carcteres");
        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                    .setIcon(R.drawable.logo_confra_round)
                    .setTitle("¿Está seguro de enviar su petición?")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Si", (dialog, which) -> {
                        String peticion =
                                "Nombre: " + txt_nombre.getText().toString() + "\n" +
                                "Correo: " + txt_correo.getText().toString() + "\n" +
                                "Telefono: " + txt_telefono.getText().toString() + "\n" +
                                "Num. Identificación: " + txt_cedula.getText().toString() + "\n\n" +
                                "Petición: " + txt_peticion.getText().toString();
                        txt_peticion.setText(peticion);



                        Calendar rightNow = Calendar.getInstance();
                        int year = rightNow.get(Calendar.YEAR);
                        int month = rightNow.get(Calendar.MONTH)+1;
                        int day = rightNow.get(Calendar.DAY_OF_MONTH);
                        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
                        int min = rightNow.get(Calendar.MINUTE);
                        int sec = rightNow.get(Calendar.SECOND);

                        String dateTime;
                        if(month<10){
                            dateTime = year + "/0" + month + "/" + day + " " + hour + ":" + min + ":" + sec;
                        }else{
                            dateTime = year + "/" + month + "/" + day + " " + hour + ":" + min + ":" + sec;
                        }

                        Map<String, Object> mapPeticiones = new HashMap<>();
                        mapPeticiones.put("idUsuario", mFirebaseAuth.getCurrentUser().getUid());
                        mapPeticiones.put("fecha_hora", dateTime);
                        mapPeticiones.put("titulo", txt_titulo_peticion.getText().toString());
                        mapPeticiones.put("peticion", txt_peticion.getText().toString());
                        mapPeticiones.put("leido", "no");
//                        mapPeticiones.put("uidDestino", mFirebaseAuth.getCurrentUser().getUid());
                        mapPeticiones.put("uidDestino", uidPastor);

                        mapPeticiones.put("nombre", txt_nombre.getText().toString());
                        mapPeticiones.put("correo", txt_correo.getText().toString());
                        mapPeticiones.put("telefono", txt_telefono.getText().toString());
                        mapPeticiones.put("cedula", txt_cedula.getText().toString());

                        String uid = UUID.randomUUID().toString();
                        mDatabase.child("peticiones").child(uid).setValue(mapPeticiones);
                        MainActivity.navigateTo("peticiones");
                        Snackbar.make(getView(), "Petición enviada exitosamente", Snackbar.LENGTH_LONG).show();
                    })
                    .show();
        }
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
        btn_contactar_phone.setOnClickListener(v -> phoneAction(context, liderPhone, "call"));
        Glide.with(context).load(R.drawable.contact_call)
                .centerInside()
                .into(btn_contactar_phone);

        btn_contactar_wapp = popupContacto.findViewById(R.id.btn_contactar_wapp);
        btn_contactar_wapp.setOnClickListener(v -> phoneAction(context, liderPhone, "wapp"));
        Glide.with(context).load(R.drawable.contat_wapp)
                .centerInside()
                .into(btn_contactar_wapp);



    }

    private static void phoneAction(Context context, String phone, String action){
        String text = "";

        if(phone.length()==10){
            //SmsManager smsManager = SmsManager.getDefault();
            //smsManager.sendTextMessage("phone-number", null, "body-message", null, null);

            //Intent Llamada
            Intent call_intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));

            //Intent Mensaje de texto
            Intent msg_intent = new Intent(Intent.ACTION_VIEW);
            msg_intent.setData(Uri.parse("smsto:"));
            msg_intent.setType("vnd.android-dir/mms-sms");
            msg_intent.putExtra("address", phone);
            msg_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TOP);

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
            else if (action.equals("msg")){

                if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.SEND_SMS}, 1000);
                }else{
                    context.startActivity(msg_intent);
                }
            }
            else if (action.equals("wapp")){
                context.startActivity(wap_intent);
            }
        }else{
            Toast.makeText(context, "El numero del usuario es invalio, intente contactarlo por otros medios", Toast.LENGTH_SHORT).show();
        }
    }





    private void gotoURL(String URL){

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(getActivity(), Uri.parse(URL));
    }
}