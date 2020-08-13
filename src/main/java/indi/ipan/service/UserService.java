package indi.ipan.service;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import indi.ipan.model.User;
import indi.ipan.result.Result;

public interface UserService {
    /**
     * register a new user into system
     * @param user username and password of new user
     * @return (200, success, 1) if success, others if not
     */
    @SuppressWarnings("rawtypes")
    Result register(User user);
    /**
     * prepare code for user to validate email address
     * @param user username and email address of user
     * @return (200, success, 1) if success, others if not
     */
    @SuppressWarnings("rawtypes")
    Result registerEmail(User user);
    /**
     * bind email address with user's account
     * @param user user username and email address of user
     * @param code code received from email
     * @return (200, success, 1) if success, others if not
     */
    @SuppressWarnings("rawtypes")
    Result bindEmail(User user, String code);
    /**
     * login by username and password
     * @param user username and password of user
     * @return (200, success, 1) if success, others if not
     */
    @SuppressWarnings("rawtypes")
    Result login(User user);
    /**
     * change password of user
     * @param user username and new password of user
     * @return (200, success, 1) if success, others if not
     */
    @SuppressWarnings("rawtypes")
    Result changePassword(User user);
    /**
     * delete given username from database
     * delete folder of given username from local file system
     * @param username username of user
     * @return (200, success, 1) if success, others if not
     */
    @SuppressWarnings("rawtypes")
    Result deleteAccount(String username);
    /**
     * get all user in iPan system
     * @param current target page number
     * @param size number of item in one page
     * @return (200, success, 1, list of user) if success, others if not
     */
    @SuppressWarnings("rawtypes")
    Result getAllUser(Long current, Long size);
    /**
     * check user account information
     * @param username username of user
     * @return (200, success, 1, user and file information) if success, others if not
     */
    @SuppressWarnings("rawtypes")
    Result checkOneUser(String username);
    /**
     * export all user information in excel file for downloading
     * @param response http servlet response
     */
    void downloadAllUser(HttpServletResponse response);
    /**
     * batch add user by excel file
     * @param file excel file, support version 2003/2007
     * @return (200, success, number of new user) if success, others if not
     */
    @SuppressWarnings("rawtypes")
    Result batchAddUser(MultipartFile file);
    /**
     * delete all user and all file in system
     * reset administer to (admin, 123456)
     * @return (200, success, 1) if success, others if not
     */
    @SuppressWarnings("rawtypes")
    Result resetSystem();
}
