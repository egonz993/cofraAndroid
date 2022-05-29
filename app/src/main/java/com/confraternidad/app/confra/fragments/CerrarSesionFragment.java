package com.confraternidad.app.confra.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.confraternidad.app.confra.MainActivity;
import com.confraternidad.app.confra.R;
import com.confraternidad.app.confra.SplashActivity;
import com.google.firebase.auth.FirebaseAuth;

public class CerrarSesionFragment extends Fragment {

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MainActivity.navigateTo("home");
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.logo_confra_round)
                .setTitle("¿Desea cerrar su sesión?")
                .setPositiveButton("Si", (dialog, which) -> {
                    mFirebaseAuth.signOut();
                    startActivity(new Intent(getActivity(), SplashActivity.class));
                    getActivity().finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cerrar_sesion, container, false);
        return view;
    }
}
