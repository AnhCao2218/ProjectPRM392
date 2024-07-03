package com.appmanager.myproject.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.appmanager.myproject.R;

import io.paperdb.Paper;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Paper.init(this);
        Thread thread= new Thread(){
            public void run(){
                try{
                    sleep(2000);
                }catch (Exception e){

                }finally {
                    if(Paper.book().read("User")==null){
                        Intent intent= new Intent(getApplicationContext(), DangNhapActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Intent home= new Intent(getApplicationContext(), MainActivity2.class);
                        startActivity(home);
                        finish();
                    }

                }
            }
        };
        thread.start();
    }

}