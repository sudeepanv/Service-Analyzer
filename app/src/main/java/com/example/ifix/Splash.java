package com.example.ifix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check if the app is being launched for the first time
        if (!isTaskRoot()) {
            // If this is not the root task, finish this activity
            finish();
            return;
        }

        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i=new Intent(Splash.this,AuthActivity.class);
                startActivity(i);
                finish();
            }
        },200);
    }
}