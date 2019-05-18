package com.healthmate.client.Objects;

public class MyFoodItem {
    private String meal_name;
    private String calories;

    public MyFoodItem(String meal_name, String calories){
        this.meal_name = meal_name;
        this.calories = calories;
    }

    public String getMeal_name() {
        return meal_name;
    }

    public void setMeal_name(String meal_name) {
        this.meal_name = meal_name;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }
}
