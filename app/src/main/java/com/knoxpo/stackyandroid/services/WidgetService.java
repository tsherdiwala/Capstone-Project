package com.knoxpo.stackyandroid.services;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.knoxpo.stackyandroid.R;
import com.knoxpo.stackyandroid.activities.DetailActivity;
import com.knoxpo.stackyandroid.data.StackyContract;

/**
 * Created by Tejas Sherdiwala on 25/01/17.
 */

public class WidgetService extends RemoteViewsService {

    private static final String TAG = WidgetService.class.getSimpleName();

    private static final int
            INDEX_ID = 0,
            INDEX_TITLE = 1,
            INDEX_ANSWER_COUNT = 2,
            INDEX_IS_ANSWERED = 3;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        final int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        return new RemoteViewsFactory() {

            private Cursor questionData = null;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                Log.d(TAG, "onDataSetChanged: Service called");

                if (questionData != null) {
                    questionData.close();
                }

                final long identityToken = Binder.clearCallingIdentity();

                questionData = getContentResolver().query(
                        StackyContract.QuestionEntry.CONTENT_URI,
                        new String[]{
                                StackyContract.QuestionEntry._ID,
                                StackyContract.QuestionEntry.COLUMN_TITLE,
                                StackyContract.QuestionEntry.COLUMN_ANSWER_COUNT,
                                StackyContract.QuestionEntry.COLUMN_IS_QUESTION_ANSWERED
                        },
                        null,
                        null,
                        StackyContract.QuestionEntry.COLUMN_START_DATE + " desc"
                );

                Log.d(TAG, "Question Count: " + (questionData == null ? 0 : questionData.getCount()));
                Binder.restoreCallingIdentity(identityToken);

            }

            @Override
            public void onDestroy() {
                if (questionData != null) {
                    questionData.close();
                    questionData = null;
                }
            }

            @Override
            public int getCount() {
                return questionData == null ? 0 : questionData.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        questionData == null || !questionData.moveToPosition(position)) {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.item_widget);

                views.setViewVisibility(
                        R.id.iv_is_answered,
                        questionData.getInt(INDEX_IS_ANSWERED) > 0 ? View.VISIBLE : View.INVISIBLE);

                views.setTextViewText(
                        android.R.id.text1,
                        questionData.getString(INDEX_TITLE)
                );


                int answerCount = questionData.getInt(INDEX_ANSWER_COUNT);
                views.setTextViewText(
                        android.R.id.text2,
                        getString(
                                answerCount == 1 ? R.string.answer_count_singular : R.string.answer_count_plural,
                                answerCount
                        )
                );

                Bundle extras = new Bundle();
                extras.putLong(DetailActivity.EXTRA_QUESTION_ID, getItemId(position));
                Intent fillInIntent = new Intent();
                fillInIntent.putExtras(extras);

                views.setOnClickFillInIntent(R.id.ll_container, fillInIntent);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return null;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (questionData.moveToPosition(position))
                    return questionData.getLong(INDEX_ID);
                return -1; //invalid entry
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
