package com.knoxpo.stackyandroid.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.knoxpo.stackyandroid.R;
import com.knoxpo.stackyandroid.fragments.MainFragment;

/**
 * Created by knoxpo on 15/01/17.
 */

public class MainActivity extends ToolbarActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
    }

    @Override
    protected Fragment getFragment() {
        return new MainFragment();
    }
}
