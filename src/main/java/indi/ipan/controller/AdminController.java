package indi.ipan.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import indi.ipan.model.File;
import indi.ipan.model.User;
import indi.ipan.model.UserAndFile;
import indi.ipan.result.Result;
import indi.ipan.service.FileService;
import indi.ipan.service.UserService;


@RestController
public class AdminController {
	@Autowired
	private FileService fileService;
	@Autowired
    private UserService userService;
	
	@RequestMapping(value = "/admin/users", method = RequestMethod.GET)
    @ResponseBody
    public List<User> checkAllUser() {
	    return userService.getAllUser();
	}
	
	@RequestMapping(value = "/admin/download-users", method = RequestMethod.GET)
    @ResponseBody
    public String downloadAllUser(HttpServletResponse response) {
	    if (userService.downloadAllUser(response)) {
	        return "Download ready";
        }else {
            return "Error: Download prepare fail";
        }
	}
	/**
	 * get all file information in file system
	 * @param current target page number
	 * @param size number of item in one page
	 * @return (200, success, list of file, number of file) if success, others if not
	 */
	@SuppressWarnings("rawtypes")
    @GetMapping(value = "/admin/files")
    public Result checkAllFile(@RequestParam(value = "current", defaultValue = "1") Long current
            , @RequestParam(value = "size", defaultValue = "-1") Long size) {
	    return fileService.listAllFile(current, size);
	}
	
	@RequestMapping(value = "/admin", method = RequestMethod.GET)
    @ResponseBody
    public UserAndFile checkOneUser(@RequestParam String username) {
	    return userService.checkOneUser(username);
    }

	@RequestMapping(value = "/admin/import-users", method = RequestMethod.POST)
    @ResponseBody
    public String batchRegister(@RequestParam MultipartFile file) {
	    if (file.isEmpty()) {
	        return "Error: empty file";
        }
	    Integer res = userService.batchAddUser(file);
	    if (res > 0) {
	        return "Registered " + res + " new users";
        }else if (res == 0) {
            return "Error: Database operation fail or no new user found in file"; 
        }else if (res == -1) {
            return "Error: no valid data found in file";
        }else {
            return "Error: Unexpected error";
        }
    }
	
//	@RequestMapping(value = "/admin/reset", method = RequestMethod.GET)
//    @ResponseBody
//    public String reset() {
//		if (userService.resetSystem()) {
//			return "Reset success";
//		}else {
//			return "Reset fail"; 
//		}
//    }
}
