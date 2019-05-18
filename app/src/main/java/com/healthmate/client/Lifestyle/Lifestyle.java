package com.healthmate.client.Lifestyle;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.healthmate.client.Activities.Activities;
import com.healthmate.client.Auth.LogIn;
import com.healthmate.client.Community.Profile;
import com.healthmate.client.Objects.HealthTipsAdapter;
import com.healthmate.client.Objects.HealthTipsItem;
import com.healthmate.client.Objects.MealRecommendationAdapter;
import com.healthmate.client.Objects.MealRecommendationItem;
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
import static com.healthmate.client.Community.Community.MY_PREFS_NAME;

public class Lifestyle extends Fragment {

    private TextView TvSteps,TvUsername;
    String token,username,profile_pic;
    ImageView btn_breakfast, btn_lunch, btn_dinner;

    private RecyclerView recyclerView;
    private LinearLayout empty_layout;
    public HealthTipsItem healthTipsItem;
    private HealthTipsAdapter healthTipsAdapter;
    public List<HealthTipsItem> healthTipsItemList;
    InputStream is = null;
    String line = null;
    String result = null;
    String food_name;
    String food_calories, topic, details;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_lifestyle,container, false);
        SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);
        String restoredText = prefs.getString("login", null);
        if(restoredText != null){


            token = prefs.getString("token",null);
            username = prefs.getString("profile_username",null);
            profile_pic = prefs.getString("profile_pic", null);
            CircleImageView profile = view.findViewById(R.id.profile);
            Glide.with(view.getContext())
                    .load(profile_pic)
                    .into(profile);

        }

        healthTipsItemList = new ArrayList<>();

        //new GetRecommendationTask().execute(token);

        recyclerView = view.findViewById(R.id.health_tips);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        empty_layout = getActivity().findViewById(R.id.empty_layout);

        healthTipsAdapter = new HealthTipsAdapter(getContext(),healthTipsItemList);
        recyclerView.setAdapter(healthTipsAdapter);

        return  view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        load_views();
        TvUsername = (TextView) Objects.requireNonNull(getView().findViewById(R.id.profile_username));
        TvUsername.setText(username);

        recyclerView.setVisibility(View.GONE);

        CircleImageView profile = view.findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Profile.class));
            }
        });

        btn_breakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Meal.class)
                        .putExtra("indicator","Breakfast"));
            }
        });

        btn_lunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Meal.class)
                        .putExtra("indicator","Lunch"));
            }
        });

        btn_dinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Meal.class)
                        .putExtra("indicator","Dinner"));
            }
        });
    }

    public void load_views(){
        btn_breakfast = Objects.requireNonNull(getActivity()).findViewById(R.id.btn_breakfast);
        btn_lunch = getActivity().findViewById(R.id.btn_lunch);
        btn_dinner = getActivity().findViewById(R.id.btn_dinner);
    }

    @SuppressLint("StaticFieldLeak")
    class GetHealthTipsTask extends AsyncTask<String, String, JSONArray> {
        @Override
        protected void onPreExecute() {

        }


        @Override
        protected JSONArray doInBackground(String... strings) {

            String auth_token = strings[0];
            try {
                Log.e("IOexcep", "try");
                URL url = new URL("https://healthmate-api-heroku.herokuapp.com/gethealthtips");
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

            healthTipsItemList.clear();
            if(s!=null) {
                empty_layout.setVisibility(View.GONE);
                try {
                    for (int i = 0; i < s.length(); i++) {
                        JSONObject jo = s.getJSONObject(i);
                        topic = jo.getString("topic");
                        details = jo.getString("body");

                        healthTipsItem = new HealthTipsItem(topic,details);
                        healthTipsItemList.add(healthTipsItem);
                    }

                    recyclerView.getRecycledViewPool().clear();
                    healthTipsAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }

}
