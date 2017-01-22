package com.knoxpo.stackyandroid.activities;

import android.support.v4.app.Fragment;

import com.knoxpo.stackyandroid.fragments.DetailFragment;

/**
 * Created by khushboo on 22/1/17.
 */

public class DetailActivity extends ToolbarActivity {
    @Override
    protected Fragment getFragment() {
        return new DetailFragment();
    }
}
