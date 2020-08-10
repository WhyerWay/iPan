package indi.ipan.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.management.RuntimeErrorException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import indi.ipan.dao.FileDao;
import indi.ipan.dao.LogDao;
import indi.ipan.dao.UserDao;
import indi.ipan.model.File;
import indi.ipan.model.User;
import indi.ipan.model.UserAndFile;
import indi.ipan.model.UserOperationLog;
import indi.ipan.service.LogService;
import indi.ipan.service.UserService;
import indi.ipan.util.ExcelUtil;
import indi.ipan.util.FileSystemUtil;

@Service
@MapperScan("indi.ipan.dao")
public class UserServiceImpl implements UserService{
    @Autowired
    private UserDao userDao;
    @Autowired
    private FileDao FileDao;
    @Autowired
    private LogService logService;
    @Autowired
    private FileSystemUtil fileSystemUtil;
    @Autowired
    private ExcelUtil excelUtil;
    
    @Override
    public Boolean register(User user) {
        if (userDao.isUsernameExist(user) == null 
                && userDao.addUser(user) == 1) {
            return true;
        }else {
            return false;
        }
    }

    @Override
    public Integer login(User user) {
        if (userDao.matchPassword(user) != null) {
            if (user.getUsername().equals("admin")) {
                return 0;
            }else {
                return 1;
            }
        }else {
            return -1;
        }
    }

    @Override
    public Boolean changePassword(User user) {
        if (userDao.changePassword(user) == 1) {
            return true;
        }else {
            return false;
        }
    }

    @Override
    public UserAndFile checkOneUser(String username) {
        UserAndFile uf = new UserAndFile();
        uf.setUser(userDao.getOneUser(username));
        uf.setFile(FileDao.getAllFileByUsername(username));
        return uf;
    }
    
    @Override
    public Integer deleteAccount(String username) {
        Integer numOfFile = FileDao.countFileByUsername(username);
        Integer resUser = userDao.deleteUser(username);
        Integer resFile = FileDao.deleteFileByUsername(username);
        if (resUser == 1) {
            if (resFile.equals(numOfFile)) {
                if (fileSystemUtil.deleteUserFolder(username)) {
                    return 0;
                }else {
                    return -1;
                }
            }else {
                return -2;
            }
        }else {
            return -3;
        }
    }

    @Override
    public List<User> getAllUser() {
        return userDao.getAllUser();
    }

    @Override
    public Boolean downloadAllUser(HttpServletResponse response) {
        List<User> users = userDao.getAllUser();
        response.setContentType("application/binary;charset=UTF-8");
        try {
            response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode("userlist.xls", "UTF-8"));
            ServletOutputStream out = response.getOutputStream();
            if (excelUtil.exportUser(out, users)) {
                return true;
            }else {
                return false;
            }
        } catch (Exception e) {// UnsupportedEncodingException | IOException
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Integer batchAddUser(MultipartFile file) {
        InputStream in;
        List<User> users;
        try {
            in = file.getInputStream();
            users = excelUtil.importUser(in,file.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return userDao.batchAddUser(users);
    }

//    @Override
//    public Boolean resetSystem() {
//        // create log object
//        UserOperationLog userOperationLog = new UserOperationLog();
//        Integer unum = userDao.countUser();
//        Integer fnum = FileDao.countFile();
//        userOperationLog.setUsername("admin");
//        userOperationLog.setOperation("reset system");
//        userOperationLog.setCount(unum + fnum);
//        // save operation log
//        if (LogDao.addLog(userOperationLog) != 1) {
//            throw new RuntimeException("Error: Log save fail");
//        }
//        // delete data from database
//        if (userDao.deleteAllUser() != unum) {
//            throw new RuntimeException("Error: User in database delete fail");
//        }
//        if (FileDao.deleteAllFile() != fnum) {
//            throw new RuntimeException("Error: File in database delete fail");
//        }
//        Boolean res = true;
////        if (unum > 0 && userDao.deleteAllUser().equals(0)) {
////            res = false;
////        }
////        if (fnum > 0 && FileDao.deleteAllFile().equals(0)) {
////            res = false;
////        }
////        res = fileSystemUtil.deleteSystemFolder();
////        res = fileSystemUtil.openFileSystem();
//        User admin = new User();
//        admin.setUsername("admin");
//        admin.setPassword("123456");
//        res = userDao.addUser(admin).equals(1) ? true : false;
//        return res;
//    }
}
