package com.healthmate.client.Objects;

import java.util.ArrayList;

public class ChallengeObject {
    private String title;
    private String image_url;
    private  String goal;
    private String end_date;
    private String challenge_id;
    private String steps;
    private ArrayList<Challenge_user> challenge_user;
    private String description;
    private String creator;


    public ChallengeObject(String title, String image_url, String goal, String end_date,
                           String challenge_id, String steps, ArrayList<Challenge_user> challenge_user,
                           String description, String creator){
        this.title = title;
        this.image_url = image_url;
        this.goal = goal;
        this.end_date = end_date;
        this.challenge_id = challenge_id;
        this.steps = steps;
        this.challenge_user = challenge_user;
        this.description = description;
        this.creator = creator;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getChallenge_id() {
        return challenge_id;
    }

    public void setChallenge_id(String challenge_id) {
        this.challenge_id = challenge_id;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public ArrayList<Challenge_user> getChallenge_user() {
        return challenge_user;
    }

    public void setChallenge_user(ArrayList<Challenge_user> challenge_user) {
        this.challenge_user = challenge_user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
