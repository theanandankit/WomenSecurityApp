package com.project.womensecurityapp.Notification;

import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.project.womensecurityapp.R;

import static com.project.womensecurityapp.MainActivity.editor;
import static com.project.womensecurityapp.MainActivity.preferences;

public class notification extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.e("noti", preferences.getString("current_user_city", "GWA"));

        String a = remoteMessage.getData().get("message");
        int i = a.indexOf('(');
        int y = a.indexOf(')');
        Log.e("message", a);
        Log.e("a", String.valueOf(i));
        Log.e("b", String.valueOf(y));
        String b = a.substring(i + 1, y - 1);
        Log.e("lk", b);


        editor.putString("notification_info", b);
        editor.commit();


        // check users is verified or not
//        if (!preferences.getString("verified", "no").equals("no")) {
//            System.out.println("jk");

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "GWALIOR")
                    .setContentTitle(remoteMessage.getData().get("title"))
                    .setSmallIcon(R.drawable.ic_security)
                    .setContentText(remoteMessage.getData().get("message"))
                    .setAutoCancel(true);

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
            managerCompat.notify(999, builder.build());

     //   }
    }
}
