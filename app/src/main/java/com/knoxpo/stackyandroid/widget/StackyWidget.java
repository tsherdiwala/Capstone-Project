package com.knoxpo.stackyandroid.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.knoxpo.stackyandroid.R;
import com.knoxpo.stackyandroid.activities.DetailActivity;
import com.knoxpo.stackyandroid.activities.MainActivity;
import com.knoxpo.stackyandroid.services.WidgetService;

/**
 * Created by Tejas Sherdiwala on 25/01/17.
 */

public class StackyWidget extends AppWidgetProvider {

    private static final String TAG = StackyWidget.class.getSimpleName();

    private static final String REFRESH_ACTION = "com.mypackage.appwidget.action.REFRESH";

    public static void sendRefreshBroadcast(Context context) {
        Intent intent = new Intent(REFRESH_ACTION);
        intent.setComponent(new ComponentName(context, StackyWidget.class));
        context.sendBroadcast(intent);
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        final String action = intent.getAction();

        if (REFRESH_ACTION.equals(action)) {
            // refresh all your widgets
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, StackyWidget.class);
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.list_view);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "Update called");
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId){
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        remoteViews.setTextViewText(android.R.id.title,context.getString(R.string.app_name));
        remoteViews.setRemoteAdapter(
                R.id.list_view,
                new Intent(context, WidgetService.class)
        );

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        remoteViews.setOnClickPendingIntent(android.R.id.title, pendingIntent);

        Intent detailIntent = new Intent(context, DetailActivity.class);
        detailIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
        PendingIntent detailPI = PendingIntent.getActivity(context,0,detailIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setPendingIntentTemplate(R.id.list_view,detailPI);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

}
