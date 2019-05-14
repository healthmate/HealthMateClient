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
import java.util.Objects;

import static com.healthmate.client.Community.Community.MY_PREFS_NAME;

public class User_posts extends AppCompatActivity {

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
    String token,user_id,post_id,username,current_userid,purpose, profile_username;
    TextView TvUsername;
    Boolean is_liked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_posts);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TvUsername = findViewById(R.id.username);
        SharedPreferences prefs = this.getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);

        String restoredText = prefs.getString("login", null);

        if(restoredText != null){
            token = prefs.getString("token",null);

            Log.e("TOKEN", token);

        }
        Bundle intent = getIntent().getExtras();
        if(intent != null){
            purpose = intent.getString("purpose");
            profile_username = intent.getString("username");
            if(Objects.equals(purpose, "other_user")){
                current_userid = intent.getString("user_id");
                new GetPostTask().execute(token,purpose,current_userid);
            }else {
                new GetPostTask().execute(token,purpose);
            }

        }
        postObjectList = new ArrayList<>();



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
    class GetPostTask extends AsyncTask<String, String, JSONArray> {
        @Override
        protected void onPreExecute() {

        }


        @Override
        protected JSONArray doInBackground(String... strings) {

            String auth_token = strings[0];
            String purpose = strings[1];
            try {
                Log.e("IOexcep", "try");
                URL url;
                if(Objects.equals(purpose, "other_user")){
                    String userid = strings[2];
                    url = new URL("https://healthmate-api-heroku.herokuapp.com/" +
                            "getuserposts/" +
                            userid);
                }else{
                    url = new URL("https://healthmate-api-heroku.herokuapp.com/getuserposts");
                }
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
                        String profile_pic = jo.getString("profile_pic");
                        is_liked = jo.getBoolean("is_liked");
                        Log.e("IMAGE_URL "+i, image_url);
                        postObject = new PostObject(description, image_url, create_at, user_id,
                                username,likes,post_id, profile_pic, is_liked);
                        postObjectList.add(postObject);
                    }
                    postAdapter.notifyDataSetChanged();
                    if(username!=null){
                        TvUsername.setText(username);
                    }else{
                        TvUsername.setText(profile_username);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }
}
