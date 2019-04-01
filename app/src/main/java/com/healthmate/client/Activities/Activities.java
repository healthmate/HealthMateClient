package com.healthmate.client.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.healthmate.client.Auth.LogIn;
import com.healthmate.client.MainActivity;
import com.healthmate.client.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;
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
        //mChart.setOnChartGestureListener(Activities.this);
        //mChart.setOnChartValueSelectedListener(Activities.this);

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
            Log.e("stepsbef", Integer.toString(numSteps) );
            numSteps++;
            Log.e("stepsafter", Integer.toString(numSteps) );
            TvSteps.setText(Integer.toString(numSteps));


            SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE).edit();
            Log.e("steps", Integer.toString(numSteps) );
            editor.remove("steps");
            editor.putInt("steps", numSteps);
            editor.apply();

            Log.e("finish", "finish");
        }
    }
}
