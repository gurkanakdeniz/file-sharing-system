package com.share.fileupload.handler.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice
@RestController
public class GlobalExceptionHandler {
  
    @ExceptionHandler(value = Exception.class)
    public String handleException(Exception e) {
        return "oops";
    }
    
    @ExceptionHandler(value = RuntimeException.class)
    public String handleFoundException(RuntimeException e) {
        return "oops";
    }
}