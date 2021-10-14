package com.example.notesapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static String getFormattedDate(Date date) {
        return new SimpleDateFormat("E, dd MMM yyyy hh:mm a", Locale.getDefault()).format(date);
    }

}
