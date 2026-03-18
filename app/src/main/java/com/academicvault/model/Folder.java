package com.academicvault.model;

import android.content.Context;
import android.widget.Toast;

import java.io.File;

public class Folder {
    // -----------------CREATE SUBJECT FOLDER METHOD : CREATES FOLDER WITH SUBJECT ID AS NAME----------------------------------------
    public static void createSubjectFolder(Context context, long subjectId){
        File subjectFolder = new File(context.getExternalFilesDir(null),""+subjectId);
        if(!subjectFolder.exists()) {
            subjectFolder.mkdir();
        }
        boolean created = subjectFolder.mkdir();

    }
    public static void  deleteSubjectFolder (Context context,long subjectId){
        File subjectFolder = new File(context.getExternalFilesDir(null),""+subjectId);
        if(subjectFolder.exists()) {
            boolean deleted= subjectFolder.delete();
        }
    }
}
