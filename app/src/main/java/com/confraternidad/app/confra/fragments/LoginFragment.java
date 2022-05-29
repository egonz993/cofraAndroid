package com.confraternidad.app.confra.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.confraternidad.app.confra.R;
import com.confraternidad.app.confra.SplashActivity;
import com.confraternidad.app.confra.modelos_adapters.UsuarioModelo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference();

    private UsuarioModelo usuario;


    private ImageView user_avatar, loginConfraLogo;
    private Button btn_signup, btn_login;
    private EditText et_correo, et_contraseña;
    private TextView txt_resetPass;

    private String correo = "";
    private String contraseña = "";

    private View popupNewUser = null;
    private EditText et_NewIglesia, et_NewNombre, et_NewApellido, et_NewFechaNac,
            et_NewTelefono, et_NewGenero, et_NewCorreo, et_NewContrasena, et_NewContrasenaConfirm;
    private Button btn_cancelNewUser, btn_addNewUser;

    private String NewAvatar, NewIglesia, NewNombre, NewApellido, NewFechaNac, NewCorreo, NewGenero,
            NewTelefono, NewContrasena, NewContrasenaConfirm;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mFirebaseAuth.useAppLanguage();

        loginConfraLogo = view.findViewById(R.id.loginConfraLogo);
        Glide.with(getActivity())
                .load(R.drawable.logo_confra)
                .circleCrop()
                .into(loginConfraLogo);


        et_correo = view.findViewById(R.id.txt_user_email);

        et_contraseña = view.findViewById(R.id.txt_pass);
        et_contraseña.setOnKeyListener((v, keyCode, event) -> {
            if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                btn_login();
                return true;
            }
            return false;
        });

        btn_login = view.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(v -> btn_login());

        btn_signup = view.findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener(v -> showPopupNewUser());

        txt_resetPass = view.findViewById((R.id.txt_resetPass));
        txt_resetPass.setOnClickListener(v -> onClickResetPass());

        return view;
    }

    private void btn_login(){
        boolean Flag = true;


        correo = et_correo.getText().toString();
        contraseña = et_contraseña.getText().toString();

        if (correo.isEmpty()){
            et_correo.setError("Este campo es requerido");
            Flag = false;
        }
        if (contraseña.isEmpty()){
            et_contraseña.setError("Este campo es requerido");
            Flag = false;
        }

        if(Flag){
            btn_login.setClickable(false);
            btn_signup.setClickable(false);
            txt_resetPass.setClickable(false);

            loginUsuario();
        }
    }

    private void loginUsuario() {

        mFirebaseAuth.signInWithEmailAndPassword(correo, contraseña).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Intent intent = new Intent(getActivity(), SplashActivity.class);
                intent.putExtra("APLICACION", "home");
                startActivity(intent);

                getActivity().finish();
            }else{
                btn_signup.setClickable(true);
                btn_login.setClickable(true);
                txt_resetPass.setClickable(true);

                mFirebaseAuth.useAppLanguage();
                Snackbar.make(getView(), task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void onClickResetPass(){
        final EditText email = new EditText(getActivity());

        LinearLayout.LayoutParams txt_container = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        email.setLayoutParams(txt_container);


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setIcon(R.drawable.logo_confra_round)
                .setTitle("Restablecer contraseña")
                .setMessage("Ingrese su dirección de correo")
                .setView(email);

        alertDialog.setNegativeButton("Cancelar",
                (dialog, which) -> dialog.dismiss());

        alertDialog.setPositiveButton("Continuar",
                (dialog, which) -> {
                    String emailAddress = email.getText().toString();
                    if(emailAddress.isEmpty()){
                        email.setError("Ingrese email");
                        Toast.makeText(getActivity(),
                                "Correo Inválido",
                                Toast.LENGTH_SHORT).show();

                    } else{
                        mFirebaseAuth.sendPasswordResetEmail(emailAddress)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(),
                                                "Se ha enviado un email a la dirección de correo especificada para restablecer la contraseña",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    dialog.dismiss();
                });

        alertDialog.show();
        email.requestFocus();
    }



    public void showPopupNewUser(){

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        popupNewUser = layoutInflater.inflate(R.layout.popup_login_new_user, null);

        user_avatar = popupNewUser.findViewById(R.id.user_avatar);

        et_NewIglesia = popupNewUser.findViewById(R.id.txt_iglesia);

        et_NewCorreo = popupNewUser.findViewById(R.id.txt_useremail);
        et_NewContrasena = popupNewUser.findViewById(R.id.txt_password);
        et_NewContrasenaConfirm = popupNewUser.findViewById(R.id.txt_password_confirm);

        et_NewNombre = popupNewUser.findViewById(R.id.txt_name);
        et_NewApellido = popupNewUser.findViewById(R.id.txt_lastname);
        et_NewFechaNac = popupNewUser.findViewById(R.id.txt_birthdate);
        et_NewGenero = popupNewUser.findViewById(R.id.txt_genero);
        et_NewTelefono = popupNewUser.findViewById(R.id.txt_telefono);
        btn_addNewUser = popupNewUser.findViewById(R.id.btn_addNewUser);
        btn_cancelNewUser = popupNewUser.findViewById(R.id.btn_cancelNewUser);


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity())
                .setCancelable(true)
                .setView(popupNewUser);

        final AlertDialog createUser = alertDialogBuilder.create();
        createUser.show();


        et_NewFechaNac.setOnClickListener(v -> setBirthday());
        et_NewGenero.setOnClickListener(v -> setGenero());

        btn_cancelNewUser.setOnClickListener(v -> createUser.dismiss());
        btn_addNewUser.setOnClickListener(v -> {

            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                    .setIcon(R.drawable.logo_confra_round)
                    .setTitle("¿Acepta nuestras políticas de privacidad?")
                    .setNeutralButton("Privacidad",(dialog, which) -> {
                        gotoURL("https://confra-b3ec5.web.app/#openModal_Privacidad");
                    })
                    .setPositiveButton("Si", (dialog, which) -> OnClickignup())
                    .show();
        });
    }

    public boolean OnClickignup(){

        boolean Flag = true;

        NewIglesia = et_NewIglesia.getText().toString();
        NewNombre = et_NewNombre.getText().toString();
        NewApellido = et_NewApellido.getText().toString();
        NewCorreo = et_NewCorreo.getText().toString();
        NewFechaNac = et_NewFechaNac.getText().toString();
        NewGenero = et_NewGenero.getText().toString();
        NewTelefono = et_NewTelefono.getText().toString();
        NewContrasena = et_NewContrasena.getText().toString();
        NewContrasenaConfirm = et_NewContrasenaConfirm.getText().toString();

        if (NewNombre.isEmpty()){
            et_NewNombre.setError("Este campo es requerido");
            Flag = false;
        }
        if (NewApellido.isEmpty()){
            et_NewApellido.setError("Este campo es requerido");
            Flag = false;
        }
        if (NewFechaNac.isEmpty()){
            et_NewFechaNac.setError("Este campo es requerido");
            Flag = false;
        }

        if (NewGenero.isEmpty()){
            et_NewGenero.setError("Este campo es requerido");
            Flag = false;
        }

        if (NewTelefono.isEmpty()){
            et_NewTelefono.setError("Este campo es requerido");
            Flag = false;
        }

        if (NewCorreo.isEmpty()){
            et_NewCorreo.setError("Este campo es requerido");
            Flag = false;
        }
        if (NewContrasena.isEmpty()){
            et_NewContrasena.setError("Este campo es requerido");
            Flag = false;
        }else if (NewContrasena.length()<6){
            et_NewContrasena.setError("La contraseña debe tener al menos seis caracteres");
            Flag = false;
        }
        if (NewContrasenaConfirm.isEmpty()){
            et_NewContrasenaConfirm.setError("Este campo es requerido");
            Flag = false;
        }else if (!NewContrasenaConfirm.equals(NewContrasena)){
            et_NewContrasenaConfirm.setError("Las contraseñas no coinciden");
            Flag = false;
        }

        if(Flag)    registrarUsuario();
        return Flag;
    }

    private void registrarUsuario() {
        mFirebaseAuth.createUserWithEmailAndPassword(NewCorreo, NewContrasena)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        String id = mFirebaseAuth.getCurrentUser().getUid();

                        Map<String, Object> mapNewUser = new HashMap<>();

                        //Header
                        mapNewUser.put("avatar", NewAvatar);
                        mapNewUser.put("password", NewContrasena);

                        //Acerca de mi
                        mapNewUser.put("DescripcionPersonal", "");

                        //Informacion Personal
                        mapNewUser.put("isAdmin", "no");
                        mapNewUser.put("nombre", NewNombre);
                        mapNewUser.put("apellido", NewApellido);
                        mapNewUser.put("direccion", "");
                        mapNewUser.put("telefono", NewTelefono);
                        mapNewUser.put("correo", NewCorreo);
                        mapNewUser.put("fechaNac", NewFechaNac);
                        mapNewUser.put("genero", NewGenero);
                        mapNewUser.put("profesion", "");

                        //Informacion Comunidad
                        mapNewUser.put("iglesiaSucursal", NewIglesia);
                        mapNewUser.put("discipulados", "");
                        mapNewUser.put("ministerios", "");
                        mapNewUser.put("grupos", "");

                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(NewNombre + " " + NewApellido)
                                .build();


                        mDatabase.child("usuarios").child(id).setValue(mapNewUser).addOnCompleteListener(task1 -> {
                            mFirebaseAuth.getCurrentUser().sendEmailVerification();
                            mFirebaseAuth.getCurrentUser().updateProfile(userProfileChangeRequest);
                            Intent intent = new Intent(getActivity(), SplashActivity.class);
                            intent.putExtra("APLICACION", "perfil");
                            startActivity(intent);

                            getActivity().finish();
                        });
                    }else{
                        Toast.makeText(getActivity(), "Error en registro", Toast.LENGTH_SHORT).show();
                    }
                });

    }



    private void setBirthday() {
        Calendar rightNow = Calendar.getInstance();
        int day = rightNow.get(Calendar.DAY_OF_MONTH);
        int month = rightNow.get(Calendar.MONTH);
        int year = rightNow.get(Calendar.YEAR);

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                monthOfYear++;

                et_NewFechaNac = popupNewUser.findViewById(R.id.txt_birthdate);
                et_NewFechaNac.setText(formatDate(year + "/" + monthOfYear + "/" + dayOfMonth));
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), dateSetListener, year, month, day);
        datePickerDialog.show();
    }

    private void setGenero(){

        final String[] generos = {"Masculino", "Femenino"};

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        mBuilder.setTitle("Seleccione una opcion");
        mBuilder.setIcon(R.drawable.logo_confra_round);
        mBuilder.setSingleChoiceItems(generos, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which){
                    case 0:
                        et_NewGenero.setText("Masculino");
                        NewAvatar = "man";
                        Glide.with(getActivity())
                                .load(R.drawable.man)
                                .circleCrop()
                                .error(R.drawable.ic_user)
                                .into(user_avatar);
                        break;

                    case 1:
                        et_NewGenero.setText("Femenino");
                        NewAvatar = "woman";
                        Glide.with(getActivity())
                                .load(R.drawable.woman)
                                .circleCrop()
                                .error(R.drawable.ic_user)
                                .into(user_avatar);
                        break;
                }

                et_NewGenero.setText(generos[which]);
                dialog.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private String formatDate(String date){
        String newDate, year, month, day;
        String[] date_array;

        date_array = date.split("/");
        year = date_array[0];
        month = date_array[1];
        day = date_array[2];

        if (day.length()==1){
            day = "0" + day;
        }

        if (month.length()==1){
            month = "0" + month;
        }

        //newDate = day + "/" + month + "/" + year;
        newDate = year + "/" + month + "/" + day;
        return newDate;
    }

    private void gotoURL(String URL){
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(getActivity(), Uri.parse(URL));
    }
}
