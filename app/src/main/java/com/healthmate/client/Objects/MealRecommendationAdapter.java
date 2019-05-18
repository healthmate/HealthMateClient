package com.healthmate.client.Objects;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.healthmate.client.R;

import java.io.InputStream;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MealRecommendationAdapter extends RecyclerView.Adapter<MealRecommendationAdapter.ViewHolder>{
    public Context mContext;
    public List<MealRecommendationItem> mMeal;
    private EditText selected_meal;

    public MealRecommendationAdapter(Context mContext, List<MealRecommendationItem> mMeal, EditText selected_meal) {
        this.mContext = mContext;
        this.mMeal = mMeal;
        this.selected_meal = selected_meal;
    }

    @NonNull
    @Override
    public MealRecommendationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.meal_rec_item, viewGroup, false);
        return new MealRecommendationAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MealRecommendationAdapter.ViewHolder viewHolder, int i) {

        final MealRecommendationItem meal = mMeal.get(i);
        viewHolder.food_name.setText(meal.getFood_name());
        viewHolder.calories.setText(meal.getCalorie());

        viewHolder.bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_meal.setText(meal.getFood_name());

            }
        });
    }

    @Override
    public int getItemCount() {
        return mMeal.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView food_name, calories;
        public ImageView bConfirm;

        public ViewHolder(View itemView) {
            super(itemView);

            food_name = itemView.findViewById(R.id.food_name);
            calories = itemView.findViewById(R.id.food_calories);
            bConfirm = itemView.findViewById(R.id.id_confirm);
        }
    }
}
