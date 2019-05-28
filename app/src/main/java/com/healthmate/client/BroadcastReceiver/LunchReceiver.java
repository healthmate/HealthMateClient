package com.healthmate.client.BroadcastReceiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.healthmate.client.Auth.LogIn;
import com.healthmate.client.MainActivity;
import com.healthmate.client.R;
import com.healthmate.client.ServiceClass.StepService;

public class LunchReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        //Intent new_intent = new Intent(context, StepService.class);
        //context.startService(new_intent);

        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle("Lunch").setContentText("Lunch time 🥘")
                .setSmallIcon(R.drawable.hm_notification);
        Intent notifyIntent = new Intent(context, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 4,
                notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notificationCompat = builder.build();
        //NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Log.e("final", "onCreate: final" );
        String channelId = "lunch_notification";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Lunch",NotificationManager.IMPORTANCE_DEFAULT);
            assert nm != null;
            nm.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }
        assert nm != null;
        nm.notify(4,notificationCompat);

        LogIn login = new LogIn();
        login.set_Lunch_Alarm(context);
    }
}
