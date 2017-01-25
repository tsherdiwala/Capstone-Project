package com.knoxpo.stackyandroid.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.knoxpo.stackyandroid.R;
import com.knoxpo.stackyandroid.activities.DetailActivity;
import com.knoxpo.stackyandroid.activities.MainActivity;
import com.knoxpo.stackyandroid.data.StackyContract;
import com.knoxpo.stackyandroid.models.Answer;
import com.knoxpo.stackyandroid.models.User;
import com.knoxpo.stackyandroid.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by khushboo on 23/1/17.
 */

public class StackySyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = StackySyncAdapter.class.getSimpleName();

    private static final int NET_CONNECT_TIMEOUT_MILLIS = 15000;  // 15 seconds
    private static final int NET_READ_TIMEOUT_MILLIS = 10000;  // 10 seconds

    public static final int SYNC_INTERVAL = 60 *15;  //seconds
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    private static final String
            JSON_A_ITEMS = "items",
            JSON_O_OWNER = "owner";

    public StackySyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "Syncing application");
        if(!Constants.isConnectedToInternet(getContext())){
            Log.d(TAG, "Not connected to internet");
            return;
        }


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        long lastSyncMillis = preferences.getLong(Constants.SP.LAST_SYNC_TIME, 0);

        Cursor cursor = getContext()
                .getContentResolver()
                .query(
                        StackyContract.QuestionEntry.CONTENT_URI,
                        new String[]{StackyContract.QuestionEntry._ID},
                        null,
                        null,
                        null
                );

        HashMap<Integer, ArrayList<Long>> requestMap = new HashMap<>();

        if (cursor != null && cursor.moveToFirst()) {
            int i = 0;
            do {
                long questionId = cursor.getLong(0);
                ArrayList<Long> delimitedList = requestMap.get(i % 100);
                if (delimitedList == null) {
                    delimitedList = new ArrayList<Long>();
                }
                delimitedList.add(questionId);
                i++;
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        cursor = getContext().getContentResolver().query(
                StackyContract.AnswerEntry.CONTENT_URI,
                new String[]{StackyContract.AnswerEntry._ID},
                null,
                null,
                null
        );

        ArrayList<Long> existingAnswerIds = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                existingAnswerIds.add(cursor.getLong(0));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        cursor = getContext().getContentResolver().query(
                StackyContract.UserEntry.CONTENT_URI,
                new String[]{StackyContract.UserEntry._ID},
                null,
                null,
                null
        );

        ArrayList<Long> existingUserIds = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                existingUserIds.add(cursor.getLong(0));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        StringBuffer buffer = null;

        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

        HashMap<Long, Answer> parsedAnswers = new HashMap<Long, Answer>();
        HashMap<Long, User> parsedUsers = new HashMap<Long, User>();

        for (ArrayList<Long> questionIds : requestMap.values()) {
            BufferedReader reader = null;
            try {
                URL url = new URL(Constants.Api.getAnswerUrl(questionIds, lastSyncMillis));
                InputStream stream = downloadUrl(url);
                buffer = new StringBuffer();

                if (stream == null) {
                    return;
                }

                reader = new BufferedReader(new InputStreamReader(stream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return;
                }

                JSONObject response = new JSONObject(buffer.toString());

                JSONArray itemsArray = response.getJSONArray(JSON_A_ITEMS);

                for (int i = 0; i < itemsArray.length(); i++) {
                    JSONObject answersObject = itemsArray.getJSONObject(i);
                    JSONObject userObject = answersObject.getJSONObject(JSON_O_OWNER);

                    User user = new User(userObject);
                    Answer answer = new Answer(answersObject);

                    parsedUsers.put(user.getId(), user);
                    parsedAnswers.put(answer.getId(), answer);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }

            for (int i = 0; i < existingUserIds.size(); i++) {
                User user = parsedUsers.get(existingAnswerIds.get(i));
                if (user != null) {
                    ContentValues cv = user.toContentValues();

                    cv.remove(StackyContract.UserEntry._ID);

                    batch.add(ContentProviderOperation
                            .newUpdate(StackyContract.UserEntry.buildUserUri(user.getId()))
                            .withValues(cv)
                            .build()
                    );
                    parsedUsers.remove(user.getId());
                }
            }

            for (User user : parsedUsers.values()) {
                batch.add(
                        ContentProviderOperation
                                .newInsert(StackyContract.UserEntry.CONTENT_URI)
                                .withValues(user.toContentValues())
                                .build()
                );
            }

            int updatedAnswers = 0;
            for (int i = 0; i < existingAnswerIds.size(); i++) {
                Answer answer = parsedAnswers.get(existingAnswerIds.get(i));
                if (answer != null) {
                    ContentValues cv = answer.toContentValues();
                    cv.remove(StackyContract.AnswerEntry._ID);

                    batch.add(
                            ContentProviderOperation
                                    .newUpdate(StackyContract.AnswerEntry.buildAnswerUri(answer.getQuestionId(), answer.getId()))
                                    .withValues(cv)
                                    .build()
                    );
                    updatedAnswers++;
                    parsedAnswers.remove(answer.getId());
                }
            }

            int addedAnswers = 0;
            HashSet<Long> addedQuestionIds = new HashSet<>();
            for (Answer answer : parsedAnswers.values()) {
                batch.add(
                        ContentProviderOperation.newInsert(
                                StackyContract.AnswerEntry.buildAnswersOfQuestionUri(answer.getQuestionId())
                        )
                                .withValues(answer.toContentValues())
                                .build()
                );
                addedAnswers++;
                addedQuestionIds.add(answer.getQuestionId());
            }

            if (addedAnswers > 0 || updatedAnswers > 0) {
                preferences
                        .edit()
                        .putLong(
                                Constants.SP.LAST_SYNC_TIME,
                                new Date().getTime()
                        )
                        .apply();
            }

            Log.d(TAG, "New answers: " + addedAnswers + " | Updated Answers: " + updatedAnswers);


            NotificationManagerCompat manager = NotificationManagerCompat.from(getContext());

            if (addedQuestionIds.size() == 1) {

                long questionId = addedQuestionIds.iterator().next();

                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra(DetailActivity.EXTRA_QUESTION_ID, questionId);

                PendingIntent pi = PendingIntent.getActivity(getContext(), 0, intent, 0);

                Notification notification = new Notification.Builder(getContext())
                        .setContentTitle("New answers for " + questionIds)
                        .setContentIntent(pi)
                        .build();
                manager.notify(0, notification);
            } else if (addedQuestionIds.size() > 1) {

                Intent intent = new Intent(getContext(), MainActivity.class);

                PendingIntent pi = PendingIntent.getActivity(getContext(), 0, intent, 0);

                Notification notification = new Notification.Builder(getContext())
                        .setContentTitle("New answers")
                        .setContentText(questionIds.size() + " questions have new answers.")
                        .setContentIntent(pi)
                        .build();

                manager.notify(0, notification);
            }
        }
    }

    private InputStream downloadUrl(final URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(NET_READ_TIMEOUT_MILLIS /* milliseconds */);
        conn.setConnectTimeout(NET_CONNECT_TIMEOUT_MILLIS /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        StackySyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

}
