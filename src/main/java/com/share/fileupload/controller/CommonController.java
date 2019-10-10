package com.share.fileupload.controller;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.share.fileupload.model.ResponseModel;
import com.share.fileupload.service.FileService;
import com.share.fileupload.service.UserService;

@Controller
public class CommonController extends BaseController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private FileService fileService;

    @RequestMapping(
        value  = "/index",
        method = RequestMethod.GET
    )
    public ResponseModel index() {
        var modelAndView = new ResponseModel();

        if (!userService.isLoginCurrentUser()) {
            return new ResponseModel("redirect:/login");
        }

        var auth = SecurityContextHolder.getContext().getAuthentication();
        var user = userService.findUserByEmail(auth.getName());

        modelAndView.addObject("userName",
                               "Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
        modelAndView.addObject("adminMessage", "Content Available Only for Users");
        modelAndView.setViewName("index");

        return modelAndView;
    }

    @RequestMapping(
        value  = "/oops",
        method = RequestMethod.GET
    )
    public ResponseModel oops() {
        var modelAndView = new ResponseModel();

        modelAndView.addObject("errorMessage", "something wrong");
        modelAndView.setViewName("oops");

        return modelAndView;
    }
    
    @GetMapping(value = "/hash/{guid}")
    public ResponseEntity<Resource>  sharedFile(@PathVariable String guid, HttpServletRequest request) {
        
        try {
            String sharedFilePath = fileService.sharedFile(guid);

            var resource = new UrlResource("file://" + sharedFilePath);
            
            String contentType = null;
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (Exception ex) {}

            if(contentType == null) {
                contentType = "application/octet-stream";
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return new ResponseEntity<Resource>(HttpStatus.NO_CONTENT);
    }
        
    
    
}
