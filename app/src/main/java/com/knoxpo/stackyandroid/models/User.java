package com.knoxpo.stackyandroid.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by khushboo on 21/1/17.
 */

public class User {

    private static final String
            JSON_N_REPUTATION = "reputation",
            JSON_N_USER_ID = "user_id",
            JSON_S_DISPLAY_NAME = "display_name",
            JSON_S_LINK = "link",
            JSON_S_PROFILE_IMAGE = "profile_image";


    private long mId;
    private String mDisplayName, mLink,mProfileImage;
    private int mReputation;

    public User(JSONObject userObject) throws JSONException{
        mId = userObject.getLong(JSON_N_USER_ID);
        mDisplayName = userObject.getString(JSON_S_DISPLAY_NAME);
        mLink = userObject.getString(JSON_S_LINK);
        mReputation = userObject.getInt(JSON_N_REPUTATION);
        mProfileImage = userObject.getString(JSON_S_PROFILE_IMAGE);
    }

    public long getId() {
        return mId;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public String getLink() {
        return mLink;
    }

    public String getProfileImage() {
        return mProfileImage;
    }

    public int getReputation() {
        return mReputation;
    }
}
