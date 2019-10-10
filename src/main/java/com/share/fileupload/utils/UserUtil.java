package com.share.fileupload.utils;

import org.apache.commons.lang3.StringUtils;

import com.share.fileupload.entity.UserEntity;
import com.share.fileupload.model.UserModel;

import io.netty.util.internal.StringUtil;

public class UserUtil {
    
    public static UserModel convert(UserEntity user) {
        if (user == null) {
            return null;
        }
        var userModel = new UserModel();
        userModel.setEmail(user.getEmail());
        userModel.setId(user.getId());
        userModel.setLastName(user.getLastName());
        userModel.setName(user.getName());
        userModel.setPassword(user.getPassword());
        //userModel.setRoles(user.getRoles());
        
        return userModel;
    }
    
    /* 
    
    ^                 # start-of-string
    (?=.*[0-9])       # a digit must occur at least once
    (?=.*[a-z])       # a lower case letter must occur at least once
    (?=.*[A-Z])       # an upper case letter must occur at least once
    (?=.*[@#$%^&+=])  # a special character must occur at least once
    (?=\S+$)          # no whitespace allowed in the entire string
    .{8,}             # anything, at least eight places though
    $                 # end-of-string
    
    */
    public static boolean checkStrongPassword(String password) {
        if (StringUtil.isNullOrEmpty(password)) {
            return false;
        }
        
        String pattern = "(?=.{9,})(?=.*?[^\\w\\s])(?=.*?[0-9])(?=.*?[A-Z]).*?[a-z].*";
        return password.matches(pattern);
    }


}
