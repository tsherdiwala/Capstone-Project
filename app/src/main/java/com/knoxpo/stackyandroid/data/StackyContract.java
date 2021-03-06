package com.knoxpo.stackyandroid.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.List;

/**
 * Created by Tejas Sherdiwala on 17/1/17.
 */

public class StackyContract {

    public static final String CONTENT_AUTHORITY = "com.knoxpo.stackyandroid";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String
            PATH_QUESTION = "question",
            PATH_ANSWER = "answer",
            PATH_USER = "user";

    /**
     * questions
     */

    public static final class QuestionEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_QUESTION).build();

        public static final String
                CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_QUESTION,
                CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_QUESTION;

        public static final String
                TABLE_NAME = "question";

        public static final String
                COLUMN_TITLE = "title",
                COLUMN_SCORE = "score",
                COLUMN_CREATION_DATE = "creation_date",
                COLUMN_LAST_ACTIVITY_DATE = "last_activity_date",
                COLUMN_START_DATE = "start_date", //to track when the user has added
                COLUMN_LINK = "link",
                COLUMN_OWNER_ID = "owner_id",
                COLUMN_ANSWER_COUNT = "answer_count",
                COLUMN_IS_QUESTION_ANSWERED = "is_question_answered";

        public static Uri buildQuestionUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getQuestionIdFromUri(Uri uri){
            List<String> paths = uri.getPathSegments();
            return Long.parseLong(paths.get(1));
        }
    }

    public static final class AnswerEntry implements BaseColumns {
        public static final Uri CONTENT_URI
                = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ANSWER).build();

        public static final String
                CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ANSWER,
                CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ANSWER;

        public static final String
                TABLE_NAME = "answer";

        public static final String
                COLUMN_SCORE = "score",
                COLUMN_QUESTION_ID = "question_id",
                COLUMN_IS_ACCEPTED = "is_accepted",
                COLUMN_CREATION_DATE = "creation_date",
                COLUMN_LAST_ACTIVITY_DATE = "last_activity_date",
                COLUMN_OWNER_ID = "owner_id";

        /**
         * URI format : question/{id}/answer
         */
        public static long getQuestionIdFromUri(Uri uri){
            List<String> paths = uri.getPathSegments();
            return Long.parseLong(paths.get(1));
        }

        /**
         * URI format : question/{question_id}/answer/{answer_id}
         */
        public static long getAnswerIdFromUri(Uri uri){
            return Long.parseLong(uri.getLastPathSegment());
        }

        public static Uri buildAnswerUri(long questionId, long answerId) {
            return QuestionEntry.CONTENT_URI.buildUpon()
                    .appendPath(String.valueOf(questionId))
                    .appendPath(PATH_ANSWER)
                    .appendPath(String.valueOf(answerId))
                    .build();
        }

        public static Uri buildAnswersOfQuestionUri(long questionId){
            return QuestionEntry.CONTENT_URI.buildUpon()
                    .appendPath(String.valueOf(questionId))
                    .appendPath(PATH_ANSWER)
                    .build();
        }
    }

    public static final class UserEntry implements BaseColumns {
        public static final Uri CONTENT_URI
                = BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER).build();

        public static final String
                CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER,
                CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER;

        public static final String
                TABLE_NAME = "user";

        public static final String
                COLUMN_REPUTATION = "reputation",
                COLUMN_DISPLAY_NAME = "display_name",
                COLUMN_PROFILE_IMAGE = "profile_image",
                COLUMN_LINK = "link";

        /**
         * URI format user/{user_id}
         */
        public static long getUserIdFromUri(Uri uri){
            return Long.parseLong(uri.getLastPathSegment());
        }

        public static Uri buildUserUri(long userId) {
            return ContentUris.withAppendedId(CONTENT_URI,userId);
        }
    }
}
