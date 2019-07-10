package com.fagi.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {
    public static String convertDate(Date date) {
        if (date == null) {
            return "";
        }

        long now = new Date().getTime();
        long then = date.getTime();
        long diff = now - then;

        long diffDays = diff / (24 * 60 * 60 * 1000);
        if (diffDays != 0) {
            SimpleDateFormat format = new SimpleDateFormat(
                    diffDays > 365 ? "MM/dd/yyyy" : diffDays < 7 ? "EEE" : "MMM d");
            return format.format(date);
        }

        long diffHours = diff / (60 * 60 * 1000) % 24;
        if (diffHours != 0) {
            SimpleDateFormat format = new SimpleDateFormat("h:mm a");
            return format.format(date);
        }

        long diffMinutes = diff / (60 * 1000) % 60;
        if (diffMinutes != 0) {
            return diffMinutes + " min";
        }
        return "now";
    }
}
