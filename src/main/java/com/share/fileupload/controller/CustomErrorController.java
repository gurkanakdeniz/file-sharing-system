package com.share.fileupload.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.share.fileupload.model.ResponseModel;

@Controller
public class CustomErrorController extends BaseController implements ErrorController {

    @GetMapping(value = "/error")
    public ResponseModel handleError(HttpServletRequest request) {
        var status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            var statusCode = Integer.valueOf(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return new ResponseModel("redirect:/oops");
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return new ResponseModel("redirect:/oops");
            }
        }

        return new ResponseModel("redirect:/oops");
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}