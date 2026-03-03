package com.academicvault.model;

public class Subject {
    private int id ;
    private String name;
    private int docCount;
    public Subject(int id,String name,int docCount){
        this.id=id;
        this.name=name;
        this.docCount=docCount;
    }
    public void setName(String name){this.name=name;}
    public void setId(int id){this.id=id;}
    public void setDocCount(int docCount){this.docCount=docCount;}
    public String getName(){return name;}
    public int getId(){return id;}
    public int getDocCount(){return docCount;}
}
