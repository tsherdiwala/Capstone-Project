package com.knoxpo.stackyandroid.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.knoxpo.stackyandroid.R;

/**
 * Created by knoxpo on 15/01/17.
 * © KNOXPO
 */

public abstract class SingleFragmentActivity extends AppCompatActivity {

    protected abstract Fragment getFragment();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentResId());

        FragmentManager fm = getSupportFragmentManager();
        Fragment existingFragment = fm.findFragmentById(getFragmentContainerId());
        if(existingFragment==null){
            fm.beginTransaction().replace(getFragmentContainerId(),getFragment()).commit();
        }

    }

    protected int getContentResId(){
        return R.layout.activity_single_fragment;
    }

    protected int getFragmentContainerId(){
        return R.id.fragment_container;
    }
}
