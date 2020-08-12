package indi.ipan.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import indi.ipan.result.Result;
import indi.ipan.service.FileService;
import indi.ipan.service.UserService;


@RestController
@ResponseBody
public class AdminController {
	@Autowired
	private FileService fileService;
	@Autowired
    private UserService userService;

	private static final String DEFAULT_CURRENT_PAGE_NUMBER = "1";
    private static final String DEFAULT_PAGE_SIZE = "10";
	
    /**
     * check all user in iPan system
     * @param current target page number
     * @param size number of item in one page
     * @return (200, success, 1, list of user) if success, others if not
     */
    @SuppressWarnings("rawtypes")
    @GetMapping(value = "/admin/users")
    public Result checkAllUser(@RequestParam(value = "current", defaultValue = DEFAULT_CURRENT_PAGE_NUMBER) Long current
            , @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) Long size) {
	    return userService.getAllUser(current, size);
	}
	/**
	 * download an excel file containing all user's account information in iPan system
	 * @param response HttpServletResponse
	 */
    @GetMapping(value = "/admin/download-users")
    public void downloadAllUser(HttpServletResponse response) {
	    userService.downloadAllUser(response);
	}
	/**
	 * get all file information in file system
	 * @param current target page number
	 * @param size number of item in one page
	 * @return (200, success, number of file, list of file) if success, others if not
	 */
	@SuppressWarnings("rawtypes")
    @GetMapping(value = "/admin/files")
    public Result checkAllFile(@RequestParam(value = "current", defaultValue = "1") Long current
            , @RequestParam(value = "size", defaultValue = "-1") Long size) {
	    return fileService.listAllFile(current, size);
	}
	/**
	 * check a user's account and file information
	 * @param username username of user
	 * @return (200, success, 1, user and file information) if success, others if not
	 */
    @SuppressWarnings("rawtypes")
    @GetMapping(value = "/admin")
    public Result checkOneUser(@RequestParam String username) {
	    return userService.checkOneUser(username);
    }
    /**
     * batch register from an excel file
     * @param file excel file contains list of username and password
     * @return (200, success, number of new user) if success, others if not
     */
	@SuppressWarnings("rawtypes")
    @PostMapping(value = "/admin/import-users")
    public Result batchRegister(@RequestParam MultipartFile file) { // wait to test duplicated primary key
	    return userService.batchAddUser(file);
    }
	/**
	 * delete all user and all file in system
	 * reset administer to (admin, 123456)
	 * @return (200, success, 1) if success, others if not
	 */
    @SuppressWarnings("rawtypes")
    @GetMapping(value = "/admin/reset")
    public Result reset() {
		return userService.resetSystem();
    }
}
