package com.academicvault.model;

public class Document {
    private long id;
    private long subject_id;
    private String title;
    private String filePath;
    private String fileType;
    private String fileSize;
    private String dateAdded;
    public Document(long Subject_id,String title ,String filePath,String fileType ,String fileSize,String dateAdded){
        this.subject_id=Subject_id;
        this.title=title;
        this.filePath=filePath;
        this.fileType=fileType;
        this.fileSize=fileSize;
        this.dateAdded=dateAdded;
    }
    public Document(long id ,long Subject_id,String title ,String filePath,String fileType ,String fileSize,String dateAdded){
        this.id=id;
        this.subject_id=Subject_id;
        this.title=title;
        this.filePath=filePath;
        this.fileType=fileType;
        this.fileSize=fileSize;
        this.dateAdded=dateAdded;
    }
    public void setId(long id){this.id= id;}
    public void setTitle(String title){this.title= title;}
    public void setSubjectId(long subject_id){this.subject_id= subject_id;}
    public void setFilePath(String filePath){this.filePath= filePath;}
    public void setFileSize(String fileSize){this.fileSize= fileSize;}
    public void setDateAdded(String dateAdded){this.dateAdded= dateAdded;}
    public void setFileType(String fileType){this.fileType= fileType;}
    public String getTitle(){return title;}
    public long getSubjectId(){return subject_id;}
    public String getFilePath(){return filePath;}
    public String getFileSize(){return fileSize;}
    public String getDateAdded(){return dateAdded;}
    public long getDocId(){return id;}
    public String getFileType(){return fileType;}

}
