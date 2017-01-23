package com.knoxpo.stackyandroid.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.knoxpo.stackyandroid.fragments.DetailFragment;

/**
 * Created by khushboo on 22/1/17.
 */

public class DetailActivity extends ToolbarActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    public static final String
            EXTRA_QUESTION_ID = TAG + ".EXTRA_QUESTION_ID";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected Fragment getFragment() {
        long questionId = getIntent().getLongExtra(EXTRA_QUESTION_ID,-1);
        if(questionId == -1){
            throw new IllegalArgumentException(TAG + " should have a questionId");
        }
        return DetailFragment.newInstance(questionId);
    }
}
