package com.mihealth.notifyxp;

import android.app.Application;
import android.util.Log;

public class App extends Application {
    private static final String TAG = "MiHealthNotifyXP";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Xposed模块应用已启动");
    }
}
