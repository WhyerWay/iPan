package indi.ipan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import indi.ipan.model.User;
import indi.ipan.service.UserService;

@RestController
public class UserController {
	@Autowired
	private UserService userService;
	
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public String login(@RequestParam String username
            , @RequestParam String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        Integer status = userService.login(user);
    	if (status == 0) {
    	    return "Admin login success";
		}else if (status == 1) {
		    return "Login success";
        }else if (status == -1) {
            return "Error: username and password mismatch";
        }else {
            return "Error: unexpected error";
        }
    }
    
    @RequestMapping(value = "/login/register", method = RequestMethod.POST)
    @ResponseBody
    public String register(@RequestParam String username
            , @RequestParam String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        if (userService.register(user)) {
            return "Register success";
        }else {
            return "Error: invalid username";
        }
    }
    
    @RequestMapping(value = "/menu/change-password", method = RequestMethod.POST)
    @ResponseBody
    public String changePassword(@RequestParam String username
            , @RequestParam String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
    	if (userService.changePassword(user)) {
    		return "Password change success";
		}else {
			return "Error: Password change fail";
		}
    }
    
    @RequestMapping(value = "/menu/delete-account", method = RequestMethod.POST)
    @ResponseBody
    public String deleteAccount(@RequestParam String username) {
    	if (username.equals("admin")) {
    		return "Error: Cannot delete admin account";
		}
    	Integer res = userService.deleteAccount(username);
    	if (res == 0) {
    	    return "Account delete success";
        }else if (res == -1) {
            return "Error: Folder delete fail";
        }else if (res == -2) {
            return "Error: Table user delete fail";
        }else if (res == -3) {
            return "Error: Table user delete fail";
        }else {
            return "Error: Unexpected error";
        }
    }
}
