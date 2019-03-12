package com.healthmate.client.Objects;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.healthmate.client.Community.Comments;
import com.healthmate.client.R;

import java.io.InputStream;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.healthmate.client.Auth.LogIn.MY_PREFS_NAME;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>  {

    public Context mContext;
    public List<Comment> mPost;
    InputStream is = null;
    String line = null;
    String result = null;
    String token;
    Comment comment;

    public CommentAdapter(Context mContext, List<Comment> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, viewGroup, false);

        return new CommentAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final CommentAdapter.ViewHolder viewHolder, int i) {

        comment = mPost.get(i);
        viewHolder.username.setText(comment.getUsername());

        viewHolder.comment_tv.setText(comment.getComment());

    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username, comment_tv;

        public ViewHolder(View itemView) {
            super(itemView);

            comment_tv = itemView.findViewById(R.id.comment);
            username = itemView.findViewById(R.id.username);
        }
    }
}
