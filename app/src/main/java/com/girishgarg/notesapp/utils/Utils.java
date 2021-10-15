package com.girishgarg.notesapp.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.inputmethod.InputMethodManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static String getFormattedDate(Date date) {
        return new SimpleDateFormat("E, dd MMM yyyy hh:mm a", Locale.getDefault()).format(date);
    }

    public static void hideKeyBoard(Context context) {
        ((InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    public static String getPathFromUri(Uri contentUri, Context context) {
        String filePath;
        Cursor cursor = context.getContentResolver()
                .query(contentUri, null, null, null, null);
        if (cursor == null) {
            filePath = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }

        return filePath;
    }


}
