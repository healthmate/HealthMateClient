package com.healthmate.client.Lifestyle;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.healthmate.client.Community.Community;
import com.healthmate.client.MainActivity;
import com.healthmate.client.Objects.MealRecommendationAdapter;
import com.healthmate.client.Objects.MealRecommendationItem;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;
import static com.healthmate.client.Auth.LogIn.MY_PREFS_NAME;

public class Meal_Recommendation extends Fragment {

    private RecyclerView recyclerView;
    public MealRecommendationItem meal_recommendation;
    private MealRecommendationAdapter mealRecommendationAdapter;
    public List<MealRecommendationItem> meal_recommendationList;
    private  RelativeLayout relativeLayout;
    private TextView save,show;
    private EditText meal_item;
    InputStream is = null;
    String line = null;
    String result = null;
    String food_name, token;
    String food_calories,indicator;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.meal_recommendation_activity,container, false);
        meal_recommendationList = new ArrayList<>();

        relativeLayout = view.findViewById(R.id.passed_time);
        relativeLayout.setVisibility(View.GONE);
        SharedPreferences prefs = this.getActivity().getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);

        String restoredText = prefs.getString("login", null);

        if(restoredText != null){
            token = prefs.getString("token",null);
            Log.e("TOKEN", token);
        }

        Bundle intent = getActivity().getIntent().getExtras();
        if(intent != null){
            indicator = intent.getString("indicator");
        }

        Check_Time_Of_Day();

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        meal_item = view.findViewById(R.id.selected_meal);
        mealRecommendationAdapter = new MealRecommendationAdapter(getContext(),meal_recommendationList, meal_item, indicator);
        recyclerView.setAdapter(mealRecommendationAdapter);

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        show = view.findViewById(R.id.id_meal_type);
        save = view.findViewById(R.id.save);

        SharedPreferences prefs = getActivity().getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);
        String restoredText = prefs.getString("login", null);

        if(restoredText != null){

            String meal = prefs.getString(indicator, "none");
            if(!meal.equals("none")){
                meal_item.setText(meal);
            }
        }
        show.setText(indicator);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!meal_item.getText().toString().equals("")){
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE).edit();

                    editor.remove(indicator);
                    editor.putString(indicator, meal_item.getText().toString());
                    editor.apply();


                    SharedPreferences prefs = getActivity().getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);
                    String restoredText = prefs.getString("login", null);

                    if(restoredText != null){
                        String value = prefs.getString(indicator + "_cal", "0");
                        Log.e(indicator + "_cal", value);
                    }
                    startActivity(new Intent(getActivity(), MainActivity.class));
                }
            }
        });

    }

    public void Check_Time_Of_Day(){
        Integer currentTime = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        relativeLayout.setVisibility(View.VISIBLE);
        if(indicator.equals("Breakfast") && currentTime < 12){

            new GetRecommendationTask().execute(token);
        }
        else if(indicator.equals("Lunch") && currentTime > 11 && currentTime < 17){
            //relativeLayout.setVisibility(View.VISIBLE);
            new GetRecommendationTask().execute(token);
        }else if(indicator.equals("Dinner") && currentTime > 16){
            //relativeLayout.setVisibility(View.VISIBLE);
            new GetRecommendationTask().execute(token);
        }
    }

    public void loadRecyclerViewData(){
        mSwipeRefreshLayout.setRefreshing(true);
        Check_Time_Of_Day();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @SuppressLint("StaticFieldLeak")
    class GetRecommendationTask extends AsyncTask<String, String, JSONArray> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected JSONArray doInBackground(String... strings) {

            String auth_token = strings[0];
            try {
                Log.e("IOexcep", "try");
                URL url = new URL("https://healthmate-api-heroku.herokuapp.com/getrecommendation");
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
            relativeLayout.setVisibility(View.GONE);

            meal_recommendationList.clear();
            if(s!=null) {
                try {
                    for (int i = 0; i < s.length(); i++) {
                        JSONObject jo = s.getJSONObject(i);
                        food_name = jo.getString("name_of_food");
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
