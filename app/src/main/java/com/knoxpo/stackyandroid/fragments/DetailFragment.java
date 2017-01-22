package com.knoxpo.stackyandroid.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.knoxpo.stackyandroid.R;

/**
 * Created by khushboo on 22/1/17.
 */
public class DetailFragment extends DataUriListFragment<DetailFragment.AnswerVH> {

    private TextView
            mTitleTV,
            mScoreTV,
            mDisplayNameTV,
            mCreationDateTV,
            mReputationTV,
            mAnsweredTV;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        return v;
    }

    private void init(View v) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_detail;
    }

    @Override
    public Uri getUri() {
        return null;
    }

    @Override
    public void onBindView(AnswerVH holder, Cursor cursor) {

    }

    @Override
    public int getItemLayoutId() {
        return 0;
    }

    @Override
    public AnswerVH onCreateViewHolder(View v) {
        return null;
    }

    public class AnswerVH extends RecyclerView.ViewHolder {

        public AnswerVH(View itemView) {
            super(itemView);
        }
    }
}
