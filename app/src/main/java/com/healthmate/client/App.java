package com.healthmate.client;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.healthmate.client.Activities.Activities;

import java.util.Objects;

import static com.healthmate.client.Community.Community.MY_PREFS_NAME;

public class App extends Application {

    public static final String CHANNEL_ID = "StepsJobService";


    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "steps",
                    NotificationManager.IMPORTANCE_LOW
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);

        }
    }
}
