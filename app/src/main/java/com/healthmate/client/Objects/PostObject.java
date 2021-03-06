package com.healthmate.client.Objects;

public class PostObject {
    private String description;
    private String image_url;
    private  String create_at;
    private String user_id;
    private String username;
    private String likes;
    private String post_id;
    private String profile_pic;
    private  Boolean is_liked;

    public PostObject(String description, String image_url, String create_at,
                      String user_id, String username, String likes, String post_id, String profile_pic, Boolean is_liked){
        this.description = description;
        this.image_url = image_url;
        this.create_at = create_at;
        this.user_id = user_id;
        this.username = username;
        this.likes = likes;
        this.post_id = post_id;
        this.profile_pic = profile_pic;
        this.is_liked = is_liked;
    }

    public Boolean getIs_liked() {
        return is_liked;
    }

    public void setIs_liked(Boolean is_liked) {
        this.is_liked = is_liked;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
