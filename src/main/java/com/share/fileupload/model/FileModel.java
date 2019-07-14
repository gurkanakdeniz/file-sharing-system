package com.share.fileupload.model;

import java.nio.file.Path;

import java.time.LocalDateTime;

import com.share.fileupload.utils.CommonUtil;

public class FileModel extends BaseModel {
    
    private int index;

    private String name;

    private Long size;

    private LocalDateTime date;

    private String dateString;

    private String sizeString;

    public FileModel(int index, Path path, Long size, LocalDateTime date) {
        super();
        this.index      = index;
        this.name       = path.toString();
        this.size       = size;
        this.date       = date;
        this.dateString = CommonUtil.getFileDateFormat(this.date);
        this.sizeString = CommonUtil.getFileSizeString(this.size);
    }

    public FileModel(String name, Long size, LocalDateTime date) {
        super();
        this.name       = name;
        this.size       = size;
        this.date       = date;
        this.dateString = CommonUtil.getFileDateFormat(this.date);
        this.sizeString = CommonUtil.getFileSizeString(this.size);
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getSizeString() {
        return sizeString;
    }

    public void setSizeString(String sizeString) {
        this.sizeString = sizeString;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    } 
}
