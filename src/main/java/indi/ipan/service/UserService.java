package indi.ipan.service;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import indi.ipan.model.User;
import indi.ipan.model.UserAndFile;

public interface UserService {
    /**
     * register a new user into system
     * @param user username and password of new user
     * @return true if register success, false if not
     */
    Boolean register(User user);
    /**
     * login by username and password
     * @param user username and password of user
     * @return -1 if fail, 0 if admin login success, 1 if normal user login success
     */
    Integer login(User user);
    /**
     * change password of user
     * @param user username and password of user
     * @return true if success, false if not
     */
    Boolean changePassword(User user);
    /**
     * delete given username from database
     * delete folder of given username from local file system
     * @param user username of user
     * @return 0 if success, -1 if folder delete fail
     *     , -2 if table file delete fail
     *     , -3 if table user delete fail
     */
//    Integer deleteAccount(String username);
    /**
     * get all user in iPan system
     * @return list of user
     */
    List<User> getAllUser();
    /**
     * check user account information
     * @param username username of user
     * @return (username, password) and list of user's file
     */
    UserAndFile checkOneUser(String username);
    /**
     * export all user information in excel file for downloading
     * @param response http servlet response
     * @return true if success, false if not
     */
    Boolean downloadAllUser(HttpServletResponse response);
    /**
     * batch add user by excel file
     * @param file excel file, support version 2003/2007
     * @return true if success, false if not
     */
    Integer batchAddUser(MultipartFile file);
    /**
     * delete all user and all file in system
     * reset administer to (admin, 123456)
     * @return true if success, false if not
     */
//    Boolean resetSystem();
}
