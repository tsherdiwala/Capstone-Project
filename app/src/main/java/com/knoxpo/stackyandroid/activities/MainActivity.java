package com.knoxpo.stackyandroid.activities;

import android.support.v4.app.Fragment;

import com.knoxpo.stackyandroid.fragments.MainFragment;

/**
 * Created by knoxpo on 15/01/17.
 */

public class MainActivity extends ToolbarActivity {
    @Override
    protected Fragment getFragment() {
        return new MainFragment();
    }
}
