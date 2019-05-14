package com.healthmate.client.Community;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.healthmate.client.Objects.Comment;
import com.healthmate.client.Objects.CommentAdapter;
import com.healthmate.client.Objects.PostAdapter;
import com.healthmate.client.Objects.PostObject;
import com.healthmate.client.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.healthmate.client.Community.Community.MY_PREFS_NAME;

public class Comments extends AppCompatActivity {

    TextView comment,post_btn;
    String token, profile_username;
    InputStream is = null;
    String line = null;
    String result = null;
    private RecyclerView recyclerView;
    public CommentAdapter commentAdapter;
    private List<Comment> commentObjectList;
    String comment_post;
    String create_at;
    String username,post_id, description, int_profile_pic, profile_pic;
    Comment commentObject;
    TextView description_tv, username_tv;
    String message;
    String status;
    ImageView profile;
    ProgressDialog progressDialog;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        description_tv = findViewById(R.id.display_description);
        username_tv = findViewById(R.id.username);
        commentObjectList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
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
            profile_username = prefs.getString("profile_username",null);
            profile_pic = prefs.getString("profile_pic", null);
            CircleImageView circleImageView = findViewById(R.id.image_profile1);
            Glide.with(this)
                    .load(profile_pic)
                    .into(circleImageView);

        }
        Bundle intent = getIntent().getExtras();
        if(intent != null){
            post_id = intent.getString("post_id");
            description = intent.getString("description");
        }
        new GetCommentTask().execute(post_id,token);

        Log.e("Comments ", commentObjectList.toString());
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        commentObjectList.clear();
        commentAdapter = new CommentAdapter(this,commentObjectList);
        recyclerView.setAdapter(commentAdapter);

        username_tv.setText(profile_username);
        description_tv.setText(description);

        comment = findViewById(R.id.add_comment);
        post_btn = findViewById(R.id.post);

        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new PostTask().execute(post_id,comment.getText().toString(),token);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        new GetCommentTask().execute(post_id,token);
    }

    @SuppressLint("StaticFieldLeak")
    class GetCommentTask extends AsyncTask<String, String, JSONArray> {
        @Override
        protected void onPreExecute() {

        }


        @Override
        protected JSONArray doInBackground(String... strings) {

            String post_id = strings[0];
            String auth_token = strings[1];
            try {
                Log.e("IOexcep", "try");

                String base_url = "https://healthmate-api-heroku.herokuapp.com/getcomment/";
                Log.e("postid", post_id);
                URL url = new URL(base_url + post_id);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Authorization", "JWT "+auth_token);
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type","application/json");
                con.connect();

                int resp=con.getResponseCode();
                Log.e("IOexcepcomment", Integer.toString(resp));
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
            commentObjectList.clear();
            if(s!=null) {
                try {
                    for (int i = 0; i < s.length(); i++) {
                        JSONObject jo = s.getJSONObject(i);
                        create_at = jo.getString("create_at");
                        username = jo.getString("username");
                        comment_post = jo.getString("comment");
                        int_profile_pic = jo.getString("profile_pic");
                        Log.e("comment", comment_post );
                        commentObject = new Comment(username, comment_post, create_at,int_profile_pic);
                        commentObjectList.add(commentObject);
                    }
                    commentAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    @SuppressLint("StaticFieldLeak")
    class PostTask extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Posting");
            progressDialog.setCancelable(false);
            progressDialog.show();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }


        @Override
        protected JSONObject doInBackground(String... strings) {
            String post_id = strings[0];
            String comment = strings[1];
            String auth_token = strings[2];
            try {
                Log.e("IOexcep", "try");
                URL url = new URL("https://healthmate-api-heroku.herokuapp.com/comment");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Authorization", "JWT "+auth_token);
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type","application/json");
                con.connect();

                JSONObject jsonparam = new JSONObject();
                jsonparam.put("comment",comment);
                jsonparam.put("post_id",post_id);

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
            if(s != null){
                try {
                    message = s.getString("message");
                    status = s.getString("status");
                    progressDialog.dismiss();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                    comment.setText("");
                    new GetCommentTask().execute(post_id,token);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
    }
}
