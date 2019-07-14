package com.share.fileupload.model;

public class ErrorModel extends BaseModel {

    private Exception ex;

    private String description;

    public ErrorModel() {}

    public ErrorModel(Exception ex) {
        this.ex = ex;
    }

    public ErrorModel(Exception ex, String desc) {
        this.ex          = ex;
        this.description = desc;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Exception getEx() {
        return ex;
    }

    public void setEx(Exception ex) {
        this.ex = ex;
    }
}
