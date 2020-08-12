package indi.ipan.util;

import java.io.File;
import java.io.IOException;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import indi.ipan.exception.CustomizedExcption;
import indi.ipan.exception.ResultEnum;
import indi.ipan.model.FileSystemOperationResult;

@Component
public class FileSystemUtil {
    private final static Logger logger = LoggerFactory.getLogger(FileSystemUtil.class);
    private static final String LOCAL_FILE_SYSTEM_PATH = 
            "C:\\Users\\Administrator\\eclipse-workspace\\iPan\\src\\file_system\\";
    private static final String LOCAL_FILE_SYSTEM_CACHE_PATH = 
            "C:\\Users\\Administrator\\eclipse-workspace\\iPan\\src\\cache\\";
    /**
     * commit file system operation
     * @param resultStack list of file operation path including origin, cache and destination
     */
    public void commit(Stack<FileSystemOperationResult> resultStack) {
        while (!resultStack.empty()) {
            FileSystemOperationResult result = resultStack.pop();
            File cacheFile = new File(result.getCache());
            File destFile = null;
            if (result.getDestination() == null) { // destination path does not exist
                if (!cacheFile.delete()) {
                    throw new CustomizedExcption(ResultEnum.FILE_SYSTEM_OPERATION_ERROR);
                }
            }else { // destination path exists
                destFile = new File(result.getDestination());
                if (!destFile.getParentFile().exists()) { // create folder if needed
                    destFile.getParentFile().mkdir();
                }
                if (!cacheFile.renameTo(destFile)) {
                    throw new CustomizedExcption(ResultEnum.FILE_SYSTEM_OPERATION_ERROR);
                }
            }
        }
    }
    /**
     * rollback file system operation
     * @param resultStack list of file operation path including origin, cache and destination
     */
    public void rollback(Stack<FileSystemOperationResult> resultStack) {
        if (resultStack != null) {
            while (!resultStack.empty()) {
                FileSystemOperationResult result = resultStack.pop();
                File cacheFile = new File(result.getCache());
                File originFile = null;
                if (result.getOrigin() == null) { // original path does not exist
                    if (!cacheFile.delete()) {
                        throw new CustomizedExcption(ResultEnum.FILE_SYSTEM_OPERATION_ERROR);
                    }
                }else { // original path exists
                    originFile = new File(result.getOrigin());
                    if (!cacheFile.renameTo(originFile)) {
                        throw new CustomizedExcption(ResultEnum.FILE_SYSTEM_OPERATION_ERROR);
                    }
                }
            }
        }
    }
    /**
     * delete one file from file system
     * @param id id of current operation log
     * @param username username of file
     * @param filename file name of file to be deleted
     * @return list of (original path, cache path, destination path)
     */
    public Stack<FileSystemOperationResult> deleteUserFile(Integer id, String username, String filename) {
        Stack<FileSystemOperationResult> resultStack = new Stack<>();
        File file = new File(LOCAL_FILE_SYSTEM_PATH + username + File.separator + filename);
        deleteFile(file, id, resultStack);
        return resultStack;
    }
    /**
     * delete folder of given username
     * @param id id of current operation log
     * @param username username of file
     * @return list of (original path, cache path, destination path)
     */
    public Stack<FileSystemOperationResult> deleteUserFolder(Integer id, String username) {
        File userFolder = new File(LOCAL_FILE_SYSTEM_PATH + username);
        Stack<FileSystemOperationResult> resultStack = new Stack<>();
        deleteFolder(userFolder, id, resultStack);
        return resultStack;
    }
    /**
     * delete folder of whole file system
     * @return list of (original path, cache path, destination path)
     */
    public Stack<FileSystemOperationResult> deleteSystemFolder(Integer id) {
        File systemFolder = new File(LOCAL_FILE_SYSTEM_PATH);
        Stack<FileSystemOperationResult> resultStack = new Stack<>();
        deleteFolder(systemFolder, id, resultStack);
        return resultStack;
    }
    /**
     * rename one file in file system
     * @param id id of current operation log
     * @param username username of user
     * @param oldFileName old file name
     * @param newFileName new file name
     * @return list of (original path, cache path, destination path)
     */
    public Stack<FileSystemOperationResult> renameFile(Integer id, String username, String oldFileName, String newFileName) {
        Stack<FileSystemOperationResult> resultStack = new Stack<>();
        FileSystemOperationResult result = new FileSystemOperationResult();
        result.setDestination(LOCAL_FILE_SYSTEM_PATH + username + File.separator + newFileName);
        result.setCache(LOCAL_FILE_SYSTEM_CACHE_PATH + id.toString() + username + oldFileName);
        result.setOrigin(LOCAL_FILE_SYSTEM_PATH + username + File.separator + oldFileName);
        File originFile = new File(result.getOrigin());
        if (!originFile.renameTo(new File(result.getCache()))) {
            throw new CustomizedExcption(ResultEnum.FILE_SYSTEM_OPERATION_ERROR);
        }
        resultStack.add(result);
        return resultStack;
    }
    /**
     * upload a file to file system
     * @param id id of current operation log
     * @param username username of user
     * @param file file to be uploaded
     * @return list of (original path, cache path, destination path)
     */
    public Stack<FileSystemOperationResult> uploadFile(Integer id, String username, MultipartFile file) {
        Stack<FileSystemOperationResult> resultStack = new Stack<>();
        FileSystemOperationResult result = new FileSystemOperationResult();
        String fileName = file.getOriginalFilename();
        result.setDestination(LOCAL_FILE_SYSTEM_PATH + username + File.separator + fileName);
        result.setCache(LOCAL_FILE_SYSTEM_CACHE_PATH + id.toString() + username + fileName);
        result.setOrigin(null);
        try {
            file.transferTo(new File(result.getCache()));
        } catch (IllegalStateException | IOException e) {
            logger.error(e.getMessage(), e);
            throw new CustomizedExcption(ResultEnum.FILE_SYSTEM_OPERATION_ERROR);
        }
        resultStack.add(result);
        return resultStack;
    }
    /**
     * upload list of file to file system
     * @param id id of current operation log
     * @param username username of user
     * @param file list of file to be uploaded
     * @return list of (original path, cache path, destination path)
     */
    public Stack<FileSystemOperationResult> uploadMultiFile(Integer id, String username, MultipartFile[] file) {
        Stack<FileSystemOperationResult> resultStack = new Stack<>();
        for (MultipartFile fileItemFile : file) {
            resultStack = uploadFile(id, username, fileItemFile);
        }
        return resultStack;
    }
    /**
     * delete a folder in file system
     * @param file folder file to be deleted
     * @param id id of current operation log
     * @param resultStack list of (original path, cache path, destination path)
     */
    private void deleteFolder(File file, Integer id, Stack<FileSystemOperationResult> resultStack) {
        // check whether user folder or file system folder
        if (file.isDirectory()) {
            File[] childfiles = file.listFiles();
            for(File childfile : childfiles){                           
                if (childfile.isDirectory()) {
                    deleteFolder(childfile, id, resultStack);
                }else {
                    deleteFile(childfile, id, resultStack);
                }
            }
            deleteEmptyFolder(file, id, resultStack);
        }
    }
    /**
     * delete a file in file system
     * @param file file to be deleted
     * @param id id of current operation log
     * @param resultStack list of (original path, cache path, destination path)
     */
    private void deleteFile(File file, Integer id, Stack<FileSystemOperationResult> resultStack) {
        FileSystemOperationResult result = new FileSystemOperationResult();
        result.setDestination(null);
        String filename = file.getName();
        String username = file.getParentFile().getName();
        result.setCache(LOCAL_FILE_SYSTEM_CACHE_PATH + id.toString() + username + filename);
        result.setOrigin(file.getPath());
        if (!file.renameTo(new File(result.getCache()))) {
            throw new CustomizedExcption(ResultEnum.FILE_SYSTEM_OPERATION_ERROR);
        }
        resultStack.add(result);
    }
    /**
     * delete empty folder in file system
     * @param file folder file to be deleted
     * @param id id of current operation log
     * @param resultStack list of (original path, cache path, destination path)
     */
    private void deleteEmptyFolder(File file, Integer id, Stack<FileSystemOperationResult> resultStack) {
        FileSystemOperationResult result = new FileSystemOperationResult();
        result.setDestination(null);
        String username = file.getName();
        result.setCache(LOCAL_FILE_SYSTEM_CACHE_PATH + id.toString() + username);
        result.setOrigin(file.getPath());
        if (!file.renameTo(new File(result.getCache()))) {
            throw new CustomizedExcption(ResultEnum.FILE_SYSTEM_OPERATION_ERROR);
        }
        resultStack.add(result);
    }
    private Boolean openFileSystem() { //TODO
        File baseFolder = new File(LOCAL_FILE_SYSTEM_PATH);
        if (!baseFolder.exists()) {
            return baseFolder.mkdir();
        }
        return true;
    }
}
