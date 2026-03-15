package com.academicvault.model;

public class Note {
    private int id;
    private int subjectId;
    private String title;
    private String content;
    private String dateAdded;

    public Note(int id, int subjectId, String title, String content, String dateAdded) {
        this.id = id;
        this.subjectId = subjectId;
        this.title = title;
        this.content = content;
        this.dateAdded = dateAdded;
    }

    public int getId() { return id; }
    public int getSubjectId() { return subjectId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getDateAdded() { return dateAdded; }

    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
}
