package com.share.fileupload.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity
@Table(name = "user_file")
public class UserFileEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_file_id")
    private Integer id;

    @Column(name = "user_id")
    @JoinTable(
        name                     = "user",
        joinColumns              = @JoinColumn(
            name                 = "user_id",
            referencedColumnName = "user_id"
        ) ,
        inverseJoinColumns       = @JoinColumn(
            name                 = "user_id",
            referencedColumnName = "user_id"
        )
    )
    private Integer user_id;

    @Column(name = "file_id")
    @JoinTable(
        name                     = "file",
        joinColumns              = @JoinColumn(
            name                 = "file_id",
            referencedColumnName = "file_id"
        ) ,
        inverseJoinColumns       = @JoinColumn(
            name                 = "file_id",
            referencedColumnName = "file_id"
        )
    )
    private Integer file_id;
    
//    @ManyToOne
//    @MapsId("user_id")
//    private UserEntity user;
    
//    @ManyToOne
//    @MapsId("file_id")
//    private FileEntity file;

//    public UserEntity getUser() {
//        return user;
//    }
//
//    public void setUser(UserEntity user) {
//        this.user = user;
//    }

//    public FileEntity getFile() {
//        return file;
//    }
//
//    public void setFile(FileEntity file) {
//        this.file = file;
//    }

    public Integer getFile_id() {
        return file_id;
    }

    public void setFile_id(Integer file_id) {
        this.file_id = file_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }
}