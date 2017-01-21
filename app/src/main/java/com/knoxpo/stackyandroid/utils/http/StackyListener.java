package com.knoxpo.stackyandroid.utils.http;

import com.android.volley.Response;

import org.json.JSONObject;

/**
 * Created by khushboo on 21/1/17.
 */

public interface StackyListener {
    void onResponse(JSONObject response,int request);
}
