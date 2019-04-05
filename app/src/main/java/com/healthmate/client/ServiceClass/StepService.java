package com.healthmate.client.ServiceClass;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.healthmate.client.Activities.Activities;
import com.healthmate.client.MainActivity;
import com.healthmate.client.R;

import java.util.Calendar;

public class StepService extends JobIntentService{
    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("My Title").setContentText("This is the body")
                .setSmallIcon(R.drawable.ic_launcher_background);
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 2,
                notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notificationCompat = builder.build();
        //NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Log.e("final", "onCreate: final" );
        String channelId = "my_channel_id";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Testing",NotificationManager.IMPORTANCE_DEFAULT);
            assert nm != null;
            nm.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }
        assert nm != null;
        nm.notify(1,notificationCompat);
    }
}
