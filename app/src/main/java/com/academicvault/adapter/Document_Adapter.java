package com.academicvault.adapter;

import android.content.Context;
import android.database.Cursor;

import com.academicvault.model.Document;
import com.academicvault.model.Subject;

import java.util.ArrayList;

public class Document_Adapter {
    private Subject subject;
    private ArrayList<Document> notes;
    private ArrayList<Document> books;
    private ArrayList<Document> assignment;
    private ArrayList<Document> important;
    private ArrayList<Document> pyqs;
    private Context context;
    public Document_Adapter(Context contex,Cursor cursor,Subject subject){
        this.context=contex;
        this.subject=subject;
        notes= new ArrayList<>();
        books= new ArrayList<>();
        assignment= new ArrayList<>();
        important= new ArrayList<>();
        pyqs= new ArrayList<>();
        ArrayList<Document> documents=new ArrayList<>(); // TEMP HOLDER FOR ALL DOCUMENTS OF THE SUBJECT
        while (cursor.moveToNext()){
            documents.add(new Document(cursor.getInt(0),cursor.getInt(3),
                    cursor.getString(2), cursor.getString(4), cursor.getString(5),
                    cursor.getString(6), cursor.getString(7)));
        }
        cursor.close();
//        DIFFERENTIATING DOCS ON BASIS OF TYPES
        for (Document item:documents) {
            if(item.getFileType().equals("Notes")){
                notes.add(item);
            }
            if(item.getFileType().equals("Books")){
                books.add(item);
            }
            if(item.getFileType().equals("Assignment")){
                assignment.add(item);
            }
            if(item.getFileType().equals("Important")){
                important.add(item);
            }
            if(item.getFileType().equals("PYQs")){
                pyqs.add(item);
            }
        }
    }
}
