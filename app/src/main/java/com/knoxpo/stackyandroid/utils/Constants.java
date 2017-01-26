package com.knoxpo.stackyandroid.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by khushboo on 21/1/17.
 */

public class Constants {

    public static class Api {

        private static final String BASE_URL = "https://api.stackexchange.com/2.2";

        private static final String
                PARAM_SITE = "site",
                VALUE_SITE = "stackoverflow",
                PARAM_KEY = "key",
                VALUE_KEY = "INSERT_API_KEY_HERE",
                PARAM_SORT = "sort",
                VALUE_SORT = "activity",
                PARAM_MIN = "min",
                PATH_QUESTIONS = "questions",
                PATH_ANSWERS="answers";

        private static final Uri BASE_URI
                = Uri
                .parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter(PARAM_SITE, VALUE_SITE)
                .appendQueryParameter(PARAM_KEY, VALUE_KEY)
                .appendQueryParameter(PARAM_SORT,VALUE_SORT)
                .build();

        public static String getQuestionUrl(String question) {
            return BASE_URI.buildUpon()
                    .appendPath(PATH_QUESTIONS)
                    .appendPath(question)
                    .build()
                    .toString();
        }

        public static String getAnswerUrl(String question){
            return BASE_URI
                    .buildUpon()
                    .appendPath(PATH_QUESTIONS)
                    .appendPath(question)
                    .appendPath(PATH_ANSWERS)
                    .build()
                    .toString();
        }

        public static String getAnswerUrl(ArrayList<Long> questionIds, long minMillis){

            String delimitedQuestionIds = TextUtils.join(";",questionIds);

            return BASE_URI
                    .buildUpon()
                    .appendPath(PATH_QUESTIONS)
                    .appendPath(delimitedQuestionIds)
                    .appendPath(PATH_ANSWERS)
                    .appendQueryParameter(PARAM_MIN,String.valueOf(minMillis))
                    .build()
                    .toString();
        }
    }

    private static final String
            BASE_ANSWER_URL = "http://stackoverflow.com",
            PATH_ANSWER = "a",
            PATH_QUESTON = "q";
    public static Uri getAnswerUri(long id){
        return Uri
                .parse(BASE_ANSWER_URL)
                .buildUpon()
                .appendPath(PATH_ANSWER)
                .appendPath(String.valueOf(id))
                .build();
    }


    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

    }

    public static class SP{
        public static final String LAST_SYNC_TIME = "last_sync_time";
    }

    /**
     * Replace strings here
     */
    public static class Ads{
        public static final String
                AD_APP_ID = "ADD_APP_ID_HERE",
                TEST_DEVICE_ID = "TEST_DEVICE_ID_HERE";
    }
}