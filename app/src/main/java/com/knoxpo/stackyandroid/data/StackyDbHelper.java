package com.knoxpo.stackyandroid.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.knoxpo.stackyandroid.data.StackyContract.UserEntry;
import com.knoxpo.stackyandroid.data.StackyContract.QuestionEntry;
import com.knoxpo.stackyandroid.data.StackyContract.AnswerEntry;

/**
 * Created by Tejas Sherdiwala on 17/1/17.
 */

public class StackyDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "stacky.db";

    public StackyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_USERS_TABLE =
                "CREATE TABLE " + UserEntry.TABLE_NAME + "("
                        + UserEntry._ID + " INTEGER,"
                        + UserEntry.COLUMN_DISPLAY_NAME + " TEXT NOT NULL,"
                        + UserEntry.COLUMN_REPUTATION + " INTEGER NOT NULL DEFAULT 0,"
                        + UserEntry.COLUMN_PROFILE_IMAGE + " TEXT NOT NULL,"
                        + UserEntry.COLUMN_LINK + " TEXT NOT NULL"
                        + ")";

        final String SQL_CREATE_QUESTION_TABLE =
                "CREATE TABLE " + QuestionEntry.TABLE_NAME + "("
                        + QuestionEntry._ID + " INTEGER PRIMARY KEY,"
                        + QuestionEntry.COLUMN_TITLE + " TEXT NOT NULL,"
                        + QuestionEntry.COLUMN_SCORE + " INTEGER NOT NULL DEFAULT 0,"
                        + QuestionEntry.COLUMN_CREATION_DATE + " INTEGER NOT NULL,"
                        + QuestionEntry.COLUMN_LAST_ACTIVITY_DATE + " INTEGER NOT NULL,"
                        + QuestionEntry.COLUMN_START_DATE + " INTEGER NOT NULL,"
                        + QuestionEntry.COLUMN_LINK + " TEXT NOT NULL,"
                        + QuestionEntry.COLUMN_OWNER_ID + " INTEGER NOT NULL,"
                        + " FOREIGN KEY (" + QuestionEntry.COLUMN_OWNER_ID + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry._ID + ")"
                        + ")";

        final String SQL_CREATE_ANSWERS_TABLE =
                "CREATE TABLE " + AnswerEntry.TABLE_NAME + "("
                        + AnswerEntry._ID + " INTEGER PRIMARY KEY,"
                        + AnswerEntry.COLUMN_SCORE + " INTEGER NOT NULL DEFAULT 0,"
                        + AnswerEntry.COLUMN_QUESTION_ID + " INTEGER,"
                        + AnswerEntry.COLUMN_IS_ACCEPTED + " INTEGER DEFAULT 0,"
                        + AnswerEntry.COLUMN_CREATION_DATE + " INTEGER NOT NULL,"
                        + AnswerEntry.COLUMN_LAST_ACTIVITY_DATE + " INTEGER NOT NULL,"
                        + AnswerEntry.COLUMN_OWNER_ID + " INTEGER,"
                        + " FOREIGN KEY ("+AnswerEntry.COLUMN_QUESTION_ID+") REFERENCES "+QuestionEntry.TABLE_NAME + "("+QuestionEntry._ID+"),"
                        + " FOREIGN KEY ("+AnswerEntry.COLUMN_OWNER_ID+") REFERENCES "+UserEntry.TABLE_NAME+ "("+ UserEntry._ID+")"
                        + ")";

        db.execSQL(SQL_CREATE_USERS_TABLE);
        db.execSQL(SQL_CREATE_QUESTION_TABLE);
        db.execSQL(SQL_CREATE_ANSWERS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QuestionEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AnswerEntry.TABLE_NAME);
        onCreate(db);
    }
}
