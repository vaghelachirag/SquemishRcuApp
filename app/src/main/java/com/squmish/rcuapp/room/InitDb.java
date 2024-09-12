package com.squmish.rcuapp.room;

import android.app.Application;

import com.google.firebase.FirebaseApp;


public class InitDb extends Application {
    public static AppDatabase appDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        appDatabase = AppDatabase.getDatabase(this);
    }

}
