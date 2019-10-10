package com.share.fileupload.controller;

import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.share.fileupload.entity.UserEntity;
import com.share.fileupload.model.DownloadModel;
import com.share.fileupload.model.ResponseModel;
import com.share.fileupload.model.UploadModel;
import com.share.fileupload.service.DownloadService;
import com.share.fileupload.service.FileService;
import com.share.fileupload.service.UserService;
import com.share.fileupload.utils.CommonUtil;

@Controller
@RequestMapping("/file")
public class FileController extends BaseController {

    @Autowired
    UserService userService;

    @Autowired
    FileService fileService;

    @Autowired
    DownloadService downloadService;
    
    @RequestMapping(value = { "", "/index" }, method = RequestMethod.GET)
    public ResponseModel index() {

        // TODO: Auth
        var currentUser = userService.getCurrentUser();

        if (!currentUser.isGuest()) {
            return new ResponseModel("redirect:/file/list");
        }

        var model = new ResponseModel();

        model.setViewName("redirect:/file/list");

        return model;
    }

    @RequestMapping(value = { "/list", "/listasd/*" }, method = RequestMethod.GET)
    public ResponseModel list() {

        // TODO: Auth
        var currentUser = userService.getCurrentUser();
        var model = new ResponseModel();
        var filesPaths = fileService.loadAll(currentUser);
        
        //INFO: tabloda olmayanları admin kullanıcı degil ise listeleme ekranından cikar
        //INFO: sadece admin, tabloda olmayan dosyalari gorebilsin       
        if (!currentUser.isAdmin()) {
            var dbFileList = currentUser.getFiles().stream().filter(f -> f.isActive()).map(f -> f.getName()).collect(Collectors.toList());
            filesPaths = filesPaths.filter(fp -> dbFileList.contains(fp.getFileName().toString()));
        }
        
        var files = CommonUtil.prepareFiles(filesPaths);

        model.addObject("model", new UploadModel());
        model.addObject("files", files);
        model.setViewName("file/list");

        return model;
    }

    @RequestMapping(value = { "/upload", "/upload/*" }, method = RequestMethod.POST)
    public String upload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        
        //TODO:GA: filename check
        
        var result = fileService.save(file, false, false, false);

        if (result.isSuccess()) {
            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded " + file.getOriginalFilename() + "!");

            return "redirect:/file/list";
        }

        return "redirect:/file/list";
    }

    @PostMapping(path = { "/uploadUrl", "/uploadUrl/*", "/upload-url", "/upload-url/*" })
    public String uploadUrl(@ModelAttribute UploadModel model) {

        if(StringUtils.isEmpty(model.getUrl())) {
            return "redirect:/file/list";
        }
        
        // TODO: Auth
        var currentUser = userService.getCurrentUser();
        
        //TODO:GA: filename check
        
        var downloadModel = new DownloadModel(model.getUrl(), model.getFileName());
        
        var result = downloadService.downloadFile(downloadModel, currentUser);
        
        
        return "redirect:/file/list";
    }
    
    @GetMapping(value = {"/list/download/", "/list/download"})
    public ResponseEntity<Resource> downloadFile(@RequestParam Map<String,String> allRequestParams, HttpServletRequest request) {
        
        String fileName = "";
        fileName = CommonUtil.getFileNameReqParam(allRequestParams);
        
        if (StringUtils.isEmpty(fileName)) {
            return null;
        }
        
        
//        if () {
//            //TODO:GA: filename check
//        }
        
        // TODO: Auth
        UserEntity currentUser = userService.getCurrentUser();
        
        Resource resource = fileService.loadAsResource(currentUser, fileName);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
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
    
    @GetMapping(value = { "/list/delete/", "/list/delete" })
    public String deleteFile(@RequestParam Map<String, String> allRequestParams) {

        String fileName = "";
        fileName = CommonUtil.getFileNameReqParam(allRequestParams);

        if (!StringUtils.isEmpty(fileName)) {

            // if () {
//          //TODO:GA: filename check
            // }
            
            // TODO: Auth
            UserEntity currentUser = userService.getCurrentUser();

            fileService.deleteFile(currentUser, fileName);

        }

        return "redirect:/file/list";
    }
    
    
    @GetMapping(value = {"/list/removeExtension/", "/list/removeExtension"})
    public String removeExtension(@RequestParam Map<String,String> allRequestParams) {
        
        String fileName = "";
        fileName = CommonUtil.getFileNameReqParam(allRequestParams);
        
        if (!StringUtils.isEmpty(fileName)) {
            
        //  if () {
//          //TODO:GA: filename check
    //  }
            
            // TODO: Auth
            UserEntity currentUser = userService.getCurrentUser();
            
            fileService.removeExtension(currentUser, fileName);
        }

        return "redirect:/file/list";
    }
    
    
    @GetMapping(value = {"/list/changeName/", "/list/changeName"})
    public String changeName(@RequestParam Map<String,String> allRequestParams) {
        
        String fileName = "";
        fileName = CommonUtil.getFileNameReqParam(allRequestParams);
        
        String newFileName = "";
        newFileName = CommonUtil.getFileNameReqParam(allRequestParams, "newFileName");
        
        if (!StringUtils.isEmpty(fileName) && !StringUtils.isEmpty(newFileName)) {
            
        //  if () {
//          //TODO:GA: filename check
    //  }
            
            try {
                String removeExtensionNewFileName = FilenameUtils.removeExtension(newFileName);
                CommonUtil.checkSafeFileName(removeExtensionNewFileName);
                
                // TODO: Auth
                UserEntity currentUser = userService.getCurrentUser();
                
                fileService.changeFileName(currentUser, fileName, newFileName);
                
            } catch (Exception e) {
                e.printStackTrace();
                // TODO: handle exception
            }
        }

        return "redirect:/file/list";
    }
    
    @PostMapping(value = {"/list/share/", "/list/share"})
    public @ResponseBody String shareFile(@RequestBody String fileName) {
        
        if (!StringUtils.isEmpty(fileName) ) {
            try {
                // TODO: Auth
                UserEntity currentUser = userService.getCurrentUser();
                Path userSavePath = fileService.getUserSavePath(currentUser);
                Path filePath = userSavePath.resolve(fileName);
                
                String hash = fileService.shareFile(filePath.toAbsolutePath().toString());
                
                return new Gson().toJson(hash);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "";
    }
    
}
