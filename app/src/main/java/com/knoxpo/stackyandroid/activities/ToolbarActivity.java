package com.knoxpo.stackyandroid.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.knoxpo.stackyandroid.R;

/**
 * Created by knoxpo on 15/01/17.
 * © KNOXPO
 */

public abstract class ToolbarActivity extends SingleFragmentActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        setSupportActionBar(mToolbar);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_toolbar;
    }

    private void init(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }
}
