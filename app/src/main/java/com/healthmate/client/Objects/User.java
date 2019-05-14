package com.healthmate.client.Objects;

import com.google.gson.annotations.SerializedName;

public class User {

    private String username;
    private boolean isFollowing;
    private String user_id;
    public  String profile_pic;

    public User(String username,boolean isFollowing, String user_id, String profile_pic){

        this.username = username;
        this.user_id = user_id;
        this.isFollowing = isFollowing;
        this.profile_pic = profile_pic;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
