package com.trinity.wordsrcite.wordsrcite;

import android.app.Application;
import android.content.Context;

import java.lang.ref.WeakReference;

/**
 * Created by foryoung on 2018/4/29.
 */

public class MyApplication extends Application {

    private static WeakReference<MyApplication> msApp;

    @Override
    public void onCreate() {
        super.onCreate();
        msApp = new WeakReference<MyApplication>(this);
    }

    public static Context getGlobalContext() {
        MyApplication app = msApp.get();
        if (app != null) {
            return app.getApplicationContext();
        }

        return null;
    }
}
