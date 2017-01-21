package com.knoxpo.stackyandroid.utils.http;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by khushboo on 21/1/17.
 */

public class StackyRequest extends JsonObjectRequest {


    private StackyListener mListener;
    private StackyErrorListener mErrorListener;
    private int mRequest;

    public StackyRequest(int method, String url, int request, StackyListener listener, StackyErrorListener errorListener) {
        super(method, url, null, null, null);
        mListener = listener;
        mErrorListener = errorListener;
        mRequest = request;

    }

    public StackyRequest(String url, int request, StackyListener listener, StackyErrorListener errorListener) {
        super(url, null, null, null);
        mListener = listener;
        mErrorListener = errorListener;
        mRequest = request;
    }


    @Override
    public void deliverError(VolleyError error) {
        mErrorListener.onErrorResponse(error, mRequest);
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        mListener.onResponse(response, mRequest);
    }
}
