package com.share.fileupload.model;

import java.util.HashMap;

public class BaseModel {
    
    private HashMap<String, String> notification = new HashMap<String, String> ();
    private HashMap<String, String> errors = new HashMap<String, String> ();

    public HashMap<String, String> getErrors() {
        return errors;
    }

    public void setErrors(HashMap<String, String> errors) {
        this.errors = errors;
    }

    public HashMap<String, String> getNotification() {
        return notification;
    }

    public void setNotification(HashMap<String, String> notification) {
        this.notification = notification;
    }
}