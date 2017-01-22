package com.knoxpo.stackyandroid.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

import java.util.HashMap;

/**
 * Created by khushboo on 18/1/17.
 */

public class StackyProvider extends ContentProvider {

    private static final int
            QUESTION = 0,
            QUESTION_WITH_ID = 1,
            ANSWER = 2,
            ANSWER_WITH_ID = 3,
            USER = 4,
            USER_WITH_ID = 5;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = StackyContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, StackyContract.PATH_QUESTION, QUESTION);
        matcher.addURI(authority, StackyContract.PATH_QUESTION + "/#", QUESTION_WITH_ID);
        matcher.addURI(authority, StackyContract.PATH_QUESTION + "/#/" + StackyContract.PATH_ANSWER, ANSWER);
        matcher.addURI(authority, StackyContract.PATH_QUESTION + "/#/" + StackyContract.PATH_ANSWER + "/#", ANSWER_WITH_ID);
        matcher.addURI(authority, StackyContract.PATH_USER, USER);
        matcher.addURI(authority, StackyContract.PATH_USER + "/#", USER_WITH_ID);


        return matcher;
    }

    private static SQLiteQueryBuilder sQuestionQueryBuilder;
    private static HashMap<String, String> sQuestionQueryProjectionMap;

    static {
        sQuestionQueryBuilder = new SQLiteQueryBuilder();
        sQuestionQueryBuilder.setTables(
                StackyContract.QuestionEntry.TABLE_NAME +
                        " LEFT JOIN " +
                        StackyContract.AnswerEntry.TABLE_NAME +
                        " ON " +
                        StackyContract.QuestionEntry.TABLE_NAME + "." + StackyContract.QuestionEntry._ID +
                        " = " +
                        StackyContract.AnswerEntry.TABLE_NAME + "." + StackyContract.AnswerEntry.COLUMN_QUESTION_ID +
                        " INNER JOIN " +
                        StackyContract.UserEntry.TABLE_NAME +
                        " ON " +
                        StackyContract.UserEntry.TABLE_NAME + "." + StackyContract.UserEntry._ID +
                        " = " +
                        StackyContract.QuestionEntry.TABLE_NAME + "." + StackyContract.QuestionEntry.COLUMN_OWNER_ID
        );

        sQuestionQueryProjectionMap = new HashMap<>();
        sQuestionQueryProjectionMap.put(
                BaseColumns._ID,
                StackyContract.QuestionEntry.TABLE_NAME + "." + StackyContract.QuestionEntry._ID
        );
        sQuestionQueryProjectionMap.put(
                StackyContract.QuestionEntry.COLUMN_TITLE,
                StackyContract.QuestionEntry.TABLE_NAME + "." + StackyContract.QuestionEntry.COLUMN_TITLE
        );
        sQuestionQueryProjectionMap.put(
                StackyContract.QuestionEntry.COLUMN_SCORE,
                StackyContract.QuestionEntry.TABLE_NAME + "." + StackyContract.QuestionEntry.COLUMN_SCORE
        );
        sQuestionQueryProjectionMap.put(
                StackyContract.QuestionEntry.COLUMN_CREATION_DATE,
                StackyContract.QuestionEntry.TABLE_NAME + "." + StackyContract.QuestionEntry.COLUMN_CREATION_DATE
        );
        sQuestionQueryProjectionMap.put(
                StackyContract.QuestionEntry.COLUMN_LAST_ACTIVITY_DATE,
                StackyContract.QuestionEntry.TABLE_NAME + "." + StackyContract.QuestionEntry.COLUMN_LAST_ACTIVITY_DATE
        );
        sQuestionQueryProjectionMap.put(
                StackyContract.QuestionEntry.COLUMN_START_DATE,
                StackyContract.QuestionEntry.TABLE_NAME + "." + StackyContract.QuestionEntry.COLUMN_START_DATE
        );
        sQuestionQueryProjectionMap.put(
                StackyContract.QuestionEntry.COLUMN_LINK,
                StackyContract.QuestionEntry.TABLE_NAME + "." + StackyContract.QuestionEntry.COLUMN_LINK
        );
        sQuestionQueryProjectionMap.put(
                StackyContract.QuestionEntry.COLUMN_OWNER_ID,
                StackyContract.QuestionEntry.TABLE_NAME + "." + StackyContract.QuestionEntry.COLUMN_OWNER_ID
        );
        sQuestionQueryProjectionMap.put(
                StackyContract.QuestionEntry.COLUMN_ANSWER_COUNT,
                "COUNT (" + StackyContract.AnswerEntry.TABLE_NAME + "." + StackyContract.AnswerEntry._ID + ")"
        );
        sQuestionQueryProjectionMap.put(
                StackyContract.QuestionEntry.COLUMN_IS_QUESTION_ANSWERED,
                "SUM ("+ StackyContract.AnswerEntry.TABLE_NAME + "."+StackyContract.AnswerEntry.COLUMN_IS_ACCEPTED+")"
        );
        sQuestionQueryProjectionMap.put(
                StackyContract.UserEntry.COLUMN_DISPLAY_NAME,
                StackyContract.UserEntry.COLUMN_DISPLAY_NAME
        );
        sQuestionQueryProjectionMap.put(
                StackyContract.UserEntry.COLUMN_PROFILE_IMAGE,
                StackyContract.UserEntry.COLUMN_PROFILE_IMAGE
        );

        sQuestionQueryProjectionMap.put(
                StackyContract.UserEntry.COLUMN_REPUTATION,
                StackyContract.UserEntry.COLUMN_REPUTATION
        );



        sQuestionQueryBuilder.setProjectionMap(sQuestionQueryProjectionMap);
    }

    private static SQLiteQueryBuilder sAnswersQueryBuilder;
    private static HashMap<String, String> sAnswersQueryProjectionMap;

    static {
        sAnswersQueryBuilder = new SQLiteQueryBuilder();
        sAnswersQueryBuilder.setTables(
                StackyContract.AnswerEntry.TABLE_NAME +
                        " INNER JOIN " +
                        StackyContract.UserEntry.TABLE_NAME +
                        " ON " +
                        StackyContract.UserEntry.TABLE_NAME + "." + StackyContract.UserEntry._ID +
                        " = " +
                        StackyContract.AnswerEntry.TABLE_NAME + "." + StackyContract.AnswerEntry.COLUMN_OWNER_ID
        );

        sAnswersQueryProjectionMap = new HashMap<>();

        sAnswersQueryProjectionMap.put(
                StackyContract.AnswerEntry._ID,
                StackyContract.AnswerEntry.TABLE_NAME + "." + StackyContract.AnswerEntry._ID
        );

        sAnswersQueryProjectionMap.put(
                StackyContract.AnswerEntry.COLUMN_SCORE,
                StackyContract.AnswerEntry.TABLE_NAME + "." + StackyContract.AnswerEntry.COLUMN_SCORE
        );
        sAnswersQueryProjectionMap.put(
                StackyContract.AnswerEntry.COLUMN_QUESTION_ID,
                StackyContract.AnswerEntry.TABLE_NAME + "." + StackyContract.AnswerEntry.COLUMN_QUESTION_ID
        );
        sAnswersQueryProjectionMap.put(
                StackyContract.AnswerEntry.COLUMN_IS_ACCEPTED,
                StackyContract.AnswerEntry.TABLE_NAME + "." + StackyContract.AnswerEntry.COLUMN_IS_ACCEPTED
        );
        sAnswersQueryProjectionMap.put(
                StackyContract.AnswerEntry.COLUMN_CREATION_DATE,
                StackyContract.AnswerEntry.TABLE_NAME + "." + StackyContract.AnswerEntry.COLUMN_CREATION_DATE
        );
        sAnswersQueryProjectionMap.put(
                StackyContract.AnswerEntry.COLUMN_LAST_ACTIVITY_DATE,
                StackyContract.AnswerEntry.TABLE_NAME + "." + StackyContract.AnswerEntry.COLUMN_LAST_ACTIVITY_DATE
        );
        sAnswersQueryProjectionMap.put(
                StackyContract.AnswerEntry.COLUMN_OWNER_ID,
                StackyContract.AnswerEntry.TABLE_NAME + "." + StackyContract.AnswerEntry.COLUMN_OWNER_ID
        );

        sAnswersQueryProjectionMap.put(
                StackyContract.UserEntry.COLUMN_DISPLAY_NAME,
                StackyContract.UserEntry.TABLE_NAME + "."+ StackyContract.UserEntry.COLUMN_DISPLAY_NAME
        );

        sAnswersQueryProjectionMap.put(
                StackyContract.UserEntry.COLUMN_PROFILE_IMAGE,
                StackyContract.UserEntry.COLUMN_PROFILE_IMAGE
        );

        sAnswersQueryProjectionMap.put(
                StackyContract.UserEntry.COLUMN_REPUTATION,
                StackyContract.UserEntry.COLUMN_REPUTATION
        );

        sAnswersQueryBuilder.setProjectionMap(sAnswersQueryProjectionMap);
    }

    private Cursor getQuestions(String[] projection, String sortOrder) {
        if (projection == null || projection.length == 0) {
            projection = (String[]) sQuestionQueryProjectionMap.keySet().toArray();
        }
        Cursor cursor = sQuestionQueryBuilder.query(
                mDbHelper.getReadableDatabase(),
                projection,
                null,
                null,
                StackyContract.QuestionEntry.TABLE_NAME + "." + StackyContract.QuestionEntry._ID,
                null,
                sortOrder
        );
        return cursor;
    }

    private Cursor getAnswersForQuestion(Uri uri, String[] projection, String sortOrder) {
        if (projection == null || projection.length == 0) {
            projection = (String[]) sAnswersQueryProjectionMap.keySet().toArray();
        }

        long questionId = StackyContract.AnswerEntry.getQuestionIdFromUri(uri);

        Cursor cursor = sAnswersQueryBuilder.query(
                mDbHelper.getReadableDatabase(),
                projection,
                StackyContract.AnswerEntry.COLUMN_QUESTION_ID + "= ?",
                new String[]{String.valueOf(questionId)},
                null,
                null,
                sortOrder
        );

        return cursor;
    }

    private StackyDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new StackyDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case QUESTION:
                return StackyContract.QuestionEntry.CONTENT_TYPE;
            case ANSWER:
                return StackyContract.AnswerEntry.CONTENT_TYPE;
            case USER_WITH_ID:
                return StackyContract.UserEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }


    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match = sUriMatcher.match(uri);
        Cursor returnCursor;

        switch (match) {
            case QUESTION:
                returnCursor = getQuestions(projection, sortOrder);
                break;
            case ANSWER:
                returnCursor = getAnswersForQuestion(uri, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }


    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = sUriMatcher.match(uri);

        Uri returnUri;

        switch (match) {
            case QUESTION:
                long questionId = mDbHelper.getWritableDatabase().insert(
                        StackyContract.QuestionEntry.TABLE_NAME,
                        null,
                        values
                );
                if (questionId > 0) {
                    returnUri = StackyContract.QuestionEntry.buildQuestionUri(questionId);
                } else {
                    throw new SQLException("Failed to insert row into URI : " + uri);
                }
                break;
            case ANSWER:
                questionId = StackyContract.AnswerEntry.getQuestionIdFromUri(uri);
                values.put(StackyContract.AnswerEntry.COLUMN_QUESTION_ID, questionId);
                long answerId = mDbHelper.getWritableDatabase().insert(
                        StackyContract.AnswerEntry.TABLE_NAME,
                        null,
                        values
                );
                if (answerId > 0) {
                    returnUri = StackyContract.AnswerEntry.buildAnswerUri(questionId, answerId);
                    //Also need to notify the questionlist
                    getContext().getContentResolver().notifyChange(StackyContract.QuestionEntry.CONTENT_URI, null);
                } else {
                    throw new SQLException("Failed to insert row into URI : " + uri);
                }
                break;
            case USER:
                long userId = mDbHelper.getWritableDatabase().insert(
                        StackyContract.UserEntry.TABLE_NAME,
                        null,
                        values
                );

                if (userId > 0) {
                    returnUri = StackyContract.UserEntry.buildUserUri(userId);
                } else {
                    throw new SQLException("Failed to insert row into URI : " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }


        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        switch (match) {
            case USER:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        if (insertValue(db, StackyContract.UserEntry.TABLE_NAME, value)) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                return returnCount;
            case ANSWER:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        if (insertValue(db, StackyContract.AnswerEntry.TABLE_NAME, value)) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(
                        uri,
                        null
                );
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    private boolean insertValue(SQLiteDatabase db, String tableName, ContentValues values) {
        long _id = db.insert(
                tableName,
                null,
                values);
        if (_id > 0) {
            return true;
        }
        return false;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case QUESTION_WITH_ID:
                long questionId = StackyContract.QuestionEntry.getQuestionIdFromUri(uri);
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                int answerDeleteCount = db.delete(
                        StackyContract.AnswerEntry.TABLE_NAME,
                        StackyContract.AnswerEntry.COLUMN_QUESTION_ID + "=?",
                        new String[]{String.valueOf(questionId)}
                );

                db.delete(
                        StackyContract.QuestionEntry.TABLE_NAME,
                        StackyContract.QuestionEntry._ID + "=?",
                        new String[]{String.valueOf(questionId)}
                );

                getContext().getContentResolver().notifyChange(
                        StackyContract.AnswerEntry.buildAnswersOfQuestionUri(questionId),
                        null
                );

                getContext().getContentResolver().notifyChange(
                        StackyContract.QuestionEntry.buildQuestionUri(questionId),
                        null
                );
                return answerDeleteCount;
            default:
                throw new UnsupportedOperationException("Could not delete URI : " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int match = sUriMatcher.match(uri);
        int updatedRows = 0;
        switch (match) {
            case QUESTION_WITH_ID:
                long questionId = StackyContract.QuestionEntry.getQuestionIdFromUri(uri);
                updatedRows = mDbHelper.getWritableDatabase()
                        .update(
                                StackyContract.QuestionEntry.TABLE_NAME,
                                values,
                                StackyContract.QuestionEntry._ID + " = ?",
                                new String[]{String.valueOf(questionId)}
                        );
                break;
            case ANSWER_WITH_ID:
                long answerId = StackyContract.AnswerEntry.getAnswerIdFromUri(uri);
                updatedRows = mDbHelper.getWritableDatabase()
                        .update(
                                StackyContract.AnswerEntry.TABLE_NAME,
                                values,
                                StackyContract.AnswerEntry._ID + " = ?",
                                new String[]{String.valueOf(answerId)}
                        );
                break;
            case USER_WITH_ID:
                long userId = StackyContract.UserEntry.getUserIdFromUri(uri);
                updatedRows = mDbHelper.getWritableDatabase()
                        .update(
                                StackyContract.UserEntry.TABLE_NAME,
                                values,
                                StackyContract.UserEntry._ID + " = ?",
                                new String[]{String.valueOf(userId)}
                        );
                break;
            default:
                throw new UnsupportedOperationException("Could not update URI : " + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return updatedRows;
    }
}