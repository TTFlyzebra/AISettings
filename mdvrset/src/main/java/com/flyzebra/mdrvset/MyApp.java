package com.flyzebra.mdrvset;

import android.app.Application;

import com.flyzebra.core.Fzebra;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fzebra.get().init();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }
}
