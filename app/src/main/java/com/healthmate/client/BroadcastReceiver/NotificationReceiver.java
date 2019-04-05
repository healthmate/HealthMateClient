package com.healthmate.client.BroadcastReceiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.healthmate.client.Activities.Activities;
import com.healthmate.client.MainActivity;
import com.healthmate.client.R;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.healthmate.client.Community.Community.MY_PREFS_NAME;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //Intent new_intent = new Intent(context, StepService.class);
        //context.startService(new_intent);
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);
        String restoredText = prefs.getString("login", null);

        if(restoredText != null){

            SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE).edit();
            editor.remove("steps");
            editor.putInt("steps", 0);
            editor.apply();

            Log.e("Test", "finish" );

        }
    }
}
