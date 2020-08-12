package indi.ipan.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Stack;

import javax.jws.soap.SOAPBinding.Use;
import javax.management.RuntimeErrorException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import indi.ipan.dao.FileDao;
import indi.ipan.dao.LogDao;
import indi.ipan.dao.UserDao;
import indi.ipan.exception.CustomizedExcption;
import indi.ipan.exception.ResultEnum;
import indi.ipan.mapper.FileMapperTest;
import indi.ipan.mapper.UserMapperTest;
import indi.ipan.model.File;
import indi.ipan.model.FileSystemOperationResult;
import indi.ipan.model.User;
import indi.ipan.model.UserAndFile;
import indi.ipan.model.UserOperationLog;
import indi.ipan.result.Result;
import indi.ipan.result.ResultUtil;
import indi.ipan.service.FileService;
import indi.ipan.service.LogService;
import indi.ipan.service.UserService;
import indi.ipan.util.ExcelUtil;
import indi.ipan.util.FileSystemUtil;

@Service
@MapperScan("indi.ipan.dao")
public class UserServiceImpl extends ServiceImpl<UserMapperTest, User> implements UserService{
    @Autowired
    UserMapperTest userMapperTest;
    @Autowired
    ResultUtil resultUtil;
    @Autowired
    FileService fileService;
    @Autowired
    FileMapperTest fileMapperTest;
    @Autowired
    private UserDao userDao;
//    @Autowired
//    private FileDao FileDao;
    @Autowired
    private LogService logService;
    @Autowired
    private FileSystemUtil fileSystemUtil;
    @Autowired
    private ExcelUtil excelUtil;
    @Autowired
    DataSourceTransactionManager dataSourceTransactionManager;
    @Autowired
    TransactionDefinition transactionDefinition;
    
    private final static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    private static final String COLUMN_USERNAME = "username";
//    private static final Long DEFAULT_CURRENT_PAGE_NUMBER = 1L;
//    private static final Long DEFAULT_PAGE_SIZE = -1L;
    
    @SuppressWarnings("rawtypes")
    @Override
    @Transactional
    public Result register(User user) {
        // create log object
        UserOperationLog userOperationLog = new UserOperationLog();
        userOperationLog.setUsername(user.getUsername());
        userOperationLog.setOperation(Thread.currentThread().getStackTrace()[1].getMethodName()); // get method name
        userOperationLog.setCount(1L);
        // save operation log and get id
        if (!logService.addOperationLog(userOperationLog)) {
            throw new CustomizedExcption(ResultEnum.UNEXPECTED_DATABASE_OPERATION_RESULT);
        }
        // validate username
        if (this.isUsernameExist(user)) {
            throw new CustomizedExcption(ResultEnum.INVALID_INPUT);
        }
        // update database
        if (userMapperTest.insert(user) != 1) {
            throw new CustomizedExcption(ResultEnum.UNEXPECTED_DATABASE_OPERATION_RESULT);
        }
        userOperationLog.setStatus(true);
        if (!logService.addOperationResult(userOperationLog)) {
            throw new CustomizedExcption(ResultEnum.UNEXPECTED_DATABASE_OPERATION_RESULT);
        }
        return resultUtil.success();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Result login(User user) {
        // create log object
        UserOperationLog userOperationLog = new UserOperationLog();
        userOperationLog.setUsername(user.getUsername());
        userOperationLog.setOperation(Thread.currentThread().getStackTrace()[1].getMethodName()); // get method name
        userOperationLog.setCount(1L);
        // save operation log and get id
        if (!logService.addOperationLog(userOperationLog)) {
            throw new CustomizedExcption(ResultEnum.UNEXPECTED_DATABASE_OPERATION_RESULT);
        }
        // search in database
        if (userMapperTest.selectCount(new QueryWrapper<>(user)) != 1) {
            throw new CustomizedExcption(ResultEnum.MISMATCH_USERNAME_AND_PASSWORD);
        }
        // update log
        userOperationLog.setStatus(true);
        if (!logService.addOperationResult(userOperationLog)) {
            throw new CustomizedExcption(ResultEnum.UNEXPECTED_DATABASE_OPERATION_RESULT);
        }
        return resultUtil.success();
    }

    @SuppressWarnings("rawtypes")
    @Override
    @Transactional
    public Result changePassword(User user) {
        // create log object
        UserOperationLog userOperationLog = new UserOperationLog();
        userOperationLog.setUsername(user.getUsername());
        userOperationLog.setOperation(Thread.currentThread().getStackTrace()[1].getMethodName()); // get method name
        userOperationLog.setCount(1L);
        // save operation log and get id
        if (!logService.addOperationLog(userOperationLog)) {
            throw new CustomizedExcption(ResultEnum.UNEXPECTED_DATABASE_OPERATION_RESULT);
        }
        // update database
        if (userMapperTest.update(user
                , new QueryWrapper<User>()
                .eq(COLUMN_USERNAME, user.getUsername())) != 1) {
            throw new CustomizedExcption(ResultEnum.UNEXPECTED_DATABASE_OPERATION_RESULT);
        }
        // update log
        userOperationLog.setStatus(true);
        if (!logService.addOperationResult(userOperationLog)) {
            throw new CustomizedExcption(ResultEnum.UNEXPECTED_DATABASE_OPERATION_RESULT);
        }
        return resultUtil.success();
    }

    @Override
    public UserAndFile checkOneUser(String username) {
        UserAndFile uf = new UserAndFile();
//        uf.setUser(userDao.getOneUser(username));
//        uf.setFile(FileDao.getAllFileByUsername(username));
        uf.setUser(userMapperTest.selectOne(new QueryWrapper<User>().eq(COLUMN_USERNAME, username)));
        uf.setFile(fileMapperTest.selectList(new QueryWrapper<File>().eq(COLUMN_USERNAME, username)));
        return uf;
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public Result deleteAccount(String username) {
        Stack<FileSystemOperationResult> resultStack = null;
        // create log object
        UserOperationLog userOperationLog = new UserOperationLog();
        userOperationLog.setUsername(username);
        userOperationLog.setOperation(Thread.currentThread().getStackTrace()[1].getMethodName()); // get method name
        userOperationLog.setCount(1L);
        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
        try {
            // save operation log and get id
            if (!logService.addOperationLog(userOperationLog)) {
                throw new CustomizedExcption(ResultEnum.UNEXPECTED_DATABASE_OPERATION_RESULT);
            }
            // validate username
            if (username.equals("admin")) {
                throw new CustomizedExcption(ResultEnum.INVALID_INPUT);
            }
            // update database
            if (userMapperTest.delete(new QueryWrapper<User>().eq(COLUMN_USERNAME, username)) != 1) {
                throw new CustomizedExcption(ResultEnum.UNEXPECTED_DATABASE_OPERATION_RESULT);
            }
            if (fileMapperTest.selectCount(new QueryWrapper<File>().eq(COLUMN_USERNAME, username)) != 0 
                    && fileMapperTest.delete(new QueryWrapper<File>().eq(COLUMN_USERNAME, username)) == 0) {
                throw new CustomizedExcption(ResultEnum.UNEXPECTED_DATABASE_OPERATION_RESULT);
            }
            // update file system
            resultStack = fileSystemUtil.deleteUserFolder(userOperationLog.getId(), username);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // rollback database
            dataSourceTransactionManager.rollback(transactionStatus);
            // rollback file system operation
            fileSystemUtil.rollback(resultStack);
            // update operation status
            userOperationLog.setStatus(false);
            if (!logService.addOperationResult(userOperationLog)) {
                throw new CustomizedExcption(ResultEnum.UNEXPECTED_DATABASE_OPERATION_RESULT);
            }
            //  throw it again so that GlobalExceptionHandler can process it
            throw e;
        }
        // commit database
        dataSourceTransactionManager.commit(transactionStatus);
        // commit file system operation
        fileSystemUtil.commit(resultStack);
        // update operation status
        userOperationLog.setStatus(true);
        if (!logService.addOperationResult(userOperationLog)) {
            throw new CustomizedExcption(ResultEnum.UNEXPECTED_DATABASE_OPERATION_RESULT);
        }
        return resultUtil.success();
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
    private Boolean isUsernameExist(User user) {
        if (userMapperTest.selectCount(new QueryWrapper<User>()
                .eq(COLUMN_USERNAME, user.getUsername())) == 1) {
            return true;
        }
        return false;
    }
}
