package com.knoxpo.stackyandroid.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.knoxpo.stackyandroid.R;
import com.knoxpo.stackyandroid.data.StackyContract;
import com.knoxpo.stackyandroid.utils.VolleyHelper;

/**
 * Created by khushboo on 22/1/17.
 */
public class DetailFragment extends DataUriListFragment<DetailFragment.AnswerVH> {

    private static final String
            TAG = DetailFragment.class.getSimpleName(),
            ARGS_QUESTION_ID = TAG + ".ARGS_QUESTION_ID";

    private static final int
            LOADER_QUESTION_ID = 0,
            LOADER_ANSWERS_ID = 1;

    private static final int
            INDEX_ANSWER_ID = 0,
            INDEX_ANSWER_SCORE = 1,
            INDEX_QUESTION_ID = 3,
            INDEX_IS_ACCEPTED = 4,
            INDEX_ANSWER_CREATION_DATE = 5,
            INDEX_USER_DISPLAY_NAME = 6,
            INDEX_USER_PROFILE_IMAGE = 7,
            INDEX_USER_REPUTATION = 8;

    private static final int
            INDEX_QUESTION_TITLE = 0,
            INDEX_QUESTION_SCORE = 1,
            INDEX_QUESTION_CREATION_DATE = 2,
            INDEX_IS_QUESTION_ANSWERED = 3,
            INDEX_QUESTION_USER_DISPLAY_NAME = 4,
            INDEX_QUESTION_USER_PROFILE_IMAGE = 5,
            INDEX_QUESTION_USER_REPUTATION = 6;


    public static DetailFragment newInstance(long questionId) {
        Bundle args = new Bundle();
        args.putLong(ARGS_QUESTION_ID, questionId);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private long mQuestionId;

    private TextView
            mTitleTV,
            mScoreTV,
            mDisplayNameTV,
            mCreationDateTV,
            mReputationTV,
            mAnsweredTV;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        mQuestionId = arguments.getLong(ARGS_QUESTION_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        init(v);


        return v;
    }

    private void init(View v) {
        mTitleTV = (TextView) v.findViewById(R.id.tv_title);
        mScoreTV = (TextView) v.findViewById(R.id.tv_score);
        mDisplayNameTV = (TextView) v.findViewById(R.id.tv_display_name);
        mCreationDateTV = (TextView) v.findViewById(R.id.tv_creation_date);
        mReputationTV = (TextView) v.findViewById(R.id.tv_reputation);
        mAnsweredTV = (TextView) v.findViewById(R.id.tv_answered);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_detail;
    }

    @Override
    public Uri getUri() {
        return StackyContract.AnswerEntry.buildAnswersOfQuestionUri(mQuestionId);
    }

    @Override
    public void onBindView(AnswerVH holder, Cursor cursor) {

    }

    @Override
    public int getItemLayoutId() {
        return R.layout.item_answer;
    }

    @Override
    public AnswerVH onCreateViewHolder(View v) {
        return null;
    }

    @Override
    protected int getLoaderId() {
        return LOADER_ANSWERS_ID;
    }

    @Override
    protected String[] getProjection() {
        return new String[]{
                StackyContract.AnswerEntry._ID,
                StackyContract.AnswerEntry.COLUMN_SCORE,
                StackyContract.AnswerEntry.COLUMN_QUESTION_ID,
                StackyContract.AnswerEntry.COLUMN_IS_ACCEPTED,
                StackyContract.AnswerEntry.COLUMN_CREATION_DATE,
                StackyContract.UserEntry.COLUMN_DISPLAY_NAME,
                StackyContract.UserEntry.COLUMN_PROFILE_IMAGE,
                StackyContract.UserEntry.COLUMN_REPUTATION
        };
    }

    @Override
    protected String getSortOrder() {
        return StackyContract.AnswerEntry.COLUMN_CREATION_DATE + " DESC";
    }

    private String[] getQuestionProjection() {
        return new String[]{
                StackyContract.QuestionEntry.COLUMN_TITLE,
                StackyContract.QuestionEntry.COLUMN_SCORE,
                StackyContract.QuestionEntry.COLUMN_CREATION_DATE,
                StackyContract.QuestionEntry.COLUMN_IS_QUESTION_ANSWERED,
                StackyContract.UserEntry.COLUMN_DISPLAY_NAME,
                StackyContract.UserEntry.COLUMN_PROFILE_IMAGE,
                StackyContract.UserEntry.COLUMN_REPUTATION
        };
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ANSWERS_ID) {
            return super.onCreateLoader(id, args);
        } else if (id == LOADER_QUESTION_ID) {
            return new CursorLoader(
                    getActivity(),
                    StackyContract.QuestionEntry.buildQuestionUri(mQuestionId),
                    getQuestionProjection(),
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int loaderId = loader.getId();
        if (loaderId == LOADER_ANSWERS_ID) {
            super.onLoadFinished(loader, data);
        } else if (loaderId == LOADER_QUESTION_ID) {
            updateUI(data);
        }
    }

    private void updateUI(Cursor data) {
        data.moveToFirst();
        mTitleTV.setText(data.getString(INDEX_QUESTION_TITLE));
        mScoreTV.setText(String.valueOf(data.getInt(INDEX_QUESTION_SCORE)));

        mDisplayNameTV.setText(String.valueOf(data.getInt(INDEX_QUESTION_USER_DISPLAY_NAME)));
        mCreationDateTV.setText(String.valueOf(data.getLong(INDEX_QUESTION_CREATION_DATE)));
        mReputationTV.setText(String.valueOf(data.getInt(INDEX_QUESTION_USER_REPUTATION)));

        mAnsweredTV.setVisibility(
                data.getInt(INDEX_IS_QUESTION_ANSWERED) > 0
                        ? View.VISIBLE
                        : View.INVISIBLE
        );
    }

    public class AnswerVH extends RecyclerView.ViewHolder {


        private NetworkImageView mProfileIV;

        private TextView
                mReputationTV,
                mDisplayNameTV,
                mCreationDateTV,
                mScoreIV;

        private ImageView mIsAnsweredIV;


        public AnswerVH(View itemView) {
            super(itemView);
            mProfileIV = (NetworkImageView)itemView.findViewById(R.id.iv_profile);
            mReputationTV = (TextView)itemView.findViewById(R.id.tv_reputation);
            mDisplayNameTV = (TextView)itemView.findViewById(R.id.tv_display_name);
            mCreationDateTV = (TextView)itemView.findViewById(R.id.tv_creation_date);
            mScoreIV = (TextView)itemView.findViewById(R.id.tv_score);
            //mIsAnsweredIV = (ImageView)itemView.findViewById(R.id.tv_answered);
        }

        public void bind(Cursor cursor) {
            mProfileIV.setDefaultImageResId(R.drawable.ic_profile_placeholder);
            mProfileIV.setErrorImageResId(R.drawable.ic_profile_placeholder);
            mProfileIV.setImageUrl(
                    cursor.getString(INDEX_USER_PROFILE_IMAGE),
                    VolleyHelper.getInstance(getActivity()).getImageLoader()
            );

            mReputationTV.setText(
                    String.valueOf(cursor.getInt(INDEX_USER_REPUTATION))
            );

            mDisplayNameTV.setText(
                    cursor.getString(INDEX_USER_DISPLAY_NAME)
            );


            /*mScoreTV.setText(
                    String.valueOf()
            );*/
        }
    }

}