package com.knoxpo.stackyandroid.activities;

import android.support.v4.app.Fragment;

import com.knoxpo.stackyandroid.fragments.DetailFragment;

/**
 * Created by khushboo on 22/1/17.
 */

public class DetailActivity extends ToolbarActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    public static final String
            EXTRA_QUESTION_ID = TAG + ".EXTRA_QUESTION_ID";

    @Override
    protected Fragment getFragment() {
        long questionId = getIntent().getLongExtra(EXTRA_QUESTION_ID,-1);
        if(questionId == -1){
            throw new IllegalArgumentException(TAG + " should have a questionId");
        }
        return new DetailFragment();
    }
}
