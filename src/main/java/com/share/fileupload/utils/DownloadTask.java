package com.share.fileupload.utils;

import java.nio.file.Path;

import org.apache.commons.io.FilenameUtils;

import com.share.fileupload.entity.UserEntity;
import com.share.fileupload.service.FileService;

public class DownloadTask implements Runnable {

    private String name;
    private final String urlPath;
    private final Path savePath;
    private final FileService fileService;
    private final UserEntity currentUser;
    
    public DownloadTask(String name, String urlPath, Path savePath, FileService fileService, UserEntity currentUser) {
        this.name = name;
        this.urlPath = urlPath;
        this.savePath = savePath;
        this.fileService = fileService;
        this.currentUser = currentUser;
    }

    @Override
    public void run() {
        // surround with try-catch if downloadFile() throws something
//        downloadFile(name, toPath);
        
        DownloadOperations.downloadWithApacheCommons(urlPath, name, savePath);
        
        var fileName = name + "." + CommonUtil.checkSafeFileNameWithDownload(FilenameUtils.getExtension(urlPath));
        var filePath = savePath.resolve(fileName);
        
        fileService.insertFile(filePath, currentUser);
    }
    
    
    
}