package com.a2driano.note100.util;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by Andrii Papai on 16.02.2017.
 */

public class CommonToast {
    /** Common view toast method */
    public static void showToast(Activity activity, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }
}
