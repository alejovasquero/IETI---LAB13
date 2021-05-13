package com.example.myapplication.services;

import android.content.Context;
import androidx.room.Room;
import com.example.myapplication.entity.Task;
import com.example.myapplication.network.service.TaskService;
import com.example.myapplication.persistence.AppDatabase;
import com.example.myapplication.persistence.repository.TaskDAO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;

/**
 * @author alejovasquero
 * This is just an experimental class. All observers and live features are not included...YET ;)
 */
public class TaskLiveService {

    private TaskDAO taskDAO;

    private TaskService taskService;

    private final ExecutorService executorService = Executors.newFixedThreadPool( 1 );

    public TaskLiveService(Context context){
        AppDatabase db = Room.databaseBuilder(context,
                AppDatabase.class, "database-name").build();
        taskDAO = db.taskDAO();
    }

    public void setTaskService(TaskService taskService){
        this.taskService = taskService;
    }

    public void saveAll(List<Task> tasks){
        taskDAO.insertAll(tasks);
    }

    public List<Task> getAll() {
        final List<Task> tasks = new ArrayList<Task>();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Response<List<Task>> response =
                            taskService.getTasks().execute();
                    if (!response.isSuccessful()) {
                        tasks.addAll(getAllFromDB());
                    } else {
                        System.out.println("NET");
                        tasks.addAll(response.body());
                        saveAll(tasks);
                    }
                } catch (IOException e) {
                    tasks.addAll(getAllFromDB());
                    e.printStackTrace();
                }
            }
        });
        System.out.println("RESULT " + tasks);
        return tasks;
    }

    private List<Task> getAllFromDB(){
        return taskDAO.getAll();
    }

}
