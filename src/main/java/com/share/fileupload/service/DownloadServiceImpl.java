package com.share.fileupload.service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.share.fileupload.entity.UserEntity;
import com.share.fileupload.model.DownloadModel;
import com.share.fileupload.model.ErrorModel;
import com.share.fileupload.model.ResultModel;
import com.share.fileupload.utils.CommonUtil;
import com.share.fileupload.utils.DownloadTask;

@Service("downloadService")
public class DownloadServiceImpl implements DownloadService {
    
    /** The file service. */
    @Autowired
    FileService fileService;
    
    public ResultModel downloadFile(DownloadModel downloadModel, UserEntity currentUser) {
        var result = new ResultModel();

        if (downloadModel == null || currentUser == null) {
            result.setError(new ErrorModel(new Exception(), "null parametters"));
            return result;
        }
       
        downloadExec(downloadModel, currentUser);

        return result;
    }
    
    private void downloadExec(DownloadModel downloadModel, UserEntity currentUser) {
        var downloadList = new ArrayList<DownloadModel>();
        downloadList.add(downloadModel);
        
        downloadExec(downloadList, currentUser);
    }
   
    private void downloadExec(ArrayList<DownloadModel> downloadList, UserEntity currentUser) {
        
        Path savePath = fileService.getUserSavePath(currentUser);
        
        ExecutorService pool = Executors.newFixedThreadPool(10);
        //ExecutorService  pool = Executors.newCachedThreadPool();
        for (DownloadModel downModel : downloadList) {
            
            var url = downModel.getUrl();
            var name = downModel.getName();
            if(!CommonUtil.urlCheck(url) || CommonUtil.isNull(name)) {
                continue;
            }
            
            name = "d_" + CommonUtil.getRandomString(10) + "_" + name;
            
            pool.submit(new DownloadTask(name, url, savePath, fileService, currentUser));
        }
        
        pool.shutdown();
        
        try {
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
