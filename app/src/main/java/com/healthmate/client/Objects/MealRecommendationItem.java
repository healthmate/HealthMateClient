package com.healthmate.client.Objects;

public class MealRecommendationItem {

    private String food_name;
    private   String calorie;

    public MealRecommendationItem(String food_name, String calorie) {

        this.food_name = food_name;
        this.calorie = calorie;
    }

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public String getCalorie() {
        return calorie;
    }

    public void setCalorie(String calorie) {
        this.calorie = calorie;
    }
}
