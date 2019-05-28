package com.healthmate.client.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.healthmate.client.Community.Profile;
import com.healthmate.client.Objects.ChallengeAdapter;
import com.healthmate.client.Objects.ChallengeObject;
import com.healthmate.client.Objects.Challenge_user;
import com.healthmate.client.Objects.MyBarRenderer;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static com.healthmate.client.Community.Community.MY_PREFS_NAME;
import static java.lang.Math.round;

public class Activities extends Fragment {

    private TextView TvSteps,TvUsername, TvCaloriesBurnt, TvTargetSteps;
    String token,username,profile_pic,weight;
    LinearLayout no_data_layout;
    BarChart mChart;
    InputStream is = null;
    String line = null;
    String result = null;
    int numSteps;
    RelativeLayout metrics;

    ///recycle view variables
    private RecyclerView recyclerView;
    public ChallengeAdapter challengeAdapter;
    public List<ChallengeObject> challengeObjectList;
    ChallengeObject challengeObject;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);
        String restoredText = prefs.getString("login", null);
        View view = inflater.inflate(R.layout.activity_activities,container, false);
        if(restoredText != null){

            numSteps = prefs.getInt("steps", -1);
            weight = prefs.getString("weight","0");
            Log.e("steps", Integer.toString(numSteps) );
            Log.e("weight", weight );
            token = prefs.getString("token",null);
            username = prefs.getString("profile_username",null);
            profile_pic = prefs.getString("profile_pic", null);
            CircleImageView profile = view.findViewById(R.id.profile);
            Glide.with(view.getContext())
                    .load(profile_pic)
                    .into(profile);

            new StoreSteps().execute(String.valueOf(numSteps), token, get_Date());
        }
        ///// Initializing Recycle View ...

        challengeObjectList = new ArrayList<>();


        //Task to get challenge data
        new GetChallengeTask().execute(token);

        Log.e("Postobj ", challengeObjectList.toString());
        recyclerView = view.findViewById(R.id.challenge_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        @SuppressLint("InflateParams") View pop_up = getLayoutInflater().inflate(R.layout.challenge_details, null);
        challengeAdapter = new ChallengeAdapter(getContext(),challengeObjectList, pop_up);
        recyclerView.setAdapter(challengeAdapter);

        return view;
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // get steps
        mChart = Objects.requireNonNull(getView()).findViewById(R.id.barchart);
        mChart.setVisibility(GONE);
        metrics = view.findViewById(R.id.metrics);

        new GetStepsTask().execute(token);
        TvSteps = Objects.requireNonNull(getView()).findViewById(R.id.tv_steps);

        TvSteps.setText(Integer.toString(numSteps));

        TvUsername = (TextView) Objects.requireNonNull(getView().findViewById(R.id.profile_username));
        TvCaloriesBurnt = view.findViewById(R.id.calories_burnt);
        CalculateCaloriesBurnt();
        TvUsername.setText(username);

        no_data_layout = view.findViewById(R.id.no_data_layout);

        CircleImageView profile = view.findViewById(R.id.profile);
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
        ///call get Challenge task again
        ///new GetChallengeTask().execute(token);
    }

    private String get_Date(){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Date currentLocalTime = cal.getTime();
        @SuppressLint("SimpleDateFormat") DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        date.setTimeZone(TimeZone.getTimeZone("GMT"));
        return date.format(currentLocalTime);
    }

    @SuppressLint("SetTextI18n")
    private void CalculateCaloriesBurnt(){
        Double Cal_Per_Step = (0.57 * Integer.parseInt(weight))/2000;
        Log.e("cal_per_step", Double.toString(Cal_Per_Step) );
        TvCaloriesBurnt.setText(Integer.toString((int)(Cal_Per_Step * numSteps)));
    }

    @SuppressLint("StaticFieldLeak")
    class StoreSteps extends AsyncTask<String, String, Integer> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Integer doInBackground(String... strings) {
            String steps = strings[0];
            String auth_token = strings[1];
            String date = strings[2];
            try {
                Log.e("IOexcep", "try");
                URL url = new URL("https://healthmate-api-heroku.herokuapp.com/storesteps/"+steps);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Authorization", "JWT "+auth_token);
                con.setDoOutput(true);
                con.setConnectTimeout(3000);
                con.setRequestProperty("Content-Type","application/json");
                con.connect();

                JSONObject jsonparam = new JSONObject();
                jsonparam.put("date",date);

                OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
                out.write(jsonparam.toString());
                out.close();

                int resp=con.getResponseCode();
                Log.e("IOexcep", "connnect");
                Log.e("IOexcep", Integer.toString(resp));
                BufferedInputStream is = new BufferedInputStream(con.getInputStream());
                //READ IS content into a string
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));

                int response = con.getResponseCode();
                is.close();
                br.close();
                con.disconnect();

                return response;


            } catch (MalformedURLException e) {
                Log.e("IOexcep", "Malformed URL");
            } catch (IOException e) {
                Log.e("IOexcep", "Not Connected");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return  null;
        }

        @Override
        protected void onPostExecute(Integer s) {

        }
    }

    @SuppressLint("StaticFieldLeak")
    class GetChallengeTask extends AsyncTask<String, String, JSONArray> {
        @Override
        protected void onPreExecute() {

        }


        @Override
        protected JSONArray doInBackground(String... strings) {

            String auth_token = strings[0];
            try {
                Log.e("IOexcep", "try");
                URL url = new URL("https://healthmate-api-heroku.herokuapp.com/challenge/getchallenges");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Authorization", "JWT "+auth_token);
                con.connect();

                int resp=con.getResponseCode();
                Log.e("ChallengeTask", Integer.toString(resp));
                Log.e("ChallengeTask", "connnect");

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

                Log.e("CTdoInbackground", result );

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
        private String title;
        private String image_url;
        private String goal;
        private String end_date;
        private String challenge_id;
        private String steps;
        @Override
        protected void onPostExecute(JSONArray s) {
            challengeObjectList.clear();
            if(s!=null) {

                try {

                    if(s.getJSONObject(0)!=null){
                        no_data_layout.setVisibility(GONE);
                    }
                    for (int i = 0; i < s.length(); i++) {
                        JSONObject jo = s.getJSONObject(i);
                        title = jo.getString("challenge_name");
                        image_url = jo.getString("image_url");
                        goal = jo.getString("goal");
                        end_date = jo.getString("end_date");
                        challenge_id = jo.getString("challenge_id");
                        steps = jo.getString("steps");
                        String description = jo.getString("challenge_description");
                        String creator = jo.getString("creator");
                        JSONArray challenge_user_array = jo.getJSONArray("users");
                        ArrayList<Challenge_user> challenge_users_list = new ArrayList<>();
                        Log.e("IMAGE_URL "+10, "dontcare");
                        for(int j = 0; j<challenge_user_array.length(); j++){
                            JSONObject challenge_user_object = challenge_user_array.getJSONObject(j);
                            String username = challenge_user_object.getString("username");
                            String user_steps = challenge_user_object.getString("steps");
                            String user_role = challenge_user_object.getString("role");
                            Challenge_user challenge_user = new Challenge_user(username, user_role, user_steps);
                            challenge_users_list.add(challenge_user);
                        }

                        //post_id = jo.getString("post_id");

                        challengeObject = new ChallengeObject(title, image_url, goal, end_date,
                                challenge_id,steps, challenge_users_list, description, creator);
                        challengeObjectList.add(challengeObject);
                    }
                    recyclerView.getRecycledViewPool().clear();
                    challengeAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    @SuppressLint("StaticFieldLeak")
    class GetStepsTask extends AsyncTask<String, String, JSONArray> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected JSONArray doInBackground(String... strings) {

            String auth_token = strings[0];
            try {
                Log.e("IOexcep", "try");
                URL url = new URL("https://healthmate-api-heroku.herokuapp.com/getsteps/"+5);
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

                Log.e("getting steps", result );

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

            if(s!=null) {
                metrics.setVisibility(View.GONE);
                mChart.setVisibility(View.VISIBLE);

                mChart.setDrawBarShadow(false);
                mChart.setDrawValueAboveBar(true);
                mChart.setMaxVisibleValueCount(50);
                mChart.setPinchZoom(false);
                mChart.setDrawGridBackground(false);
                mChart.setScaleEnabled(false);
                mChart.setDoubleTapToZoomEnabled(false);

                //mChart.zoom(2f,2f,0,0);

                ArrayList<BarEntry> barEntries = new ArrayList<>();
                final ArrayList<String> days = new ArrayList<>();
                try {
                    for (int i = 0; i < s.length(); i++) {
                        JSONObject jo = s.getJSONObject(i);
                        String date = jo.getString("date");
                        String steps = jo.getString("steps");
                        barEntries.add(new BarEntry(i, Float.parseFloat(steps)));
                        String[] parts = date.split(",", 2);
                        days.add(parts[0]);
                        Log.e("days", "onPostExecute: " + parts[0] );

                    }
                    Log.e("days", "onPostExecute: " + days );
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                BarDataSet set = new BarDataSet(barEntries, "Data set 1");
                set.setColors(ColorTemplate.rgb("205004"));

                //ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                //dataSets.add(set);
                BarData data = new BarData(set);
                data.setBarWidth(0.3f);


                XAxis axis = mChart.getXAxis();
                axis.setCenterAxisLabels(true);
                axis.setGranularity(1f);

                //


                axis.setLabelCount(days.size()+1,true);
                axis.setPosition(XAxis.XAxisPosition.BOTTOM);
                axis.setGranularityEnabled(true);
                mChart.getAxisLeft().setDrawLabels(false);
                mChart.getAxisRight().setDrawLabels(false);
                mChart.getLegend().setEnabled(false);
                //remove grid lines
                mChart.getAxisLeft().setDrawGridLines(false);
                mChart.getAxisRight().setDrawGridLines(false);
                mChart.getXAxis().setDrawGridLines(false);


                Description description = new Description();
                description.setText("");
                mChart.setDescription(description);

                axis.setValueFormatter(new IndexAxisValueFormatter(){
                    @Override
                    public String getFormattedValue(float value) {
                        int index = round(value);
                        if(index >= days.size()){
                            index = days.size()-1;
                        }
                        return days.get(index);
                    }
                });
                mChart.setRenderer(new MyBarRenderer(mChart,
                        mChart.getAnimator(),
                        mChart.getViewPortHandler()));
                mChart.setData(data);
                mChart.notifyDataSetChanged();
                mChart.invalidate();
            }

        }
    }
}
