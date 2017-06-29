package com.a2driano.note100.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Andrii Papai on 21.12.2016.
 */

public class UtilNote {
    /**
     * Modifier for short date vision
     */
    public static String getReadableModifiedDate(Date date) {
//        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat sdf;
        //if have current year, "year" don`t display
        if (checkCurrentYear(date)) {
            sdf = new SimpleDateFormat("dd MMM", Locale.getDefault());
        } else {
            sdf = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());
        }
        String displayDate = sdf.format(date);
        return displayDate;
    }

    private static boolean checkCurrentYear(Date date) {
        Calendar c = Calendar.getInstance();
        int year = date.getYear() + 1900; //strange hack
        int yearCurrent = c.get(Calendar.YEAR);
        if (year == yearCurrent)
            return true;
        else
            return false;
    }

    /**
     * Modifier for date and time vision
     */
    public static String getReadableModifiedDateForNoteActivity(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault());
        //if have current year, "year" don`t display
        if (checkCurrentYear(date)) {
            sdf = new SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault());
        } else {
            sdf = new SimpleDateFormat("dd MMM, yyyy HH:mm", Locale.getDefault());
        }
        String displayDate = sdf.format(date);
        return displayDate;
    }
}
