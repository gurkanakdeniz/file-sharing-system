package com.share.fileupload.model;

public class UserFileListModel {
    private String name;
    private String date;
    private String size;
    private UserFileProcess process;

    public UserFileListModel() {

    }

    public UserFileListModel(String name, String date, String size, UserFileProcess process) {
        super();
        this.name = name;
        this.date = date;
        this.size = size;
        this.process = process;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public UserFileProcess getProcess() {
        return process;
    }

    public void setProcess(UserFileProcess process) {
        this.process = process;
    }

}
