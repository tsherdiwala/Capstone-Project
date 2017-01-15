package com.knoxpo.stackyandroid.activities;

import com.knoxpo.stackyandroid.R;

/**
 * Created by knoxpo on 15/01/17.
 * © KNOXPO
 */

public abstract class ToolbarActivity extends SingleFragmentActivity {

    @Override
    protected int getContentResId() {
        return R.layout.activity_toolbar;
    }
}
