package com.example.myapplication.network.service;

import com.example.myapplication.entity.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TaskService {

    @GET("api/task")
    public Call<List<Task>> getTasks();

}
