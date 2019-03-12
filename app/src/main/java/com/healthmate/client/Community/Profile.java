package com.healthmate.client.Community;

import android.annotation.SuppressLint;
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

import static com.healthmate.client.Community.Community.MY_PREFS_NAME;

public class Profile extends AppCompatActivity {
    private RecyclerView recyclerView;
    public PostAdapter postAdapter;
    public List<PostObject> postObjectList;
    InputStream is = null;
    String line = null;
    String result = null;
    String description;
    String image_url;
    String create_at;
    String likes;
    PostObject postObject;
    String community,user_id,username,posts,post_id;
    TextView posts_tv,username_tv,community_tv;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        posts_tv = findViewById(R.id.posts);
        username_tv = findViewById(R.id.username);
        community_tv = findViewById(R.id.following);

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
            Log.e("TOKEN", token);

        }

        new UserProfileTask().execute(token);

        postObjectList = new ArrayList<>();

        new GetPostTask().execute(token);

        Log.e("Postobj ", postObjectList.toString());
        recyclerView = findViewById(R.id.recycler_view_profile);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        postAdapter = new PostAdapter(this,postObjectList);
        recyclerView.setAdapter(postAdapter);
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
                        //userProfile = new UserProfile(username,user_id,community,posts);

                        community_tv.setText(community);
                        username_tv.setText(username);
                        posts_tv.setText(posts);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        new GetPostTask().execute(token);
    }

    @SuppressLint("StaticFieldLeak")
    class GetPostTask extends AsyncTask<String, String, JSONArray> {
        @Override
        protected void onPreExecute() {

        }


        @Override
        protected JSONArray doInBackground(String... strings) {

            String auth_token = strings[0];
            try {
                Log.e("IOexcep", "try");
                URL url = new URL("https://healthmate-api-heroku.herokuapp.com/getuserposts");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
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

                Log.e("doInbackground", result );

                return new JSONArray(result);


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
        protected void onPostExecute(JSONArray s) {
            postObjectList.clear();
            if(s!=null) {
                try {
                    for (int i = 0; i < s.length(); i++) {
                        JSONObject jo = s.getJSONObject(i);
                        description = jo.getString("description");
                        image_url = jo.getString("image_url");
                        create_at = jo.getString("create_at");
                        user_id = jo.getString("user_id");
                        username = jo.getString("username");
                        likes = jo.getString("likes");
                        post_id = jo.getString("post_id");
                        Log.e("IMAGE_URL "+i, image_url);
                        postObject = new PostObject(description, image_url, create_at, user_id, username,likes,post_id);
                        postObjectList.add(postObject);
                    }
                    postAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }

}
