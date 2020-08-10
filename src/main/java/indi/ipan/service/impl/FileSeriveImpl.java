package indi.ipan.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import indi.ipan.dao.FileDao;
import indi.ipan.dao.LogDao;
import indi.ipan.exception.CustomizedExcption;
import indi.ipan.exception.ResultEnum;
import indi.ipan.mapper.FileMapperTest;
//import indi.ipan.mapper.FileMapperTest;
import indi.ipan.model.File;
import indi.ipan.model.FileSystemOperationResult;
import indi.ipan.model.ServiceResult;
import indi.ipan.model.UserOperationLog;
import indi.ipan.result.Result;
import indi.ipan.result.ResultUtil;
import indi.ipan.service.FileService;
import indi.ipan.service.LogService;
import indi.ipan.util.FileSystemUtil;

@Service
@MapperScan("indi.ipan.dao")
//public class FileSeriveImpl implements FileService{
public class FileSeriveImpl extends ServiceImpl<FileMapperTest, File> implements FileService{
    @Autowired
    private FileDao fileDao;
    @Autowired
    private LogService logService;
    @Autowired
    DataSourceTransactionManager dataSourceTransactionManager;
    @Autowired
    TransactionDefinition transactionDefinition;
    @Autowired
    private FileSystemUtil fileSystemUtil;
    @Autowired
    ResultUtil resultUtil;
    @Autowired
    FileMapperTest fileMapperTest;

    private final static Logger logger = LoggerFactory.getLogger(FileSeriveImpl.class);
    
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_FILE_NAME = "file_name";
    
    @SuppressWarnings("rawtypes")
    @Override
    public Result listFileByUsername(String username, Long current, Long size) {
        Page<File> files = fileMapperTest.selectPage(new Page<>(current, size)
                , new QueryWrapper<File>().eq(COLUMN_USERNAME, username));
        return resultUtil.success(files);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Result listFile(Long current, Long size) {
        Page<File> files = fileMapperTest.selectPage(new Page<>(current, size), null);
        return resultUtil.success(files);
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public Result listFileByFilename(File file, Long current, Long size) {
        Page<File> files = fileMapperTest.selectPage(new Page<>(current, size)
                , new QueryWrapper<File>()
                .eq(COLUMN_USERNAME, file.getUsername())
                .like(COLUMN_FILE_NAME, file.getFileName()));
        return resultUtil.success(files);
    }
    
    @Override
    public Boolean isFilenameExist(File file) {
        return fileMapperTest.selectCount(new QueryWrapper<File>(file)) == 1;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Result renameFile(File oldFile, File newFile) {
        if (!isFilenameExist(oldFile)) {
            throw new CustomizedExcption(ResultEnum.TARGET_FILE_NOT_EXIST);
        }
        if (isFilenameExist(newFile)) {
            throw new CustomizedExcption(ResultEnum.INVALID_INPUT);
        }
        if (fileMapperTest.update(newFile, new UpdateWrapper<File>(oldFile)) != 1) {
            throw new CustomizedExcption(ResultEnum.UNEXPECTED_DATABASE_OPERATION_RESULT);
        }
        if (fileSystemUtil.renameFile(oldFile.getUsername()
                , oldFile.getFileName()
                , newFile.getFileName())) {
            return resultUtil.success();
        }else {
            throw new CustomizedExcption(ResultEnum.UNKONW_ERROR);
        }
    }

    @Override
    public Integer uploadFile(String username, MultipartFile file) {
        if (file.isEmpty()) {
            return -1;
        }
        String fileName = file.getOriginalFilename();
        File file_db = new File();
        file_db.setUsername(username);
        file_db.setFileName(fileName);
        if (fileDao.isFilenameExist(file_db) != null) {
            return -2;
        }
        if (fileDao.addFile(file_db) != 1) {
            return -3;
        }
        if (fileSystemUtil.uploadFile(username, file)) {
            return 0;
        }else {
            return -4;
        }
    }

    @Override
    public ServiceResult uploadMultiFile(String username, MultipartFile[] file) {
        ServiceResult result = new ServiceResult();
        List<String> errorInfo = new ArrayList<>();
        List<String> filenames = new ArrayList<>();
        // filter empty files
        for (MultipartFile f : file) {
            if (f.isEmpty()) {
                errorInfo.add(f.getOriginalFilename());
                continue;
            }
            filenames.add(f.getOriginalFilename());
        }
        if (!errorInfo.isEmpty()) {
            result.setIndex(-1);
            result.setErrorInfo(errorInfo);
            return result;
        }
        // filter duplicated file name
        errorInfo = fileDao.isMultiFilenameExist(username, filenames);
        if (!errorInfo.isEmpty()) {
            result.setIndex(-2);
            result.setErrorInfo(errorInfo);
            return result;
        }
        // insert in database
        Integer num_db = fileDao.addMultiFile(username, filenames);
        result.setIndex(num_db);
        if (num_db <= 0) {
            result.setIndex(-3);
            return result;
        }
        // transfer to file system
        Integer num_f = fileSystemUtil.uploadMultiFile(username, file);
        if (num_f != num_db) {
            result.setIndex(-4);
            return result;
        }
        result.setErrorInfo(null);
        return result;
    }

    @Override
    public Result deleteFile(File file){
        List<FileSystemOperationResult> resultList = null;
        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
        try {
            // create log object
            UserOperationLog userOperationLog = new UserOperationLog();
            userOperationLog.setUsername(file.getUsername());
            userOperationLog.setOperation(Thread.currentThread().getStackTrace()[1].getMethodName()); // get method name
            userOperationLog.setCount(1);
            // save operation log and get id
            if (!logService.addOperationLog(userOperationLog)) {
                throw new CustomizedExcption(ResultEnum.UNEXPECTED_DATABASE_OPERATION_RESULT);
                }
            // check whether file is valid
            if (!this.isFilenameExist(file)) {
                throw new CustomizedExcption(ResultEnum.TARGET_FILE_NOT_EXIST);
            }
            // process data in database
            if (fileDao.deleteFile(file) != 1) {
                throw new CustomizedExcption(ResultEnum.UNEXPECTED_DATABASE_OPERATION_RESULT);
                }
            resultList = fileSystemUtil.deleteFile(userOperationLog.getId(), file.getUsername(), file.getFileName());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // rollback database
            dataSourceTransactionManager.rollback(transactionStatus);
            // rollback file system operation
            fileSystemUtil.rollback(resultList);
            //  throw it again so that GlobalExceptionHandler can process it
            throw e;
        }
        // commit database
        dataSourceTransactionManager.commit(transactionStatus);
        // commit file system operation
        fileSystemUtil.commit(resultList);
        return resultUtil.success();
    }

    @Override
    public Boolean clearFile(String username) {
        Integer num = fileDao.countFileByUsername(username);
        if (num == 0) {
            return true;
        }
        if (num.equals(fileDao.clearFile(username))) {
            if (fileSystemUtil.deleteUserFolder(username)) {
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
        
    }


}
