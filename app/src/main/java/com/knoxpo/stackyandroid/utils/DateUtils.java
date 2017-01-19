package com.knoxpo.stackyandroid.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by khushboo on 19/1/17.
 */

public class DateUtils {

    private static DateFormat sDateFormat = DateFormat.getDateInstance(DateFormat.SHORT);

    public static String formatDate(long millis) {
        return sDateFormat.format(new Date(millis));
    }
}
