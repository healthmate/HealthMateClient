package com.healthmate.client.JobService;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import static com.healthmate.client.Community.Community.MY_PREFS_NAME;

public class StoreStepsJobScheduler extends JobService {
    private String token;
    private int steps;
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        /*SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);
        String restoredText = prefs.getString("login", null);

        if(restoredText != null){

            steps = prefs.getInt("steps", -1);

            Log.e("steps", Integer.toString(steps) );
            token = prefs.getString("token",null);

        }*/
        storeSteps(jobParameters);
        return true;
    }

    private void storeSteps(final JobParameters params){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("storeSteps", "Store steps called");
                String steps = params.getExtras().getString("steps");
                String auth_token = params.getExtras().getString("token");
                String date = params.getExtras().getString("date");
                try {
                    Log.e("storeSteps", "try");
                    URL url = new URL("https://healthmate-api-heroku.herokuapp.com/storesteps/"+steps);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Authorization", "JWT "+auth_token);
                    con.setDoOutput(true);
                    con.setConnectTimeout(3000);
                    con.setRequestProperty("Content-Type","application/json");
                    con.connect();

                    JSONObject jsonparam = new JSONObject();
                    jsonparam.put("date",date);

                    OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
                    out.write(jsonparam.toString());
                    out.close();

                    int resp=con.getResponseCode();
                    Log.e("storeSteps", "connnect");
                    //Log.e("IOexcep", Integer.toString(resp));
                    BufferedInputStream is = new BufferedInputStream(con.getInputStream());
                    //READ IS content into a string
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));

                    //int response = con.getResponseCode();
                    is.close();
                    br.close();
                    con.disconnect();
                    Log.e("storeSteps", Integer.toString(resp));

                } catch (MalformedURLException e) {
                    Log.e("storeSteps", "Malformed URL");
                } catch (IOException e) {
                    Log.e("storeSteps", "Not Connected");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                jobFinished(params, false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }
}
