package com.share.fileupload.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.share.fileupload.model.ResponseModel;
import com.share.fileupload.service.UserService;

@Controller
public class CommonController extends BaseController {

    @Autowired
    private UserService userService;

    @RequestMapping(
        value  = "/index",
        method = RequestMethod.GET
    )
    public ResponseModel index() {
        var modelAndView = new ResponseModel();

        if (!userService.isLoginCurrentUser()) {
            return new ResponseModel("redirect:/login");
        }

        var auth = SecurityContextHolder.getContext().getAuthentication();
        var user = userService.findUserByEmail(auth.getName());

        modelAndView.addObject("userName",
                               "Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
        modelAndView.addObject("adminMessage", "Content Available Only for Users");
        modelAndView.setViewName("index");

        return modelAndView;
    }

    @RequestMapping(
        value  = "/oops",
        method = RequestMethod.GET
    )
    public ResponseModel oops() {
        var modelAndView = new ResponseModel();

        modelAndView.addObject("errorMessage", "something wrong");
        modelAndView.setViewName("oops");

        return modelAndView;
    }
}
