package com.share.fileupload.configuration;

import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageProperties {

    private String location = "uploadFiles";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}