package com.confraternidad.app.confra.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.confraternidad.app.confra.MainActivity;
import com.confraternidad.app.confra.R;
import com.confraternidad.app.confra.SplashActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

public class PerfilFragment extends Fragment {

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference();

    private FloatingActionButton fabEditUserInformation, fabChangeUservAvatar, fabRotateUservAvatar;

    //Header
    private ImageView userImg;
    private TextView txt_userFullName, txt_userIglesiaSucursal, txt_userEdad;

    //Acerca de Mi
    private CardView card_AcercaDeMiHead, card_AcercaDeMi;
    private TextView txt_userAcercaDeMi;

    //Informacion Personal
    private CardView card_InformacionPersonalHead, card_InformacionPersonal;
    private TextView txt_userNombre, txt_userApellido, txt_userDireccion, txt_userTelefono,
            txt_userCorreo, txt_userFechaNac, txt_userGenero, txt_userProfesion;

    //Informacion Comunidad
    private CardView card_InformacionComunidadHead, card_InformacionComunidad;
    private TextView txt_userIglesia, txt_userDiscipulados,
            txt_userMinisterios, txt_userGrupos;

    //User Info
    private String password, avatar, nombre, apellido, fullName, descripcionPersonal,
            direccion, telefono, correo, clave, fechaNac, genero, profesion,
            iglesiaSucursal, discipulados, ministerios, grupos, edad;

    //Edit Information
    private ImageView userNewImg;

    private EditText txt_editAcercaDeMi, txt_editNombre, txt_editApellido, txt_editDireccion,
            txt_editTelefono, txt_editCorreo, txt_editClave, txt_editFechaNac, txt_editGenero, txt_editProfesion,
            txt_editIglesia, txt_editDiscipulados, txt_editMinisterios, txt_editGrupos, txt_edtitAvatar;

    private StorageReference NewImgUserRef;
    private Bitmap newBitmap;
    private byte[] dataImg;
    private float angle = 0;

    private String New_avatar, New_descripcionPersonal, New_nombre, New_apellido,
            New_direccion, New_telefono, New_correo, New_clave, New_fechaNac, New_genero, New_profesion,
            New_iglesiaSucursal, New_discipulados, New_ministerios, New_grupos, New_edad;

    //EditInformacion
    private View popupUpdateInformation, popupChangeAvatar;


    //Activity Variables
    private int USER_AVATAR_SELECTED = 10;         //onActivitiResult: USER AVATAR SELECTED



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_perfil, container, false);


        //Header
        userImg = view.findViewById(R.id.img_NewUserAvatar);
        txt_userFullName = view.findViewById(R.id.txt_userFullName);
        txt_userIglesiaSucursal = view.findViewById(R.id.txt_userIglesiaSucursal);
        txt_userEdad = view.findViewById(R.id.txt_userEdad);

        //Acerca de Mi
        card_AcercaDeMiHead = view.findViewById(R.id.card_AcercaDeMiHead);
        card_AcercaDeMi = view.findViewById(R.id.card_AcercaDeMi);
        txt_userAcercaDeMi = view.findViewById(R.id.txt_userAcercaDeMi);

        //Informacion Personal
        card_InformacionPersonalHead = view.findViewById(R.id.card_InformacionPersonalHead);
        card_InformacionPersonal = view.findViewById(R.id.card_InformacionPersonal);
        txt_userNombre = view.findViewById(R.id.txt_userNombre);
        txt_userApellido = view.findViewById(R.id.txt_userApellido);
        txt_userDireccion = view.findViewById(R.id.txt_userDireccion);
        txt_userTelefono = view.findViewById(R.id.txt_userTelefono);
        txt_userCorreo = view.findViewById(R.id.txt_userCorreo);
        txt_userFechaNac = view.findViewById(R.id.txt_userFechaNac);
        txt_userGenero = view.findViewById(R.id.txt_userGenero);
        txt_userProfesion = view.findViewById(R.id.txt_userProfesion);

        //Informacion Comunidad
        card_InformacionComunidadHead = view.findViewById(R.id.card_InformacionComunidadHead);
        card_InformacionComunidad = view.findViewById(R.id.card_InformacionComunidad);
        txt_userIglesia = view.findViewById(R.id.txt_userIglesia);
        txt_userDiscipulados = view.findViewById(R.id.txt_userDiscipulados);
        txt_userMinisterios = view.findViewById(R.id.txt_userMinisterios);
        txt_userGrupos = view.findViewById(R.id.txt_userGrupos);

        card_AcercaDeMiHead.setOnClickListener(v -> {
            if (card_AcercaDeMi.getVisibility() == view.VISIBLE){
                card_AcercaDeMi.setVisibility(View.GONE);
            }else{
                card_AcercaDeMi.setAlpha(0f);
                card_AcercaDeMi.setVisibility(View.VISIBLE);
                card_AcercaDeMi.animate()
                        .alpha(1f)
                        .setDuration(getResources().getInteger(android.R.integer.config_longAnimTime));
            }
        });
        card_InformacionPersonalHead.setOnClickListener(v -> {
            if (card_InformacionPersonal.getVisibility() == view.VISIBLE){
                card_InformacionPersonal.setVisibility(View.GONE);
            }else{
                card_InformacionPersonal.setAlpha(0f);
                card_InformacionPersonal.setVisibility(View.VISIBLE);
                card_InformacionPersonal.animate()
                        .alpha(1f)
                        .setDuration(getResources().getInteger(android.R.integer.config_longAnimTime));
            }
        });
        card_InformacionComunidadHead.setOnClickListener(v -> {
            if (card_InformacionComunidad.getVisibility() == view.VISIBLE){
                card_InformacionComunidad.setVisibility(View.GONE);
            }else{
                card_InformacionComunidad.setAlpha(0f);
                card_InformacionComunidad.setVisibility(View.VISIBLE);
                card_InformacionComunidad.animate()
                        .alpha(1f)
                        .setDuration(getResources().getInteger(android.R.integer.config_longAnimTime));
            }
        });

        fabEditUserInformation = view.findViewById(R.id.fabEditUserInformation);
        fabEditUserInformation.setOnClickListener(v -> showPopupEditInformation());

        getUserInfo();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fabEditUserInformation = view.findViewById(R.id.fabEditUserInformation);
        fabEditUserInformation.animate().translationY(-getResources().getDimension(R.dimen.standard_65));
        fabEditUserInformation.show();
    }


    private void showPopupEditInformation(){
        angle = 0;
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        popupUpdateInformation = layoutInflater.inflate(R.layout.popup_perfil_edit, null);

        //final ImageView userNewImg = popupUpdateInformation.findViewById(R.id.img_NewUserAvatar);
        userNewImg = popupUpdateInformation.findViewById(R.id.img_NewUserAvatar);
        txt_edtitAvatar = popupUpdateInformation.findViewById(R.id.txt_edtitAvatar);

        txt_editAcercaDeMi = popupUpdateInformation.findViewById(R.id.txt_editAcercaDeMi);

        txt_editNombre = popupUpdateInformation.findViewById(R.id.txt_editNombre);
        txt_editApellido = popupUpdateInformation.findViewById(R.id.txt_editApellido);
        txt_editDireccion = popupUpdateInformation.findViewById(R.id.txt_editDireccion);
        txt_editTelefono = popupUpdateInformation.findViewById(R.id.txt_editTelefono);
        txt_editFechaNac = popupUpdateInformation.findViewById(R.id.txt_editFechaNac);
        txt_editGenero = popupUpdateInformation.findViewById(R.id.txt_editGenero);
        txt_editProfesion = popupUpdateInformation.findViewById(R.id.txt_editProfesion);

        txt_editIglesia = popupUpdateInformation.findViewById(R.id.txt_editIglesia);
        txt_editDiscipulados = popupUpdateInformation.findViewById(R.id.txt_editDiscipulados);
        txt_editMinisterios = popupUpdateInformation.findViewById(R.id.txt_editMinisterios);
        txt_editGrupos = popupUpdateInformation.findViewById(R.id.txt_editGrupos);

        txt_editCorreo = popupUpdateInformation.findViewById(R.id.txt_editCorreo);
        txt_editClave = popupUpdateInformation.findViewById(R.id.txt_editClave);



        txt_edtitAvatar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                New_avatar = s.toString();

                StorageReference userImgRef = mStorage.child("media/images/userAvatar/"+New_avatar+".png");
                Glide.with(getActivity()).load(userImgRef)
                        .error(R.drawable.ic_user)
                        .circleCrop()
                        .into(userNewImg);
            }
        });

        txt_edtitAvatar.setText(avatar);
        txt_editAcercaDeMi.setText(descripcionPersonal);

        txt_editNombre.setText(nombre);
        txt_editApellido.setText(apellido);
        txt_editDireccion.setText(direccion);
        txt_editTelefono.setText(telefono);
        txt_editFechaNac.setText(fechaNac);
        txt_editGenero.setText(genero);
        txt_editProfesion.setText(profesion);

        txt_editIglesia.setText(iglesiaSucursal);
        txt_editDiscipulados.setText(discipulados);
        txt_editMinisterios.setText(ministerios);
        txt_editGrupos.setText(grupos);

        txt_editCorreo.setText(correo);
        txt_editClave.setText(clave);

        txt_editFechaNac.setOnClickListener(v -> setBirthday());
        txt_editGenero.setOnClickListener(v -> setGenero());
        txt_editDiscipulados.setOnClickListener(v -> setDiscipulados());
        txt_editMinisterios.setOnClickListener(v -> setMinisterios());

        fabChangeUservAvatar = popupUpdateInformation.findViewById(R.id.fabChangeUservAvatar);
        fabChangeUservAvatar.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Selecciona un imagen"), USER_AVATAR_SELECTED);
        });


        fabRotateUservAvatar = popupUpdateInformation.findViewById(R.id.fabRotateUservAvatar);
        fabRotateUservAvatar.setOnClickListener(v ->{
            if(newBitmap!=null)
                newBitmap = rotateBitmap(newBitmap, angle+90);
        });

        TextView txt_click_changePass = popupUpdateInformation.findViewById(R.id.txt_click_changePass);
        txt_click_changePass.setOnClickListener(v -> {
            ResetPass();
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity())
                .setCancelable(true)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    updateUserInfo();
                    getUserInfo();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .setView(popupUpdateInformation);

        final AlertDialog editUser = alertDialogBuilder.create();
        editUser.show();
    }

    private void getUserInfo(){
        String id = mFirebaseAuth.getCurrentUser().getUid();
        mDatabase.child("usuarios").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    password = dataSnapshot.child("password").getValue().toString();
                    avatar = dataSnapshot.child("avatar").getValue().toString();

                    descripcionPersonal = dataSnapshot.child("DescripcionPersonal").getValue().toString();

                    nombre = dataSnapshot.child("nombre").getValue().toString();
                    apellido = dataSnapshot.child("apellido").getValue().toString();
                    direccion = dataSnapshot.child("direccion").getValue().toString();
                    telefono = dataSnapshot.child("telefono").getValue().toString();
                    fechaNac = dataSnapshot.child("fechaNac").getValue().toString();
                    genero = dataSnapshot.child("genero").getValue().toString();
                    profesion = dataSnapshot.child("profesion").getValue().toString();

                    correo = dataSnapshot.child("correo").getValue().toString();
                    clave = dataSnapshot.child("password").getValue().toString();

                    iglesiaSucursal = dataSnapshot.child("iglesiaSucursal").getValue().toString();
                    discipulados = dataSnapshot.child("discipulados").getValue().toString();
                    ministerios = dataSnapshot.child("ministerios").getValue().toString();
                    grupos = dataSnapshot.child("grupos").getValue().toString();
                    edad = calcularEdad(fechaNac);
                    fullName = nombre +" " + apellido;

                    //User Avatar

                    StorageReference userImgRef = mStorage.child("media/images/userAvatar/"+avatar+".png");
                    try{
                        Glide.with(getActivity())
                            .load(userImgRef)
                            .error(R.drawable.ic_user)
                            .circleCrop()
                            .into(userImg);
                    }catch (Exception e){
//                      finish();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.putExtra("PAGE", "perfil");
                        startActivity(intent);
                    }

                    if(avatar.equals("man") || avatar.equals("woman")){
                        userImg.setColorFilter(getResources().getColor(R.color.colorBackground));
                    }else{
                        userImg.clearColorFilter();
                    }


                    //Header
                    txt_userFullName.setText(fullName);
                    txt_userIglesiaSucursal.setText(iglesiaSucursal);
                    txt_userEdad.setText(edad);

                    //Acerca de Mi
                    txt_userAcercaDeMi.setText(descripcionPersonal);

                    //Informacion Personal
                    txt_userNombre.setText(nombre);
                    txt_userApellido.setText(apellido);
                    txt_userDireccion.setText(direccion);
                    txt_userTelefono.setText(telefono);
                    txt_userCorreo.setText(correo);
                    txt_userFechaNac.setText(fechaNac);
                    txt_userGenero.setText(genero);
                    txt_userProfesion.setText(profesion);

                    //Informacion Comunidad
                    txt_userIglesia.setText(iglesiaSucursal);
                    txt_userDiscipulados.setText(discipulados);
                    txt_userMinisterios.setText(ministerios);
                    txt_userGrupos.setText(grupos);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateUserInfo(){
        New_avatar = txt_edtitAvatar.getText().toString();
        New_descripcionPersonal = txt_editAcercaDeMi.getText().toString();

        New_nombre = txt_editNombre.getText().toString();
        New_apellido = txt_editApellido.getText().toString();
        New_direccion = txt_editDireccion.getText().toString();
        New_telefono = txt_editTelefono.getText().toString();
        New_fechaNac = txt_editFechaNac.getText().toString();
        New_genero = txt_editGenero.getText().toString();
        New_profesion = txt_editProfesion.getText().toString();

        New_correo = txt_editCorreo.getText().toString();
        New_clave = txt_editClave.getText().toString();

        New_iglesiaSucursal = txt_editIglesia.getText().toString();
        New_discipulados = txt_editDiscipulados.getText().toString();
        New_ministerios = txt_editMinisterios.getText().toString();
        New_grupos = txt_editGrupos.getText().toString();

        final String uid = mFirebaseAuth.getUid();

        if (!New_avatar.equals(avatar) ){
            Snackbar.make(getView(), "Actualizando datos\nEste proceso puede demorar algunos segundos", Snackbar.LENGTH_LONG).show();

            NewImgUserRef = mStorage.child("media/images/userAvatar/"+New_avatar+".png");

            if(avatar.equals("man") || avatar.equals("woman")){
                NewImgUserRef.putBytes(dataImg).addOnCompleteListener(task ->
                        mDatabase.child("usuarios").child(uid).child("avatar").setValue(New_avatar)
                        .addOnCompleteListener(task1 ->
                                Snackbar.make(getView(),
                                        "Todos los datos han sido actualizados",
                                        Snackbar.LENGTH_LONG).show()));
                userImg.setColorFilter(getResources().getColor(R.color.colorBackground));

            }
            else{
                //Se elimina el viejo avatar y se sube el nuevo
                mStorage.child("media/images/userAvatar/"+avatar+".png").delete()
                        .addOnCompleteListener(task -> NewImgUserRef.putBytes(dataImg)
                                .addOnCompleteListener(task12 ->
                                        mDatabase.child("usuarios").child(uid).child("avatar")
                                                .setValue(New_avatar)
                                        .addOnCompleteListener(task121 ->
                                                Snackbar.make(getView(),
                                                        "Todos los datos han sido actualizados",
                                                        Snackbar.LENGTH_LONG).show())));
                userImg.clearColorFilter();
            }
        }

        if (!New_descripcionPersonal.equals(descripcionPersonal)){
            mDatabase.child("usuarios").child(uid).child("DescripcionPersonal").setValue(New_descripcionPersonal);
        }

        if (!New_nombre.equals(nombre)){
            mDatabase.child("usuarios").child(uid).child("nombre").setValue(New_nombre);
        }
        if (!New_apellido.equals(apellido)){
            mDatabase.child("usuarios").child(uid).child("apellido").setValue(New_apellido);
        }
        if (!New_direccion.equals(direccion)){
            mDatabase.child("usuarios").child(uid).child("direccion").setValue(New_direccion);
        }
        if (!New_telefono.equals(telefono)){
            mDatabase.child("usuarios").child(uid).child("telefono").setValue(New_telefono);
        }
        if (!New_fechaNac.equals(fechaNac)){
            mDatabase.child("usuarios").child(uid).child("fechaNac").setValue(New_fechaNac);
        }
        if (!New_genero.equals(genero)){
            mDatabase.child("usuarios").child(uid).child("genero").setValue(New_genero);
        }
        if (!New_profesion.equals(profesion)){
            mDatabase.child("usuarios").child(uid).child("profesion").setValue(New_profesion);
        }

        if (!New_correo.equals(correo)){
            mFirebaseAuth.getCurrentUser().updateEmail(correo).addOnCompleteListener(task -> {
                mDatabase.child("usuarios").child("correo").setValue(New_correo)
                        .addOnCompleteListener(task1 -> {
                            mFirebaseAuth.getCurrentUser().sendEmailVerification();
                        });
            });
        }
        if (!New_clave.equals(clave)){
            mFirebaseAuth.getCurrentUser().updatePassword(clave).addOnCompleteListener(task -> {
                mDatabase.child("usuarios").child(uid).child("password").setValue(New_clave);
            });
        }


        if (!New_iglesiaSucursal.equals(iglesiaSucursal)){
            mDatabase.child("usuarios").child(uid).child("iglesiaSucursal").setValue(New_iglesiaSucursal);
        }
        if (!New_discipulados.equals(discipulados)){
            mDatabase.child("usuarios").child(uid).child("discipulados").setValue(New_discipulados);
        }
        if (!New_ministerios.equals(ministerios)){
            mDatabase.child("usuarios").child(uid).child("ministerios").setValue(New_ministerios);
        }
        if (!New_grupos.equals(grupos)){
            mDatabase.child("usuarios").child(uid).child("grupos").setValue(New_grupos);
        }

        if (!New_nombre.equals(nombre) || !New_apellido.equals(apellido)){
            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(New_nombre + " " + New_apellido)
                    .build();

            mFirebaseAuth.getCurrentUser().updateProfile(userProfileChangeRequest);
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == USER_AVATAR_SELECTED) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {

                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    int bmapH = bitmap.getHeight();
                    int bmapW = bitmap.getWidth();
                    int dif = Math.abs(bmapH-bmapW)/2;

                    if (bmapH > bmapW){
                        bitmap= Bitmap.createBitmap(bitmap, 0 , dif, bmapW, bmapW);
                    }else{
                        bitmap = Bitmap.createBitmap(bitmap, dif , 0, bmapH, bmapH);
                    }

                    if (bitmap.getWidth()>300){
                        bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, false);
                    }


                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

                    dataImg = baos.toByteArray();
                    newBitmap = bitmap;

                    String mUID = FirebaseAuth.getInstance().getUid();
                    String idNewAvatar = UUID.randomUUID().toString();
                    New_avatar = mUID+"/"+idNewAvatar;
                    txt_edtitAvatar.setText(New_avatar);

                    fabRotateUservAvatar.show();

                    try{
                        Glide.with(getActivity())
                                .load(bitmap)
                                .error(R.drawable.ic_user)
                                .circleCrop()
                                .into(userNewImg);
                    }catch (Exception e){
//                        getActivity().finish();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.putExtra("PAGE", "perfil");
                        startActivity(intent);
                    }

                }
            } else if (resultCode == Activity.RESULT_CANCELED)  {
                Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
            }
        }

    }


    private void ResetPass(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setIcon(R.drawable.logo_confra_round)
                .setTitle("Cambiar contraseña")
                .setMessage("Se enviará un enlce a su dirección de correo para realizar el cambio de contraseña, ¿Desea continuar?");

        alertDialog.setNegativeButton("Cancelar",
                (dialog, which) -> dialog.dismiss());

        alertDialog.setPositiveButton("Continuar",
                (dialog, which) -> {
                    String emailAddress = mFirebaseAuth.getCurrentUser().getEmail();
                    mFirebaseAuth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(),
                                            "Se ha enviado un email a la dirección de correo especificada para restablecer la contraseña",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                    dialog.dismiss();
                });

        alertDialog.show();
    }


    private String calcularEdad(String edad) {
        int years = 0;

        String[] fecha = edad.split("/");
        String dateYear =  fecha[0];    //Año
        String dateMonth = fecha[1];    //Mes
        String dateDay =   fecha[2];    //Dia


        Calendar rightNow = Calendar.getInstance();
        int todayDay = rightNow.get(Calendar.DAY_OF_MONTH);
        int todayMonth = rightNow.get(Calendar.MONTH) + 1;
        int todayYear = rightNow.get(Calendar.YEAR);

        Calendar birthday = Calendar.getInstance();
        int birthDay = Integer.parseInt(dateDay);
        int birthMonth = Integer.parseInt(dateMonth);;
        int birthYear = Integer.parseInt(dateYear);;
        birthday.set(birthYear, birthMonth, birthDay);

        //Calcular edad
        if(todayYear == birthYear){
            years = 0;
        }else{
            years = todayYear - birthYear;
        }

        if (todayMonth < birthMonth){
            years--;
        }

        if(todayMonth == birthMonth){
            if(todayDay < birthDay){
                years--;
            }
        }

        edad = years + " años";
        return edad;
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

    private Bitmap rotateBitmap(Bitmap bmap, float angle){

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmap = Bitmap.createBitmap(bmap,0,0, bmap.getWidth(), bmap.getHeight(), matrix, false);
        bmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

        dataImg = baos.toByteArray();
        newBitmap = bmap;

        Glide.with(getActivity())
                .load(bmap)
                .error(R.drawable.ic_user)
                .circleCrop()
                .into(userNewImg);

        return bmap;
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

                txt_editFechaNac.setText(formatDate(year + "/" + monthOfYear + "/" + dayOfMonth));
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), dateSetListener, year, month, day);
        datePickerDialog.show();
    }

    private void setGenero(){

        final String[] generos = {"Masculino", "Femenino"};

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        mBuilder.setTitle("Seleccione una opción");
        mBuilder.setIcon(R.drawable.logo_confra_round);
        mBuilder.setSingleChoiceItems(generos, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                txt_editGenero.setText(generos[which]);
                dialog.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void setDiscipulados(){
        String[] discipulados = new String[]{
                "Discipulado 1",
                "Discipulado 2",
                "Escuela de Líderes",
                "Cómo mejorar relaciones interpersonales (CMRI)",
                "Desarrolla tu máximo potencial",
                "Administración financiera"
        };

        final boolean[] checkedItems = new boolean[]{
                false,
                false,
                false,
                false,
                false,
                false
        };


        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        mBuilder.setTitle("Seleccione los discipulados que ha completado");
        mBuilder.setIcon(R.drawable.logo_confra_round);

        mBuilder.setMultiChoiceItems(discipulados, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkedItems[which] = isChecked;
            }
        });

        mBuilder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        mBuilder.setPositiveButton("OK", (dialog, which) -> {
            txt_editDiscipulados.setText("");

            for(int i = 0; i<checkedItems.length; i++){
                if (checkedItems[i]){
                    if(txt_editDiscipulados.getText().length()>0){
                        txt_editDiscipulados.setText(
                                txt_editDiscipulados.getText() + "\n" +
                                discipulados[i]);
                    }else{
                        txt_editDiscipulados.setText(
                                txt_editDiscipulados.getText() +
                                discipulados[i]);
                    }
                }
            }
        });


                AlertDialog mDialog = mBuilder.create();
        mDialog.show();

    }

    private void setMinisterios(){
        String[] ministerios = new String[]{
                "Niños o educación cristiana:  Aventura Confra",
                "Adolescentes: impacto Junior",
                "Juvenil: impacto Senior",
                "Años dorados",
                "Parejas: empareja2 enamora2",
                "Diáconos: Logistica",
                "Discipulado: Grupos de conexión",
                "Alabanza y Adoración"
        };

        final boolean[] checkedItems = new boolean[]{
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false
        };


        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        mBuilder.setTitle("Seleccione los ministerios a los que pertenece");
        mBuilder.setIcon(R.drawable.logo_confra_round);

        mBuilder.setMultiChoiceItems(ministerios, checkedItems, (
                dialog, which, isChecked) -> checkedItems[which] = isChecked);

        mBuilder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        mBuilder.setPositiveButton("OK", (dialog, which) -> {
            txt_editMinisterios.setText("");

            for(int i = 0; i<checkedItems.length; i++){
                if (checkedItems[i]){
                    if(txt_editMinisterios.getText().length()>0){
                        txt_editMinisterios.setText(
                                txt_editMinisterios.getText() + "\n\n" +
                                ministerios[i]);
                    }else{
                        txt_editMinisterios.setText(
                                txt_editMinisterios.getText() +
                                ministerios[i]);
                    }
                }
            }
        });


        AlertDialog mDialog = mBuilder.create();
        mDialog.show();

    }

}