package com.healthmate.client.Objects;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.healthmate.client.R;

import java.util.List;

public class HealthTipsAdapter extends RecyclerView.Adapter<HealthTipsAdapter.ViewHolder>{

    public Context mContext;
    public List<HealthTipsItem> mHealthTipsItem;

    public HealthTipsAdapter(Context mContext, List<HealthTipsItem> mHealthTipsItem) {
        this.mContext = mContext;
        this.mHealthTipsItem = mHealthTipsItem;
    }

    @NonNull
    @Override
    public HealthTipsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.health_tips_item, viewGroup, false);

        return new HealthTipsAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final HealthTipsAdapter.ViewHolder viewHolder, int i) {

        HealthTipsItem healthTipsItem = mHealthTipsItem.get(i);
        viewHolder.topic.setText(healthTipsItem.getTopic());
        viewHolder.details.setText(healthTipsItem.getDetails());

    }

    @Override
    public int getItemCount() {
        return mHealthTipsItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView topic, details;

        public ViewHolder(View itemView) {
            super(itemView);

            topic = itemView.findViewById(R.id.topic);
            details = itemView.findViewById(R.id.details);
        }
    }

}
