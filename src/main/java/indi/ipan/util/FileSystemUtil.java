package indi.ipan.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import indi.ipan.exception.CustomizedExcption;
import indi.ipan.exception.ResultEnum;
import indi.ipan.model.FileSystemOperationResult;

@Component
public class FileSystemUtil {
    private static final String LOCAL_FILE_SYSTEM_PATH = 
            "C:\\Users\\Administrator\\eclipse-workspace\\iPan\\src\\file_system\\";
    private static final String LOCAL_FILE_SYSTEM_CACHE_PATH = 
            "C:\\Users\\Administrator\\eclipse-workspace\\iPan\\src\\cache\\";
    /**
     * commit file system operation
     * @param resultList list of file operation path including origin, cache and destination
     */
    public void commit(List<FileSystemOperationResult> resultList) {
        for (FileSystemOperationResult result : resultList) {
            File cacheFile = new File(result.getCache());
            File destFile = null;
            if (result.getDestination() != null) {
                destFile = new File(result.getDestination());
            }
            if (result.getDestination() == null) {
                if (!cacheFile.delete()) {
                    throw new CustomizedExcption(ResultEnum.FILE_SYSTEM_OPERATION_ERROR);
                }
            }else if (!cacheFile.renameTo(destFile)) {
                throw new CustomizedExcption(ResultEnum.FILE_SYSTEM_OPERATION_ERROR);
            }else {
                throw new CustomizedExcption(ResultEnum.UNKONW_ERROR);
            }
        }
    }
    /**
     * rollback file system operation
     * @param resultList list of file operation path including origin, cache and destination
     */
    public void rollback(List<FileSystemOperationResult> resultList) {
        for (FileSystemOperationResult result : resultList) {
            File originFile = new File(result.getOrigin());
            File cacheFile = new File(result.getCache());
            if (!cacheFile.renameTo(originFile)) {
                throw new CustomizedExcption(ResultEnum.FILE_SYSTEM_OPERATION_ERROR);
            }
        }
    }
    public Boolean openFileSystem() {
        File baseFolder = new File(LOCAL_FILE_SYSTEM_PATH);
        if (!baseFolder.exists()) {
            return baseFolder.mkdir();
        }
        return true;
    }
    public List<FileSystemOperationResult> deleteFile(Integer id, String username, String filename) {
        List<FileSystemOperationResult> resultList = new ArrayList<>();
        FileSystemOperationResult result = new FileSystemOperationResult();
        result.setDestination(null);
        result.setCache(LOCAL_FILE_SYSTEM_CACHE_PATH + id.toString() + username + filename);
        result.setOrigin(LOCAL_FILE_SYSTEM_PATH + username + File.separator + filename);
        File originFile = new File(result.getOrigin());
        if (!originFile.renameTo(new File(result.getCache()))) {
            throw new CustomizedExcption(ResultEnum.FILE_SYSTEM_OPERATION_ERROR);
        }
        resultList.add(result);
        return resultList;
    }
    public Boolean deleteUserFolder(String username) {
        File userFolder = new File(LOCAL_FILE_SYSTEM_PATH + username);
        return deleteFolder(userFolder);
    }
    public Boolean deleteSystemFolder() {
        File systemFolder = new File(LOCAL_FILE_SYSTEM_PATH);
        return deleteFolder(systemFolder);
    }
    public Boolean deleteFolder(File file) {
        if (file.isDirectory()) {
            File[] childfiles = file.listFiles();
            for(File childfile : childfiles){                           
                if (!deleteFolder(childfile)) {
                    return false;
                }          
            }
        }
        return file.delete();
    }
    
    public Boolean renameFile(String username, String oldFileName, String newFileName) {
        File oldFile = new File(LOCAL_FILE_SYSTEM_PATH + username, oldFileName);
        return oldFile.renameTo(new File(LOCAL_FILE_SYSTEM_PATH + username, newFileName));
    }
    
    public Boolean uploadFile(String username, MultipartFile file) {
        String fileName = file.getOriginalFilename();
        File filepath = new File(LOCAL_FILE_SYSTEM_PATH + username, fileName);
        if (!filepath.getParentFile().exists()) {
            filepath.getParentFile().mkdirs();
        }
        try {
            file.transferTo(new File(LOCAL_FILE_SYSTEM_PATH + username + File.separator + fileName));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public Integer uploadMultiFile(String username, MultipartFile[] file) {
        Integer count = 0;
        String storePath = LOCAL_FILE_SYSTEM_PATH + username;
        File folder = new File(storePath);
        if (!folder.exists()) {
            folder.mkdir();
        }
        for (MultipartFile f : file) {
            String fileName = f.getOriginalFilename();
            try {
                f.transferTo(new File(storePath + File.separator + fileName));
                count++;
            } catch (Exception e) {
                e.printStackTrace();
                return count;
            }
        }
        return count;
    }
}
