package com.healthmate.client.Objects;

public class Challenge_user {
    private String username;
    //private String image_url;
    private  String role;
    private String steps;


    public Challenge_user(String username, String role, String steps){
        this.username = username;
        this.role = role;
        this.steps = steps;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }
}
