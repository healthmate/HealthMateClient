package com.healthmate.client.Auth;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.healthmate.client.MainActivity;
import com.healthmate.client.R;
import com.healthmate.client.Services.UserService;

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

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
            Log.e("LOGIN", login);
            if (login.equals("true")){
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

               Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

               auth_token = s.getString("auth_token");
               SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE).edit();
               editor.putString("login","true");
               editor.putString("token",auth_token);
               editor.putInt("steps", 0);
               editor.putString("profile_username",username);
               editor.putString("profile_fullname",fullname);
               editor.putString("profile_pic",profile_pic);


               editor.apply();

               startActivity(new Intent(LogIn.this, MainActivity.class));
               finish();

           } catch (JSONException e) {
               e.printStackTrace();
           }

       }
   }

}
