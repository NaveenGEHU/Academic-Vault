package com.academicvault.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.academicvault.model.*;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="academic_vault.db";
    private static final int DATABASE_VERSION=3;
    private static final String TABLE_SUBJECTS="subjects";
    private static final String TABLE_DOCUMENTS="documents";
    private static final String TABLE_NOTES="notes";
    private static final String TABLE_LINKS="links";

    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE IF NOT EXISTS "+ TABLE_SUBJECTS + 
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, subject_name TEXT NOT NULL, doc_count INTEGER DEFAULT 0)");
        
        db.execSQL("CREATE TABLE IF NOT EXISTS "+ TABLE_DOCUMENTS + 
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, subject_id INTEGER, " +
                "filePath TEXT NOT NULL, fileType TEXT NOT NULL, fileSize TEXT NOT NULL, " +
                "dateAdded TEXT, mimeType TEXT)");
        
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NOTES + 
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, content TEXT, " +
                "subject_id INTEGER, dateAdded TEXT)");
        
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_LINKS + 
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, url TEXT NOT NULL, " +
                "subject_id INTEGER, dateAdded TEXT)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NOTES + " (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, content TEXT, subject_id INTEGER, dateAdded TEXT)");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_LINKS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, url TEXT NOT NULL, subject_id INTEGER, dateAdded TEXT)");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_DOCUMENTS + " ADD COLUMN mimeType TEXT");
        }
    }

    public long insertSubject(String name ){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("subject_name", name);
        return db.insert(TABLE_SUBJECTS, null, values);
    }

    public Cursor getAllSubjects(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_SUBJECTS, null);
    }

    public void deleteSubject(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_SUBJECTS, "id=?", new String[]{String.valueOf(id)});
            db.delete(TABLE_DOCUMENTS, "subject_id=?", new String[]{String.valueOf(id)});
            db.delete(TABLE_NOTES, "subject_id=?", new String[]{String.valueOf(id)});
            db.delete(TABLE_LINKS, "subject_id=?", new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public long insertDocument(Document dc){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("title", dc.getTitle());
            values.put("subject_id", dc.getSubjectId());
            values.put("filePath", dc.getFilePath());
            values.put("fileType", dc.getFileType());
            values.put("fileSize", dc.getFileSize());
            values.put("dateAdded", dc.getDateAdded());
            values.put("mimeType", dc.getMimeType());
            
            long id = db.insert(TABLE_DOCUMENTS, null, values);
            if (id != -1) {
                db.execSQL("UPDATE " + TABLE_SUBJECTS + " SET doc_count=doc_count+1 WHERE id=" + dc.getSubjectId());
                db.setTransactionSuccessful();
            }
            return id;
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error inserting document", e);
            return -1;
        } finally {
            db.endTransaction();
        }
    }

    public void deleteDocument(Document dc){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            int rows = db.delete(TABLE_DOCUMENTS, "id=?", new String[]{String.valueOf(dc.getId())});
            if (rows > 0) {
                db.execSQL("UPDATE " + TABLE_SUBJECTS + " SET doc_count=doc_count-1 WHERE id=" + dc.getSubjectId());
                db.setTransactionSuccessful();
            }
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getAllDocuments(int subject_id){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_DOCUMENTS + " WHERE subject_id=?", new String[]{String.valueOf(subject_id)});
    }

    public long insertNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", note.getTitle());
        values.put("content", note.getContent());
        values.put("subject_id", note.getSubjectId());
        values.put("dateAdded", note.getDateAdded());
        return db.insert(TABLE_NOTES, null, values);
    }

    public Cursor getAllNotes(int subject_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NOTES + " WHERE subject_id=?", new String[]{String.valueOf(subject_id)});
    }

    public void deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, "id=?", new String[]{String.valueOf(id)});
    }

    public long insertLink(Link link) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", link.getTitle());
        values.put("url", link.getUrl());
        values.put("subject_id", link.getSubjectId());
        values.put("dateAdded", link.getDateAdded());
        return db.insert(TABLE_LINKS, null, values);
    }

    public Cursor getAllLinks(int subject_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_LINKS + " WHERE subject_id=?", new String[]{String.valueOf(subject_id)});
    }

    public void deleteLink(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LINKS, "id=?", new String[]{String.valueOf(id)});
    }
}
