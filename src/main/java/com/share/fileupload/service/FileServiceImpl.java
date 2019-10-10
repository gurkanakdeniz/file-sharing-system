package com.share.fileupload.service;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.share.fileupload.configuration.StorageProperties;
import com.share.fileupload.entity.FileEntity;
import com.share.fileupload.entity.ShareFileEntity;
import com.share.fileupload.entity.UserEntity;
import com.share.fileupload.entity.UserFileEntity;
import com.share.fileupload.model.ResultModel;
import com.share.fileupload.repository.FileRepository;
import com.share.fileupload.repository.ShareFileRepository;
import com.share.fileupload.repository.UserFileRepository;
import com.share.fileupload.utils.CommonUtil;

@Service("fileService")
public class FileServiceImpl implements FileService {

    private final Path rootLocation;

    @Autowired
    UserService userService;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private UserFileRepository userFileRepository;
    
    @Autowired
    private ShareFileRepository shareFileRepository;

    @Autowired
    public FileServiceImpl(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
        init();
    }

    private void addFileToZip(final String fileName, final InputStream inputStream, final Path zipPath)
            throws IOException, ArchiveException {

        // TODO:GA: duzenle calisiyor
        var archiveStream = new FileOutputStream(zipPath.toString());
        var archive = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP, archiveStream);
        var entryName = fileName;
        var entry = new ZipArchiveEntry(entryName);

        archive.putArchiveEntry(entry);

        var input = new BufferedInputStream(inputStream);

        IOUtils.copy(input, archive);
        input.close();
        archive.closeArchiveEntry();
        archive.finish();
        archiveStream.close();
    }

    private void copyFile(final InputStream inputStream, final Path savePath, final String filename,
            String customExtension, boolean removeExtension, boolean useZip, boolean useRandomName)
            throws IOException, ArchiveException {
        var saveFileName = filename;
        var saveInputStream = inputStream;

        if (useRandomName) {
            saveFileName = CommonUtil.getRandomString(50) + "." + FilenameUtils.getExtension(saveFileName);
        }

        if (useZip) {
            Path zipPath = getZip(savePath);

            addFileToZip(saveFileName, saveInputStream, zipPath);
            insertFile(zipPath);
        } else {
            if (!removeExtension && !StringUtils.isEmpty(customExtension)) {
                saveFileName = FilenameUtils.removeExtension(saveFileName);
                saveFileName = saveFileName + "." + customExtension;
            }

            if (removeExtension) {
                saveFileName = FilenameUtils.removeExtension(saveFileName);
            }

            var filePath = savePath.resolve(saveFileName);

            Files.copy(saveInputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            insertFile(filePath);
        }
    }

    private void createFile(Path location) {
        try {
            Files.createFile(location);
        } catch (IOException e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void createFolder() {
        var folder = "";

        createFolder(this.rootLocation, folder);
    }

    private void createFolder(Path location) {
        var folder = "";

        createFolder(location, folder);
    }

    private void createFolder(Path location, String folder) {
        try {
            var folderPath = location.resolve(folder);

            Files.createDirectories(folderPath);
        } catch (IOException e) {
            e.printStackTrace();

            // TODO:GA: ex
            // throw new GlobalFileException("Could not initialize storage", e);
        }
    }

    public ResultModel deleteAll() {
        var result = new ResultModel();

        try {
            FileSystemUtils.deleteRecursively(rootLocation.toFile());
        } catch (Exception e) {
            result.setError(e);
        }

        return result;
    }
    
    public void init() {
        createFolder();
    }

    private void insertFile(Path filePath) {
        var currentUser = userService.getCurrentUser();
        insertFile(filePath, currentUser);
    }
    
    public void insertFile(Path filePath, UserEntity currentUser) {
        try {
            var fileRecord = new FileEntity();

            fileRecord.setActive(true);
            fileRecord.setDate(CommonUtil.getFileDateFormat(filePath));
            fileRecord.setName(filePath.getFileName().toString());
            fileRecord.setSize(CommonUtil.getFileSizeString(filePath));
            saveFile(fileRecord);

            var userFile = new UserFileEntity();

            userFile.setFile_id(fileRecord.getId());
            userFile.setUser_id(currentUser.getId());
            saveUserFile(userFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public ResultModel removeExtension(UserEntity user, String fileName) {
        
        ResultModel result = null;
        
        try {
            String removeExtensionFileName = FilenameUtils.removeExtension(fileName);
            CommonUtil.checkSafeFileName(removeExtensionFileName);
            
            Path userPath = getUserSavePath(user);
            
            var file = load(userPath, fileName);
            
            Files.move(file, file.resolveSibling(removeExtensionFileName));
            
            FileEntity fileEntity = CommonUtil.getFileEntity(user.getFiles(), fileName);
            fileEntity.setName(removeExtensionFileName);
            
            saveFile(fileEntity);
            
            result = new ResultModel();
            
        } catch (Exception e) {
            result = new ResultModel(e);
            // TODO: handle exception
        }
        
        return result;
    }
    
    public ResultModel deleteFile(UserEntity user, String fileName) {
        boolean deleteOnDisk = false;
        return deleteFile(user, fileName, deleteOnDisk);
    }
    
    //admin disinda istek olursa sadece dbde true false yapar tamamen silmez
    public ResultModel deleteFile(UserEntity user, String fileName, boolean deleteOnDisk) {
        
        ResultModel result = null;
        
        try {
            String removeExtensionFileName = FilenameUtils.removeExtension(fileName);
            CommonUtil.checkSafeFileName(removeExtensionFileName);
            
            if (user.isAdmin() || deleteOnDisk) {
                Path userPath = getUserSavePath(user);
                
                var file = load(userPath, fileName);
                
                Files.delete(file);
            }
            
            FileEntity fileEntity = CommonUtil.getFileEntity(user.getFiles(), fileName);
            fileEntity.setActive(false);
            
            saveFile(fileEntity);
            
            result = new ResultModel();
            
        } catch (Exception e) {
            result = new ResultModel(e);
            // TODO: handle exception
        }
        
        return result;
    }
    
    public ResultModel changeFileName(UserEntity user, String fileName, String newFileName) {
        
        ResultModel result = null;
        
        try {
            String removeExtensionFileName = FilenameUtils.removeExtension(fileName);
            CommonUtil.checkSafeFileName(removeExtensionFileName);
            
            String removeExtensionNewFileName = FilenameUtils.removeExtension(newFileName);
            CommonUtil.checkSafeFileName(removeExtensionNewFileName);
            
            Path userPath = getUserSavePath(user);
            
            var file = load(userPath, fileName);
            
            Files.move(file, file.resolveSibling(newFileName));
            
            FileEntity fileEntity = CommonUtil.getFileEntity(user.getFiles(), fileName);
            fileEntity.setName(newFileName);
            
            saveFile(fileEntity);
            
            result = new ResultModel();
            
        } catch (Exception e) {
            result = new ResultModel(e);
            // TODO: handle exception
        }
        
        return result;
    }
    
    

    /*
     * (non-Javadoc)
     * 
     * @see com.share.fileupload.service.FileService#load(java.lang.String)
     */
    public Path load(Path userPath, String filename) {
        return userPath.resolve(filename);
    }

    // private Stream<Path> loadAll() {
    // try {
    // return Files.walk(this.rootLocation, 1).filter(path -> !path.equals(this.rootLocation))
    // .map(this.rootLocation::relativize);
    // } catch (IOException e) {
    // //TODO:GA: ex
    // //throw new GlobalFileException("Failed to read stored files", e);
    // }
    //
    // return null;
    // }

    /*
     * (non-Javadoc)
     * 
     * @see com.share.fileupload.service.FileService#loadAll(com.share.fileupload.entity.UserEntity)
     */
    public Stream<Path> loadAll(UserEntity user) {
        if (CommonUtil.isNull(user)) {
            return null;
        }

        Stream<Path> result = null;

        try {
            Path userPath = getUserSavePath(user);

            result = Files.walk(userPath, 1).filter(path -> !path.equals(userPath));
        } catch (IOException e) {
            result = null;
            e.printStackTrace();
        }

        return result;
    }

    public Resource loadAsResource(UserEntity user, String fileName) {
        try {
            String removeExtension = FilenameUtils.removeExtension(fileName);
            CommonUtil.checkSafeFileName(removeExtension);
            
            Path userPath = getUserSavePath(user);
            
            var file = load(userPath, fileName);
            var resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new Exception();
                // TODO:GA: ex
                // throw new GlobalFileNotFoundException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();

            // TODO:GA: ex
            // throw new GlobalFileNotFoundException("Could not read file: " + filename, e);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public ResultModel save(MultipartFile file) {
        var currentUser = userService.getCurrentUser();
        var removeExtension = false;
        var useZip = false;
        var useRandomName = false;
        var customExtension = "";

        return save(file, currentUser, removeExtension, useZip, useRandomName, customExtension);
    }

    public ResultModel save(MultipartFile file, boolean removeExtension) {
        var currentUser = userService.getCurrentUser();
        var useZip = false;
        var useRandomName = false;
        var customExtension = "";

        return save(file, currentUser, removeExtension, useZip, useRandomName, customExtension);
    }

    public ResultModel save(MultipartFile file, String customExtension) {
        var currentUser = userService.getCurrentUser();
        var removeExtension = false;
        var useZip = false;
        var useRandomName = false;

        return save(file, currentUser, removeExtension, useZip, useRandomName, customExtension);
    }

    public ResultModel save(MultipartFile file, boolean removeExtension, boolean useZip) {
        var currentUser = userService.getCurrentUser();
        var useRandomName = false;
        var customExtension = "";

        return save(file, currentUser, removeExtension, useZip, useRandomName, customExtension);
    }

    public ResultModel save(MultipartFile file, boolean removeExtension, boolean useZip, boolean useRandomName) {
        var currentUser = userService.getCurrentUser();
        var customExtension = "";

        return save(file, currentUser, removeExtension, useZip, useRandomName, customExtension);
    }

    public ResultModel save(MultipartFile file, UserEntity user, boolean removeExtension, boolean useZip,
            boolean useRandomName, String customExtension) {
        ResultModel result = null;

        if ((file == null) || (user == null)) {
            result = new ResultModel(new Exception(), "null parameters");

            return result;
        }

        var orgFileName = file.getOriginalFilename();

        if (orgFileName == null) {
            result = new ResultModel(new Exception(), "null parameters orginal filename");

            return result;
        }

        var filename = StringUtils.cleanPath(orgFileName);

        try {
            if (file.isEmpty()) {
                var desc = "Failed to store empty file " + filename;

                result = new ResultModel(new Exception(), desc);
            } else if (filename.contains("..") || customExtension.contains("..")) {

                // TODO:GA:check null byte
                var desc = "Cannot store file with relative path outside current directory ";

                result = new ResultModel(new Exception(), desc);
            } else {
                try (InputStream inputStream = file.getInputStream()) {
                    Path savePath = getUserSavePath(user);

                    copyFile(inputStream, savePath, filename, customExtension, removeExtension, useZip, useRandomName);
                }
            }
        } catch (ArchiveException e) {
            result = new ResultModel(e, "Failed to archive file");
        } catch (IOException e) {
            result = new ResultModel(e, "Failed to store file");
        }

        if (result == null) {
            result = new ResultModel();
        }

        return result;
    }

    @Override
    public void saveFile(FileEntity file) {
        fileRepository.save(file);
    }

    @Override
    public void saveUserFile(UserFileEntity userFile) {
        userFileRepository.save(userFile);
    }

    @Override
    public String shareFile(String filePath) {
        ShareFileEntity entity = new ShareFileEntity();
        entity.setActive(true);
        entity.setDate(new Date().toString());
        entity.setFilePath(filePath);
        String hash = UUID.randomUUID().toString().replaceAll("-", "");
        entity.setHashValue(hash);
        
        shareFileRepository.save(entity);
        return hash;
    }
    
    @Override
    public String sharedFile(String hash) {
        
        ShareFileEntity shareFile = shareFileRepository.findbyUUID(hash);
        
        if (shareFile != null && shareFile.isActive()) {
            shareFile.setActive(false);
            shareFileRepository.save(shareFile);
            
            return shareFile.getFilePath();
        }
        
        return null;
    }

    public Path getUserSavePath(UserEntity user) {
        var saveFolder = CommonUtil.int2String(user.getId());
        var savePath = this.rootLocation.resolve(saveFolder);

        createFolder(savePath);

        return savePath;
    }

    private Path getZip(final Path savePath) {
        var zipName = CommonUtil.getRandomString(50) + ".zip";
        var zipPath = savePath.resolve(zipName);

        createFile(zipPath);

        return zipPath;
    }
}
