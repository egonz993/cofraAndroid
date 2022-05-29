package com.confraternidad.app.confra.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.confraternidad.app.confra.MainActivity;
import com.confraternidad.app.confra.PruebasActivity;
import com.confraternidad.app.confra.R;
import com.confraternidad.app.confra.SillasActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class HomeFragment extends Fragment {

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference();

    private FloatingActionButton fabSendNotification;

    private CardView cardView_predicas, cardView_ministerios, cardView_discipulados, cardView_peticiones,
            cardView_contribuciones, cardView_contactanos, cardView_estudioBiblico, cardView_comunidad,
            cardView_administrador, cardView_reservaciones;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        fabSendNotification = view.findViewById(R.id.fabSendNotification);
        fabSendNotification.setOnClickListener(v -> onClickFabSendNotification());
        showFAB();

        cardView_reservaciones = view.findViewById(R.id.cardView_reservaciones);
        cardView_reservaciones.setOnClickListener(v -> {
            if(mFirebaseAuth.getCurrentUser() == null){
                MainActivity.navigateTo("home");

                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.logo_confra_round)
                        .setTitle("Debe iniciar sesión para reservar el ingreso")
                        .setMessage("¿Desea iniciar sesión ahora?")
                        .setPositiveButton("Si", (dialog, which) -> {
                            MainActivity.navigateTo("login");
                            dialog.dismiss();
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            }else {
                startActivity(new Intent(getActivity(), SillasActivity.class));
            }
        });

        cardView_predicas = view.findViewById(R.id.cardView_predicas);
        cardView_predicas.setOnClickListener(v -> MainActivity.navigateTo("predicas"));

        cardView_peticiones = view.findViewById(R.id.cardView_peticiones);
        cardView_peticiones.setOnClickListener(v -> MainActivity.navigateTo("peticiones"));

        cardView_ministerios = view.findViewById(R.id.cardView_ministerios);
        cardView_ministerios.setOnClickListener(v -> MainActivity.navigateTo("ministerios"));

        cardView_discipulados = view.findViewById(R.id.cardView_discipulados);
        cardView_discipulados.setOnClickListener(v -> MainActivity.navigateTo("discipulados"));

        cardView_contribuciones = view.findViewById(R.id.cardView_contribuciones);
        cardView_contribuciones.setOnClickListener(v -> MainActivity.navigateTo("contribuciones"));

        cardView_contactanos = view.findViewById(R.id.cardView_contactanos);
        cardView_contactanos.setOnClickListener(v -> MainActivity.navigateTo("contactanos"));

        cardView_estudioBiblico = view.findViewById(R.id.cardView_estudioBiblico);
        cardView_estudioBiblico.setOnClickListener(v -> MainActivity.navigateTo("estudioBiblico"));

        cardView_administrador = view.findViewById(R.id.cardView_administrador);
        cardView_administrador.setOnClickListener(v ->
                gotoURL("https://confra-b3ec5.web.app/dashboard")
        );
        if (MainActivity.isAdmin)       cardView_administrador.setVisibility(View.VISIBLE);
        else                            cardView_administrador.setVisibility(View.GONE);





        return view;
    }

    private void onClickFabSendNotification(){
        String URL = "https://console.firebase.google.com/u/0/project/confra-b3ec5/notification?hl=es-419";
        gotoURL(URL);
    }

    private void gotoURL(String URL){

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(getActivity(), Uri.parse(URL));
    }

    private void showFAB(){
        if(MainActivity.isAdmin){
            fabSendNotification.animate().translationY(-getResources().getDimension(R.dimen.standard_65));
            fabSendNotification.show();
        }else{
            fabSendNotification.animate().translationY(0);
            fabSendNotification.hide();
        }
    }

}