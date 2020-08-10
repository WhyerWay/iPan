package indi.ipan.dao;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.stereotype.Repository;

import indi.ipan.model.File;

@Repository
public interface FileDao {
    /**
     * count number of file of given username in table file
     * @param username
     * @return non-negative integer if success
     */
    public Integer countFileByUsername(String username);
    /**
     * count number of file in table file
     * @return non-negative integer
     */
    public Integer countFile();
    /**
     * delete all files of given username from table file
     * @param username username of user
     * @return positive integer if success, 0 if fail
     */
    public Integer deleteFileByUsername(String username);
    /**
     * delete all files from table file
     * @return positive integer if success, 0 if fail
     */
    public Integer deleteAllFile();
    /**
     * get all filename of files of given username from table file
     * @param username
     * @return list of file
     */
    public List<File> getAllFileByUsername(String username);
    /**
     * get all file information from table file
     * @return list of (username, file_name)
     */
    public List<File> getAllFile();
    /**
     * check whether (username, file_name) exist in table file
     * @param file username and file_name
     * @return true if exist, null if not
     */
    public Boolean isFilenameExist(File file);
    /**
     * update filename of user for a file in table file
     * @param map username, oldname and new name
     * @return 1 if success, 0 if not
     */
    public Integer renameFile(Map map);
    /**
     * add a file in table file
     * @param file username and file_name
     * @return 1 if success, 0 if not
     */
    public Integer addFile(File file);
    /**
     * check whether a list of (usernaem, filename) exist in table file 
     * @param username username of user
     * @param filenames list of filename
     * @return list of existed file name
     */
    public List<String> isMultiFilenameExist(String username, List<String> filenames);
    /**
     * add multiple files into table file
     * @param username username of user
     * @param filenames list of file name
     * @return positive integer if success
     */
    public Integer addMultiFile(String username, List<String> filenames);
    /**
     * delete a file from table file
     * @param file username and file_name
     * @return 1 if success, 0 if not
     */
    public Integer deleteFile(File file);
    /**
     * delete all file of given username from table file
     * @param username username of user
     * @return positive integer if success, 0 if not
     */
    public Integer clearFile(String username);
}
