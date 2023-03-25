package com.example.furnacethermometer.lib;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.furnacethermometer.MainActivity;
import com.example.furnacethermometer.R;

public class MyNotificationManager {
    private static final String TAG = "MyNotificationManager";

    //    Main activity is an application context
    MainActivity mainActivity;

    public MyNotificationManager(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void createNotificationChannel(String name, String description, int importance, String notification_channel_id) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(notification_channel_id, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = mainActivity.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void createNotification(String textTitle, String textContent, int notificationId, String NOTIFICATION_CHANNEL_ID, int priority) {
        if (priority == 2) {
            Uri alertSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mainActivity, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(textTitle)
                    .setContentText(textContent)
                    .setSound(alertSound)
                    .setPriority(priority);
//                .setPriority(NotificationCompat.PRIORITY_LOW);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mainActivity);
// notificationId is a unique int for each notification that you must define
            notificationManager.notify(notificationId, builder.build());

        } else {
            //            Notification without sound
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mainActivity, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(textTitle)
                    .setContentText(textContent)
                    .setPriority(priority);
//                .setPriority(NotificationCompat.PRIORITY_LOW);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mainActivity);
// notificationId is a unique int for each notification that you must define
            notificationManager.notify(notificationId, builder.build());
        }
    }
}
