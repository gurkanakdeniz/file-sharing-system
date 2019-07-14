package com.share.fileupload.enm;

public enum ResultStatus {

    ERROR("21"),

    SUCCESS("42");

    private String code;

    ResultStatus(String code) {
        this.code = code;
    }

    public String code() {
        return this.code;
    }
}