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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.healthmate.client.Community.OtherProfile;
import com.healthmate.client.Community.Post;
import com.healthmate.client.Community.Search;
import com.healthmate.client.R;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;
import static com.healthmate.client.Community.Community.MY_PREFS_NAME;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ImageViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean isFragment;
    InputStream is = null;
    String line = null;
    String result = null;
    String token;
    String message;
    String status;

    //private FirebaseUser firebaseUser;

    public UserAdapter(Context context, List<User> users) {
        mContext = context;
        mUsers = users;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public UserAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserAdapter.ImageViewHolder holder, final int position) {

        final User user = mUsers.get(position);

        holder.btn_follow.setVisibility(View.VISIBLE);
        //isFollowing(user.getId(), holder.btn_follow);
        if (user.isFollowing()) {
            holder.btn_follow.setText("connected");
        } else {
            holder.btn_follow.setText("connect");
        }

        holder.username.setText(user.getUsername());
        Glide.with(mContext).load(user.getProfile_pic()).into(holder.image_profile);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, OtherProfile.class);
                intent.putExtra("user_id", user.getUser_id());
                intent.putExtra("isFollowing", Boolean.toString(user.isFollowing()));
                intent.putExtra("profile_pic", user.getProfile_pic());
                mContext.startActivity(intent);

            }
        });

        holder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.btn_follow.getText().toString().equals("connect")) {
                    SharedPreferences prefs = mContext.getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);
                    String restoredText = prefs.getString("login", null);

                    if(restoredText != null){
                        token = prefs.getString("token",null);
                    }

                    new FollowTask().execute(token, user.getUser_id());
                    holder.btn_follow.setText("CONNECTED");
                }
            }

        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, OtherProfile.class);
                intent.putExtra("user_id", user.getUser_id());
                intent.putExtra("isFollowing", Boolean.toString(user.isFollowing()));
                intent.putExtra("profile_pic", user.getProfile_pic());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public CircleImageView image_profile;
        public Button btn_follow;

        public ImageViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            image_profile = itemView.findViewById(R.id.image_profile);
            btn_follow = itemView.findViewById(R.id.btn_follow);
        }
    }

    @SuppressLint("StaticFieldLeak")
    class FollowTask extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {

        }


        @Override
        protected JSONObject doInBackground(String... strings) {

            String auth_token = strings[0];
            String user_id = strings[1];
            try {
                Log.e("IOexcep", "try");
                String base_url = "https://healthmate-api-heroku.herokuapp.com/community/request/";
                URL url = new URL(base_url + user_id);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type","application/json");
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

                return new JSONObject(result);


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
            try {
                message = s.getString("message");
                status = s.getString("status");

                Toast.makeText(mContext,message,Toast.LENGTH_LONG).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
