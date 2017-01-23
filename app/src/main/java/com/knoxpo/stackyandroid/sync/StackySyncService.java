package com.knoxpo.stackyandroid.sync;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by khushboo on 23/1/17.
 */

public class StackySyncService extends Service {

    private static final String TAG = StackySyncService.class.getSimpleName();

    private static final Object sSyncAdapterLock = new Object();
    private static StackySyncAdapter sStackySyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate - StackySyncService");
        synchronized (sSyncAdapterLock) {
            if (sStackySyncAdapter == null) {
                sStackySyncAdapter = new StackySyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sStackySyncAdapter.getSyncAdapterBinder();
    }
}