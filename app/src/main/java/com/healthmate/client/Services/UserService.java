package com.healthmate.client.Services;

import com.healthmate.client.Objects.Response;
import com.healthmate.client.Objects.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UserService {
    @POST("auth/register")
    Call<User> register(@Body User user);

    @POST("auth/login")
    Call<User> get(@Body User user);
}
