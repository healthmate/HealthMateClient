package com.healthmate.client.Objects;

public class HealthTipsItem {
    private String topic;
    private String details;

    public HealthTipsItem(String topic, String details){
        this.topic = topic;
        this.details = details;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
