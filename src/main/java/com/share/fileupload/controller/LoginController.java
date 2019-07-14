package com.share.fileupload.controller;

import java.util.HashMap;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.share.fileupload.entity.UserEntity;
import com.share.fileupload.model.ResponseModel;
import com.share.fileupload.model.UserModel;
import com.share.fileupload.service.UserService;
import com.share.fileupload.utils.UserUtil;

import io.netty.util.internal.StringUtil;

@Controller
public class LoginController extends BaseController {

    @Autowired
    private UserService userService;

    @RequestMapping(
        value  = { "/", "/login" },
        method = RequestMethod.GET
    )
    public ResponseModel login() {
        var currentUser = userService.getCurrentUser();

        if (currentUser.isAdmin()) {
            return new ResponseModel("redirect:/admin/index");
        } else if (!currentUser.isGuest()) {
            return new ResponseModel("redirect:/index");
        }

        var modelAndView = new ResponseModel();
        var userModel    = new UserModel();
        modelAndView.addObject("user", userModel); // registration icin

        modelAndView.setViewName("login");

        return modelAndView;
    }

    @RequestMapping(
        value  = "/registration",
        method = RequestMethod.POST
    )
    public ResponseModel registration(@Valid UserModel usermodel, BindingResult bindingResult) {
        var model = new ResponseModel();
        var userExists   = userService.findUserByEmail(usermodel.getEmail());
        var errors = new HashMap<String, String>();
        var notification = new HashMap<String, String>();
        
        if (userExists != null) {
            bindingResult.rejectValue("email",
                                      "error.user",
                                      "There is already a user registered with the email provided");
            errors.put("email", "There is already a user registered with the email provided");
        } else {
            if (!StringUtil.isNullOrEmpty(usermodel.getPassword())) {
                if (!UserUtil.checkStrongPassword(usermodel.getPassword())) {
                    bindingResult.rejectValue("password",
                            "error.model",
                            "New Password not strong");
                    errors.put("newPassword", "New Password not strong");
                }
            } else {
                bindingResult.rejectValue("password",
                        "error.user",
                        "Password not correct");
                errors.put("password", "Password not correct");
            }
        }

        if (bindingResult.hasErrors()) {
            //TODO:GA:
        } else {
            var user = new UserEntity();

            user.setEmail(usermodel.getEmail());
            user.setName(usermodel.getName());
            user.setLastName(usermodel.getLastName());
            user.setPassword(usermodel.getPassword());
            userService.saveUser(user);
            notification.put("success", "User has been registered successfully!");
        }
        usermodel.setErrors(errors);
        usermodel.setNotification(notification);
        model.addObject("user", usermodel);
        
        model.setViewName("redirect:/login");
        return model;
    }
}