package com.academicvault.model;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
// ------------------GETING META DATA OF THE FILE------------------------------------------------

public class MetaData {
//------------GET NAME---------------------
    public static String getFileName(Context context, Uri uri){
        String name = null;
        Cursor cursor = context.getContentResolver().query(uri,null,null,null,null);
        if(cursor != null && cursor.moveToFirst()){
            name = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
        }
        return  name;
    }
// -----------------GET FILE SIZE--------------------------
    public static String getFileSize(Context context, Uri uri){
        String size = null;
        Cursor cursor = context.getContentResolver().query(uri,null,null,null,null);
        if(cursor != null && cursor.moveToFirst()){
            int sizeInBytes = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.SIZE)));
            size= String.valueOf(sizeInBytes/1024.0);
        }
        return  size+"mb";
    }
// -----------------------------GET DATE-------------------------------------
    public static String getDate() {
        // Define the format
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        // Get current system date
        Date date = new Date();
        // Return formatted string
        return formatter.format(date);
    }

}
