package com.knoxpo.stackyandroid.fragments;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.knoxpo.stackyandroid.R;
import com.knoxpo.stackyandroid.data.StackyContract;
import com.knoxpo.stackyandroid.data.StackyContract.UserEntry;
import com.knoxpo.stackyandroid.data.StackyContract.QuestionEntry;
import com.knoxpo.stackyandroid.data.StackyProvider;
import com.knoxpo.stackyandroid.dialogs.InputDialogFragment;
import com.knoxpo.stackyandroid.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by knoxpo on 15/01/17.
 * © KNOXPO
 */
public class MainFragment extends DataUriListFragment<MainFragment.QuestionVH> implements Response.ErrorListener, Response.Listener<JSONObject> {

    private static final String
            TAG = MainFragment.class.getSimpleName(),
            TAG_INPUT_DIALOG = TAG + ".TAG_INPUT_DIALOG";


    private static final int
            INDEX_ID = 0,
            INDEX_TITLE = 1,
            INDEX_SCORE = 2,
            INDEX_ANSWER_COUNT = 3,
            INDEX_START_DATE = 4;

    private static final int
            REQUEST_QUESTION_ID = 0;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_add) {

            InputDialogFragment fragment = InputDialogFragment.newInstance(R.string.hint_question_id, InputType.TYPE_CLASS_NUMBER);
            fragment.setTargetFragment(this, REQUEST_QUESTION_ID);
            fragment.show(getFragmentManager(), TAG_INPUT_DIALOG);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_QUESTION_ID && resultCode == Activity.RESULT_OK) {
            String questionId = data.getStringExtra(InputDialogFragment.EXTRA_INPUT);
            getQuestionDetails(questionId);
            //fetch details of the question here
        }
    }

    private void getQuestionDetails(String questionId) {
        String path = Uri.parse(Constants.Api.QUESTIONS)
                .buildUpon()
                .appendPath(questionId)
                .build()
                .toString();

        JsonObjectRequest request = new JsonObjectRequest(
                path,
                null,
                this,
                this
        );

        Log.d(TAG, "Path: " + path);
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    private static final String
            JSON_N_QUESTION_ID = "question_id",
            JSON_S_TITLE = "title",
            JSON_N_SCORE = "score",
            JSON_N_CREATION_DATE = "creation_date",
            JSON_N_LAST_ACTIVITY_DATE = "last_activity_date",
            JSON_O_OWNER = "owner",
            JSON_N_REPUTATION = "reputation",
            JSON_N_USER_ID = "user_id",
            JSON_S_DISPLAY_NAME = "display_name",
            JSON_S_LINK = "link",
            JSON_S_PROFILE_IMAGE = "profile_image";


    @Override
    public void onResponse(JSONObject response) {

        try {

            JSONObject userObject = response.getJSONObject(JSON_O_OWNER);

            ContentValues cv = new ContentValues();
            cv.put(UserEntry._ID, userObject.getLong(JSON_N_USER_ID));
            cv.put(UserEntry.COLUMN_DISPLAY_NAME, userObject.getString(JSON_S_DISPLAY_NAME));
            cv.put(UserEntry.COLUMN_LINK, userObject.getString(JSON_S_LINK));
            cv.put(UserEntry.COLUMN_REPUTATION, userObject.getInt(JSON_N_REPUTATION));
            cv.put(UserEntry.COLUMN_PROFILE_IMAGE, userObject.getString(JSON_S_PROFILE_IMAGE));

            ContentProviderOperation insertOwner =
                    ContentProviderOperation
                            .newInsert(UserEntry.CONTENT_URI)
                            .withValues(cv)
                            .build();

            cv = new ContentValues();
            cv.put(QuestionEntry._ID, response.getLong(JSON_N_QUESTION_ID));
            cv.put(QuestionEntry.COLUMN_TITLE, response.getString(JSON_S_TITLE));
            cv.put(QuestionEntry.COLUMN_CREATION_DATE, response.getLong(JSON_N_CREATION_DATE));
            cv.put(QuestionEntry.COLUMN_LAST_ACTIVITY_DATE, response.getLong(JSON_N_LAST_ACTIVITY_DATE));
            cv.put(QuestionEntry.COLUMN_OWNER_ID, userObject.getLong(JSON_N_USER_ID));
            cv.put(QuestionEntry.COLUMN_LINK, response.getString(JSON_S_LINK));
            cv.put(QuestionEntry.COLUMN_SCORE, response.getInt(JSON_N_SCORE));
            cv.put(QuestionEntry.COLUMN_START_DATE, new Date().getTime());

            ContentProviderOperation insertQuestion =
                    ContentProviderOperation
                            .newInsert(QuestionEntry.CONTENT_URI)
                            .withValues(cv)
                            .build();

            ArrayList<ContentProviderOperation> operations = new ArrayList<>();
            operations.add(insertOwner);
            operations.add(insertQuestion);

            ContentProviderResult[] results = getActivity()
                    .getContentResolver()
                    .applyBatch(StackyContract.CONTENT_AUTHORITY, operations);

            if(results[0].uri == null){
                Log.d(TAG, "Error in inserting user");
            }


        } catch (JSONException e) {

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
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
                            answers == 1 ? R.string.answer_count_singular : R.string.answer_count_plural,
                            answers
                    )
            );

            int votes = cursor.getInt(INDEX_SCORE);
            mVotesTV.setText(
                    getString(
                            votes == 1 ? R.string.vote_count_singular : R.string.vote_count_plural,
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
