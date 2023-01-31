package com.rocket.quizzy.service;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class RocketQuizzy extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
