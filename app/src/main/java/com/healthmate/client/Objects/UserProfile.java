package com.healthmate.client.Objects;

public class UserProfile {

    private String username;
    private String community;
    private String user_id;
    private String posts;

    public UserProfile(String username,String community, String posts, String user_id){

        this.username = username;
        this.user_id = user_id;
        this.community = community;
        this.posts = posts;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPosts() {
        return posts;
    }

    public void setPosts(String posts) {
        this.posts = posts;
    }
}
