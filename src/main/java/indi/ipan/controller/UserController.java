package indi.ipan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import indi.ipan.model.User;
import indi.ipan.result.Result;
import indi.ipan.service.UserService;

@RestController
public class UserController {
	@Autowired
	private UserService userService;
	
	/**
	 * login by username and password
     * @param username username of new user
     * @param password password of new user
     * @return (200, success, 1) if success, others if not
	 */
    @SuppressWarnings("rawtypes")
    @PostMapping(value = "/login")
    public Result login(@RequestParam String username
            , @RequestParam String password) {
        User user = new User(username, password);
        return userService.login(user);
    }
    /**
     * register a new user into system
     * @param username username of new user
     * @param password password of new user
     * @return (200, success, 1) if success, others if not
     */
    @SuppressWarnings("rawtypes")
    @PostMapping(value = "/login/register")
    public Result register(@RequestParam String username
            , @RequestParam String password) {
        User user = new User(username, password);
        return userService.register(user);
    }
    /**
     * change password of user
     * @param username username of user
     * @param password new password of user
     * @return (200, success, 1) if success, others if not
     */
    @SuppressWarnings("rawtypes")
    @PostMapping(value = "/menu/change-password")
    public Result changePassword(@RequestParam String username
            , @RequestParam String password) {
        User user = new User(username, password);
        return userService.changePassword(user);
    }
    /**
     * delete given username from database
     * delete folder of given username from local file system
     * @param username username of user
     * @return (200, success, 1) if success, others if not
     */
    @SuppressWarnings("rawtypes")
    @PostMapping(value = "/menu/delete-account")
    public Result deleteAccount(@RequestParam String username) {
        return userService.deleteAccount(username);
    }
}
