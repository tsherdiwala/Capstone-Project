package com.knoxpo.stackyandroid.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.provider.BaseColumns;
import android.support.v7.widget.RecyclerView;

/**
 * Created by knoxpo on 15/01/17.
 * © KNOXPO
 */

public abstract class CursorRecyclerViewAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH>{
    private Context mContext;
    private Cursor mCursor;
    private boolean mDataValid;
    private int mRowColumnId;
    private DataSetObserver mDataSetObserver;

    public CursorRecyclerViewAdapter(Context context,Cursor cursor){
        mContext = context;
        mCursor = cursor;
        mDataValid = cursor != null;
        mRowColumnId = mDataValid ? mCursor.getColumnIndex(BaseColumns._ID):-1;
        mDataSetObserver = new NotifyingDatasetObserver();
        if(mCursor !=null){
            mCursor.registerDataSetObserver(mDataSetObserver);
        }
    }

    public Cursor getCursor() {
        return mCursor;
    }

    @Override
    public int getItemCount() {

        if(mDataValid && mCursor != null){
            return  mCursor.getCount();
        }
        return 0;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        if(mDataValid && mCursor != null && mCursor.moveToPosition(position)){
            return mCursor.getLong(mRowColumnId);
        }
        return 0;
    }

    public abstract void onBindView(VH holder ,Cursor cursor);

    @Override
    public void onBindViewHolder(VH holder, int position) {
        if(!mDataValid){
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }

        if(!mCursor.moveToPosition(position)){
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }

        onBindView(holder,mCursor);
    }

    public void changeCursor(Cursor cursor){
        Cursor oldCursor = swapCursor(cursor);
        if(oldCursor != null){
            oldCursor.close();
        }
    }

    public Cursor swapCursor(Cursor newCursor){
        if(mCursor == newCursor){
            return null;
        }

        Cursor oldCursor = mCursor;
        if(oldCursor != null && mDataSetObserver != null){
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }

        mCursor = newCursor;
        if(mCursor != null) {
            if (mDataSetObserver != null) {
                mCursor.registerDataSetObserver(mDataSetObserver);
            }
            mRowColumnId = mCursor.getColumnIndexOrThrow(BaseColumns._ID);
            mDataValid = true;
            notifyDataSetChanged();
        }
        return oldCursor;
    }

    private class NotifyingDatasetObserver extends DataSetObserver {

        @Override
        public void onChanged() {
            super.onChanged();
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            mDataValid = false;
            notifyDataSetChanged();
        }
    }
}