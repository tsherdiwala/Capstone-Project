package com.knoxpo.stackyandroid.utils.http;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Created by khushboo on 21/1/17.
 */

public interface StackyErrorListener{
        void onErrorResponse(VolleyError error, int requestCode);
}
