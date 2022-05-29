package com.confraternidad.app.confra.firebase;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.confraternidad.app.confra.SplashActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MiFirebaseMessagingService extends FirebaseMessagingService {

    public static final String TAG = "MyFirebaseMessaging";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(TAG, "onNewToken");
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "Mensaje Recibido: " + remoteMessage.getNotification().getTitle());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data: " + remoteMessage.getData());
        }

        sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
    }



    private void sendNotification(String title, String msg) {
        Intent intent = new Intent(this, SplashActivity.class);

        //Crear Aqui notificaciones inApp

    }
}


