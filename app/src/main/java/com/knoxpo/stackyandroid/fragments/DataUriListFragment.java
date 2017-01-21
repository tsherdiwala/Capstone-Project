package com.knoxpo.stackyandroid.fragments;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.knoxpo.stackyandroid.R;
import com.knoxpo.stackyandroid.adapter.CursorRecyclerViewAdapter;

/**
 * Created by knoxpo on 15/01/17.
 * © KNOXPO
 */

public abstract class DataUriListFragment<VH extends RecyclerView.ViewHolder>
        extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String
            TAG = DataUriListFragment.class.getSimpleName(),
            ARGS_SELECTED_LIST = TAG + ".ARGS_SELECTED_LIST";

    private static final int DEFAULT_LOADER_ID = 0;
    private RecyclerView mListRV;

    private Adapter mAdapter;
    private TextView mEmptyTV;

    public abstract Uri getUri();
    public abstract  void onBindView(VH holder, Cursor cursor);
    public abstract int getItemLayoutId();
    public abstract VH onCreateViewHolder(View v);


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(getLayoutId(),container,false);
        init(v);
        mListRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListRV.setAdapter(mAdapter);

        getLoaderManager().initLoader(getLoaderId(),null,this);
        updateUI();
        return v;
    }


    protected int getLoaderId(){
        return DEFAULT_LOADER_ID;
    }

    private void init(View v){
        mListRV = (RecyclerView)v.findViewById(R.id.rv_items);
        mAdapter = new Adapter(getActivity(),null);
        mEmptyTV = (TextView) v.findViewById(R.id.tv_empty);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                getUri(),
                getProjection(),
                null,
                null,
                getSortOrder()
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        updateUI();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }



    public final int getItemCount(){
        return mAdapter.getItemCount();
    }

    protected int getMessage(){
        return R.string.no_data;
    }

    protected int getLayoutId(){
        return R.layout.fragment_data_uri_list;
    }

    protected String[] getProjection(){
        return null;
    }

    protected String getSortOrder(){
        return null;
    }

    private void updateUI(){
        mListRV.setVisibility(getItemCount() == 0? View.GONE:View.VISIBLE);

        if(mEmptyTV !=null){
            mEmptyTV.setText(getMessage());
            mEmptyTV.setVisibility(getItemCount() == 0?View.VISIBLE:View.GONE);
        }
    }

    private class Adapter extends CursorRecyclerViewAdapter<VH> {

        private LayoutInflater mLayoutInflater;

        public Adapter(Context context, Cursor cursor) {
            super(context, cursor);
            mLayoutInflater = LayoutInflater.from(getActivity());
        }

        @Override
        public void onBindView(VH holder, Cursor cursor) {
            DataUriListFragment.this.onBindView(holder,cursor);
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = mLayoutInflater.inflate(getItemLayoutId(),parent,false);
            return DataUriListFragment.this.onCreateViewHolder(v);
        }
    }
}
