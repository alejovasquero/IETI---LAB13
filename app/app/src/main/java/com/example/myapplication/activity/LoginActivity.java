package com.example.myapplication.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.network.service.AuthService;
import com.example.myapplication.R;
import com.example.myapplication.entity.LoginWrapper;
import com.example.myapplication.entity.Token;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
    }

    private final ExecutorService executorService = Executors.newFixedThreadPool( 1 );


    public void login(View view){
         EditText userText = (EditText) findViewById(R.id.loginUserName);
         EditText passwordText = (EditText) findViewById(R.id.loginPassword);
         String username = userText.getText().toString();
         String password = passwordText.getText().toString();
         System.out.println(username);
         System.out.println(password);
         boolean correct = true;
         if( password.length() == 0){
             userText.setError("Las contraseña no puede ser vacía >:v");
             correct = false;
         }

        if(username.length() ==0){
            userText.setError("El nombre de usuario no puede ser vacío >:v");
            correct = false;
        }

        if(correct){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://localhost:8080") //localhost for emulator
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            System.out.println(2);
            AuthService authService = retrofit.create(AuthService.class);
            LoginWrapper wrapper = new LoginWrapper();
            wrapper.setEmail(username);
            wrapper.setPassword(password);
            makeRequest(view, wrapper);
        }
    }

    private void makeRequest(View view, LoginWrapper wrapper){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.14:8080") //localhost for emulator
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AuthService authService = retrofit.create(AuthService.class);
        executorService.execute( new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Response<Token> response =
                            authService.createToken( wrapper ).execute();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (response.isSuccessful()) {
                                Token token = response.body();
                                System.out.println(token.getAccessToken());
                                SharedPreferences sharedPref =
                                        getSharedPreferences( getString( R.string.preference_file_key ), Context.MODE_PRIVATE );
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("TOKEN_KEY", token.getAccessToken());
                                editor.apply();
                            } else {
                                EditText userText = (EditText) findViewById(R.id.loginUserName);
                                EditText passwordText = (EditText) findViewById(R.id.loginPassword);
                                userText.setError("CREDENCIALES INVALIDAS");
                                passwordText.setError("CREDENCIALES INVALIDAS");
                            }

                        }
                    });
                }
                catch ( IOException e )
                {
                    e.printStackTrace();
                }
            }
        } );
    }
}
