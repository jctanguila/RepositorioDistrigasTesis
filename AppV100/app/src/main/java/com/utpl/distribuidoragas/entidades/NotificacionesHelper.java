package com.utpl.distribuidoragas.entidades;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;

import androidx.core.app.NotificationCompat;

import com.utpl.distribuidoragas.R;

public class NotificacionesHelper extends ContextWrapper {

    private NotificationManager manager;
    public static final String PRIMARY_CHANNEL = "canaldistrigas";
    public static final String PRIMARY_CHANNEL_ID = "canaldistrigasID";

    public NotificacionesHelper(Context base) {
        super(base);
        crearCanales();
    }

    public void crearCanales(){
        NotificationChannel channel = new NotificationChannel(PRIMARY_CHANNEL_ID, PRIMARY_CHANNEL, NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(R.color.white);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(channel);
    }

    public NotificationCompat.Builder getNotification(String title, String body) {
        return new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.cilindro)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    public NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

}
