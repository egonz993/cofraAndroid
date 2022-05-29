package com.confraternidad.app.confra.firebase;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class OfflineFirebase extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference("scores");
        //scoresRef.keepSynced(false);
        //scoresRef.keepSynced(true);
    }
}
