package com.healthmate.client.Objects;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.healthmate.client.R;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.healthmate.client.Auth.LogIn.MY_PREFS_NAME;

public class ChallengeDetailsAdapter extends RecyclerView.Adapter<ChallengeDetailsAdapter.ViewHolder> {

    public Context mContext;
    public List<Challenge_user> mChallenge;
    Challenge_user challengeObject;

    public ChallengeDetailsAdapter(Context mContext, List<Challenge_user> mChallenge) {
        this.mContext = mContext;
        this.mChallenge = mChallenge;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.challenge_details_item, viewGroup, false);

        return new ChallengeDetailsAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        challengeObject = mChallenge.get(i);
        viewHolder.challenge_username_tv.setText(challengeObject.getUsername());
        viewHolder.challenge_role_tv.setText(challengeObject.getRole());
        viewHolder.challenge_steps_tv.setText(challengeObject.getSteps());

    }

    @Override
    public int getItemCount() {
        return mChallenge.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView challenge_username_tv, challenge_role_tv, challenge_steps_tv;

        public ViewHolder(View itemView) {
            super(itemView);

            challenge_username_tv = itemView.findViewById(R.id.challenge_username);
            challenge_role_tv = itemView.findViewById(R.id.challenge_role);
            challenge_steps_tv = itemView.findViewById(R.id.challenge_steps);
        }
    }
}
