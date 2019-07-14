package com.share.fileupload.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import com.share.fileupload.entity.FileEntity;
import com.share.fileupload.entity.UserEntity;
import com.share.fileupload.model.FileModel;
import com.share.fileupload.model.UserModel;

public class CommonUtil {

    private static final String dateTimePattern = "yyyy-MM-dd HH:mm:ss";

    public static String int2String(int value) {
        return Integer.toString(value);
    }

    public static String int2String(Integer value) {
        return (value == null)
               ? ""
               : value.toString();
    }

    public static String int2StringNotZero(int value) {
        return (value == 0)
               ? ""
               : Integer.toString(value);
    }

    public static String long2String(Long value) {
        return (value == null)
               ? ""
               : value.toString();
    }

    public static List<FileModel> prepareFiles(Stream<Path> filesPaths) {
        List<FileModel> models = new ArrayList<>();

        //TODO:GA: kontrol et?
        AtomicInteger counter = new AtomicInteger(0);
        filesPaths.forEach(f -> {        
            models.add(new FileModel(counter.getAndIncrement(), f.getFileName(), getFileSize(f), getFileDate(f)));
        });

        return models;
    }

    public static List<UserModel> prepareUsers(List<UserEntity> users) {
        List<UserModel> models = new ArrayList<>();

        for (UserEntity user : users) {
            models.add(user.toModel());
        }

        return models;
    }

    public static LocalDateTime getFileDate(Path file) {
        LocalDateTime result = null;

        try {
            BasicFileAttributes fileAttr = Files.readAttributes(file, BasicFileAttributes.class);

            result = LocalDateTime.ofInstant(fileAttr.creationTime().toInstant(), ZoneId.systemDefault());
        } catch (Exception e) {
            result = LocalDateTime.of(2005, 3, 24, 0, 0);
            e.printStackTrace();
        }

        return result;
    }

    public static String getFileDateFormat(LocalDateTime localDateTime) {
        if (isNull(localDateTime)) {
            return "";
        }

        String            result    = "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimePattern);

        result = localDateTime.format(formatter);

        return result;
    }

    public static String getFileDateFormat(Path file) {
        if (isNull(file)) {
            return "";
        }

        String result = "";

        result = getFileDateFormat(getFileDate(file));

        return result;
    }

    public static Long getFileSize(Path file) {
        Long result = null;

        try {
            result = Files.size(file);
        } catch (Exception e) {
            result = (long) 42;
            e.printStackTrace();
        }

        return result;
    }

    public static String getFileSizeString(Long value) {
        String result = "";

        try {
            result = FileUtils.byteCountToDisplaySize(value);
        } catch (Exception e) {
            result = "";
            e.printStackTrace();
        }

        return result;
    }

    public static String getFileSizeString(Path file) {
        String result = "";

        try {
            result = getFileSizeString(Files.size(file));
        } catch (Exception e) {
            result = "";
            e.printStackTrace();
        }

        return result;
    }

    public static boolean isNull(Object item) {
        return item == null;
    }
    
    public static boolean isEmpty(Set item) {
        return (item == null || item.isEmpty());
    }
    
    public static <T> boolean isEmpty(List<T> item) {
        return (item == null || item.isEmpty());
    }

    public static String getRandomString() {
        int length = 50;

        return getRandomString(length);
    }

    public static String getRandomString(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }
    
    public static boolean urlCheck(String url) {
        
        //TODO:GA: url check
        
        if (isNull(url) || url.isEmpty()) {
            return false;
        }
        
        return true;
    }
    
    public static String checkSafeFileName(String fileName) throws Exception {
        
        if (StringUtils.isBlank(fileName)) {
            throw new Exception();
        }
        
        String outFileName = fileName.replaceAll("[^a-zA-Z0-9\\_\\- ]", "");
        
        if ((!fileName.equals(outFileName) || fileName.length() != outFileName.length())) {
            throw new Exception();
        }
        
        return outFileName;
    }
    
    public static String checkSafeFileNameWithDownload(String fileName) {
        
        String outFileName = fileName.replaceAll("[^a-zA-Z0-9\\_\\-]", "");
        
        if (!fileName.equals(outFileName) || fileName.length() != outFileName.length()) {
            return "custom";
        }
        
        return outFileName;
    }
    
    public static String getFileNameReqParam(Map<String,String> allRequestParams) {        
        String paramName = "fileName";
        return  getFileNameReqParam(allRequestParams, paramName);
    }
    
    public static String getFileNameReqParam(Map<String,String> allRequestParams, String paramName) {        
        return  allRequestParams.get(paramName);
    }
    
    public static FileEntity getFileEntity(Set<FileEntity> files, String fileName) {
        
        for (FileEntity fileEntity : files) {
            if (fileEntity.getName().equals(fileName) && fileEntity.isActive()) {
                return fileEntity;
            }
        }
        
        return null;
    }
    
    public static String getFileName (Set<FileEntity> files,  Integer fileId) {
        boolean checkActive = true;
        return getFileName(files, fileId, checkActive);
    }
    
    public static String getFileName (Set<FileEntity> files,  Integer fileId, boolean checkActive) {
        
        for (FileEntity fileEntity : files) {
            if (fileEntity.getId().equals(fileId) && (fileEntity.isActive() || !checkActive)) {
                return fileEntity.getName();
            }
        }
        
        return "";
    }
    
}
