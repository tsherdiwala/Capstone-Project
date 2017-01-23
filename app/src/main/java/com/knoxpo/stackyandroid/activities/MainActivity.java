package com.knoxpo.stackyandroid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.knoxpo.stackyandroid.R;
import com.knoxpo.stackyandroid.fragments.DetailFragment;
import com.knoxpo.stackyandroid.fragments.MainFragment;

/**
 * Created by knoxpo on 15/01/17.
 */

public class MainActivity extends ToolbarActivity implements MainFragment.Callback{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
    }

    @Override
    protected Fragment getFragment() {
        return new MainFragment();
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onQuestionClicked(long questionId) {
        if(findViewById(R.id.detail_fragment_container) == null){
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_QUESTION_ID,questionId);
            startActivity(intent);
        }else{
            Fragment fragment = DetailFragment.newInstance(questionId);
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.detail_fragment_container,fragment).commit();
        }
    }
}
