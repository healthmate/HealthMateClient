package com.healthmate.client.Lifestyle;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.healthmate.client.Activities.Activities;
import com.healthmate.client.Community.Profile;
import com.healthmate.client.R;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;
import static com.healthmate.client.Community.Community.MY_PREFS_NAME;

public class Lifestyle extends Fragment {

    private TextView TvSteps,TvUsername;
    String token,username,profile_pic;


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
        return  view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TvUsername = (TextView) Objects.requireNonNull(getView().findViewById(R.id.profile_username));
        TvUsername.setText(username);

        CircleImageView profile = view.findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Profile.class));
            }
        });
    }

}
