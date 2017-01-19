package com.knoxpo.stackyandroid.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

import com.knoxpo.stackyandroid.R;
import com.knoxpo.stackyandroid.data.StackyContract.QuestionEntry;

/**
 * Created by knoxpo on 15/01/17.
 * © KNOXPO
 */
public class MainFragment extends DataUriListFragment<MainFragment.QuestionVH> {

    private static final int
            INDEX_ID = 0,
            INDEX_TITLE = 1,
            INDEX_SCORE = 2,
            INDEX_ANSWER_COUNT = 3,
            INDEX_START_DATE = 4;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main,menu);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public Uri getUri() {
        return QuestionEntry.CONTENT_URI;
    }

    @Override
    public void onBindView(QuestionVH holder, Cursor cursor) {
        holder.bindQuestion(cursor);
    }

    @Override
    public int getItemLayoutId() {
        return R.layout.item_question;
    }

    @Override
    public QuestionVH onCreateViewHolder(View v) {
        return new QuestionVH(v);
    }

    @Override
    protected String[] getProjection() {
        return new String[]{
                QuestionEntry._ID,
                QuestionEntry.COLUMN_TITLE,
                QuestionEntry.COLUMN_SCORE,
                QuestionEntry.COLUMN_ANSWER_COUNT,
                QuestionEntry.COLUMN_START_DATE
        };
    }

    public class QuestionVH extends RecyclerView.ViewHolder {

        private TextView
                mTitleTV,
                mAnswersTV,
                mVotesTV,
                mStartDateTV;

        public QuestionVH(View itemView) {
            super(itemView);
            mTitleTV = (TextView) itemView.findViewById(R.id.tv_title);
            mAnswersTV = (TextView) itemView.findViewById(R.id.tv_answers);
            mVotesTV = (TextView) itemView.findViewById(R.id.tv_votes);
            mStartDateTV = (TextView) itemView.findViewById(R.id.tv_start_date);
        }

        public void bindQuestion(Cursor cursor) {

            mTitleTV.setText(
                    cursor.getString(INDEX_TITLE)
            );

            int answers = cursor.getInt(INDEX_ANSWER_COUNT);
            mAnswersTV.setText(
                    getString(
                            answers==1?R.string.answer_count_singular:R.string.answer_count_plural,
                            answers
                    )
            );

            int votes = cursor.getInt(INDEX_SCORE);
            mVotesTV.setText(
                    getString(
                            votes == 1? R.string.vote_count_singular:R.string.vote_count_plural,
                            votes
                    )
            );

            mStartDateTV.setText(
                    getString(
                            R.string.start_date,
                            DateUtils.formatDateTime(
                                    getActivity(),
                                    cursor.getLong(INDEX_START_DATE),
                                    DateUtils.FORMAT_ABBREV_ALL
                            )
                    )
            );

        }
    }

}
