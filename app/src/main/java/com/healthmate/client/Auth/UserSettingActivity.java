package com.healthmate.client.Auth;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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

import static com.healthmate.client.Auth.LogIn.MY_PREFS_NAME;

public class UserSettingActivity extends AppCompatActivity {

    private EditText ed_height, ed_weight, ed_goalWeight;
    InputStream is = null;
    String line = null;
    String result = null;
    String message,status,token;
    ProgressDialog progressDialog;
    String height,weight = "",goal_weight,is_diabetic_input, active_level_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);

        load_views();
        progressDialog = new ProgressDialog(this);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);

        String restoredText = prefs.getString("login", null);

        if(restoredText != null){
            token = prefs.getString("token",null);
            Log.e("TOKEN", token);

        }

        final Spinner is_diabetic = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(UserSettingActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.is_diabetic));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        is_diabetic.setAdapter(myAdapter);


        final Spinner active_level = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> newAdapter = new ArrayAdapter<String>(UserSettingActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.activitylevel));
        newAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        active_level.setAdapter(newAdapter);


        findViewById(R.id.update_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                height = ed_height.getText().toString();
                weight = ed_weight.getText().toString();
                goal_weight = ed_goalWeight.getText().toString();
                is_diabetic_input = is_diabetic.getSelectedItem().toString();
                active_level_input = active_level.getSelectedItem().toString();

                if(is_diabetic.getSelectedItem().toString().equals("True")){
                    goal_weight = ed_weight.getText().toString();
                }

                Log.e("weight", weight);
                Log.e("goal_weight", goal_weight);

                new PostUserSettingTask().execute(height,weight,
                        goal_weight,is_diabetic_input,active_level_input,token);
            }
        });

        is_diabetic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(is_diabetic.getSelectedItem().toString().equals("True")){
                    ed_goalWeight.setText(weight);
                    goal_weight = ed_weight.getText().toString();
                    ed_goalWeight.setEnabled(false);
                }else{
                    ed_goalWeight.setEnabled(true);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void load_views(){
        ed_height = findViewById(R.id.id_height);
        ed_weight = findViewById(R.id.weight);
        ed_goalWeight = findViewById(R.id.id_goal_weight);
    }

    @SuppressLint("StaticFieldLeak")
    class PostUserSettingTask extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Saving...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }


        @Override
        protected JSONObject doInBackground(String... strings) {
            String height = strings[0];
            String weight = strings[1];
            String goal_weight = strings[2];
            String is_diabetic_input = strings[3];
            String active_level_input = strings[4];
            String auth_token = strings[5];

            try {
                Log.e("IOexcep", "try");
                URL url = new URL("https://healthmate-api-heroku.herokuapp.com/updatesettings");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Authorization", "JWT "+auth_token);
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type","application/json");
                con.connect();


                JSONObject jsonparam = new JSONObject();
                jsonparam.put("height", height)
                        .put("average_weight",weight)
                        .put("goal_weight",goal_weight)
                        .put("is_diabetic",is_diabetic_input)
                        .put("activity_level",active_level_input);

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

                return new JSONObject(result);


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
            if(s != null){
                try {
                    String daily_calorie = s.getString("daily_calorie");
                    String weight = s.getString("weight");

                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE).edit();
                    editor.putString("daily_calorie_target",daily_calorie);
                    editor.putString("weight",weight);
                    editor.apply();
                    startActivity(new Intent(UserSettingActivity.this, MainActivity.class));
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_LONG).show();
            }

        }
    }
}
