package com.example.jipark.memorycache.notification;

/**
 * Created by brandonderbidge on 2/10/18.
 */

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import android.view.View;

import com.example.jipark.memorycache.R;

public class Notify implements INotify {

    private static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "my_notification_channel";
    private static int UNIQUE_REQUEST_CODE = 2;
    public void notification(View view)
    {

        String str = "Things";
        String url = "https://firebasestorage.googleapis.com/v0/b/roomme-ff743.appspot.com/o/2012333761?alt=media&token=315c3a44-f875-423a-bd91-f47183dd1d52";


    }

    @Override
    public void Notify(String url, String Text, IInformation information, Context context) {


        Intent resultIntent = new Intent(context, information.getClass());
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//        Intent backIntent = new Intent(context, new MapActivity().getClass());
//        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        resultIntent.putExtra("url", url);
        resultIntent.putExtra("text", Text);
        final PendingIntent resultPendingIntent = PendingIntent.getActivities(context, UNIQUE_REQUEST_CODE++,
                new Intent[] {resultIntent}, PendingIntent.FLAG_ONE_SHOT);




        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setVibrate(new long[]{0, 100, 100, 100, 100, 100})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("New Memory")
                .setContentText("Tap to see the memory you have found")
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle());

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}

