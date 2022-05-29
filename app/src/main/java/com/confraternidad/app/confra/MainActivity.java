package com.confraternidad.app.confra;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.confraternidad.app.confra.modelos_adapters.UsuarioModelo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference();

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private static NavController navController;
    private AppBarConfiguration mAppBarConfiguration;
    private TextView textUserMenu, textEmailMenu;
    private ImageView imgUserMenu;
    private FloatingActionButton fab;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        getUserInfo();

        textUserMenu = findViewById(R.id.textUserMenu);
        textEmailMenu = findViewById(R.id.textEmailMenu);
        imgUserMenu = findViewById(R.id.imgUserMenu);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navController = Navigation.findNavController(this, R.id.main_fragment);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_perfil, R.id.nav_login, R.id.nav_predicas,
                R.id.nav_ministerios,R.id.nav_discipulados, R.id.nav_peticiones,
                R.id.nav_contribuciones, R.id.nav_estudio_biblico, R.id.nav_contactanos, R.id.nav_cerrar_sesion)
                .setDrawerLayout(drawer)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> navController.navigate(R.id.nav_home));
        fab.setOnLongClickListener(v ->{
            if(isAdmin){
                isAdmin = false;
                startActivity(new Intent(this, SplashActivity.class));
                finish();
            }else{
                userIsAdmin();
            }
            return true;
        });

        ScreenInit_withUserInformtion();

    }


    @Override
    public void onBackPressed() {
        if(navController.getCurrentDestination().getId() != R.id.nav_home){
            navController.navigate(R.id.nav_home);
        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                    .setIcon(R.drawable.logo_confra_round)
                    .setTitle("¿Desea cerrar la aplicacion?")
                    .setPositiveButton("Si", (dialog, which) -> finish())
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.main_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    public static void navigateTo(String page){

        switch (page){
            case "home":
                navController.navigate(R.id.nav_home);
                break;
            case "perfil":
                navController.navigate(R.id.nav_perfil);
                break;
            case "login":
                navController.navigate(R.id.nav_login);
                break;
            case "predicas":
                navController.navigate(R.id.nav_predicas);
                break;
            case "ministerios":
                navController.navigate(R.id.nav_ministerios);
                break;
            case "discipulados":
                navController.navigate(R.id.nav_discipulados);
                break;
            case "peticiones":
                navController.navigate(R.id.nav_peticiones);
                break;
            case "contribuciones":
                navController.navigate(R.id.nav_contribuciones);
                break;
            case "contactanos":
                navController.navigate(R.id.nav_contactanos);
                break;
            case "estudioBiblico":
                navController.navigate(R.id.nav_estudio_biblico);
                break;

            case "comunidad":
                navController.navigate(R.id.nav_perfil);
                break;
        }
    }

    private void ScreenInit_withUserInformtion(){

        View nav_header = navigationView.getHeaderView(0);
        CardView btn_dropDownMenu = nav_header.findViewById(R.id.btn_dropDownMenu);
        btn_dropDownMenu.setOnClickListener(v -> {
            drawer.closeDrawer(GravityCompat.START);
            if(mFirebaseAuth.getCurrentUser() == null){
                navigateTo("login");
            }else{
                navigateTo("perfil");
            }
        });

        if (getIntent().getStringExtra("PAGE") != null) {
            navigateTo(getIntent().getStringExtra("PAGE"));
        }else{
            if(mFirebaseAuth.getCurrentUser() == null)
                navigateTo("login");
        }


        if(mFirebaseAuth.getCurrentUser() != null){
            String id = mFirebaseAuth.getCurrentUser().getUid();
            mDatabase.child("usuarios").child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        navigationView.getMenu().getItem(8).setVisible(false);
                        navigationView.getMenu().getItem(9).setVisible(true);
                        navigationView.getMenu().getItem(10).setVisible(true);

                        String avatar = dataSnapshot.child("avatar").getValue().toString();
                        String nombre = dataSnapshot.child("nombre").getValue().toString();
                        String apellido = dataSnapshot.child("apellido").getValue().toString();
                        String correo = dataSnapshot.child("correo").getValue().toString();

                        View nav_header = navigationView.getHeaderView(0);
                        TextView textUserMenu = nav_header.findViewById(R.id.textUserMenu);
                        TextView textEmailMenu = nav_header.findViewById(R.id.textEmailMenu);
                        textUserMenu.setText(nombre + " " + apellido);
                        textEmailMenu.setText(correo);


                        //User Avatar
                        ImageView userImg = nav_header.findViewById(R.id.imgUserMenu);
                        StorageReference userImgRef = mStorage.child("media/images/userAvatar/"+avatar+".png");


                        try{
                            Glide.with(MainActivity.this)
                                    .load(userImgRef)
                                    .error(R.drawable.ic_user)
                                    .circleCrop()
                                    .into(userImg);
                        }catch (Exception e){
//                            finish();
                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                            intent.putExtra("PAGE", "perfil");
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    navigationView = findViewById(R.id.nav_view);
                    View nav_header = navigationView.getHeaderView(0);
                    TextView textUserMenu = (TextView)nav_header.findViewById(R.id.textUserMenu);
                    TextView textEmailMenu = (TextView)nav_header.findViewById(R.id.textEmailMenu);

                    textUserMenu.setText(R.string.nav_header_title);
                    textEmailMenu.setText(R.string.nav_header_subtitle);
                }
            });
        }
        else{
            navigationView = findViewById(R.id.nav_view);
            navigationView.getMenu().getItem(8).setVisible(true);
            navigationView.getMenu().getItem(9).setVisible(false);
            navigationView.getMenu().getItem(10).setVisible(false);

            ImageView userImg = nav_header.findViewById(R.id.imgUserMenu);
            Glide.with(this)
                    .load(R.drawable.logo_confra)
                    .circleCrop()
                    .into(userImg);
        }
    }

    public static boolean isAdmin = false;
    public void userIsAdmin(){
        if(mFirebaseAuth.getCurrentUser() != null){
            String uid = mFirebaseAuth.getCurrentUser().getUid();

            mDatabase.child("usuarios").child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child("isAdmin").getValue().toString().equals("si")){
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setIcon(R.drawable.logo_confra_round)
                                .setTitle("¿Desea acceder a funciones de administrador?")
                                .setPositiveButton("Si", (dialog, which) -> {
                                    isAdmin = true;
                                    navigateTo("home");
                                    Snackbar.make(fab, "Ahora eres Administrador", Snackbar.LENGTH_LONG).show();
                                })
                                .setNegativeButton("No", (dialog, which) -> {
                                    isAdmin = false;
                                })
                                .show();

                    }else{
                        isAdmin = false;
                        startActivity(new Intent(MainActivity.this, SplashActivity.class));
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else isAdmin = false;
    }


    public static UsuarioModelo usuario = null;
    private void getUserInfo(){

        String id = mFirebaseAuth.getCurrentUser().getUid();
        mDatabase.child("usuarios").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    String password, avatar, nombre, apellido, fullName, descripcionPersonal,
                            direccion, telefono, correo, fechaNac, genero, profesion,
                            iglesiaSucursal, discipulados, ministerios, grupos, edad, admin;

                    password = dataSnapshot.child("password").getValue().toString();
                    avatar = dataSnapshot.child("avatar").getValue().toString();

                    descripcionPersonal = dataSnapshot.child("DescripcionPersonal").getValue().toString();

                    nombre = dataSnapshot.child("nombre").getValue().toString();
                    apellido = dataSnapshot.child("apellido").getValue().toString();
                    direccion = dataSnapshot.child("direccion").getValue().toString();
                    telefono = dataSnapshot.child("telefono").getValue().toString();
                    correo = dataSnapshot.child("correo").getValue().toString();
                    fechaNac = dataSnapshot.child("fechaNac").getValue().toString();
                    genero = dataSnapshot.child("genero").getValue().toString();
                    profesion = dataSnapshot.child("profesion").getValue().toString();

                    iglesiaSucursal = dataSnapshot.child("iglesiaSucursal").getValue().toString();
                    discipulados = dataSnapshot.child("discipulados").getValue().toString();
                    ministerios = dataSnapshot.child("ministerios").getValue().toString();
                    grupos = dataSnapshot.child("grupos").getValue().toString();
                    edad = calcularEdad(fechaNac);
                    fullName = nombre +" " + apellido;

                    admin = dataSnapshot.child("isAdmin").getValue().toString();


                    usuario = new UsuarioModelo();
                    usuario.setPassword(password);
                    usuario.setAvatar(avatar);

                    usuario.setNombre(nombre);
                    usuario.setApellido(apellido);
                    usuario.setFullName(fullName);
                    usuario.setDescripcionPersonal(descripcionPersonal);
                    usuario.setDireccion(direccion);
                    usuario.setTelefono(telefono);
                    usuario.setCorreo(correo);
                    usuario.setFechaNac(fechaNac);
                    usuario.setGenero(genero);
                    usuario.setProfesion(profesion);
                    usuario.setEdad(edad);

                    usuario.setIglesiaSucursal(iglesiaSucursal);
                    usuario.setDiscipulados(discipulados);
                    usuario.setMinisterios(ministerios);
                    usuario.setGrupos(grupos);

                    usuario.setIsAdmin(admin);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public static UsuarioModelo getCurrentUSer(){
        return usuario;
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


}