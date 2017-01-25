package com.knoxpo.stackyandroid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.knoxpo.stackyandroid.R;
import com.knoxpo.stackyandroid.fragments.DetailFragment;
import com.knoxpo.stackyandroid.fragments.MainFragment;
import com.knoxpo.stackyandroid.sync.StackySyncAdapter;
import com.knoxpo.stackyandroid.utils.AnalyticUtils;
import com.knoxpo.stackyandroid.utils.Constants;

/**
 * Created by knoxpo on 15/01/17.
 */

public class MainActivity extends ToolbarActivity implements MainFragment.Callback{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);

        StackySyncAdapter.initializeSyncAdapter(this);

        MobileAds.initialize(getApplicationContext(), Constants.Ads.AD_APP_ID);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(Constants.Ads.TEST_DEVICE_ID) .build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected Fragment getFragment() {
        return new MainFragment();
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_main;
    }

    @Override
    public void onQuestionClicked(long questionId) {

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, AnalyticUtils.EVENT_DETAIL);
        AnalyticUtils.getInstance(this).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

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
