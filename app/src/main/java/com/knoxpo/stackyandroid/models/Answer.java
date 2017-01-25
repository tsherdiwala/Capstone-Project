package com.knoxpo.stackyandroid.models;

import org.json.JSONObject;

/**
 * Created by khushboo on 24/1/17.
 */

public class Answer {

    private static final String
            JSON_N_ANSWER_ID = "answer_id",
            JSON_N_CREATION_DATE = "creation_date",
            JSON_N_SCORE = "score",
            JSON_N_QUESTION_ID = "question_id",
            JSON_B_IS_ACCEPTED = "is_accepted",
            JSON_N_LAST_ACTIVITY_DATE = "last_activity_date";


    private long
            mId,
            mCreationDate,
            mQuestionId,
            mLastActivityDate;

    private int mScore;
    private boolean mIsAccepted;

    public Answer(JSONObject answerObject) {
        mId = answerObject.optLong(JSON_N_ANSWER_ID);
        mCreationDate = answerObject.optLong(JSON_N_CREATION_DATE);

        mScore = answerObject.optInt(JSON_N_SCORE);
        mQuestionId = answerObject.optLong(JSON_N_QUESTION_ID);
        mIsAccepted = answerObject.optBoolean(JSON_B_IS_ACCEPTED);
        mLastActivityDate = answerObject.optLong(JSON_N_LAST_ACTIVITY_DATE);
    }

    public long getId() {
        return mId;
    }

    public long getCreationDate() {
        return mCreationDate;
    }

    public long getQuestionId() {
        return mQuestionId;
    }

    public long getLastActivityDate() {
        return mLastActivityDate;
    }

    public int getScore() {
        return mScore;
    }

    public boolean isAccepted() {
        return mIsAccepted;
    }
}
