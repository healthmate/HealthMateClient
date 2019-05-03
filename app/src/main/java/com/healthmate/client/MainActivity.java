package com.healthmate.client;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.healthmate.client.Activities.Activities;
import com.healthmate.client.BroadcastReceiver.DeviceBootReceiver;
import com.healthmate.client.BroadcastReceiver.StepsReceiver;
import com.healthmate.client.BroadcastReceiver.NotificationReceiver;
import com.healthmate.client.Community.Community;
import com.healthmate.client.Lifestyle.Lifestyle;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = new Lifestyle();
            item.setIcon(R.drawable.ic_carrot);
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    item.setIcon(R.drawable.ic_carrot);

                    selectedFragment = new Lifestyle();

                    break;
                case R.id.navigation_dashboard:

                    item.setIcon(R.drawable.ic_walk);
                    selectedFragment = new Activities();
                    break;
                case R.id.navigation_notifications:

                    item.setIcon(R.drawable.ic_meeting);
                    selectedFragment = new Community();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Log.e("ALARM set", "SET!!!" );
        Intent notifyIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /*ComponentName componentName = new ComponentName(this, StepsJobService.class);
        JobInfo info = new JobInfo.Builder(123, componentName)
                .*/

        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,9);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);

        /////////

        Intent notifyIntent2 = new Intent(this, StepsReceiver.class);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this,
                3, notifyIntent2, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager2 = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.HOUR_OF_DAY,23);
        calendar2.set(Calendar.MINUTE,59);
        calendar2.set(Calendar.SECOND,0);
        calendar2.set(Calendar.MILLISECOND,0);

        alarmManager2.setRepeating(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent2);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
            alarmManager2.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis(),pendingIntent2);
        }

        /*PackageManager pm = this.getPackageManager();
        ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class);
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);*/


    }

}
