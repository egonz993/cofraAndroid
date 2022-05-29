package com.confraternidad.app.confra;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference();

    private final int DURACION_SPLASH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            new Handler().postDelayed(() -> {
                if (getIntent().getStringExtra("INTERNET") != null) {
                    String URL = getIntent().getStringExtra("INTERNET");
                    gotoURL(URL);
                }
                else if (getIntent().getStringExtra("APLICACION") != null) {
                    finish();
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.putExtra("PAGE", getIntent().getStringExtra("APLICACION"));
                    startActivity(intent);
                }
                else {
                    finish();
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
            }, DURACION_SPLASH);
    }

    private void gotoURL(String URL){

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(URL));
    }

}