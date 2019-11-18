package com.replon.www.replonhomy.Notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.replon.www.replonhomy.R;

import static com.replon.www.replonhomy.Home.Home.CHANNEL_ID;

public class NotificationHelper {

    public static final String TAG = "NotificationHelper";

    public static void displayNotification(Context context, String title, String body){

    Log.i(TAG,"here in notifications");
    NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(context,CHANNEL_ID)
            .setSmallIcon(R.drawable.app_icon_notifications)
            .setContentTitle(title)
            .setContentText(body)
            .setColor(Color.parseColor("#55EFC4"))
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true);

    NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(1,mBuilder.build());
        



    }
}
