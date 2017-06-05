package com.a2driano.note100.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Andrii Papai on 21.12.2016.
 */

public class UtilNote {
    /** Modifier for short date vision */
    public static String getReadableModifiedDate(Date date) {
//        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String displayDate = sdf.format(date);
        return displayDate;
    }

    /** Modifier for date and time vision */
    public static String getReadableModifiedDateForNoteActivity(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String displayDate = sdf.format(date);
        return displayDate;
    }
}
