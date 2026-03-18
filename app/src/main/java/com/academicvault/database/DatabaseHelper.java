package com.academicvault.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import com.academicvault.model.*;

public class DatabaseHelper extends SQLiteOpenHelper {
    //database name
    private static final String DATABASE_NAME="academic_vault.db";
    // Database Version
    private static final int DATABASE_VERSION=1;
    //Table names
    private static final String TABLE_SUBJECTS="subjects";
    private static final String TABLE_DOCUMENTS="documents";

    //constructor
    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db){
    // Creating subject table SUB_NAME , ID ,DATE
        String createSubjectTable="CREATE TABLE IF NOT EXISTS "+
                TABLE_SUBJECTS + "( id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "subject_name TEXT NOT NULL,"+
                "doc_count INTEGER DEFAULT 0)";
        String createDocumentsTable =" CREATE TABLE IF NOT EXISTS "+
                TABLE_DOCUMENTS+ " ( id INTEGER PRIMARY KEY AUTOINCREMENT ,"+
                "title TEXT NOT NULL, "+
                "subject_id INTEGER, "+
                "filePath TEXT NOT NULL ,"+
                "fileType TEXT NOT NULL ,"+
                "fileSize TEXT NOT NULL ,"+
                "dateAdded TEXT)";
        db.execSQL(createSubjectTable);
        db.execSQL(createDocumentsTable);
    }
//    Runs when the database needs to be upgraded
    public void onUpgrade(SQLiteDatabase db,int oldVersion ,int newVersion){
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_SUBJECTS);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_DOCUMENTS);
        onCreate(db);
    }
// ---------------------SUBJECT METHODS ----------------

//    INSERT A NEW SUBJECT WITH NAME TO THE DATABASE
    public long insertSubject(String name ){
        SQLiteDatabase db =this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("subject_name",name);
        return db.insert(TABLE_SUBJECTS,null,values);
    }
//    GET ALL THE NAMES OF SUBJECT
    public Cursor getAllSubjects(){
        SQLiteDatabase db=this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+TABLE_SUBJECTS,null);
    }
//    DELETE A SUBJECT
    public void deleteSubject(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
//    delete(TABLENAME , WHERE CLAUSE , STRING[])
        db.delete(TABLE_SUBJECTS, "id=?", new String[]{String.valueOf(id)});
        db.delete(TABLE_DOCUMENTS,"subject_id=?",new String[]{String.valueOf(id)});
    }
//--------------------DOCUMENT METHODS--------------------------------
//    INSERT A DOCUMENT
    public long insertDocument(Document dc){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("title",dc.getTitle());
        values.put("subject_id",dc.getSubjectId());
        values.put("fileType",dc.getFileType());
        values.put("fileSize",dc.getFileSize());
        values.put("dateAdded",dc.getDateAdded());
        db.execSQL("UPDATE "+TABLE_SUBJECTS+" SET doc_count=doc_count+1 WHERE id="+dc.getSubjectId());
        return db.insert(TABLE_DOCUMENTS,null,values);
    }
//    DELETE A DOCUMENT FROM THE DATABASE
    public void deleteDocument(Document dc){
        SQLiteDatabase db= this.getWritableDatabase();
        db.delete(TABLE_DOCUMENTS,"id=?",new String[]{String.valueOf(dc.getDocId())});
        db.execSQL("UPDATE "+TABLE_SUBJECTS+" SET doc_count=doc_count-1 WHERE id="+dc.getSubjectId());
    }
//    GET ALL DOCUMENT OF A SUBJECT BY HELP OF SUBJECT ID
    public Cursor getAllDocuments(int subject_id){
        SQLiteDatabase db= this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+TABLE_DOCUMENTS+" WHERE subject_id=?",new String[]{String.valueOf(subject_id)});
    }
}
