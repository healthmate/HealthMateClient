package com.healthmate.client.Activities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEventListener;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.healthmate.client.BroadcastReceiver.StepReceiver;
import com.healthmate.client.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.SENSOR_SERVICE;
import static com.healthmate.client.Community.Community.MY_PREFS_NAME;

public class Activities extends Fragment implements SensorEventListener, StepListener {

    private TextView textView;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private TextView TvSteps;
    private Button BtnStart, BtnStop;
    private TriggerEventListener triggerEventListener;
    String token;
    Sensor motion;
    Sensor stepDect;
    LineChart mChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_activities,container, false);
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get an instance of the SensorManager
        SensorManager sensorManager = (SensorManager) Objects.requireNonNull(getActivity()).getSystemService(SENSOR_SERVICE);
        assert sensorManager != null;
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        stepDect = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        StepDetector simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        TvSteps = Objects.requireNonNull(getView()).findViewById(R.id.tv_steps);
        motion = sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
        sensorManager.registerListener(Activities.this, stepDect, SensorManager.SENSOR_DELAY_FASTEST);

        SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);
        String restoredText = prefs.getString("login", null);

        if(restoredText != null){

            int numSteps = prefs.getInt("steps", -1);
            TvSteps.setText(Integer.toString(numSteps));
            Log.e("steps", Integer.toString(numSteps) );

        }

        mChart = Objects.requireNonNull(getView()).findViewById(R.id.linechart);

        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);

        ArrayList<Entry> yValues = new ArrayList<>();

        yValues.add(new Entry(0, 60f));
        yValues.add(new Entry(1, 50f));
        yValues.add(new Entry(2, 70f));
        yValues.add(new Entry(3, 30f));
        yValues.add(new Entry(4, 65f));
        yValues.add(new Entry(5, 40f));

        LineDataSet set = new LineDataSet(yValues, "Data set 1");
        set.setFillAlpha(110);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);
        LineData data = new LineData(dataSets);
        mChart.setData(data);

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            //simpleStepDetector.updateAccel(
            //        event.timestamp, event.values[0], event.values[1], event.values[2]);
            inc_steps();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void step(long timeNs) {
        /*numSteps++;
        System.out.print(numSteps);
        TvSteps.setText(Integer.toString(numSteps));*/
        inc_steps();

    }

    @SuppressLint({"SetTextI18n", "CommitPrefEdits", "NewApi", "LocalSuppress"})
    public void inc_steps(){
        SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);
        String restoredText = prefs.getString("login", null);

        if(restoredText != null){

            int numSteps = prefs.getInt("steps", -1);
            token = prefs.getString("token",null);
            numSteps++;

            TvSteps.setText(Integer.toString(numSteps));


            SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE).edit();
            Log.e("steps", Integer.toString(numSteps) );
            editor.remove("steps");
            editor.putInt("steps", numSteps);
            editor.apply();

            new StoreSteps().execute(String.valueOf(numSteps), token);

        }

    }

    @SuppressLint("StaticFieldLeak")
    class StoreSteps extends AsyncTask<String, String, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            String steps = strings[0];
            String auth_token = strings[1];
            try {
                Log.e("IOexcep", "try");
                URL url = new URL("https://healthmate-api-heroku.herokuapp.com/storesteps/"+steps);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Authorization", "JWT "+auth_token);
                con.setDoOutput(true);
                con.setConnectTimeout(3000);
                con.setRequestProperty("Content-Type","application/json");
                con.connect();

                int resp=con.getResponseCode();
                Log.e("IOexcep", "connnect");
                Log.e("IOexcep", Integer.toString(resp));
                BufferedInputStream is = new BufferedInputStream(con.getInputStream());
                //READ IS content into a string
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));

                int response = con.getResponseCode();
                is.close();
                br.close();
                con.disconnect();

                return response;


            } catch (MalformedURLException e) {
                Log.e("IOexcep", "Malformed URL");
            } catch (IOException e) {
                Log.e("IOexcep", "Not Connected");
            }
            return  null;
        }

        @Override
        protected void onPostExecute(Integer s) {

            //if(s == 200)


        }
    }
}
