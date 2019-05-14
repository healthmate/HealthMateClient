package com.healthmate.client.Objects;

public class Comment {

    private String username;
    private String comment;
    private String create_at;
    private String profile_pic;

    public Comment(String username,String comment, String create_at, String profile_pic){

        this.username = username;
        this.comment = comment;
        this.create_at = create_at;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }
}
