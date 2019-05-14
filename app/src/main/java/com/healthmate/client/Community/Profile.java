package com.healthmate.client.Community;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.healthmate.client.Objects.PostAdapter;
import com.healthmate.client.Objects.PostObject;
import com.healthmate.client.Objects.UserProfile;
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
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.healthmate.client.Community.Community.MY_PREFS_NAME;

public class Profile extends AppCompatActivity {

    InputStream is = null;
    String line = null;
    String result = null;
    String community,user_id,username,posts,steps,profile_pic;
    TextView posts_tv,username_tv,community_tv,fullname_tv, steps_tv;
    String token, fullname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        posts_tv = findViewById(R.id.posts);
        username_tv = findViewById(R.id.username);
        community_tv = findViewById(R.id.following);
        fullname_tv = findViewById(R.id.full_name);
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


        SharedPreferences prefs = this.getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);

        String restoredText = prefs.getString("login", null);

        if(restoredText != null){
            token = prefs.getString("token",null);
            fullname = prefs.getString("profile_fullname", null);
            Log.e("TOKEN", token);
            profile_pic = prefs.getString("profile_pic", null);
            CircleImageView profile = findViewById(R.id.image_profile);
            Glide.with(this)
                    .load(profile_pic)
                    .into(profile);
        }

        new UserProfileTask().execute(token);

        posts_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), User_posts.class);
                intent.putExtra("purpose", "current_user");
                intent.putExtra("username", username);
                startActivity(intent);
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

            String auth_token = strings[0];
            try {
                Log.e("IOexcep", "try");
                URL url = new URL("https://healthmate-api-heroku.herokuapp.com/getuserprofile");
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

        @Override
        protected void onPostExecute(JSONObject s) {

            if(s!=null) {
                try {
                        community = s.getString("community");
                        user_id = s.getString("user_id");
                        Log.e("cuserid", user_id);
                        username = s.getString("username");
                        posts = s.getString("posts");
                        steps = s.getString("steps_today");
                        //userProfile = new UserProfile(username,user_id,community,posts);
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


}
