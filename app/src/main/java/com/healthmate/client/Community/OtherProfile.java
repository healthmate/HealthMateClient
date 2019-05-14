package com.healthmate.client.Community;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.healthmate.client.Objects.PostAdapter;
import com.healthmate.client.Objects.PostObject;
import com.healthmate.client.Objects.UserAdapter;
import com.healthmate.client.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.healthmate.client.Community.Community.MY_PREFS_NAME;

public class OtherProfile extends AppCompatActivity {

    InputStream is = null;
    String line = null;
    String result = null;
    TextView posts_tv,username_tv,community_tv,fullname_tv, steps_tv;
    String community,user_id,username,posts,steps,token,current_userid, message,status,fullname;
    Boolean isFollowing;
    Button connect_btn;
    String profile_pic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

        posts_tv = findViewById(R.id.posts);
        username_tv = findViewById(R.id.username);
        community_tv = findViewById(R.id.following);
        fullname_tv = findViewById(R.id.full_name);
        connect_btn = findViewById(R.id.connect_button);
        steps_tv = findViewById(R.id.steps);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Bundle intent = getIntent().getExtras();
        if(intent != null){
            current_userid = intent.getString("user_id");
            profile_pic = intent.getString("profile_pic");
            isFollowing = Boolean.parseBoolean(intent.getString("isFollowing"));
        }

        SharedPreferences prefs = this.getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);

        String restoredText = prefs.getString("login", null);

        if(restoredText != null){
            token = prefs.getString("token",null);
            Log.e("TOKEN", token);
            CircleImageView profile = findViewById(R.id.image_profile);
            Glide.with(this)
                    .load(profile_pic)
                    .into(profile);
        }

        if(isFollowing){
            connect_btn.setText("connected");
        }else{
            connect_btn.setText("connect");
        }

        new UserProfileTask().execute(current_userid, token);

        posts_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), User_posts.class);
                intent.putExtra("purpose", "other_user");
                intent.putExtra("user_id", current_userid);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connect_btn.getText().toString().equals("connect")) {
                    SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);
                    String restoredText = prefs.getString("login", null);

                    if(restoredText != null){
                        token = prefs.getString("token",null);
                    }

                    new FollowTask().execute(token, current_userid);
                }
            }

        });
    }

    @SuppressLint("StaticFieldLeak")
    class UserProfileTask extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {

        }


        @Override
        protected JSONObject doInBackground(String... strings) {

            String userid = strings[0];
            String auth_token = strings[1];
            Log.e("userid", userid);
            try {
                Log.e("IOexcep", "try");
                URL url = new URL("https://healthmate-api-heroku.herokuapp.com/getuserprofile/"
                + userid);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Authorization", "JWT "+auth_token);
                con.setDoOutput(true);
                con.connect();

                int resp=con.getResponseCode();
                Log.e("IOexcep", Integer.toString(resp));
                Log.e("IOexcep", "connnect");

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

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(JSONObject s) {

            if(s!=null) {
                try {
                    community = s.getString("community");
                    user_id = s.getString("user_id");
                    username = s.getString("username");
                    posts = s.getString("posts");
                    fullname = s.getString("full_name");
                    steps = s.getString("steps_today");

                    fullname_tv.setText(fullname);
                    community_tv.setText(community);
                    username_tv.setText(username);
                    posts_tv.setText(posts);
                    steps_tv.setText(steps);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    @SuppressLint("StaticFieldLeak")
    class FollowTask extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {

        }


        @Override
        protected JSONObject doInBackground(String... strings) {

            String auth_token = strings[0];
            String user_id = strings[1];
            try {
                Log.e("IOexcep", "try");
                String base_url = "https://healthmate-api-heroku.herokuapp.com/community/request/";
                URL url = new URL(base_url + user_id);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type","application/json");
                con.setRequestProperty("Authorization", "JWT "+auth_token);
                con.connect();

                int resp=con.getResponseCode();
                Log.e("IOexcep", Integer.toString(resp));
                Log.e("IOexcep", "connnect");

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
            try {
                message = s.getString("message");
                status = s.getString("status");
                if(Objects.equals(status, "success")){
                    connect_btn.setText("connected");
                }

                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
