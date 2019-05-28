package com.healthmate.client.Auth;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.healthmate.client.BroadcastReceiver.DeviceBootReceiver;
import com.healthmate.client.BroadcastReceiver.DinnerReceiver;
import com.healthmate.client.BroadcastReceiver.LunchReceiver;
import com.healthmate.client.BroadcastReceiver.BreakfastReceiver;
import com.healthmate.client.BroadcastReceiver.StepsReceiver;
import com.healthmate.client.MainActivity;
import com.healthmate.client.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

public class LogIn extends AppCompatActivity {
    //TODO: Associate status code with a response and make status code in backend distinct
    InputStream is = null;
    String line = null;
    String result = null;
    ProgressDialog progressDialog;
    String message;
    String auth_token;
    String status;
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        final EditText Ed_email = findViewById(R.id.email);
        final EditText Ed_password = findViewById(R.id.password);
        final Button btn_login = findViewById(R.id.login);

        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));
        progressDialog = new ProgressDialog(this);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);
        String restoredText = prefs.getString("login", null);

        if(restoredText != null){
            String login = prefs.getString("login","false");
            String is_diabetic = prefs.getString("is_diabetic","null");
            Log.e("LOGIN", login);
            if (login.equals("true") && !is_diabetic.equals("new_user")){
                startActivity(new Intent(LogIn.this, MainActivity.class));
                finish();
            }
        }


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new LoginTask().execute(Ed_email.getText().toString(), Ed_password.getText().toString());

            }
        });

        findViewById(R.id.register_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogIn.this, Register.class));

            }
        });
    }

    public void set_Breakfast_Alarm(Context context){
        Log.e("ALARM set", "SET!!!  1" );
        Intent notifyIntent = new Intent(context, BreakfastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                1, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);



        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,8);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);

        Calendar now = Calendar.getInstance();
        long _alarm = 0;
        if(calendar.getTimeInMillis() < now.getTimeInMillis()){
            _alarm = calendar.getTimeInMillis() + (AlarmManager.INTERVAL_DAY+1);
        }else{
            _alarm = calendar.getTimeInMillis();
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, _alarm,pendingIntent);
        }

        /*PackageManager pm = context.getPackageManager();
        ComponentName receiver = new ComponentName(context, DeviceBootReceiver.class);
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);*/

    }

    public void set_Lunch_Alarm(Context context){
        Log.e("ALARM set", "SET!!!  2" );
        Intent notifyIntent = new Intent(context, LunchReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,13);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);

        Calendar now = Calendar.getInstance();
        long _alarm;
        if(calendar.getTimeInMillis() < now.getTimeInMillis()){
            _alarm = calendar.getTimeInMillis() + (AlarmManager.INTERVAL_DAY+1);
        }else{
            _alarm = calendar.getTimeInMillis();
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, _alarm,pendingIntent);
        }

        /*PackageManager pm = context.getPackageManager();
        ComponentName receiver = new ComponentName(context, DeviceBootReceiver.class);
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);*/

    }

    public void set_Dinner_Alarm(Context context){
        Log.e("ALARM set", "SET!!!  3" );
        Intent notifyIntent = new Intent(context, DinnerReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                3, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,18);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);

        Calendar now = Calendar.getInstance();
        long _alarm = 0;
        if(calendar.getTimeInMillis() < now.getTimeInMillis()){
            _alarm = calendar.getTimeInMillis() + (AlarmManager.INTERVAL_DAY+1);
        }else{
            _alarm = calendar.getTimeInMillis();
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, _alarm,pendingIntent);
        }

        /*PackageManager pm = context.getPackageManager();
        ComponentName receiver = new ComponentName(context, DeviceBootReceiver.class);
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);*/

    }

    public void set_DailyStorage(Context context){
        Intent notifyIntent2 = new Intent(context, StepsReceiver.class);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context,
                4, notifyIntent2, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager2 = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.HOUR_OF_DAY,23);
        calendar2.set(Calendar.MINUTE,59);
        calendar2.set(Calendar.SECOND,59);
        calendar2.set(Calendar.MILLISECOND,0);

        Calendar now = Calendar.getInstance();
        long _alarm = 0;
        if(calendar2.getTimeInMillis() < now.getTimeInMillis()){
            _alarm = calendar2.getTimeInMillis() + (AlarmManager.INTERVAL_DAY+1);
        }else{
            _alarm = calendar2.getTimeInMillis();
        }

        alarmManager2.setRepeating(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager2.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, _alarm,pendingIntent2);
        }

        PackageManager pm = context.getPackageManager();
        ComponentName receiver = new ComponentName(context, DeviceBootReceiver.class);
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }


   @SuppressLint("StaticFieldLeak")
   class LoginTask extends AsyncTask<String, String, JSONObject> {
       @Override
       protected void onPreExecute() {
           progressDialog.setMessage("Login in...");
           progressDialog.setCancelable(false);
           progressDialog.show();
           /*LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
           params.addRule(RelativeLayout.CENTER_IN_PARENT);
           layout.addView(progressBar, params);*/
           getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

       }


       @Override
       protected JSONObject doInBackground(String... strings) {
           String email = strings[0];
           String password = strings[1];
           try {
               Log.e("IOexcep", "try");
               URL url = new URL("https://healthmate-api-heroku.herokuapp.com/auth/login");
               HttpURLConnection con = (HttpURLConnection) url.openConnection();
               con.setRequestMethod("POST");
               con.setDoOutput(true);
               con.setRequestProperty("Content-Type","application/json");
               con.connect();

               JSONObject jsonparam = new JSONObject();
               jsonparam.put("email",email);
               jsonparam.put("password",password);

               OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
               out.write(jsonparam.toString());
               out.close();
               int resp=con.getResponseCode();
               Log.e("IOexcep", "connnect");
               Log.e("IOexcep", Integer.toString(resp));
               is = new BufferedInputStream(con.getInputStream());
               //READ IS content into a string
               BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
               StringBuilder sb = new StringBuilder();


               while ((line = br.readLine()) != null) {
                   sb.append(line).append("\n");
               }

               result = sb.toString();
               is.close();
               br.close();
               con.disconnect();

               if(resp == 200) {
                   return new JSONObject(result);
               }

               if(resp == 401) {
                   Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
               }


           } catch (MalformedURLException e) {
               Log.e("IOexcep", "Malformed URL");
           } catch (IOException e) {
               Log.e("IOexcep", "Not Connected");
           } catch (JSONException e) {
               Log.e("JSONexcep", "JSON Error");
           }


           return null;
       }

       @Override
       protected void onPostExecute(JSONObject s) {
           progressDialog.dismiss();
           getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
           try {
               JSONObject data = s.getJSONObject("data");
               message = s.getString("message");
               String username = data.getString("username");
               String fullname = data.getString("fullname");
               String profile_pic = data.getString("profile_pic");
               String is_diabetic = data.getString("is_diabetic");
               String daily_calorie = data.getString("daily_calorie_target");
               String weight = data.getString("weight");
               Log.e("Login daily_calorie", daily_calorie);
               Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

               auth_token = s.getString("auth_token");
               SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE).edit();
               editor.putString("login","true");
               editor.putString("token",auth_token);
               editor.putInt("steps", 0);
               editor.putString("profile_username",username);
               editor.putString("profile_fullname",fullname);
               editor.putString("profile_pic",profile_pic);
               editor.putString("Breakfast", "none");
               editor.putString("Breakfast_cal", "0");
               editor.putString("Lunch","none");
               editor.putString("Lunch_cal", "0");
               editor.putString("Dinner","none");
               editor.putString("Dinner_cal", "0");
               if(!is_diabetic.equals("new_user")){
                   editor.putString("is_diabetic", is_diabetic);
                   editor.putString("daily_calorie_target", daily_calorie);
                   editor.putString("weight",weight);
               }
               set_DailyStorage(getApplicationContext());
               set_Breakfast_Alarm(getApplicationContext());
               set_Dinner_Alarm(getApplicationContext());
               set_Lunch_Alarm(getApplicationContext());
               editor.apply();
               if(!is_diabetic.equals("new_user")){
                   startActivity(new Intent(LogIn.this, MainActivity.class));
                   finish();
               }
               else{
                   startActivity(new Intent(LogIn.this, UserSettingActivity.class));
                   finish();
               }

           } catch (JSONException e) {
               e.printStackTrace();
           }

       }
   }

}
