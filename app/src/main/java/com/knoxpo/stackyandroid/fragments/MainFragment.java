package com.knoxpo.stackyandroid.fragments;

import android.app.Activity;
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

import com.android.volley.VolleyError;
import com.knoxpo.stackyandroid.R;
import com.knoxpo.stackyandroid.data.StackyContract;
import com.knoxpo.stackyandroid.data.StackyContract.QuestionEntry;
import com.knoxpo.stackyandroid.data.StackyContract.UserEntry;
import com.knoxpo.stackyandroid.dialogs.InputDialogFragment;
import com.knoxpo.stackyandroid.models.User;
import com.knoxpo.stackyandroid.utils.Constants;
import com.knoxpo.stackyandroid.utils.VolleyHelper;
import com.knoxpo.stackyandroid.utils.http.StackyErrorListener;
import com.knoxpo.stackyandroid.utils.http.StackyListener;
import com.knoxpo.stackyandroid.utils.http.StackyRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import static android.R.attr.path;

/**
 * Created by knoxpo on 15/01/17.
 * © KNOXPO
 */
public class MainFragment extends DataUriListFragment<MainFragment.QuestionVH>
        implements StackyErrorListener, StackyListener {

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
            REQUEST_QUESTION_ID = 0,
            REQUEST_QUESTION_DETAILS = 1,
            REQUEST_ANSWERS = 2;

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
    protected String getSortOrder() {
        return QuestionEntry.COLUMN_START_DATE + " DESC";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_QUESTION_ID && resultCode == Activity.RESULT_OK) {
            String questionId = data.getStringExtra(InputDialogFragment.EXTRA_INPUT);
            getQuestionDetails(questionId);
            getAnswerDetails(questionId);
        }
    }

    private void getQuestionDetails(String questionId) {

        StackyRequest request = new StackyRequest(
                Constants.Api.getQuestionUrl(questionId),
                REQUEST_QUESTION_DETAILS,
                this,
                this
        );

        Log.d(TAG, "Path: " + path);

        VolleyHelper.getInstance(getActivity()).addToRequestQueue(request);
    }

    private void getAnswerDetails(String questionId) {
        StackyRequest request = new StackyRequest(
                Constants.Api.getAnswerUrl(questionId),
                REQUEST_ANSWERS,
                this,
                this
        );

        VolleyHelper.getInstance(getActivity()).addToRequestQueue(request);
    }


    @Override
    public void onErrorResponse(VolleyError error, int requestCode) {

    }


    private static final String
            JSON_A_ITEMS = "items",
            JSON_N_QUESTION_ID = "question_id",
            JSON_S_TITLE = "title",
            JSON_N_SCORE = "score",
            JSON_N_CREATION_DATE = "creation_date",
            JSON_N_LAST_ACTIVITY_DATE = "last_activity_date",
            JSON_O_OWNER = "owner",
            JSON_S_LINK = "link",
            JSON_B_IS_ACCEPTED = "is_accepted",
            JSON_N_ANSWER_ID = "answer_id";

    @Override
    public void onResponse(JSONObject response, int request) {

        if (request == REQUEST_QUESTION_DETAILS) {
            try {

                JSONArray itemsArray = response.getJSONArray(JSON_A_ITEMS);

                JSONObject itemObject = itemsArray.getJSONObject(0);

                JSONObject userObject = itemObject.getJSONObject(JSON_O_OWNER);

                User user = new User(userObject);

                ContentValues cv = new ContentValues();
                cv.put(UserEntry._ID, user.getId());
                cv.put(UserEntry.COLUMN_DISPLAY_NAME, user.getDisplayName());
                cv.put(UserEntry.COLUMN_LINK, user.getLink());
                cv.put(UserEntry.COLUMN_REPUTATION, user.getReputation());
                cv.put(UserEntry.COLUMN_PROFILE_IMAGE, user.getProfileImage());

                ContentProviderOperation insertOwner =
                        ContentProviderOperation
                                .newInsert(UserEntry.CONTENT_URI)
                                .withValues(cv)
                                .build();

                cv = new ContentValues();
                cv.put(QuestionEntry._ID, itemObject.getLong(JSON_N_QUESTION_ID));
                cv.put(QuestionEntry.COLUMN_TITLE, itemObject.getString(JSON_S_TITLE));
                cv.put(QuestionEntry.COLUMN_CREATION_DATE, itemObject.getLong(JSON_N_CREATION_DATE));
                cv.put(QuestionEntry.COLUMN_LAST_ACTIVITY_DATE, itemObject.getLong(JSON_N_LAST_ACTIVITY_DATE));
                cv.put(QuestionEntry.COLUMN_OWNER_ID, user.getId());
                cv.put(QuestionEntry.COLUMN_LINK, itemObject.getString(JSON_S_LINK));
                cv.put(QuestionEntry.COLUMN_SCORE, itemObject.getInt(JSON_N_SCORE));
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

                if (results[0].uri == null) {
                    Log.d(TAG, "Error in inserting user");
                    return;
                }

                if (results[1].uri == null) {
                    Log.d(TAG, "Error in insert question");
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            }
        } else if (request == REQUEST_ANSWERS) {
            try {
                JSONArray itemsArray = response.getJSONArray(JSON_A_ITEMS);

                long questionId=-1;

                Vector<ContentValues> userVector = new Vector<>();
                Vector<ContentValues> answerVector = new Vector<>();
                for (int i = 0; i < itemsArray.length(); i++) {
                    JSONObject answerObject = itemsArray.getJSONObject(i);

                    JSONObject userObject = answerObject.getJSONObject(JSON_O_OWNER);

                    User user = new User(userObject);

                    ContentValues cv = new ContentValues();
                    cv.put(UserEntry._ID, user.getId());
                    cv.put(UserEntry.COLUMN_DISPLAY_NAME, user.getDisplayName());
                    cv.put(UserEntry.COLUMN_LINK, user.getLink());
                    cv.put(UserEntry.COLUMN_REPUTATION, user.getReputation());
                    cv.put(UserEntry.COLUMN_PROFILE_IMAGE, user.getProfileImage());

                    userVector.add(cv);

                    cv = new ContentValues();
                    cv.put(
                            StackyContract.AnswerEntry._ID,
                            answerObject.getLong(JSON_N_ANSWER_ID)
                    );

                    cv.put(
                            StackyContract.AnswerEntry.COLUMN_CREATION_DATE,
                            answerObject.getLong(JSON_N_CREATION_DATE)
                    );

                    cv.put(
                            StackyContract.AnswerEntry.COLUMN_SCORE,
                            answerObject.getInt(JSON_N_SCORE)
                    );
                    cv.put(
                            StackyContract.AnswerEntry.COLUMN_QUESTION_ID,
                            answerObject.getLong(JSON_N_QUESTION_ID)
                    );
                    cv.put(
                            StackyContract.AnswerEntry.COLUMN_IS_ACCEPTED,
                            answerObject.getBoolean(JSON_B_IS_ACCEPTED)
                    );
                    cv.put(
                            StackyContract.AnswerEntry.COLUMN_LAST_ACTIVITY_DATE,
                            answerObject.getLong(JSON_N_LAST_ACTIVITY_DATE)
                    );

                    cv.put(
                            StackyContract.AnswerEntry.COLUMN_OWNER_ID,
                            user.getId()
                    );
                    answerVector.add(cv);

                    questionId = answerObject.getInt(JSON_N_QUESTION_ID);
                }


                getActivity().getContentResolver().bulkInsert(
                    UserEntry.CONTENT_URI,
                        (ContentValues[]) userVector.toArray()
                );

                if(questionId == -1){
                    throw new IllegalArgumentException("Question ID is -1");
                }

                getActivity().getContentResolver().bulkInsert(
                        StackyContract.AnswerEntry.buildAnswersOfQuestionUri(questionId),
                        (ContentValues[]) userVector.toArray()
                );


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class QuestionVH extends RecyclerView.ViewHolder implements View.OnClickListener{

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

            itemView.setOnClickListener(this);
            itemView.setTag(cursor.getLong(INDEX_ID));

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

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();

        }
    }
}