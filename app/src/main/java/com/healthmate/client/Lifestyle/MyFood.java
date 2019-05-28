package com.healthmate.client.Lifestyle;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.healthmate.client.MainActivity;
import com.healthmate.client.Objects.MyFoodItem;
import com.healthmate.client.Objects.MyFoodSpinnerAdapter;
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
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.getDefaultSize;
import static com.healthmate.client.Auth.LogIn.MY_PREFS_NAME;

public class MyFood extends Fragment {

    // You spinner view
    private Spinner mySpinner_1;
    private Spinner mySpinner_2;
    private Spinner mySpinner_3;
    public List<MyFoodItem> myFoodItemList;
    public MyFoodItem myFoodItem;
    InputStream is = null;
    String line = null;
    String result = null;
    String token,indicator;
    ProgressDialog progressDialog;
    // Custom Spinner adapter (ArrayAdapter<User>)
    // You can define as a private to use it in the all class
    // This is the object that is going to do the "magic"
    private MyFoodSpinnerAdapter adapter;
    private TextView mealtv_1,mealtv_2,mealtv_3, total_calories_tv, calorie_limit_TV;
    private ImageView addtv_1, addtv_2, addtv_3;
    private ImageView removetv_1,removetv_2,removetv_3;
    private EditText calories_1,calories_2,calories_3;
    private String calorie_limit;
    private Button add_meal;
    private  String meal_1,meal_2,meal_3;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_my_food,container, false);
        progressDialog =new ProgressDialog(getContext());
        myFoodItemList = new ArrayList<>();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the adapter sending the current context
        // Send the simple_spinner_item layout
        // And finally send the foodlist array (Your data)
        Load_Views();


        Field popup = null;
        try {
            popup = Spinner.class.getDeclaredField("mPopup");

            popup.setAccessible(true);
            android.widget.ListPopupWindow popupWindow_1 = (android.widget.ListPopupWindow) popup.get(mySpinner_1);
            android.widget.ListPopupWindow popupWindow_2 = (android.widget.ListPopupWindow) popup.get(mySpinner_2);
            android.widget.ListPopupWindow popupWindow_3 = (android.widget.ListPopupWindow) popup.get(mySpinner_3);
            popupWindow_1.setHeight(500);
            popupWindow_2.setHeight(500);
            popupWindow_3.setHeight(500);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

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

        new LoadSpinner().execute(token,indicator);
        add_meal.setTag("BLimit");
        Calculate_Calories();

        mySpinner_1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {

                MyFoodItem myFoodItem = adapter.getItem(position);

                calories_1.setText(myFoodItem.getCalories());
                meal_1 = myFoodItem.getMeal_name();
                Log.e("Spinner1", "onItemSelected: "+ myFoodItem.getCalories() );
                Log.e("Spinner1", "onItemSelected: "+ myFoodItem.getMeal_name() );
                Calculate_Calories();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapter) {  }
        });

        mySpinner_2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {

                MyFoodItem myFoodItem = adapter.getItem(position);
                meal_2 = myFoodItem.getMeal_name();
                calories_2.setText(myFoodItem.getCalories());
                Log.e("Spinner2", "onItemSelected: "+ myFoodItem.getCalories() );
                Log.e("Spinner2", "onItemSelected: "+ myFoodItem.getMeal_name() );
                Calculate_Calories();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapter) {  }
        });

        mySpinner_3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {

                MyFoodItem myFoodItem = adapter.getItem(position);
                meal_3 = myFoodItem.getMeal_name();
                calories_3.setText(myFoodItem.getCalories());
                Log.e("Spinner3", "onItemSelected: "+ myFoodItem.getCalories() );
                Log.e("Spinner3", "onItemSelected: "+ myFoodItem.getMeal_name() );
                Calculate_Calories();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapter) {  }
        });

        addtv_1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                String value = mealtv_1.getText().toString();
                int sum = Integer.parseInt(value) + 1;
                mealtv_1.setText(Integer.toString(sum));
                Calculate_Calories();
            }
        });

        removetv_1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                String value = mealtv_1.getText().toString();
                if(!value.equals("1")){
                    int sum = Integer.parseInt(value) - 1;
                    mealtv_1.setText(Integer.toString(sum));
                    Calculate_Calories();
                }
            }
        });

        addtv_2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                String value = mealtv_2.getText().toString();
                int sum = Integer.parseInt(value) + 1;
                mealtv_2.setText(Integer.toString(sum));
                Calculate_Calories();
            }
        });

        removetv_2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                String value = mealtv_2.getText().toString();
                if(!value.equals("1")){
                    int sum = Integer.parseInt(value) - 1;
                    mealtv_2.setText(Integer.toString(sum));
                    Calculate_Calories();
                }
            }
        });

        addtv_3.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                String value = mealtv_3.getText().toString();
                int sum = Integer.parseInt(value) + 1;
                mealtv_3.setText(Integer.toString(sum));
                Calculate_Calories();
            }
        });

        removetv_3.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                String value = mealtv_3.getText().toString();
                if(!value.equals("1")){
                    int sum = Integer.parseInt(value) - 1;
                    mealtv_3.setText(Integer.toString(sum));
                    Calculate_Calories();
                }
            }
        });

        add_meal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String meal1,meal2,meal3,total_meal;
                total_meal = "";

                if(!meal_1.equals("")){
                    total_meal = total_meal + meal_1 + " with ";
                }
                if(!meal_2.equals("")){
                    total_meal = total_meal + meal_2 + " with ";
                }
                if(!meal_3.equals("")){
                    total_meal = total_meal + meal_3;
                }

                if(add_meal.getTag().equals("ALimit")){
                    Toast.makeText(getContext(),"Above Limit", Toast.LENGTH_SHORT).show();
                }
                if(!total_meal.equals("")){
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE).edit();
                    Toast.makeText(getContext(),"Success", Toast.LENGTH_SHORT).show();
                    editor.remove(indicator);
                    editor.putString(indicator, total_meal);
                    editor.remove(indicator+"_cal");
                    editor.putString(indicator+"_cal",total_calories_tv.getText().toString());
                    editor.apply();
                    startActivity(new Intent(getActivity(), MainActivity.class));
                }
            }
        });
    }

    public void Load_Views(){
        mySpinner_1 = (Spinner) getActivity().findViewById(R.id.spinner_1);
        mySpinner_2 = (Spinner) getActivity().findViewById(R.id.spinner_2);
        mySpinner_3 = (Spinner) getActivity().findViewById(R.id.spinner_3);

        calories_1 = getActivity().findViewById(R.id.calories_1);
        calories_2 = getActivity().findViewById(R.id.calories_2);
        calories_3 = getActivity().findViewById(R.id.calories_3);

        mealtv_1 = getActivity().findViewById(R.id.mealtv_1);
        addtv_1 = getActivity().findViewById(R.id.add_1);
        removetv_1 = getActivity().findViewById(R.id.remove_1);

        mealtv_2 = getActivity().findViewById(R.id.mealtv_2);
        addtv_2 = getActivity().findViewById(R.id.add_2);
        removetv_2 = getActivity().findViewById(R.id.remove_2);

        mealtv_3 = getActivity().findViewById(R.id.mealtv_3);
        addtv_3 = getActivity().findViewById(R.id.add_3);
        removetv_3 = getActivity().findViewById(R.id.remove_3);

        add_meal = getActivity().findViewById(R.id.btn_add_meal);

        total_calories_tv = getActivity().findViewById(R.id.total_calories);
        calorie_limit_TV = getActivity().findViewById(R.id.calorie_limit);
    }

    @SuppressLint("SetTextI18n")
    public void Calculate_Calories(){
        Integer meal_1 = Integer.parseInt(mealtv_1.getText().toString()) * Integer.parseInt(calories_1.getText().toString());
        Integer meal_2 = Integer.parseInt(mealtv_2.getText().toString()) * Integer.parseInt(calories_2.getText().toString());
        Integer meal_3 = Integer.parseInt(mealtv_3.getText().toString()) * Integer.parseInt(calories_3.getText().toString());

        Integer total_calories = meal_1 + meal_2 + meal_3;
        total_calories_tv.setText(Integer.toString(total_calories));
        float value = (Float.parseFloat(calorie_limit_TV.getText().toString())-(float)total_calories);
        Log.e("value", Float.toString(value) );
        Log.e("Tag1", (String) add_meal.getTag());
        if(value < 0){
            total_calories_tv.setTextColor(getActivity().getResources().getColor(R.color.red));
            add_meal.setTag("ALimit");
            add_meal.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.btn_ash));
            Log.e("Tag2","It was called");
        }else{
            total_calories_tv.setTextColor(getActivity().getResources().getColor(R.color.colorGreen));
            add_meal.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.rec_gradient));
            add_meal.setTag("BLimit");
        }

    }

    @SuppressLint("StaticFieldLeak")
    class LoadSpinner extends AsyncTask<String, String, JSONArray> {
        @Override
        protected void onPreExecute() {

            progressDialog.setMessage("Setting up");
            progressDialog.setCancelable(false);
            progressDialog.show();
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected JSONArray doInBackground(String... strings) {

            String auth_token = strings[0];
            String meal_type = strings[1];
            try {
                Log.e("IOexcep", "try");
                URL url = new URL("https://healthmate-api-heroku.herokuapp.com/sortfoods/"+ meal_type);
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

        private String meal_name, calories;
        @Override
        protected void onPostExecute(JSONArray s) {
            if(s!=null) {
                MyFoodItem sampleFoodItem = new MyFoodItem("", "0");
                myFoodItemList.add(sampleFoodItem);
                try {
                    for (int i = 0; i < s.length(); i++) {
                        JSONObject jo = s.getJSONObject(i);
                        meal_name = jo.getString("name_of_food");
                        calories = jo.getString("calories");
                        calorie_limit = jo.getString("calorie_limit");
                        myFoodItem = new MyFoodItem(meal_name,calories);

                        myFoodItemList.add(myFoodItem);
                    }

                    calorie_limit_TV.setText(calorie_limit);
                    adapter = new MyFoodSpinnerAdapter(getActivity(),
                            android.R.layout.simple_spinner_item,
                            myFoodItemList);

                    progressDialog.dismiss();
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    mySpinner_1.setAdapter(adapter); // Set the custom adapter to the spinner
                    mySpinner_2.setAdapter(adapter);
                    mySpinner_3.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }
}
