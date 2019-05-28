package com.healthmate.client.Objects;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.healthmate.client.Community.Comments;
import com.healthmate.client.Community.OtherProfile;
import com.healthmate.client.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.healthmate.client.Auth.LogIn.MY_PREFS_NAME;

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ViewHolder> {

    public Context mContext;
    public List<ChallengeObject> mChallenge;

    private View mPop_up;
    private ChallengeDetailsAdapter challengedetailsAdapter;
    private List<Challenge_user> challenge_user_list;
    Button fb;
    public ChallengeAdapter(Context mContext, List<ChallengeObject> mChallenge, View pop_up) {
        this.mContext = mContext;
        this.mChallenge = mChallenge;
        this.mPop_up = pop_up;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.challenge_item, viewGroup, false);

        return new ChallengeAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final ChallengeObject challengeObject;
        challengeObject = mChallenge.get(i);
        viewHolder.title_tv.setText(challengeObject.getTitle());
        viewHolder.goal_tv.setText(challengeObject.getSteps()+'/'+challengeObject.getGoal());
        viewHolder.end_date_tv.setText(challengeObject.getEnd_date());
        Glide.with(mContext)
                .load(challengeObject.getImage_url())
                .into(viewHolder.challenge_image);

        SharedPreferences prefs = mContext.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        //String restoredText = prefs.getString("login", null);



        challenge_user_list = new ArrayList<>();
        challenge_user_list = challengeObject.getChallenge_user();



        viewHolder.view_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open details page
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                //setting up pop up view

                final View mView = mPop_up;
                ImageView challenge_image_details_tv = mView.findViewById(R.id.challenge_image_details);
                TextView challenge_title_tv = mView.findViewById(R.id.title_tv);
                TextView challenge_description_tv = mView.findViewById(R.id.description_tv);
                TextView challenge_creator_tv = mView.findViewById(R.id.creator_tv);
                fb = mView.findViewById(R.id.close_pop_up);

                RecyclerView recyclerView = mView.findViewById(R.id.recycler_view_pop_up);
                recyclerView.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                recyclerView.setLayoutManager(linearLayoutManager);
                challengedetailsAdapter = new ChallengeDetailsAdapter(mContext,challenge_user_list);
                recyclerView.setAdapter(challengedetailsAdapter);

                ///Setting the pop up data
                Glide.with(mContext)
                        .load(challengeObject.getImage_url())
                        .into(challenge_image_details_tv);
                challenge_title_tv.setText("~"+challengeObject.getTitle()+"~");
                challenge_creator_tv.setText("created by:" + challengeObject.getCreator());
                challenge_description_tv.setText(challengeObject.getDescription());

                recyclerView.getRecycledViewPool().clear();
                challengedetailsAdapter.notifyDataSetChanged();

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.setCanceledOnTouchOutside(false);
                //((Activity)mContext).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                dialog.show();

                fb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        ((ViewGroup)mView.getParent()).removeView(mView);
                        //((Activity)mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                });


            }
        });

    }

    @Override
    public int getItemCount() {
        return mChallenge.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView challenge_image,view_details;
        TextView title_tv, goal_tv, end_date_tv;

        public ViewHolder(View itemView) {
            super(itemView);

            challenge_image = itemView.findViewById(R.id.challenge_image);
            title_tv = itemView.findViewById(R.id.challenge_title);
            goal_tv = itemView.findViewById(R.id.challenge_goal);
            end_date_tv = itemView.findViewById(R.id.challenge_date);
            view_details = itemView.findViewById(R.id.id_more_details);
        }
    }
}