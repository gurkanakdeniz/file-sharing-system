package com.share.fileupload.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.security.sasl.AuthenticationException;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.share.fileupload.entity.FileEntity;
import com.share.fileupload.model.ResponseModel;
import com.share.fileupload.model.UserFileListModel;
import com.share.fileupload.model.UserFileProcess;
import com.share.fileupload.service.FileService;
import com.share.fileupload.service.UserService;
import com.share.fileupload.utils.CommonUtil;

@Controller
@RequestMapping("/admin")
public class AdminController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private FileService fileService;

    private void checkAdminUser() throws AuthenticationException {
        var currentUser = userService.getCurrentUser();

        if (!currentUser.isAdmin()) {
            throw new AuthenticationException();
        }
    }

    @RequestMapping(
        value  = { "", "/index" },
        method = RequestMethod.GET
    )
    public ResponseModel index() throws AuthenticationException {
        
        checkAdminUser();

        // TODO: auth kontrol
        var modelAndView = new ResponseModel();
        var users        = userService.getAllUsers();
        var userModels   = CommonUtil.prepareUsers(users);

        modelAndView.addObject("users", userModels);
        modelAndView.addObject("userFileList", "adminUserFileList");
        
        modelAndView.setViewName("admin/index");

        return modelAndView;
    }
    
    @PostMapping(path = { "/adminUserFileList" })
    public @ResponseBody String userFiles(@Valid @RequestBody String userId) {
        
        if (userService.getCurrentUser().isAdmin()) {

            var user = userService.findUserById(Integer.parseInt(userId.replaceAll("\"", "")));
            var userFiles = user.getFiles();
            var userFilesPaths = fileService.loadAll(user);
            Path[] filePathArray = userFilesPaths.toArray(Path[]::new);
            
            if (!CommonUtil.isEmpty(userFiles)) {

                var list = new ArrayList<UserFileListModel>();
                for (FileEntity item : userFiles) {
                    
                    boolean flag = false;
                    for (Path path : filePathArray) {
                        if (item.getName().equals(path.getFileName() == null ? "": path.getFileName().toString())) {
                            flag = true;
                            break;
                        }
                    }
                    
                    if (!item.isActive() && !flag) {
                        continue;
                    }
                    LocalDateTime date = LocalDateTime.now();
                    try {
                        date = LocalDateTime.parse(item.getDate());
                    } catch (Exception e) {}
                    
                    list.add(new UserFileListModel(item.getName(), CommonUtil.getFileDateFormat(date),
                            item.getSize(), new UserFileProcess(user.getId(), item.getId())));
                }

                var result = new Gson().toJson(list);
                return result;
            }
        }
        
        return "";        
    }
    
    @GetMapping(value = "/user/file/download/{userId}/{fileId}")
    public ResponseEntity<Resource> downloadUserFile(@PathVariable("userId") String userId, @PathVariable("fileId") String fileId) throws AuthenticationException {
        checkAdminUser();
        
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(fileId)) {
            return null;
        }
        
        int uid = 0;
        int fid = 0;
        try {
            uid = Integer.parseInt(userId);
            fid = Integer.parseInt(fileId);
        } catch (Exception e) {
            return null;
        }
        
        var user = userService.findUserById(uid);
        var fileName = CommonUtil.getFileName(user.getFiles(), fid, false);
        if (StringUtils.isBlank(fileName)) {
            return null;
        }
        
        Resource resource = fileService.loadAsResource(user, fileName);

        String contentType = null;
        try {
            contentType = Files.probeContentType(resource.getFile().toPath());
        } catch (Exception ex) {}

        if(contentType == null) {
            contentType = "application/octet-stream";
        }
        
        if (resource == null) {
            return new ResponseEntity<Resource>(HttpStatus.NO_CONTENT);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    
    @GetMapping(value = "/user/file/delete/{userId}/{fileId}")
    public ResponseModel deleteUserFile(@PathVariable("userId") String userId, @PathVariable("fileId") String fileId) throws AuthenticationException {
        var model = new ResponseModel("redirect:/admin/index");
        
        checkAdminUser();
        
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(fileId)) {
            return null;
        }
        
        int uid = 0;
        int fid = 0;
        try {
            uid = Integer.parseInt(userId);
            fid = Integer.parseInt(fileId);
        } catch (Exception e) {
            return model;
        }
        
        var user = userService.findUserById(uid);
        var fileName = CommonUtil.getFileName(user.getFiles(), fid, false);
        if (StringUtils.isBlank(fileName)) {
            return model;
        }
        
        fileService.deleteFile(user, fileName, true);
        
        return model;
    }
    
    @GetMapping(value = "/user/file/remove/{userId}/{fileId}")
    public ResponseModel removeExtensionUserFile(@PathVariable("userId") String userId, @PathVariable("fileId") String fileId) throws AuthenticationException {
        var model = new ResponseModel("redirect:/admin/index");

        checkAdminUser();
        
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(fileId)) {
            return model;
        }
        
        int uid = 0;
        int fid = 0;
        try {
            uid = Integer.parseInt(userId);
            fid = Integer.parseInt(fileId);
        } catch (Exception e) {
            return model;
        }
        
        var user = userService.findUserById(uid);
        var fileName = CommonUtil.getFileName(user.getFiles(), fid, false);
        if (StringUtils.isBlank(fileName)) {
            return model;
        }
        
        fileService.removeExtension(user, fileName);
        
        return model;
    }

    @RequestMapping(
        value  = "/user/turn/status/{id}",
        method = RequestMethod.GET
    )
    public ResponseModel turnStatus(@PathVariable("id") int id) throws AuthenticationException {
        var currentUser = userService.getCurrentUser();

        checkAdminUser();

        if ((id > 0) && currentUser.getId().intValue() != id && currentUser.isAdmin()) {
            userService.turnStatusUser(id);
        }

        return new ResponseModel("redirect:/admin/index");
    }
}