package com.healthmate.client.JobService;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.healthmate.client.MainActivity;
import com.healthmate.client.R;

import static com.healthmate.client.App.CHANNEL_ID;
import static com.healthmate.client.Community.Community.MY_PREFS_NAME;

public class StepsJobService extends Service implements SensorEventListener {
    private static final String TAG = "StepsJobService";
    private boolean jobCancelled = false;
    Sensor stepDect;
    private int numSteps;

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepDect = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        sensorManager.registerListener(this, stepDect, SensorManager.SENSOR_DELAY_FASTEST);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);
        String restoredText = prefs.getString("login", null);

        if(restoredText != null){

            numSteps = prefs.getInt("steps", -1);

            Log.e("steps", Integer.toString(numSteps) );
        }

        startForeground(1, setNotification(Integer.toString(numSteps)));

        return START_STICKY;
    }

    private Notification setNotification(String Isteps){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0 ,notificationIntent, 0);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Steps Count")
                .setContentText(Isteps+" steps")
                .setSmallIcon(R.drawable.hm_notification)
                .setContentIntent(pendingIntent)
                .build();

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {

            inc_steps();

            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);
            String restoredText = prefs.getString("login", null);

            if(restoredText != null){

                numSteps = prefs.getInt("steps", -1);

                Log.e("steps", Integer.toString(numSteps) );
            }

            Notification notification = setNotification(Integer.toString(numSteps));
            NotificationManager notificationManager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, notification);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint({"SetTextI18n", "CommitPrefEdits", "NewApi", "LocalSuppress"})
    public void inc_steps(){
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);
        String restoredText = prefs.getString("login", null);

        if(restoredText != null){

            int numSteps = prefs.getInt("steps", -1);

            numSteps++;

            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE).edit();
            Log.e("steps", Integer.toString(numSteps) );
            editor.remove("steps");
            editor.putInt("steps", numSteps);
            editor.apply();


        }

    }
}
