package com.share.fileupload.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.share.fileupload.entity.UserEntity;

public interface UserService {

    public UserEntity findUserByEmail(String email);

    public UserEntity findUserById(int id);

    public UserDetails loadUserByUsername(String userName);

    public void saveUser(UserEntity user);

    public void turnStatusUser(int id);

    public void updateUser(UserEntity user);

    public List<UserEntity> getAllUsers();

    public UserEntity getCurrentUser();

    public boolean isLoginCurrentUser();
}
