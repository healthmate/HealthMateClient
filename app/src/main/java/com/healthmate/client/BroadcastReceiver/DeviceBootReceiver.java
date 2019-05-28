package com.healthmate.client.BroadcastReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.healthmate.client.Auth.LogIn;

import java.util.Calendar;

public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("ALARM set", "SET!!!" );
        LogIn login = new LogIn();
        login.set_Breakfast_Alarm(context);
        login.set_DailyStorage(context);
        login.set_Lunch_Alarm(context);
        login.set_Dinner_Alarm(context);
    }
}
