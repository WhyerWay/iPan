package indi.ipan.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import indi.ipan.model.File;
import indi.ipan.model.FileSystemOperationResult;
import indi.ipan.model.ServiceResult;
import indi.ipan.model.UserAndFile;
import indi.ipan.result.Result;

public interface FileService {
    /**
     * get all filename of files under given username in page
     * @param username username of user
     * @param current target page number
     * @param size number of item in one page
     * @return (200, success, list of file, number of file) if success, others if not
     */
    @SuppressWarnings("rawtypes")
    Result listFileByUsername(String username, Long current, Long size);
    /**
     * get all file information in file system
     * @param current target page number
     * @param size number of item in one page
     * @return (200, success, list of file, number of file) if success, others if not
     */
    @SuppressWarnings("rawtypes")
    Result listFile(Long current, Long size);
    /**
     * get all filename of files like given file name in page
     * @param file file name of target file
     * @param current target page number
     * @param size number of item in one page
     * @return (200, success, list of file, number of file) if success, others if not
     */
    @SuppressWarnings("rawtypes")
    Result listFileByFilename(File file, Long current, Long size);
    /**
     * check whether user has file of given filename
     * @param file username of user and filename of file
     * @return true if exist, false if not
     */
    Boolean isFilenameExist(File file);
    /**
     * rename a file for given username
     * @param oldFile original filename and username
     * @param newFile new filename and username
     * @return (200, success, 1) if success, others if not
     */
    @SuppressWarnings("rawtypes")
    Result renameFile(File oldFile, File newFile);
    /**
     * upload a file to user's account
     * @param username username of user
     * @param file target file
     * @return 0 if success
     *     , -1 if file is empty
     *     , -2 if filename already exist
     *     , -3 if insert operation in database fail
     *     , -4 if transfer operation in file system fail
     */
    Integer uploadFile(String username, MultipartFile file);
    /**
     * check whether user has given file name
     * @param username username of user
     * @param file uploaded files
     * @return error index and error information
     *     , 0 if success
     *     , -1 if empty file exist, file name of empty file in errorInfo list
     *     , -2 if duplicated file name exist, duplicated file name in errorInfo list
     */
    ServiceResult uploadMultiFile(String username, MultipartFile[] file);
    /**
     * delete desired file from user's account
     * @param file username and file_name
     * @return (200, success, 1) if success, other if not
     */
    @SuppressWarnings("rawtypes")
    Result deleteFile(File file);
    /**
     * delete all files under user's account
     * @param username username of user
     * @return (200, success, number of file affected) if success, other if not
     */
    @SuppressWarnings("rawtypes")
    Result clearUserFile(String username);
}
