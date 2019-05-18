package com.healthmate.client.Community;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;

public class Community extends Fragment {

    private RecyclerView recyclerView;
    public PostAdapter postAdapter;
    public List<PostObject> postObjectList;
    String description;
    String image_url;
    String create_at;
    String user_id;
    String username,likes,post_id,profile_pic,int_profile_pic;
    Boolean is_liked;
    PostObject postObject;
    InputStream is = null;
    String line = null;
    String result = null;
    String token;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RelativeLayout post_online;


    public static final String MY_PREFS_NAME = "MyPrefsFile";
    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.activity_community, container, false);
        postObjectList = new ArrayList<>();


        SharedPreferences prefs = this.getActivity().getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);

        String restoredText = prefs.getString("login", null);

        if(restoredText != null){
            token = prefs.getString("token",null);
            Log.e("TOKEN", token);
            profile_pic = prefs.getString("profile_pic", null);
            CircleImageView profile = view.findViewById(R.id.profile);
            Glide.with(view.getContext())
                    .load(profile_pic)
                    .into(profile);
        }

        //new GetPostTask().execute(token);


        Log.e("Postobj ", postObjectList.toString());
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        postAdapter = new PostAdapter(getContext(),postObjectList);
        recyclerView.setAdapter(postAdapter);

        //swipe refresh layout
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadRecyclerViewData();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorGreen,
                R.color.colorAccent);

        loadRecyclerViewData();

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = (Toolbar) Objects.requireNonNull(getView()).findViewById(R.id.toolbar);

        post_online = view.findViewById(R.id.post_online);
        ImageView btn_post = Objects.requireNonNull(getView()).findViewById(R.id.post);
        TextView search = Objects.requireNonNull(getView()).findViewById(R.id.search_btn);
        ImageView profile = Objects.requireNonNull(getView()).findViewById(R.id.profile);
        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Post.class));

            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Search.class));

            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Profile.class));

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        new GetPostTask().execute(token);
    }

    public void loadRecyclerViewData(){
        mSwipeRefreshLayout.setRefreshing(true);
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
                URL url = new URL("https://healthmate-api-heroku.herokuapp.com/getposts");
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
            mSwipeRefreshLayout.setRefreshing(false);
            postObjectList.clear();
            if(s!=null) {
                post_online.setVisibility(GONE);
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
                        int_profile_pic = jo.getString("profile_pic");
                        is_liked = jo.getBoolean("is_liked");

                        Log.e("IMAGE_URL "+i, image_url);
                        postObject = new PostObject(description, image_url, create_at, user_id,
                                username,likes, post_id, int_profile_pic,is_liked);
                        postObjectList.add(postObject);
                    }

                    recyclerView.getRecycledViewPool().clear();
                    postAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }

}

