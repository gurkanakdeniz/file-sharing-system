package com.share.fileupload.model;

import org.springframework.web.servlet.ModelAndView;

public class ResponseModel extends ModelAndView {

    public ResponseModel() {
        super();
    }

    public ResponseModel(String string) {
        super(string);
    }
}