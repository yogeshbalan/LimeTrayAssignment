package com.test.yogesh.limetrayassignment;

import android.app.Application;
import android.content.Context;

/**
 * Created by yogesh on 31/5/16.
 */
public class ApplicationWrapper extends Application {

    private static ApplicationWrapper applicationWrapper;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationWrapper = this;
    }

    public static ApplicationWrapper getApplicationWrapper() {
        return applicationWrapper;
    }

    public static Context getAppContext() {
        return applicationWrapper.getApplicationContext();
    }
}
