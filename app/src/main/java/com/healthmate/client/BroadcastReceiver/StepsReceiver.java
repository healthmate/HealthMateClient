package com.healthmate.client.BroadcastReceiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.util.Log;

import com.healthmate.client.Activities.Activities;
import com.healthmate.client.JobService.StoreStepsJobScheduler;
import com.healthmate.client.MainActivity;
import com.healthmate.client.R;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.healthmate.client.Community.Community.MY_PREFS_NAME;

public class StepsReceiver extends BroadcastReceiver {
    private  int steps;
    private  String token;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);
        String restoredText = prefs.getString("login", null);
        if(restoredText != null){

            steps = prefs.getInt("steps", -1);

            Log.e("steps", Integer.toString(steps) );
            token = prefs.getString("token",null);

        }

        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("steps", Integer.toString(steps));
        bundle.putString("token", token);
        ComponentName componentName = new ComponentName(context, StoreStepsJobScheduler.class);
        JobInfo info = new JobInfo.Builder(101,componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setExtras(bundle)
                .build();
        JobScheduler scheduler =(JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS){
            Log.e("Scheduler", "onReceive: Job Scheduled");
        }

        //Intent new_intent = new Intent(context, StepService.class);
        //context.startService(new_intent);
        prefs = context.getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);
        restoredText = prefs.getString("login", null);

        if(restoredText != null){

            SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE).edit();
            editor.remove("steps");
            editor.putInt("steps", 0);
            editor.apply();

            Log.e("Test", "finish" );

        }
    }
}
