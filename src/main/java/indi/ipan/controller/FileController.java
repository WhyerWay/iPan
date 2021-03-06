package indi.ipan.controller;

import indi.ipan.model.File;
import indi.ipan.model.ServiceResult;
import indi.ipan.result.Result;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import indi.ipan.service.FileService;

@RestController
public class FileController {
    @Autowired
    private FileService fileService;
    
    private static final String DEFAULT_CURRENT_PAGE_NUMBER = "1";
    private static final String DEFAULT_PAGE_SIZE = "10";
    
    /**
     * get all filename of files under given username in page
     * @param username username of user
     * @param current target page number
     * @param size number of item in one page
     * @return (200, success, number of file, list of file) if success, others if not
     */
    @SuppressWarnings("rawtypes")
    @GetMapping(value = "/menu")
    public Result menu(@RequestParam String username
            , @RequestParam(value = "current", defaultValue = DEFAULT_CURRENT_PAGE_NUMBER) Long current
            , @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) Long size) {
        return fileService.listFileByUsername(username, current, size);
    }
    /**
     * get all filename of files like target file name of given username in page
     * @param username username of user
     * @param file_name file name of target file
     * @param current target page number
     * @param size number of item in one page
     * @return (200, success, number of file, list of file) if success, others if not
     */
    @SuppressWarnings("rawtypes")
    @PostMapping(value = "/menu/check-file")
    public Result checkFile(@RequestParam String username
            , @RequestParam String file_name
            , @RequestParam(value = "current", defaultValue = DEFAULT_CURRENT_PAGE_NUMBER) Long current
            , @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) Long size) {
        File file = new File();
        file.setUsername(username);
        file.setFileName(file_name);
        return fileService.listFileByFilename(file, current, size);
    }
    /**
     * rename a file in file system for given username
     * @param username username of user
     * @param oldFileName old file name
     * @param newFileName new file name, cannot be same as existed file name in file system for given username
     * @return (200, success, 1) if success, others if not
     */
    @SuppressWarnings("rawtypes")
    @PostMapping(value = "/menu/rename")
    public Result renameFile(@RequestParam String username
            , @RequestParam String oldFileName
            , @RequestParam String newFileName) {
        File oldFile = new File(username, oldFileName);
        File newFile = new File(username, newFileName);
        return fileService.renameFile(oldFile, newFile);
    }
    /**
     * uplaod a file in file system for given username
     * @param username username of user
     * @param file target file
     * @return (200, success, 1) if success, others if not
     */
    @SuppressWarnings("rawtypes")
    @PostMapping(value = "/menu/upload")
    public Result upload(@RequestParam String username, @RequestParam MultipartFile file) {
        return fileService.uploadFile(username, file);
    }
    /**
     * uplaod a list of file in file system for given username
     * @param username username username of user
     * @param file target list of file
     * @return (200, success, number of file) if success, others if not
     */
    @SuppressWarnings("rawtypes")
    @PostMapping(value = "/menu/batch-upload")
    public Result batchUpload(@RequestParam String username
            , @RequestParam MultipartFile[] file) {
        return fileService.uploadMultiFile(username, file);
    }
    /**
     * delete a file from user's account
     * @param username username of user
     * @param file_name file name of target file
     * @return (200, success, 1) if success, others if not
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/menu/delete", method = RequestMethod.POST)
    @ResponseBody
    public Result delete(@RequestParam String username
            , @RequestParam String file_name) {
        File file = new File();
        file.setUsername(username);
        file.setFileName(file_name);
        return fileService.deleteFile(file);
    }
    /**
     * delete all file from user's account
     * @param username username of user
     * @return (200, success, number of file) if success, others if not
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/menu/clear", method = RequestMethod.POST)
    @ResponseBody
    public Result clear(@RequestParam String username) {
        return fileService.clearUserFile(username);
    }
    


}
