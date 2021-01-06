package com.project.womensecurityapp.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.project.womensecurityapp.MainActivity;
import com.project.womensecurityapp.R;
import com.project.womensecurityapp.Shake.shake_service;

public class foreground_service extends Service {

    public static Handler background_upadte_handler = new Handler();
    public static final String CHANNEL_ID = "exampleServiceChannel";
    Runnable runnable;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String input = intent.getStringExtra("inputExtra");

        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Women Security Running in Background")
                .setContentText(input)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSmallIcon(R.drawable.ic_security)
                .setContentIntent(pendingIntent)
                .build();

        startService(new Intent(getApplicationContext(), shake_service.class));


        startForeground(1, notification);


        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public void onDestroy() {
        super.onDestroy();

        stopService(new Intent(getApplicationContext(), shake_service.class));

    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
