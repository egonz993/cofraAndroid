package com.confraternidad.app.confra;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.confraternidad.app.confra.modelos_adapters.ContactoPersonaAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.confraternidad.app.confra.MainActivity.isAdmin;


public class SillasActivity extends AppCompatActivity {


    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference();

    private String url_zoom = "";
    private String url_youtube = "";

    private int card_position, card_clicked;
    private String sala = "";

    private Button btn_select_sillas;

    private String[] usuariosID = {
            "","","","","","","","","","",
            "","","","","","","","","","",
            "","","","","","","","","","",
            "","","","","","","","","","",
            "","","","","","","","","","",
            "","","","","","","","","","",
            "","","","","","","","","","",
            "","","","","","","","","","",
            "","","","","","","","","","",
            "","","","","","","","","",""
    };

    private String[] usuariosNombre = {
            "","","","","","","","","","",
            "","","","","","","","","","",
            "","","","","","","","","","",
            "","","","","","","","","","",
            "","","","","","","","","","",
            "","","","","","","","","","",
            "","","","","","","","","","",
            "","","","","","","","","","",
            "","","","","","","","","","",
            "","","","","","","","","",""
    };

    private String[] usuariosTelefono = {
            "","","","","","","","","","",
            "","","","","","","","","","",
            "","","","","","","","","","",
            "","","","","","","","","","",
            "","","","","","","","","","",
            "","","","","","","","","","",
            "","","","","","","","","","",
            "","","","","","","","","","",
            "","","","","","","","","","",
            "","","","","","","","","",""
    };

    private CardView[] cardViews = {            //ID de las sillas, para metodo onClick
            null, null, null, null, null,
            null, null, null, null, null,
            null, null, null, null, null,
            null, null, null, null, null,
            null, null, null, null, null,
            null, null, null, null, null,
            null, null, null, null, null,
            null, null, null, null, null,
    };

    private boolean[] child_status_empty = {       //Status de las sillas: vacias
            true,true,true,true,true,
            true,true,true,true,true,
            true,true,true,true,true,
            true,true,true,true,true,
            true,true,true,true,true,
            true,true,true,true,true,
            true,true,true,true,true,
            true,true,true,true,true, true
    };

    private boolean[] child_status_filled = {       //Status de las sillas: ocupadas
            false,false,false,false,false,
            false,false,false,false,false,
            false,false,false,false,false,
            false,false,false,false,false,
            false,false,false,false,false,
            false,false,false,false,false,
            false,false,false,false,false,
            false,false,false,false,false,false
    };

    private boolean[] child_status_pending_selection = {     //Status de las sillas: seleccion pendiente
            false,false,false,false,false,
            false,false,false,false,false,
            false,false,false,false,false,
            false,false,false,false,false,
            false,false,false,false,false,
            false,false,false,false,false,
            false,false,false,false,false,
            false,false,false,false,false,false
    };

    private boolean[] child_status_reserved = {       //Status de las sillas: reservadas por mi
            false,false,false,false,false,
            false,false,false,false,false,
            false,false,false,false,false,
            false,false,false,false,false,
            false,false,false,false,false,
            false,false,false,false,false,
            false,false,false,false,false,
            false,false,false,false,false,false
    };

    private int x = 0;
    private RelativeLayout infoSalas;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sillas);

        getUsuarios();
        getURLs();

        final String[] salas = {
                "Jueves 7:00pm - 8:00pm",
                "Domingo 9:00am - 10:00am",
                "Domingo 11:00am - 12:00pm"
        };

        Spinner salasList = findViewById(R.id.sala_list);
        ArrayAdapter salasAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, salas);
        salasList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sala = salasList.getSelectedItem().toString();
                mDatabase.child("reservaciones").child(sala).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        card_position = 0;
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            String status_chair = snapshot.child("status").getValue().toString();
                            String userID = snapshot.child("userID").getValue().toString();

                            if (status_chair.equals("empty")){
                                child_status_empty[card_position] = true;
                                child_status_filled[card_position] = false;
                                child_status_pending_selection[card_position] = false;
                            }

                            else if(status_chair.equals("filled")){
                                child_status_empty[card_position] = false;
                                child_status_filled[card_position] = true;
                                child_status_pending_selection[card_position] = false;

                                if (userID.equals(mFirebaseAuth.getCurrentUser().getUid())){
                                    child_status_reserved[card_position] = true;
                                }else{
                                    child_status_reserved[card_position] = false;
                                }
                            }

                            else if (status_chair.equals("pending")){
                                child_status_empty[card_position] = false;
                                child_status_filled[card_position] = true;

                                if (userID.equals(mFirebaseAuth.getCurrentUser().getUid())){
                                    child_status_pending_selection[card_position] = true;
                                }else{
                                    child_status_pending_selection[card_position] = false;
                                }
                            }

                            card_position++;
                        }


                        sillasInit();
                        showInfoSala();
                        card_setOnClick();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        salasList.setAdapter(salasAdapter);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        fab.setOnLongClickListener(v -> {
            if(isAdmin){
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setIcon(R.drawable.logo_confra_round)
                        .setTitle("¿Desea vaciar todas las sillas?")
                        .setMessage("Esta acción es irreversible. Todas las reservaciones serán eliminadas")
                        .setPositiveButton("Si", (dialog, which) -> {
                            restoreDatabase();
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .show();
            }else{
                final EditText pass = new EditText(this);
                LinearLayout.LayoutParams txt_container = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                pass.setLayoutParams(txt_container);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setIcon(R.drawable.logo_confra_round)
                        .setTitle("Ingrese Clave Administrador")
                        .setMessage("Solo los administradores pueden acceder a esta información")
                        .setView(pass)
                        .setCancelable(false)
                        .setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel())
                        .setPositiveButton("Continuar", (dialog, which) -> {
                            final String nameSala = pass.getText().toString();
                            if(nameSala.equals("00120")){
                                infoSalas = findViewById(R.id.infoSalas);
                                if(infoSalas.getVisibility()==View.VISIBLE){
                                    infoSalas.setVisibility(View.GONE);
                                    btn_select_sillas.setVisibility(View.GONE);
                                }else {
                                    infoSalas.setVisibility(View.VISIBLE);
                                    btn_select_sillas.setVisibility(View.VISIBLE);
                                }
                            }else {
                                Toast.makeText(this, "Clave inválida", Toast.LENGTH_SHORT).show();
                            }
                        });

                alertDialog.show();
            }
            return false;
        });

        card_position = 0;
        btn_select_sillas = findViewById(R.id.btn_select_sillas);
        btn_select_sillas.setVisibility(View.GONE);

        infoSalas = findViewById(R.id.infoSalas);
        infoSalas.setVisibility(View.GONE);

    }

    private void sillasInit(){
        cardViews[0] = findViewById(R.id.silla_11);
        cardViews[1] = findViewById(R.id.silla_12);
        cardViews[2] = findViewById(R.id.silla_13);
        cardViews[3] = findViewById(R.id.silla_14);
        cardViews[4] = findViewById(R.id.silla_15);

        cardViews[5] = findViewById(R.id.silla_21);
        cardViews[6] = findViewById(R.id.silla_22);
        cardViews[7] = findViewById(R.id.silla_23);
        cardViews[8] = findViewById(R.id.silla_24);
        cardViews[9] = findViewById(R.id.silla_25);

        cardViews[10] = findViewById(R.id.silla_31);
        cardViews[11] = findViewById(R.id.silla_32);
        cardViews[12] = findViewById(R.id.silla_33);
        cardViews[13] = findViewById(R.id.silla_34);
        cardViews[14] = findViewById(R.id.silla_35);

        cardViews[15] = findViewById(R.id.silla_41);
        cardViews[16] = findViewById(R.id.silla_42);
        cardViews[17] = findViewById(R.id.silla_43);
        cardViews[18] = findViewById(R.id.silla_44);
        cardViews[19] = findViewById(R.id.silla_45);

        cardViews[20] = findViewById(R.id.silla_51);
        cardViews[21] = findViewById(R.id.silla_52);
        cardViews[22] = findViewById(R.id.silla_53);
        cardViews[23] = findViewById(R.id.silla_54);
        cardViews[24] = findViewById(R.id.silla_55);

        cardViews[25] = findViewById(R.id.silla_61);
        cardViews[26] = findViewById(R.id.silla_62);
        cardViews[27] = findViewById(R.id.silla_63);
        cardViews[28] = findViewById(R.id.silla_64);
        cardViews[29] = findViewById(R.id.silla_65);

        cardViews[30] = findViewById(R.id.silla_71);
        cardViews[31] = findViewById(R.id.silla_72);
        cardViews[32] = findViewById(R.id.silla_73);
        cardViews[33] = findViewById(R.id.silla_74);
        cardViews[34] = findViewById(R.id.silla_75);

        cardViews[35] = findViewById(R.id.silla_81);
        cardViews[36] = findViewById(R.id.silla_82);
        cardViews[37] = findViewById(R.id.silla_83);
        cardViews[38] = findViewById(R.id.silla_84);
        cardViews[39] = findViewById(R.id.silla_85);

        card_position = 0;
        for(CardView card : cardViews) {

            if (child_status_empty[card_position]) {
                card.setBackgroundColor(getResources().getColor(R.color.card_no_selected));
            }
            else if (child_status_pending_selection[card_position]) {
                card.setBackgroundColor(getResources().getColor(R.color.card_selected));
            }
            else if (child_status_reserved[card_position]) {
                card.setBackgroundColor(getResources().getColor(R.color.card_reserved));
            }
            else if (child_status_filled[card_position]) {
                card.setBackgroundColor(getResources().getColor(R.color.card_occupied));
            }
            card_position++;
        }
    }

    private void card_setOnClick(){

        for(CardView card : cardViews) {
            card.setOnClickListener(v -> {
                card_position = 0;
                while(v.getId() != cardViews[card_position].getId()){
                    card_position++;
                }

                if (child_status_empty[card_position] || child_status_pending_selection[card_position] || isAdmin) {
                    if (child_status_pending_selection[card_position] ) {
                        card_clicked = card_position;
                        nombreSilla();
                    }
                    else {

                        Map<String, Object> mapreservaciones = new HashMap<>();
                        mapreservaciones.put("status","pending");
                        mapreservaciones.put("userID", mFirebaseAuth.getCurrentUser().getUid());
                        mapreservaciones.put("nombre","");

                        String child;
                        if(card_position<10){
                            child = "silla 0"+card_position;
                        }else{
                            child = "silla "+card_position;
                        }

                        card_clicked = card_position;


                        mDatabase.child("reservaciones").child(sala).child(child).setValue(mapreservaciones)
                                .addOnCompleteListener(task -> {
                                    nombreSilla();
                                });
                    }
                }
                else if (child_status_reserved[card_position] || isAdmin){
                    card_clicked = card_position;
                    nombreSilla();
                }
                else{
                    Toast.makeText(this, "Este puesto ya esta reservado", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void nombreSilla(){
        final EditText txt_name = new EditText(this);
        LinearLayout.LayoutParams txt_container = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        txt_name.setLayoutParams(txt_container);


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setIcon(R.drawable.logo_confra_round)
                .setTitle("Reservar Silla")
                .setMessage("Ingrese el nombre de la persona")
                .setView(txt_name)
                .setCancelable(false)
                .setNeutralButton("Eliminar", (dialog, which) -> {
                    String child;
                    if(card_clicked<10){
                        child = "silla 0"+card_clicked;
                    }else{
                        child = "silla "+card_clicked;
                    }

                    Map<String, Object> mapreservaciones = new HashMap<>();
                    mapreservaciones.put("status","empty");
                    mapreservaciones.put("userID", "");
                    mapreservaciones.put("nombre", "");

                    mDatabase.child("reservaciones").child(sala).child(child).setValue(mapreservaciones);

                    dialog.cancel();
                })
                .setPositiveButton("Reservar", (dialog, which) -> {
                    final String name = txt_name.getText().toString();

                    String child;
                    if(card_clicked<10){
                        child = "silla 0"+card_clicked;
                    }else{
                        child = "silla "+card_clicked;
                    }

                    Map<String, Object> mapreservaciones = new HashMap<>();
                    mapreservaciones.put("status","pending");
                    mapreservaciones.put("userID", mFirebaseAuth.getCurrentUser().getUid());
                    mapreservaciones.put("nombre", name);

                    Snackbar.make(fab, "Actualizando...", Snackbar.LENGTH_INDEFINITE).show();

                    final String silla = child;
                    if(name.length()>2){
                        mDatabase.child("reservaciones").child(sala).child(child).setValue(mapreservaciones).addOnCompleteListener(task -> {
                            mDatabase.child("reservaciones").child(sala).child(silla).child("status").setValue("filled");
                            Snackbar.make(fab, "Actualizado Correctamente", Snackbar.LENGTH_LONG).show();
                        });
                    }else{
                        Snackbar.make(fab, "Reservación exitosa pero falta información\nIngrese el nombre", Snackbar.LENGTH_LONG).show();
                    }

                    dialog.cancel();
                });

        alertDialog.show();
    }

    private void gotoURL(String URL){

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(URL));
    }

    private void getURLs(){
        mDatabase.child("reservaciones").child("url_youtube").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                url_youtube = dataSnapshot.getValue().toString();

                FloatingActionButton fab_youtube = findViewById(R.id.fab_youtube);
                fab_youtube.setOnClickListener(v -> {
                    gotoURL(url_youtube+"");
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Admin

    private void getUsuarios(){
        mDatabase.child("usuarios").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int cont = 0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String  ID = snapshot.getKey();
                    String fullName = snapshot.child("nombre").getValue().toString() + " " + snapshot.child("apellido").getValue().toString();
                    String telefono = snapshot.child("telefono").getValue().toString();

                    usuariosID[cont] = ID;
                    usuariosNombre[cont] = fullName;
                    usuariosTelefono[cont] = telefono;
                    cont++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showInfoSala(){

        mDatabase.child("reservaciones").child(sala).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                updatePopupSala(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void updatePopupSala(DataSnapshot dataSnapshot){

        final TextView rr_silla_01 = findViewById(R.id.rr_silla_01);
        final TextView r_silla_01 = findViewById(R.id.r_silla_01);
        final TextView t_silla_01 = findViewById(R.id.t_silla_01);

        final TextView rr_silla_02 = findViewById(R.id.rr_silla_02);
        final TextView r_silla_02 = findViewById(R.id.r_silla_02);
        final TextView t_silla_02 = findViewById(R.id.t_silla_02);

        final TextView rr_silla_03 = findViewById(R.id.rr_silla_03);
        final TextView r_silla_03 = findViewById(R.id.r_silla_03);
        final TextView t_silla_03 = findViewById(R.id.t_silla_03);

        final TextView rr_silla_04 = findViewById(R.id.rr_silla_04);
        final TextView r_silla_04 = findViewById(R.id.r_silla_04);
        final TextView t_silla_04 = findViewById(R.id.t_silla_04);

        final TextView rr_silla_05 = findViewById(R.id.rr_silla_05);
        final TextView r_silla_05 = findViewById(R.id.r_silla_05);
        final TextView t_silla_05 = findViewById(R.id.t_silla_05);

        final TextView rr_silla_06 = findViewById(R.id.rr_silla_06);
        final TextView r_silla_06 = findViewById(R.id.r_silla_06);
        final TextView t_silla_06 = findViewById(R.id.t_silla_06);

        final TextView rr_silla_07 = findViewById(R.id.rr_silla_07);
        final TextView r_silla_07 = findViewById(R.id.r_silla_07);
        final TextView t_silla_07 = findViewById(R.id.t_silla_07);

        final TextView rr_silla_08 = findViewById(R.id.rr_silla_08);
        final TextView r_silla_08 = findViewById(R.id.r_silla_08);
        final TextView t_silla_08 = findViewById(R.id.t_silla_08);

        final TextView rr_silla_09 = findViewById(R.id.rr_silla_09);
        final TextView r_silla_09 = findViewById(R.id.r_silla_09);
        final TextView t_silla_09 = findViewById(R.id.t_silla_09);

        final TextView rr_silla_10 = findViewById(R.id.rr_silla_10);
        final TextView r_silla_10 = findViewById(R.id.r_silla_10);
        final TextView t_silla_10 = findViewById(R.id.t_silla_10);

        final TextView rr_silla_11 = findViewById(R.id.rr_silla_11);
        final TextView r_silla_11 = findViewById(R.id.r_silla_11);
        final TextView t_silla_11 = findViewById(R.id.t_silla_11);

        final TextView rr_silla_12 = findViewById(R.id.rr_silla_12);
        final TextView r_silla_12 = findViewById(R.id.r_silla_12);
        final TextView t_silla_12 = findViewById(R.id.t_silla_12);

        final TextView rr_silla_13 = findViewById(R.id.rr_silla_13);
        final TextView r_silla_13 = findViewById(R.id.r_silla_13);
        final TextView t_silla_13 = findViewById(R.id.t_silla_13);

        final TextView rr_silla_14 = findViewById(R.id.rr_silla_14);
        final TextView r_silla_14 = findViewById(R.id.r_silla_14);
        final TextView t_silla_14 = findViewById(R.id.t_silla_14);

        final TextView rr_silla_15 = findViewById(R.id.rr_silla_15);
        final TextView r_silla_15 = findViewById(R.id.r_silla_15);
        final TextView t_silla_15 = findViewById(R.id.t_silla_15);

        final TextView rr_silla_16 = findViewById(R.id.rr_silla_16);
        final TextView r_silla_16 = findViewById(R.id.r_silla_16);
        final TextView t_silla_16 = findViewById(R.id.t_silla_16);

        final TextView rr_silla_17 = findViewById(R.id.rr_silla_17);
        final TextView r_silla_17 = findViewById(R.id.r_silla_17);
        final TextView t_silla_17 = findViewById(R.id.t_silla_17);

        final TextView rr_silla_18 = findViewById(R.id.rr_silla_18);
        final TextView r_silla_18 = findViewById(R.id.r_silla_18);
        final TextView t_silla_18 = findViewById(R.id.t_silla_18);

        final TextView rr_silla_19 = findViewById(R.id.rr_silla_19);
        final TextView r_silla_19 = findViewById(R.id.r_silla_19);
        final TextView t_silla_19 = findViewById(R.id.t_silla_19);

        final TextView rr_silla_20 = findViewById(R.id.rr_silla_20);
        final TextView r_silla_20 = findViewById(R.id.r_silla_20);
        final TextView t_silla_20 = findViewById(R.id.t_silla_20);

        final TextView rr_silla_21 = findViewById(R.id.rr_silla_21);
        final TextView r_silla_21 = findViewById(R.id.r_silla_21);
        final TextView t_silla_21 = findViewById(R.id.t_silla_21);

        final TextView rr_silla_22 = findViewById(R.id.rr_silla_22);
        final TextView r_silla_22 = findViewById(R.id.r_silla_22);
        final TextView t_silla_22 = findViewById(R.id.t_silla_22);

        final TextView rr_silla_23 = findViewById(R.id.rr_silla_23);
        final TextView r_silla_23 = findViewById(R.id.r_silla_23);
        final TextView t_silla_23 = findViewById(R.id.t_silla_23);

        final TextView rr_silla_24 = findViewById(R.id.rr_silla_24);
        final TextView r_silla_24 = findViewById(R.id.r_silla_24);
        final TextView t_silla_24 = findViewById(R.id.t_silla_24);

        final TextView rr_silla_25 = findViewById(R.id.rr_silla_25);
        final TextView r_silla_25 = findViewById(R.id.r_silla_25);
        final TextView t_silla_25 = findViewById(R.id.t_silla_25);

        final TextView rr_silla_26 = findViewById(R.id.rr_silla_26);
        final TextView r_silla_26 = findViewById(R.id.r_silla_26);
        final TextView t_silla_26 = findViewById(R.id.t_silla_26);

        final TextView rr_silla_27 = findViewById(R.id.rr_silla_27);
        final TextView r_silla_27 = findViewById(R.id.r_silla_27);
        final TextView t_silla_27 = findViewById(R.id.t_silla_27);

        final TextView rr_silla_28 = findViewById(R.id.rr_silla_28);
        final TextView r_silla_28 = findViewById(R.id.r_silla_28);
        final TextView t_silla_28 = findViewById(R.id.t_silla_28);

        final TextView rr_silla_29 = findViewById(R.id.rr_silla_29);
        final TextView r_silla_29 = findViewById(R.id.r_silla_29);
        final TextView t_silla_29 = findViewById(R.id.t_silla_29);

        final TextView rr_silla_30 = findViewById(R.id.rr_silla_30);
        final TextView r_silla_30 = findViewById(R.id.r_silla_30);
        final TextView t_silla_30 = findViewById(R.id.t_silla_30);

        final TextView rr_silla_31 = findViewById(R.id.rr_silla_31);
        final TextView r_silla_31 = findViewById(R.id.r_silla_31);
        final TextView t_silla_31 = findViewById(R.id.t_silla_31);

        final TextView rr_silla_32 = findViewById(R.id.rr_silla_32);
        final TextView r_silla_32 = findViewById(R.id.r_silla_32);
        final TextView t_silla_32 = findViewById(R.id.t_silla_32);

        final TextView rr_silla_33 = findViewById(R.id.rr_silla_33);
        final TextView r_silla_33 = findViewById(R.id.r_silla_33);
        final TextView t_silla_33 = findViewById(R.id.t_silla_33);

        final TextView rr_silla_34 = findViewById(R.id.rr_silla_34);
        final TextView r_silla_34 = findViewById(R.id.r_silla_34);
        final TextView t_silla_34 = findViewById(R.id.t_silla_34);

        final TextView rr_silla_35 = findViewById(R.id.rr_silla_35);
        final TextView r_silla_35 = findViewById(R.id.r_silla_35);
        final TextView t_silla_35 = findViewById(R.id.t_silla_35);

        final TextView rr_silla_36 = findViewById(R.id.rr_silla_36);
        final TextView r_silla_36 = findViewById(R.id.r_silla_36);
        final TextView t_silla_36 = findViewById(R.id.t_silla_36);

        final TextView rr_silla_37 = findViewById(R.id.rr_silla_37);
        final TextView r_silla_37 = findViewById(R.id.r_silla_37);
        final TextView t_silla_37 = findViewById(R.id.t_silla_37);

        final TextView rr_silla_38 = findViewById(R.id.rr_silla_38);
        final TextView r_silla_38 = findViewById(R.id.r_silla_38);
        final TextView t_silla_38 = findViewById(R.id.t_silla_38);

        final TextView rr_silla_39 = findViewById(R.id.rr_silla_39);
        final TextView r_silla_39 = findViewById(R.id.r_silla_39);
        final TextView t_silla_39 = findViewById(R.id.t_silla_39);

        final TextView rr_silla_40 = findViewById(R.id.rr_silla_40);
        final TextView r_silla_40 = findViewById(R.id.r_silla_40);
        final TextView t_silla_40 = findViewById(R.id.t_silla_40);



        TextView[] rr_sillas = {
                rr_silla_01, rr_silla_02, rr_silla_03, rr_silla_04, rr_silla_05,
                rr_silla_06, rr_silla_07, rr_silla_08, rr_silla_09, rr_silla_10,
                rr_silla_11, rr_silla_12, rr_silla_13, rr_silla_14, rr_silla_15,
                rr_silla_16, rr_silla_17, rr_silla_18, rr_silla_19, rr_silla_20,
                rr_silla_21, rr_silla_22, rr_silla_23, rr_silla_24, rr_silla_25,
                rr_silla_26, rr_silla_27, rr_silla_28, rr_silla_29, rr_silla_30,
                rr_silla_31, rr_silla_32, rr_silla_33, rr_silla_34, rr_silla_35,
                rr_silla_36, rr_silla_37, rr_silla_38, rr_silla_39, rr_silla_40
        };

        TextView[] r_sillas = {
                r_silla_01, r_silla_02, r_silla_03, r_silla_04, r_silla_05,
                r_silla_06, r_silla_07, r_silla_08, r_silla_09, r_silla_10,
                r_silla_11, r_silla_12, r_silla_13, r_silla_14, r_silla_15,
                r_silla_16, r_silla_17, r_silla_18, r_silla_19, r_silla_20,
                r_silla_21, r_silla_22, r_silla_23, r_silla_24, r_silla_25,
                r_silla_26, r_silla_27, r_silla_28, r_silla_29, r_silla_30,
                r_silla_31, r_silla_32, r_silla_33, r_silla_34, r_silla_35,
                r_silla_36, r_silla_37, r_silla_38, r_silla_39, r_silla_40
        };

        TextView[] t_sillas = {
                t_silla_01, t_silla_02, t_silla_03, t_silla_04, t_silla_05,
                t_silla_06, t_silla_07, t_silla_08, t_silla_09, t_silla_10,
                t_silla_11, t_silla_12, t_silla_13, t_silla_14, t_silla_15,
                t_silla_16, t_silla_17, t_silla_18, t_silla_19, t_silla_20,
                t_silla_21, t_silla_22, t_silla_23, t_silla_24, t_silla_25,
                t_silla_26, t_silla_27, t_silla_28, t_silla_29, t_silla_30,
                t_silla_31, t_silla_32, t_silla_33, t_silla_34, t_silla_35,
                t_silla_36, t_silla_37, t_silla_38, t_silla_39, t_silla_40
        };


        x = 0;
        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
            x++;
            rr_sillas[x-1].setText(snapshot.child("nombre").getValue().toString());


            final String userID = snapshot.child("userID").getValue().toString();
            int cont = 0;
            for(String id : usuariosID){
                if(id.equals(userID)){
                    String fullName = usuariosNombre[cont];
                    String telefono = usuariosTelefono[cont];

                    r_sillas[x-1].setText(fullName);
                    t_sillas[x-1].setText(telefono);
                }
                cont++;
            }
        }
    }

    private void createNewSala(){
        final EditText txt_nameSala = new EditText(this);
        LinearLayout.LayoutParams txt_container = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        txt_nameSala.setLayoutParams(txt_container);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setIcon(R.drawable.logo_confra_round)
                .setTitle("Crear Sala")
                .setMessage("Ingrese el nombre de la sala")
                .setView(txt_nameSala)
                .setCancelable(false)
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel())
                .setPositiveButton("Continuar", (dialog, which) -> {
                    final String nameSala = txt_nameSala.getText().toString();
                    createSala(nameSala);
                });

        alertDialog.show();
    }

    private void createSala(String sala){
        Map<String, Object> mapreservaciones = new HashMap<>();
        mapreservaciones.put("status","empty");
        mapreservaciones.put("userID", "");
        mapreservaciones.put("nombre","");

        mDatabase.child("reservaciones").child(sala).child("silla 00").setValue(mapreservaciones);
        mDatabase.child("reservaciones").child(sala).child("silla 01").setValue(mapreservaciones);
        mDatabase.child("reservaciones").child(sala).child("silla 02").setValue(mapreservaciones);
        mDatabase.child("reservaciones").child(sala).child("silla 03").setValue(mapreservaciones);
        mDatabase.child("reservaciones").child(sala).child("silla 04").setValue(mapreservaciones);

        mDatabase.child("reservaciones").child(sala).child("silla 05").setValue(mapreservaciones);
        mDatabase.child("reservaciones").child(sala).child("silla 06").setValue(mapreservaciones);
        mDatabase.child("reservaciones").child(sala).child("silla 07").setValue(mapreservaciones);
        mDatabase.child("reservaciones").child(sala).child("silla 08").setValue(mapreservaciones);
        mDatabase.child("reservaciones").child(sala).child("silla 09").setValue(mapreservaciones);

        mDatabase.child("reservaciones").child(sala).child("silla 10").setValue(mapreservaciones);
        mDatabase.child("reservaciones").child(sala).child("silla 11").setValue(mapreservaciones);
        mDatabase.child("reservaciones").child(sala).child("silla 12").setValue(mapreservaciones);
        mDatabase.child("reservaciones").child(sala).child("silla 13").setValue(mapreservaciones);
        mDatabase.child("reservaciones").child(sala).child("silla 14").setValue(mapreservaciones);

        mDatabase.child("reservaciones").child(sala).child("silla 15").setValue(mapreservaciones);
        mDatabase.child("reservaciones").child(sala).child("silla 16").setValue(mapreservaciones);
        mDatabase.child("reservaciones").child(sala).child("silla 17").setValue(mapreservaciones);
        mDatabase.child("reservaciones").child(sala).child("silla 18").setValue(mapreservaciones);
        mDatabase.child("reservaciones").child(sala).child("silla 19").setValue(mapreservaciones);

        mDatabase.child("reservaciones").child(sala).child("silla 20").setValue(mapreservaciones);
        mDatabase.child("reservaciones").child(sala).child("silla 21").setValue(mapreservaciones);
        mDatabase.child("reservaciones").child(sala).child("silla 22").setValue(mapreservaciones);
        mDatabase.child("reservaciones").child(sala).child("silla 23").setValue(mapreservaciones);
        mDatabase.child("reservaciones").child(sala).child("silla 24").setValue(mapreservaciones);

        mDatabase.child("reservaciones").child(sala).child("silla 25").setValue(mapreservaciones);
        mDatabase.child("reservaciones").child(sala).child("silla 26").setValue(mapreservaciones);
        mDatabase.child("reservaciones").child(sala).child("silla 27").setValue(mapreservaciones);
        mDatabase.child("reservaciones").child(sala).child("silla 28").setValue(mapreservaciones);
        mDatabase.child("reservaciones").child(sala).child("silla 29").setValue(mapreservaciones);

        mDatabase.child("reservaciones").child(sala).child("silla 30").setValue(mapreservaciones);
        mDatabase.child("reservaciones").child(sala).child("silla 31").setValue(mapreservaciones);
        mDatabase.child("reservaciones").child(sala).child("silla 32").setValue(mapreservaciones);
        mDatabase.child("reservaciones").child(sala).child("silla 33").setValue(mapreservaciones);
        mDatabase.child("reservaciones").child(sala).child("silla 34").setValue(mapreservaciones);

        mDatabase.child("reservaciones").child(sala).child("silla 35").setValue(mapreservaciones);
        mDatabase.child("reservaciones").child(sala).child("silla 36").setValue(mapreservaciones);
        mDatabase.child("reservaciones").child(sala).child("silla 37").setValue(mapreservaciones);
        mDatabase.child("reservaciones").child(sala).child("silla 38").setValue(mapreservaciones);
        mDatabase.child("reservaciones").child(sala).child("silla 39").setValue(mapreservaciones);
    }

    private void restoreDatabase(){
        mDatabase.child("reservaciones").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Calendar rightNow = Calendar.getInstance();
                int day = rightNow.get(Calendar.DAY_OF_MONTH);
                int month = rightNow.get(Calendar.MONTH)+1;
                int year = rightNow.get(Calendar.YEAR);

                String date = year+""+month+""+day;

                mDatabase.child("reservaciones_historial").child(date).setValue(dataSnapshot.toString())
                        .addOnCompleteListener(task -> {
                            createSala("Jueves 7:00pm - 8:00pm");
                            createSala("Domingo 9:00am - 10:00am");
                            createSala("Domingo 11:00am - 12:00pm");
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}