package com.share.fileupload.service;

import java.nio.file.Path;

import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.share.fileupload.entity.FileEntity;
import com.share.fileupload.entity.UserEntity;
import com.share.fileupload.entity.UserFileEntity;
import com.share.fileupload.model.ResultModel;

public interface FileService {
    
    ResultModel changeFileName(UserEntity user, String fileName, String newFileName);

    ResultModel deleteAll();
    
    ResultModel deleteFile(UserEntity user, String fileName);
    
    ResultModel deleteFile(UserEntity user, String fileName, boolean deleteOnDisk);

    void insertFile(Path filePath, UserEntity user);
    
    void init();

    Path getUserSavePath(UserEntity user);

    Path load(Path userPath, String filename);

    Stream<Path> loadAll(UserEntity user);

    Resource loadAsResource(UserEntity user, String filename);
    
    ResultModel removeExtension(UserEntity user, String fileName);

    ResultModel save(MultipartFile file);

    ResultModel save(MultipartFile file, boolean removeExtension);

    ResultModel save(MultipartFile file, String customExtension);

    ResultModel save(MultipartFile file, boolean removeExtension, boolean useZip);

    ResultModel save(MultipartFile file, boolean removeExtension, boolean useZip, boolean useRandomName);

    ResultModel save(MultipartFile file, UserEntity user, boolean removeExtension, boolean useZip,
            boolean useRandomName, String customExtension);

    void saveFile(FileEntity file);

    void saveUserFile(UserFileEntity userFile);

    String shareFile(String filePath);

    String sharedFile(String hash);
}
