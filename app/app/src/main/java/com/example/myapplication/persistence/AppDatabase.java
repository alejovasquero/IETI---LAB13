package com.example.myapplication.persistence;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.myapplication.entity.Task;
import com.example.myapplication.persistence.repository.TaskDAO;

@Database(entities = {Task.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TaskDAO taskDAO();
}

