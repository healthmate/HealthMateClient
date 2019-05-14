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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.healthmate.client.Objects.PostAdapter;
import com.healthmate.client.Objects.PostObject;
import com.healthmate.client.Objects.User;
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
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.healthmate.client.Community.Community.MY_PREFS_NAME;

public class Search extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    User user;

    InputStream is = null;
    String line = null;
    String result = null;
    String token;
    String username,user_id;
    boolean isFollowing;
    ImageView searchbtn;


    EditText search_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        userList = new ArrayList<>();

        SharedPreferences prefs = this.getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);

        String restoredText = prefs.getString("login", null);

        if(restoredText != null){
            token = prefs.getString("token",null);
            Log.e("TOKEN", token);

        }

        recyclerView = findViewById(R.id.recycler_view_2);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        userAdapter = new UserAdapter(this,userList);
        recyclerView.setAdapter(userAdapter);
        search_bar = findViewById(R.id.search_bar);
        searchbtn = findViewById(R.id.search_btn_btn);

        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SearchTask().execute(search_bar.getText().toString(),token);

            }
        });

        /*search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                new SearchTask().execute(charSequence.toString(),token);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
*/
    }

    @SuppressLint("StaticFieldLeak")
    class SearchTask extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {

        }


        @Override
        protected JSONObject doInBackground(String... strings) {
            String username = strings[0];
            String auth_token = strings[1];
            try {
                Log.e("IOexcep", "try");
                URL url = new URL("https://healthmate-api-heroku.herokuapp.com/search");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Authorization", "JWT "+auth_token);
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type","application/json");
                con.connect();

                JSONObject jsonparam = new JSONObject();
                jsonparam.put("username",username);

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
            userList.clear();
            if(s!=null) {
                try {
                        isFollowing = Boolean.parseBoolean(s.getString("community"));
                        Log.e("isF", s.getString("community"));
                        user_id = s.getString("user_id");
                        username = s.getString("username");
                        String profile_pic = s.getString("profile_pic");
                        user = new User(username,isFollowing, user_id, profile_pic);
                        userList.add(user);


                        userAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }


}
