package com.share.fileupload.model;

public class UserModel extends BaseModel {

    private int id;

    private String email;

    private String password;
    
    private String newPassword;
    
    private String passwordAgain;

    private String name;

    private String lastName;

    private String roles;

    private boolean status;

    public UserModel() {}

    public UserModel(String email, String password, String name, String lastName) {
        super();
        this.email    = email;
        this.password = password;
        this.name     = name;
        this.lastName = lastName;
    }

    public UserModel(int id, String email, String password, String name, String lastName) {
        super();
        this.id       = id;
        this.email    = email;
        this.password = password;
        this.name     = name;
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getPasswordAgain() {
        return passwordAgain;
    }

    public void setPasswordAgain(String passwordAgain) {
        this.passwordAgain = passwordAgain;
    }
}