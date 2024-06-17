package com.example.ifix;

import android.app.Application;

import androidx.arch.core.executor.ArchTaskExecutor;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class ifix extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // Set up other global configurations or initializations
        // ...
    }
}
