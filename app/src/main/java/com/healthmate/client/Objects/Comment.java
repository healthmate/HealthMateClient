package com.healthmate.client.Objects;

public class Comment {

    private String username;
    private String comment;
    private String create_at;

    public Comment(String username,String comment, String create_at){

        this.username = username;
        this.comment = comment;
        this.create_at = create_at;
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
