package com.healthmate.client.Objects;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.healthmate.client.Community.Comments;
import com.healthmate.client.Community.OtherProfile;
import com.healthmate.client.Community.Post;
import com.healthmate.client.Community.Profile;
import com.healthmate.client.R;

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
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.healthmate.client.Auth.LogIn.MY_PREFS_NAME;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public Context mContext;
    public List<PostObject> mPost;
    InputStream is = null;
    String line = null;
    String result = null;
    String token;

    boolean decide_like;
    String message;
    String status;
    String Profile_username;

    public PostAdapter(Context mContext, List<PostObject> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    private static class MyTaskParams{
        String postid;
        String auth_token;
        ImageView img;

        MyTaskParams(String postid, String auth_token, ImageView img){
            this.postid = postid;
            this.auth_token = auth_token;
            this.img = img;
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, viewGroup, false);

        return new PostAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final PostObject postObject;
        postObject = mPost.get(i);
        viewHolder.username.setText(postObject.getUsername());
        viewHolder.likes.setText(postObject.getLikes()+ " likes");
        Glide.with(mContext)
                .load(postObject.getImage_url())
                .into(viewHolder.post_image);
        if (postObject.getDescription().equals("")) {
            viewHolder.description.setVisibility(View.GONE);
        } else {
            viewHolder.description.setVisibility(View.VISIBLE);
            viewHolder.description.setText('"'+postObject.getDescription()+'"');
        }

        SharedPreferences prefs = mContext.getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);
        String restoredText = prefs.getString("login", null);

        if(restoredText != null){
            token = prefs.getString("token",null);
            Profile_username = prefs.getString("profile_username", null);
            Log.e("TOKEN", token);

        }

        isLiked(postObject.getPost_id(), viewHolder.like, token);

        viewHolder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!viewHolder.like.getTag().equals("liked")){
                    viewHolder.like.setImageResource(R.drawable.ic_liked);
                    viewHolder.like.setTag("liked");
                    MyTaskParams myTaskParams = new MyTaskParams(postObject.getPost_id(),token,viewHolder.like);
                    new LikeTask().execute(myTaskParams);
                }
            }
        });

        viewHolder.comment_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, Comments.class);
                intent.putExtra("post_id", postObject.getPost_id());
                intent.putExtra("description", postObject.getDescription());
                mContext.startActivity(intent);
            }
        });

        viewHolder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Profile_username.equals(viewHolder.username.getText().toString())) {
                    Intent intent = new Intent(mContext, OtherProfile.class);
                    intent.putExtra("user_id", postObject.getUser_id());
                    mContext.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView post_image,like,comment_post;
        public TextView username, description, likes;

        public ViewHolder(View itemView) {
            super(itemView);

            post_image = itemView.findViewById(R.id.post_image);
            description = itemView.findViewById(R.id.display_description);
            username = itemView.findViewById(R.id.username);
            likes = itemView.findViewById(R.id.likes);
            like = itemView.findViewById(R.id.like);
            comment_post = itemView.findViewById(R.id.comment);
        }
    }

    private void isLiked(String postid,final ImageView imageView, String tok){

        MyTaskParams myTaskParams = new MyTaskParams(postid,tok,imageView);
        new checkLikerTask().execute(myTaskParams);
    }

    @SuppressLint("StaticFieldLeak")
    class checkLikerTask extends AsyncTask<MyTaskParams, Void, Integer> {

        @Override
        protected Integer doInBackground(MyTaskParams...params) {

            String postid = params[0].postid;
            String auth_token = params[0].auth_token;
            ImageView imageView = params[0].img;

            Log.e("token", auth_token);

            try {
                Log.e("IOexcep", "try");
                URL url = new URL("https://healthmate-api-heroku.herokuapp.com/checkliker/" + postid);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Authorization", "JWT "+auth_token);
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type","application/json");
                con.connect();

                JSONObject jsonparam = new JSONObject();

                OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
                out.write(jsonparam.toString());
                out.close();
                int result=con.getResponseCode();
                Log.e("IOexcep", "connnect");
                Log.e("IOexcep", Integer.toString(result));

                con.disconnect();

                if (result == 200){
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");
                }else{
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("notliked");
                }


            } catch (MalformedURLException e) {
                Log.e("IOexcep", "Malformed URL");
            } catch (IOException e) {
                Log.e("IOexcep", "Not Connected");
            }

            return 1;
        }

    }

    @SuppressLint("StaticFieldLeak")
    class LikeTask extends AsyncTask<MyTaskParams, String, JSONObject> {
        @Override
        protected void onPreExecute() {

        }


        @Override
        protected JSONObject doInBackground(MyTaskParams... params) {
            String postid = params[0].postid;
            String auth_token = params[0].auth_token;
            ImageView imageView = params[0].img;

            try {
                Log.e("IOexcep", "try");
                Log.e("postid", postid);

                String base_url = "https://healthmate-api-heroku.herokuapp.com/like/";
                URL url = new URL(base_url + postid);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Authorization", "JWT "+auth_token);
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type","application/json");
                con.connect();

                int resp=con.getResponseCode();
                Log.e("IOexcep", "connnect");
                Log.e("IOexcep", "liked"+Integer.toString(resp));
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

                JSONObject s = new JSONObject(result);


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
        protected void onPostExecute(JSONObject s) {


        }
    }
}