package indi.ipan.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import indi.ipan.exception.CustomizedExcption;
import indi.ipan.exception.ResultEnum;
import indi.ipan.mapper.FileMapper;
import indi.ipan.model.File;
import indi.ipan.model.FileSystemOperationResult;
import indi.ipan.model.UserOperationLog;
import indi.ipan.result.Result;
import indi.ipan.result.ResultUtil;
import indi.ipan.service.FileService;
import indi.ipan.service.LogService;
import indi.ipan.util.FileSystemUtil;

@Service
public class FileSeriveImpl extends ServiceImpl<FileMapper, File> implements FileService {
    @Autowired
    private LogService logService;
    @Autowired
    private DataSourceTransactionManager dataSourceTransactionManager;
    @Autowired
    private TransactionDefinition transactionDefinition;
    @Autowired
    private FileSystemUtil fileSystemUtil;
    @Autowired
    private ResultUtil resultUtil;
    @Autowired
    private FileMapper fileMapper;

    private final static Logger logger = LoggerFactory.getLogger(FileSeriveImpl.class);
    
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_FILE_NAME = "file_name";
    
    @SuppressWarnings("rawtypes")
    @Override
    public Result listFileByUsername(String username, Long current, Long size) {
        // create log object
        UserOperationLog userOperationLog = new UserOperationLog();
        userOperationLog.setUsername(username);
        userOperationLog.setOperation(Thread.currentThread().getStackTrace()[1].getMethodName()); // get method name
        userOperationLog.setStatus(false);
        // save operation log and get id
        logService.addOperationLog(userOperationLog);
        // select in database
        Page<File> files = fileMapper.selectPage(new Page<>(current, size)
                , new QueryWrapper<File>().eq(COLUMN_USERNAME, username));
        // update operation status
        logService.addOperationResult(userOperationLog, true, files.getTotal());
        return resultUtil.success(files);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Result listAllFile(Long current, Long size) {
        // create log object
        UserOperationLog userOperationLog = new UserOperationLog();
        userOperationLog.setUsername("admin");
        userOperationLog.setOperation(Thread.currentThread().getStackTrace()[1].getMethodName()); // get method name
        userOperationLog.setStatus(false);
        // save operation log and get id
        logService.addOperationLog(userOperationLog);
        // select in database
        Page<File> files = fileMapper.selectPage(new Page<File>(current, size), null);
        // update operation status
        logService.addOperationResult(userOperationLog, true, files.getTotal());
        return resultUtil.success(files);
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public Result listFileByFilename(File file, Long current, Long size) {
        // create log object
        UserOperationLog userOperationLog = new UserOperationLog();
        userOperationLog.setUsername("admin");
        userOperationLog.setOperation(Thread.currentThread().getStackTrace()[1].getMethodName()); // get method name
        userOperationLog.setStatus(false);
        // save operation log and get id
        logService.addOperationLog(userOperationLog);
        // select in database
        Page<File> files = fileMapper.selectPage(new Page<>(current, size)
                , new QueryWrapper<File>()
                .eq(COLUMN_USERNAME, file.getUsername())
                .like(COLUMN_FILE_NAME, file.getFileName()));
        // update operation status
        logService.addOperationResult(userOperationLog, true, files.getTotal());
        return resultUtil.success(files);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Result renameFile(File oldFile, File newFile) {
        Stack<FileSystemOperationResult> resultStack = null;
        // create log object
        UserOperationLog userOperationLog = new UserOperationLog();
        userOperationLog.setUsername(oldFile.getUsername());
        userOperationLog.setOperation(Thread.currentThread().getStackTrace()[1].getMethodName()); // get method name
        userOperationLog.setCount(1L);
        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
        try {
            // save operation log and get id
            logService.addOperationLog(userOperationLog);
            // validate old file name
            if (!isFilenameExist(oldFile)) {
                throw new CustomizedExcption(ResultEnum.TARGET_FILE_NOT_EXIST);
            }
            // validate new file name
            if (isFilenameExist(newFile)) {
                throw new CustomizedExcption(ResultEnum.INVALID_INPUT);
            }
            // update database
            if (fileMapper.update(newFile, new UpdateWrapper<File>(oldFile)) != 1) {
                throw new CustomizedExcption(ResultEnum.UNEXPECTED_DATABASE_OPERATION_RESULT);
            }
            // update file system
            resultStack = fileSystemUtil.renameFile(userOperationLog.getId()
                    , oldFile.getUsername()
                    , oldFile.getFileName()
                    , newFile.getFileName());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // rollback database
            dataSourceTransactionManager.rollback(transactionStatus);
            // rollback file system operation
            fileSystemUtil.rollback(resultStack);
            // update operation status
            logService.addOperationResult(userOperationLog, false);
            //  throw it again so that GlobalExceptionHandler can process it
            throw e;
        }
        // commit database
        dataSourceTransactionManager.commit(transactionStatus);
        // commit file system operation
        fileSystemUtil.commit(resultStack);
        // update operation status
        logService.addOperationResult(userOperationLog, true);
        return resultUtil.success();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Result uploadFile(String username, MultipartFile file) {
        Stack<FileSystemOperationResult> resultStack = null;
        // create log object
        UserOperationLog userOperationLog = new UserOperationLog();
        userOperationLog.setUsername(username);
        userOperationLog.setOperation(Thread.currentThread().getStackTrace()[1].getMethodName()); // get method name
        userOperationLog.setCount(1L);
        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
        try {
            // save operation log and get id
            logService.addOperationLog(userOperationLog);
            // validate file name
            File uploadFile = new File(username, file.getOriginalFilename());
            if (isFilenameExist(uploadFile)) {
                throw new CustomizedExcption(ResultEnum.INVALID_INPUT);
            }
            // update database
            if (fileMapper.insert(uploadFile) != 1) {
                throw new CustomizedExcption(ResultEnum.UNEXPECTED_DATABASE_OPERATION_RESULT);
            }
            // update file system
            resultStack = fileSystemUtil.uploadFile(userOperationLog.getId(), username, file);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // rollback database
            dataSourceTransactionManager.rollback(transactionStatus);
            // rollback file system operation
            fileSystemUtil.rollback(resultStack);
            // update operation status
            logService.addOperationResult(userOperationLog, false);
            //  throw it again so that GlobalExceptionHandler can process it
            throw e;
        }
        // commit database
        dataSourceTransactionManager.commit(transactionStatus);
        // commit file system operation
        fileSystemUtil.commit(resultStack);
        // update operation status
        logService.addOperationResult(userOperationLog, true);
        return resultUtil.success();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Result uploadMultiFile(String username, MultipartFile[] file) {
        Stack<FileSystemOperationResult> resultStack = null;
        // create log object
        UserOperationLog userOperationLog = new UserOperationLog();
        userOperationLog.setUsername(username);
        userOperationLog.setOperation(Thread.currentThread().getStackTrace()[1].getMethodName()); // get method name
        userOperationLog.setCount(0L);
        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
        try {
            // save operation log and get id
            logService.addOperationLog(userOperationLog);
            // validate file name
            List<File> uploadFile = new ArrayList<>();
            for (MultipartFile fileItem : file) {
                File uploadFileItem = new File(username, fileItem.getOriginalFilename());
                uploadFile.add(uploadFileItem);
            }
            if (isFilenameExist(uploadFile)) { // TODO should return which file name is invalid
                throw new CustomizedExcption(ResultEnum.INVALID_INPUT);
            }
            // update database
            if (!this.saveBatch(uploadFile)) {
                throw new CustomizedExcption(ResultEnum.UNEXPECTED_DATABASE_OPERATION_RESULT);
            }
            // update file system
            resultStack = fileSystemUtil.uploadMultiFile(userOperationLog.getId(), username, file);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // rollback database
            dataSourceTransactionManager.rollback(transactionStatus);
            // rollback file system operation
            fileSystemUtil.rollback(resultStack);
            // update operation status
            logService.addOperationResult(userOperationLog, false);
            //  throw it again so that GlobalExceptionHandler can process it
            throw e;
        }
        // commit database
        dataSourceTransactionManager.commit(transactionStatus);
        // commit file system operation
        fileSystemUtil.commit(resultStack);
        // update operation status
        logService.addOperationResult(userOperationLog, true, (long) file.length);
        Result result = resultUtil.success();
        result.setCount((long) file.length);
        return result;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Result deleteFile(File file){
        Stack<FileSystemOperationResult> resultStack = null;
        // create log object
        UserOperationLog userOperationLog = new UserOperationLog();
        userOperationLog.setUsername(file.getUsername());
        userOperationLog.setOperation(Thread.currentThread().getStackTrace()[1].getMethodName()); // get method name
        userOperationLog.setCount(1L);
        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
        try {
            // save operation log and get id
            logService.addOperationLog(userOperationLog);
            // check whether file is valid
            if (!this.isFilenameExist(file)) {
                throw new CustomizedExcption(ResultEnum.TARGET_FILE_NOT_EXIST);
            }
            // process data in database
            if (fileMapper.delete(new QueryWrapper<File>(file)) != 1) {
                throw new CustomizedExcption(ResultEnum.UNEXPECTED_DATABASE_OPERATION_RESULT);
                }
            // process file in file system
            resultStack = fileSystemUtil.deleteUserFile(userOperationLog.getId(), file.getUsername(), file.getFileName());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // rollback database
            dataSourceTransactionManager.rollback(transactionStatus);
            // rollback file system operation
            fileSystemUtil.rollback(resultStack);
            // update operation status
            logService.addOperationResult(userOperationLog, false);
            //  throw it again so that GlobalExceptionHandler can process it
            throw e;
        }
        // commit database
        dataSourceTransactionManager.commit(transactionStatus);
        // commit file system operation
        fileSystemUtil.commit(resultStack);
        // update operation status
        logService.addOperationResult(userOperationLog, true);
        return resultUtil.success();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Result clearUserFile(String username) {
        Stack<FileSystemOperationResult> resultStack = null;
        // create log object
        UserOperationLog userOperationLog = new UserOperationLog();
        userOperationLog.setUsername(username);
        userOperationLog.setOperation(Thread.currentThread().getStackTrace()[1].getMethodName()); // get method name
        userOperationLog.setCount(0L);
        Long count = 0L;
        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
        try {
            // save operation log and get id
            logService.addOperationLog(userOperationLog);
            // process data in database
            count = fileMapper.selectCount(new QueryWrapper<File>().eq(COLUMN_USERNAME, username)).longValue();
            if (count == 0) { // given username has no file in file system
                logService.addOperationResult(userOperationLog, true);
                return resultUtil.success();
            }else if (fileMapper.delete(new QueryWrapper<File>().eq(COLUMN_USERNAME, username)) == 0) {
                throw new CustomizedExcption(ResultEnum.UNEXPECTED_DATABASE_OPERATION_RESULT);
            }
            // process file in file system
            resultStack = fileSystemUtil.deleteUserFolder(userOperationLog.getId(), username);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // rollback database
            dataSourceTransactionManager.rollback(transactionStatus);
            // rollback file system operation
            fileSystemUtil.rollback(resultStack);
            // update operation status
            logService.addOperationResult(userOperationLog, false);
            //  throw it again so that GlobalExceptionHandler can process it
            throw e;
        }
        // commit database
        dataSourceTransactionManager.commit(transactionStatus);
        // commit file system operation
        fileSystemUtil.commit(resultStack);
        // update operation status
        logService.addOperationResult(userOperationLog, true, count);
        Result result = resultUtil.success();
        result.setCount(count);
        return result;
    }

    private Boolean isFilenameExist(File file) {
        return fileMapper.selectCount(new QueryWrapper<File>(file)) == 1;
    }
    private Boolean isFilenameExist(List<File> file) {
        for (File fileItem : file) {
            if (fileMapper.selectCount(new QueryWrapper<File>(fileItem)) == 1) {
                return true;
            }
        }
        return false;
    }
}
