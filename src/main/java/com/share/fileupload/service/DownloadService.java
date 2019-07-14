package com.share.fileupload.service;

import com.share.fileupload.entity.UserEntity;
import com.share.fileupload.model.DownloadModel;
import com.share.fileupload.model.ResultModel;

public interface DownloadService {
    
    ResultModel downloadFile(DownloadModel downloadModel, UserEntity currentUser);
}
