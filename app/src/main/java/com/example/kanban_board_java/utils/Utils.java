package com.example.kanban_board_java.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static String formatString(long timeInMillis, String format) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
            return simpleDateFormat.format(new Date(timeInMillis));
        } catch (Exception e) {
            return "";
        }
    }

    public static String formatSecondToString(long milliseconds) {
        try {
            long seconds = (milliseconds / 1000) % 60;
            long minutes = ((milliseconds / (1000 * 60)) % 60);
            long hours = ((milliseconds / (1000 * 60 * 60)) % 24);

            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } catch (Exception e) {
            return "";
        }
    }

    public static long getSecondBetweenTime(long time1, long time2) {
        try {
            return (time2 - (time1 / 1000));
        } catch (Exception e) {
            return 0;
        }
    }

    public static long formatLong(String dateString, String format) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            Date date = simpleDateFormat.parse(dateString);
            return date != null ? date.getTime() : 0;
        } catch (ParseException e) {
            Log.e("Error", "String.formatLong ->> " + dateString + " ->> " + format);
            return 0;
        }
    }

}
