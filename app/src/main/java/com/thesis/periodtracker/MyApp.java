package com.thesis.periodtracker;

import com.facebook.stetho.Stetho;
import android.app.Application;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}