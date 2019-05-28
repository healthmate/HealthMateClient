package com.healthmate.client.JobService;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.healthmate.client.Auth.LogIn;

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

public class StoreHistoryJobScheduler extends JobService {
    private String token;
    private int steps;
    String line = null;
    String result = null;
    JSONObject s;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        storeSteps(jobParameters);
        return true;
    }

    private void storeSteps(final JobParameters params){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("storeHistory", "Store history called");
                String breakfast = params.getExtras().getString("breakfast");
                String auth_token = params.getExtras().getString("token");
                String lunch = params.getExtras().getString("lunch");
                String dinner = params.getExtras().getString("dinner");
                String brunch = params.getExtras().getString("brunch");
                String lunner = params.getExtras().getString("lunner");
                String date = params.getExtras().getString("date");
                String calorie_deficit = params.getExtras().getString("calories_left");

                try {
                    Log.e("storeSteps", "try");
                    URL url = new URL("https://healthmate-api-heroku.herokuapp.com/foodhistory/post");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Authorization", "JWT "+auth_token);
                    con.setDoOutput(true);
                    con.setConnectTimeout(3000);
                    con.setRequestProperty("Content-Type","application/json");
                    con.connect();

                    JSONObject jsonparam = new JSONObject();
                    jsonparam.put("date",date)
                            .put("breakfast",breakfast)
                            .put("lunch",lunch)
                            .put("dinner",dinner)
                            .put("brunch", brunch)
                            .put("lunner",lunner)
                            .put("calorie_deficit",calorie_deficit);

                    OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
                    out.write(jsonparam.toString());
                    out.close();

                    int resp=con.getResponseCode();
                    Log.e("storeSteps", "connnect");
                    //Log.e("IOexcep", Integer.toString(resp));
                    BufferedInputStream is = new BufferedInputStream(con.getInputStream());
                    //READ IS content into a string
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                    StringBuilder sb = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }

                    result = sb.toString();
                    //int response = con.getResponseCode();
                    is.close();
                    br.close();
                    con.disconnect();
                    Log.e("storeHistory", Integer.toString(resp));
                    if(result!=null){
                        s = new JSONObject(result);
                    }

                } catch (MalformedURLException e) {
                    Log.e("storeHistory", "Malformed URL");
                } catch (IOException e) {
                    Log.e("storeHistory", "Not Connected");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(s!=null){
                    SharedPreferences.Editor editor = getSharedPreferences(LogIn.MY_PREFS_NAME,MODE_PRIVATE).edit();
                    editor.remove("daily_calorie_target");
                    try {
                        editor.putString("daily_calorie_target", s.getString("daily_calorie"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    editor.apply();
                    Log.e("storeHistory", "History stored!!!");
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
