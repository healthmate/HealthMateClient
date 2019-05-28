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

import com.bumptech.glide.Glide;
import com.healthmate.client.Activities.Activities;
import com.healthmate.client.Auth.LogIn;
import com.healthmate.client.JobService.StoreHistoryJobScheduler;
import com.healthmate.client.JobService.StoreStepsJobScheduler;
import com.healthmate.client.MainActivity;
import com.healthmate.client.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;
import static com.healthmate.client.Community.Community.MY_PREFS_NAME;

public class StepsReceiver extends BroadcastReceiver {
    private  int steps;
    private  String token;
    private String breakfast,lunch,dinner,brunch,lunner,calorie_left;
    private Integer calories_left,calories_gained,calories_burnt,breakfast_cal, lunch_cal, dinner_cal,daily_calories;
    private Integer brunch_cal,lunner_cal;

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);
        String restoredText = prefs.getString("login", null);
        if(restoredText != null){
            steps = prefs.getInt("steps", -1);
            breakfast = prefs.getString("Breakfast","none");
            lunch = prefs.getString("Lunch", "none");
            dinner = prefs.getString("Dinner","none");
            brunch = prefs.getString("Brunch", "none");
            lunner = prefs.getString("Lunner", "none");
            token = prefs.getString("token",null);

            breakfast_cal = Integer.parseInt(prefs.getString("Breakfast_cal", "0"));
            lunch_cal = Integer.parseInt(prefs.getString("Lunch_cal", "0"));
            dinner_cal = Integer.parseInt(prefs.getString("Dinner_cal", "0"));
            brunch_cal = Integer.parseInt(prefs.getString("Brunch_cal", "0"));
            lunner_cal = Integer.parseInt(prefs.getString("Lunner_cal", "0"));
            daily_calories = Integer.parseInt(prefs.getString("daily_calorie_target","0"));
            Log.e("daily_calories", daily_calories.toString());
            calories_gained = breakfast_cal + lunch_cal + dinner_cal + brunch_cal + lunner_cal;
            calories_left = daily_calories - calories_gained;
        }

        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("steps", Integer.toString(steps));
        bundle.putString("token", token);
        bundle.putString("date", get_Date());

        PersistableBundle bundle_2 = new PersistableBundle();
        bundle_2.putString("calories_left", Integer.toString(calories_left));
        bundle_2.putString("token", token);
        bundle_2.putString("date", get_Date());
        bundle_2.putString("breakfast",breakfast);
        bundle_2.putString("lunch",lunch);
        bundle_2.putString("dinner",dinner);
        bundle_2.putString("lunner",lunner);
        bundle_2.putString("brunch",brunch);

        ComponentName componentName = new ComponentName(context, StoreStepsJobScheduler.class);
        JobInfo info = new JobInfo.Builder(101,componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setExtras(bundle)
                .build();

        ComponentName componentName_2 = new ComponentName(context, StoreHistoryJobScheduler.class);
        JobInfo info_2 = new JobInfo.Builder(112,componentName_2)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setExtras(bundle_2)
                .build();
        JobScheduler scheduler =(JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobScheduler scheduler_2 =(JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        assert scheduler != null;
        int resultCode = scheduler.schedule(info);
        assert scheduler_2 != null;
        scheduler_2.schedule(info_2);
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
            editor.remove("Breakfast");
            editor.putString("Breakfast", "none");
            editor.remove("Lunch");
            editor.putString("Lunch", "none");
            editor.remove("DInner");
            editor.putString("Dinner", "none");
            editor.remove("Brunch");
            editor.putString("Brunch", "none");
            editor.remove("Lunner");
            editor.putString("Lunner", "none");

            editor.remove("Breakfast_cal");
            editor.putString("Breakfast_cal", "0");
            editor.remove("Lunch_cal");
            editor.putString("Lunch_cal", "0");
            editor.remove("DInner_cal");
            editor.putString("Dinner_cal", "0");
            editor.remove("Brunch_cal");
            editor.putString("Brunch_cal", "0");
            editor.remove("Lunner_cal");
            editor.putString("Lunner_cal", "0");

            editor.apply();

            Log.e("Test", "finish" );

        }
        LogIn login = new LogIn();
        login.set_DailyStorage(context);
    }

    private String get_Date(){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        date.setTimeZone(TimeZone.getTimeZone("GMT"));
        return date.format(currentLocalTime);
    }
}
