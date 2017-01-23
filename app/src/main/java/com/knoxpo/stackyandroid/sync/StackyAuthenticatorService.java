package com.knoxpo.stackyandroid.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by khushboo on 23/1/17.
 */

public class StackyAuthenticatorService extends Service {

    // Instance field that stores the authenticator object
    private StackyAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new StackyAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }

}
