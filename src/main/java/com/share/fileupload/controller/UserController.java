package com.share.fileupload.controller;

import java.util.HashMap;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.share.fileupload.model.ResponseModel;
import com.share.fileupload.model.UserModel;
import com.share.fileupload.service.UserService;
import com.share.fileupload.utils.UserUtil;

import io.netty.util.internal.StringUtil;

@Controller
public class UserController extends BaseController {
    
    @Autowired
    private UserService userService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    
    @GetMapping(value = {"/profile", "/admin/profile"})
    public ResponseModel index() {
        
        var model = new ResponseModel();
        
        if (!userService.isLoginCurrentUser()) {
            model.setViewName("/error");
            return model;
        }
        
        var currentUser = userService.getCurrentUser();
        model.addObject("user", UserUtil.convert(currentUser));
        
        model.setViewName("user/profile");
        return model;
    }
    
    @PostMapping(value = {"/profile", "/admin/profile"})
    public ResponseModel update(@Valid UserModel usermodel, BindingResult bindingResult) {
        var model = new ResponseModel();
        var errors = new HashMap<String, String>();
        var notification = new HashMap<String, String>();
        
        if (!userService.isLoginCurrentUser()) {
            model.setViewName("/error");
            return model;
        }
        
        var currentUser = userService.getCurrentUser();
        var userExists   = userService.findUserByEmail(usermodel.getEmail());

        if (userExists != null && !currentUser.getId().equals(userExists.getId())) {
            bindingResult.rejectValue("email",
                                      "error.user",
                                      "There is already a user registered with the email provided");
            errors.put("email", "There is already a user registered with the email provided");
        } else {
            var inputCurrentPassword = usermodel.getPassword();
            var passMatch = false;
            if (!StringUtil.isNullOrEmpty(inputCurrentPassword)) {
                passMatch = bCryptPasswordEncoder.matches(inputCurrentPassword, currentUser.getPassword());  
            }
            
            if (!passMatch) {
                bindingResult.rejectValue("password",
                        "error.user",
                        "Password not correct");
                errors.put("password", "Password not correct");
            } else {
                var newPassword = usermodel.getNewPassword();
                var passwordAgain = usermodel.getPasswordAgain();
                if (!StringUtil.isNullOrEmpty(newPassword) && !StringUtil.isNullOrEmpty(passwordAgain)) {
                    if (!passwordAgain.equals(newPassword)) {
                        bindingResult.rejectValue("password",
                                "error.user",
                                "Two Password not correct");
                        errors.put("passwordAgain", "Two Password not correct");
                    } else {
                        if (!UserUtil.checkStrongPassword(newPassword)) {
                            bindingResult.rejectValue("password",
                                    "error.model",
                                    "New Password not strong");
                            errors.put("newPassword", "New Password not strong");
                        }
                    }
                }
               
                if (!bindingResult.hasErrors()) {
                    currentUser.setEmail(usermodel.getEmail());
                    currentUser.setName(usermodel.getName());
                    currentUser.setLastName(usermodel.getLastName());
                    if (!StringUtil.isNullOrEmpty(newPassword)) {
                        currentUser.setPassword(bCryptPasswordEncoder.encode(newPassword));
                    }
                    
                    userService.updateUser(currentUser);
                }
            }
        }
        
        if (bindingResult.hasErrors()) {
            usermodel.setErrors(errors);
            usermodel.setNotification(notification);
            model.addObject("user", usermodel);
        } else {
            var user = UserUtil.convert(currentUser);
            notification.put("success", "Profile updated!");
            user.setNotification(notification);
            model.addObject("user", user);
        }
        
        model.setViewName("user/profile");
        return model;
        
    }


}
