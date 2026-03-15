package com.academicvault.model;

public class Link {
    private int id;
    private int subjectId;
    private String title;
    private String url;
    private String dateAdded;

    public Link(int id, int subjectId, String title, String url, String dateAdded) {
        this.id = id;
        this.subjectId = subjectId;
        this.title = title;
        this.url = url;
        this.dateAdded = dateAdded;
    }

    public int getId() { return id; }
    public int getSubjectId() { return subjectId; }
    public String getTitle() { return title; }
    public String getUrl() { return url; }
    public String getDateAdded() { return dateAdded; }

    public void setTitle(String title) { this.title = title; }
    public void setUrl(String url) { this.url = url; }
}
