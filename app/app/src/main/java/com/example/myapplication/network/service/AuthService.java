package com.example.myapplication.network.service;


import com.example.myapplication.entity.LoginWrapper;
import com.example.myapplication.entity.Token;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {


    @POST("auth")
    public Call<Token> createToken(@Body LoginWrapper wrapper);

}
