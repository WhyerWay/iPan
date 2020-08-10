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
    private static final String DEFAULT_PAGE_SIZE = "-1";
    /**
     * get all filename of files under given username in page
     * @param username username of user
     * @param current target page number
     * @param size number of item in one page
     * @return (200, success, list of file, number of file) if success, others if not
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
     * @return (200, success, list of file, number of file) if success, others if not
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
    
    @RequestMapping(value = "/menu/upload", method = RequestMethod.POST)
    @ResponseBody
    public String upload(@RequestParam String username, @RequestParam MultipartFile file) {
        Integer res = fileService.uploadFile(username, file);
        if (res == 0) {
            return "Upload success";
        }else if (res == -1){
            return "Error: File is empty";
        }else if (res == -2) {
            return "Error: Duplicated file name";
        }else if (res == -3) {
            return "Error: Insert operation in database fail";
        }else if (res == -4) {
            return "Error: Upload operation in file system fail";
        }else {
            return "Error: Unexpected error";
        }
    }
    
    @RequestMapping(value = "/menu/batch-upload", method = RequestMethod.POST)
    @ResponseBody
    public String batchUpload(@RequestParam String username
            , @RequestParam MultipartFile[] file) {
        ServiceResult result = fileService.uploadMultiFile(username, file);
        Integer index = result.getIndex();
        if (index > 0) {
            return index + " file uploaded success";
        }else if (index == -1){
            StringBuilder sb = new StringBuilder();
            sb.append("Error: Empty file exist\nError file name list:");
            for(String str : result.getErrorInfo()){
                sb.append(" ");
                sb.append(str);
            }
            return sb.toString();
        }else if (index == -2) {
            StringBuilder sb = new StringBuilder();
            sb.append("Error: Duplicated file name\nError file name list:");
            for(String str : result.getErrorInfo()){
                sb.append(" ");
                sb.append(str);
            }
            return sb.toString();
        }else if (index == -3) {
            return "Error: Insert operation in database fail";
        }else if (index == -4) {
            return "Error: Upload operation in file system fail";
        }else {
            return "Error: Unexpected error";
        }
    }
    
//    @RequestMapping(value = "/menu/delete", method = RequestMethod.POST)
//    @ResponseBody
//    public String delete(@RequestParam String username
//            , @RequestParam String file_name) {
//        File file = new File();
//        file.setUsername(username);
//        file.setFile_name(file_name);
//        if (!fileService.isFilenameExist(file)) {
//            return "Error: File does not exist";
//        }
//        if (fileService.deleteFile(file)) {
//            return "File delete success";
//        }else {
//            return "File delete fail";
//        }
//    }
    @RequestMapping(value = "/menu/delete", method = RequestMethod.POST)
    @ResponseBody
    public Result delete(@RequestParam String username
            , @RequestParam String file_name) {
        File file = new File();
        file.setUsername(username);
        file.setFileName(file_name);
        return fileService.deleteFile(file);
    }
    
    @RequestMapping(value = "/menu/clear", method = RequestMethod.POST)
    @ResponseBody
    public String clear(@RequestParam String username) {
    	if (fileService.clearFile(username)) {
    		return "Clear success";
		}else {
			return "Clear fail";
		}
    }
    


}
