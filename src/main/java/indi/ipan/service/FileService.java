package indi.ipan.service;

import org.springframework.web.multipart.MultipartFile;

import indi.ipan.model.File;
import indi.ipan.model.ServiceResult;
import indi.ipan.result.Result;

public interface FileService {
    /**
     * get all filename of files under given username in page
     * @param username username of user
     * @param current target page number
     * @param size number of item in one page
     * @return (200, success, number of file, list of file) if success, others if not
     */
    @SuppressWarnings("rawtypes")
    Result listFileByUsername(String username, Long current, Long size);
    /**
     * get all file information in file system
     * @param current target page number
     * @param size number of item in one page
     * @return (200, success, number of file, list of file) if success, others if not
     */
    @SuppressWarnings("rawtypes")
    Result listAllFile(Long current, Long size);
    /**
     * get all filename of files like given file name in page
     * @param file file name of target file
     * @param current target page number
     * @param size number of item in one page
     * @return (200, success, number of file, list of file) if success, others if not
     */
    @SuppressWarnings("rawtypes")
    Result listFileByFilename(File file, Long current, Long size);
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
     * @return (200, success, 1) if success, others if not
     */
    @SuppressWarnings("rawtypes")
    Result uploadFile(String username, MultipartFile file);
    /**
     * upload list of file to file system
     * @param username username of user
     * @param file list of file to be uploaded
     * @return (200, success, number of file) if success, others if not
     */
    @SuppressWarnings("rawtypes")
    Result uploadMultiFile(String username, MultipartFile[] file);
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
