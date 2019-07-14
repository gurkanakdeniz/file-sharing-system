package com.share.fileupload.model;

public class DownloadModel {

    private String url;
    
    private String name;

    public DownloadModel(String url, String name) {
        this.url = url;
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    
    public String getName() {
        return name;
    }

}
