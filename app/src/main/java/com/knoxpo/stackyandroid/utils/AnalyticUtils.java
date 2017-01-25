package com.knoxpo.stackyandroid.utils;

import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by Tejas Sherdiwala on 25/01/17.
 */

public class AnalyticUtils {

    private static FirebaseAnalytics sFirebaseAnalytics;

    public static final String
            EVENT_ADD_QUESTION_PROMPT = "add question prompt",
            EVENT_ADDED_QUESTION = "added question",
            EVENT_DISMISSED_ADDITION = "dismissed addition",
            EVENT_DETAIL = "details view";

    public static FirebaseAnalytics getInstance(Context context) {
        if(sFirebaseAnalytics == null){
            sFirebaseAnalytics = FirebaseAnalytics.getInstance(context.getApplicationContext());
        }
        return sFirebaseAnalytics;
    }
}
