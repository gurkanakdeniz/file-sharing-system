package com.share.fileupload.model;

public class UserFileProcess {
    private Integer userId;
    private Integer fileId;
    
    public UserFileProcess(Integer userId, Integer fileId) {
        super();
        this.userId = userId;
        this.fileId = fileId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }
}