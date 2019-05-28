package com.healthmate.client;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.healthmate.client.Activities.Activities;
import com.healthmate.client.Auth.LogIn;
import com.healthmate.client.Community.Community;
import com.healthmate.client.JobService.StepsJobService;
import com.healthmate.client.Lifestyle.Lifestyle;

public class MainActivity extends AppCompatActivity {


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_lifestyle:

                    selectedFragment = new Lifestyle();
                    break;
                case R.id.navigation_activities:

                    selectedFragment = new Activities();
                    break;
                case R.id.navigation_community:

                    selectedFragment = new Community();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Fragment defaults = new Lifestyle();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, defaults).commit();

        Intent ServiceIntent = new Intent(getApplicationContext(), StepsJobService.class);
        getApplicationContext().startForegroundService(ServiceIntent);

    }

}
