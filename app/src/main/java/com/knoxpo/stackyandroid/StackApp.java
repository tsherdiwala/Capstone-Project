package com.knoxpo.stackyandroid;

import android.app.Application;

import com.knoxpo.stackyandroid.utils.AnalyticUtils;

/**
 * Created by Tejas Sherdiwala on 25/01/17.
 */

public class StackApp extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        AnalyticUtils.getInstance(this);
    }
}
