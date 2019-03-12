package com.healthmate.client.Objects;

import com.google.gson.annotations.SerializedName;

public class Response {

    @SerializedName("status")
    String status;

    @SerializedName("message")
    String message;

    @SerializedName("token")
    String token;

    public Response(String status, String message){
        this.status = status;
        this.message = message;
    }

    public Response(String status, String message, String token){
        this.status = status;
        this.message = message;
        this.token = token;
    }


}
