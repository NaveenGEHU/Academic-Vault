package com.academicvault.model;

public class Document {
    private int id;
    private int subject_id;
    private String title;
    private String filePath;
    private String fileType;
    private String fileSize;
    private String dateAdded;
    private String mimeType;

    public Document(int id, int subject_id, String title, String filePath, String fileType, String fileSize, String dateAdded, String mimeType) {
        this.id = id;
        this.subject_id = subject_id;
        this.title = title;
        this.filePath = filePath;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.dateAdded = dateAdded;
        this.mimeType = mimeType;
    }

    // Overloaded constructor for backward compatibility if needed, but preferred to use the one above
    public Document(int id, int subject_id, String title, String filePath, String fileType, String fileSize, String dateAdded) {
        this(id, subject_id, title, filePath, fileType, fileSize, dateAdded, null);
    }

    public void setTitle(String title){this.title= title;}
    public void setSubjectId(int subject_id){this.subject_id= subject_id;}
    public void setFilePath(String filePath){this.filePath= filePath;}
    public void setFileSize(String fileSize){this.fileSize= fileSize;}
    public void setDateAdded(String dateAdded){this.dateAdded= dateAdded;}
    public void setFileType(String fileType){this.fileType= fileType;}
    public void setMimeType(String mimeType){this.mimeType = mimeType;}

    public String getTitle(){return title;}
    public int getSubjectId(){return subject_id;}
    public String getFilePath(){return filePath;}
    public String getFileSize(){return fileSize;}
    public String getDateAdded(){return dateAdded;}
    public String getFileType(){return fileType;}
    public String getMimeType(){return mimeType;}
    public int getId(){return id;}
}
