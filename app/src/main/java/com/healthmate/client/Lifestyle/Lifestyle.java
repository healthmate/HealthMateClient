package com.healthmate.client.Lifestyle;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.healthmate.client.Auth.LogIn;
import com.healthmate.client.Community.Profile;
import com.healthmate.client.MainActivity;
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
import static java.lang.Math.abs;
import static java.lang.Math.round;

public class Lifestyle extends Fragment {

    private TextView TvSteps,TvUsername;
    String token,username,profile_pic;
    ImageView btn_breakfast, btn_lunch, btn_dinner, btn_brunch, btn_lunner;

    private RecyclerView recyclerView;
    private LinearLayout empty_layout;
    public HealthTipsItem healthTipsItem;
    private HealthTipsAdapter healthTipsAdapter;
    public List<HealthTipsItem> healthTipsItemList;
    public MealRecommendationItem meal_recommendation;
    public List<MealRecommendationItem> meal_recommendationList;
    private MealRecommendationAdapter mealRecommendationAdapter;
    InputStream is = null;
    String line = null;
    String result = null;
    String food_name,breakfast,lunch,dinner,brunch,lunner;
    Integer calories_left,calories_gained,calories_burnt,breakfast_cal, lunch_cal, dinner_cal,daily_calories;
    Integer brunch_cal, lunner_cal;
    String food_calories, topic, details;
    private TextView calories_left_tv, calories_gained_tv, calories_goal_tv;
    private EditText meal_item;
    private TextView save,show;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_lifestyle,container, false);
        SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);
        String restoredText = prefs.getString("login", null);
        if(restoredText != null){

            breakfast = prefs.getString("Breakfast","none");
            lunch = prefs.getString("Lunch", "none");
            dinner = prefs.getString("Dinner","none");
            brunch = prefs.getString("Brunch", "none");
            lunner = prefs.getString("Lunner", "none");
            token = prefs.getString("token",null);
            username = prefs.getString("profile_username",null);
            profile_pic = prefs.getString("profile_pic", null);
            breakfast_cal = Integer.parseInt(prefs.getString("Breakfast_cal", "0"));
            lunch_cal = Integer.parseInt(prefs.getString("Lunch_cal", "0"));
            dinner_cal = Integer.parseInt(prefs.getString("Dinner_cal", "0"));
            brunch_cal = Integer.parseInt(prefs.getString("Brunch_cal", "0"));
            lunner_cal = Integer.parseInt(prefs.getString("Lunner_cal", "0"));
            daily_calories = Integer.parseInt(prefs.getString("daily_calorie_target","0"));
            Log.e("daily_calories", daily_calories.toString());
            CircleImageView profile = view.findViewById(R.id.profile);
            Glide.with(view.getContext())
                    .load(profile_pic)
                    .into(profile);

        }
        empty_layout = view.findViewById(R.id.empty_layout);

        healthTipsItemList = new ArrayList<>();

        //new GetRecommendationTask().execute(token);

        recyclerView = view.findViewById(R.id.health_tips);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        healthTipsAdapter = new HealthTipsAdapter(getContext(),healthTipsItemList);
        recyclerView.setAdapter(healthTipsAdapter);

        return  view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        load_views();
        check_meals();
        new GetHealthTipsTask().execute(token);
        calories_gained = breakfast_cal + lunch_cal + dinner_cal + brunch_cal + lunner_cal;
        calories_left = daily_calories - calories_gained;

        calories_gained_tv.setText(Integer.toString(calories_gained));
        calories_left_tv.setText(Integer.toString(calories_left));
        calories_goal_tv.setText(Integer.toString(daily_calories));
        TvUsername = (TextView) Objects.requireNonNull(getView().findViewById(R.id.profile_username));
        TvUsername.setText(username);

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

        btn_brunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                meal_recommendationList = new ArrayList<>();
                @SuppressLint("InflateParams") final View mView = getLayoutInflater().inflate(R.layout.meal_recommendation_activity, null);
                //open details page
                show = mView.findViewById(R.id.id_meal_type);
                save = mView.findViewById(R.id.save);

                mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_container);
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

                SharedPreferences prefs = getActivity().getSharedPreferences(LogIn.MY_PREFS_NAME,MODE_PRIVATE);
                String restoredText = prefs.getString("login", null);


                RelativeLayout relativeLayout = mView.findViewById(R.id.passed_time);
                relativeLayout.setVisibility(View.GONE);
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mView.getContext());
                //setting up pop up view
                RecyclerView recyclerView = mView.findViewById(R.id.recycler_view);
                recyclerView.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mView.getContext());
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                recyclerView.setLayoutManager(linearLayoutManager);
                meal_item = mView.findViewById(R.id.selected_meal);
                if(restoredText != null){

                    String meal = prefs.getString("Brunch", "none");
                    if(!meal.equals("none")){
                        meal_item.setText(meal);
                    }
                }
                mealRecommendationAdapter = new MealRecommendationAdapter(mView.getContext(),meal_recommendationList, meal_item, "Brunch");
                recyclerView.setAdapter(mealRecommendationAdapter);
                show.setText("Brunch");
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!meal_item.getText().toString().equals("")){
                            SharedPreferences.Editor editor = getActivity().getSharedPreferences(LogIn.MY_PREFS_NAME,MODE_PRIVATE).edit();

                            editor.remove("Brunch");
                            editor.putString("Brunch", meal_item.getText().toString());
                            editor.apply();


                            SharedPreferences prefs = getActivity().getSharedPreferences(LogIn.MY_PREFS_NAME,MODE_PRIVATE);
                            String restoredText = prefs.getString("login", null);

                            if(restoredText != null){
                                String value = prefs.getString("Brunch" + "_cal", "0");
                                Log.e("Brunch" + "_cal", value);
                            }
                            dialog.dismiss();
                            ((ViewGroup)mView.getParent()).removeView(mView);
                            startActivity(new Intent(getActivity(), MainActivity.class));
                        }
                    }
                });
            }
        });

        btn_lunner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                meal_recommendationList = new ArrayList<>();
                @SuppressLint("InflateParams") final View mView = getLayoutInflater().inflate(R.layout.meal_recommendation_activity, null);
                //open details page
                show = mView.findViewById(R.id.id_meal_type);
                save = mView.findViewById(R.id.save);

                mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_container);
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

                RelativeLayout relativeLayout = mView.findViewById(R.id.passed_time);
                relativeLayout.setVisibility(View.GONE);
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mView.getContext());
                //setting up pop up view
                RecyclerView recyclerView = mView.findViewById(R.id.recycler_view);
                recyclerView.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mView.getContext());
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                recyclerView.setLayoutManager(linearLayoutManager);
                meal_item = mView.findViewById(R.id.selected_meal);
                SharedPreferences prefs = getActivity().getSharedPreferences(LogIn.MY_PREFS_NAME,MODE_PRIVATE);
                String restoredText = prefs.getString("login", null);

                if(restoredText != null){

                    String meal = prefs.getString("Lunner", "none");
                    if(!meal.equals("none")){
                        meal_item.setText(meal);
                    }
                }
                mealRecommendationAdapter = new MealRecommendationAdapter(mView.getContext(),meal_recommendationList, meal_item, "Lunner");
                recyclerView.setAdapter(mealRecommendationAdapter);
                show.setText("Lunner");
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!meal_item.getText().toString().equals("")){
                            SharedPreferences.Editor editor = getActivity().getSharedPreferences(LogIn.MY_PREFS_NAME,MODE_PRIVATE).edit();

                            editor.remove("Lunner");
                            editor.putString("Lunner", meal_item.getText().toString());
                            editor.apply();


                            SharedPreferences prefs = getActivity().getSharedPreferences(LogIn.MY_PREFS_NAME,MODE_PRIVATE);
                            String restoredText = prefs.getString("login", null);

                            if(restoredText != null){
                                String value = prefs.getString("Lunner" + "_cal", "0");
                                Log.e("Lunner" + "_cal", value);
                            }
                            dialog.dismiss();
                            ((ViewGroup)mView.getParent()).removeView(mView);
                            startActivity(new Intent(getActivity(), MainActivity.class));
                        }
                    }
                });
            }
        });

    }

    public void check_meals(){
        if(!breakfast.equals("none")){
            btn_breakfast.setImageResource(R.mipmap.ic_breakfast_filled);
        }
        if(!lunch.equals("none")){
            btn_lunch.setImageResource(R.mipmap.ic_lunch_filled);
        }
        if(!dinner.equals("none")){
            btn_dinner.setImageResource(R.mipmap.ic_dinner_filled);
        }
        if(!brunch.equals("none")){
            btn_brunch.setImageResource(R.mipmap.ic_brunch_filled);
        }
        if(!lunner.equals("none")){
            btn_lunner.setImageResource(R.mipmap.ic_lunner_filled);
        }
    }

    public void load_views(){
        btn_breakfast = Objects.requireNonNull(getActivity()).findViewById(R.id.btn_breakfast);
        btn_lunch = getActivity().findViewById(R.id.btn_lunch);
        btn_dinner = getActivity().findViewById(R.id.btn_dinner);
        btn_brunch = getActivity().findViewById(R.id.btn_brunch);
        btn_lunner = getActivity().findViewById(R.id.btn_lunner);

        calories_goal_tv = getActivity().findViewById(R.id.calories_goal);
        calories_gained_tv = getActivity().findViewById(R.id.calories_added);
        calories_left_tv = getActivity().findViewById(R.id.calories_left);
    }

    public void loadRecyclerViewData(){
        mSwipeRefreshLayout.setRefreshing(true);
        new GetSnacksTask().execute(token);
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

    @SuppressLint("StaticFieldLeak")
    class GetSnacksTask extends AsyncTask<String, String, JSONArray> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected JSONArray doInBackground(String... strings) {

            String auth_token = strings[0];
            try {
                Log.e("IOexcep", "try");
                URL url = new URL("https://healthmate-api-heroku.herokuapp.com/getsnacks");
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
            meal_recommendationList.clear();
            if(s!=null) {
                try {
                    for (int i = 0; i < s.length(); i++) {
                        JSONObject jo = s.getJSONObject(i);
                        food_name = jo.getString("name_of_snack");
                        food_calories = jo.getString("calories");

                        meal_recommendation = new MealRecommendationItem(food_name,food_calories);
                        meal_recommendationList.add(meal_recommendation);
                    }

                    recyclerView.getRecycledViewPool().clear();
                    mealRecommendationAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }

}
