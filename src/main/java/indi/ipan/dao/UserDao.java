package indi.ipan.dao;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import indi.ipan.model.File;
import indi.ipan.model.User;

@Repository
public interface UserDao {
    /**
     * check whether username exist in table user
     * @param user only user.username being checked
     * @return ture if username exist, null if not
     */
	public Boolean isUsernameExist(User user);
	/**
	 * get size of tabel user
	 * @return non-negative integer
	 */
	public Integer countUser();
	/**
	 * add a user into table user
	 * @param user username and password of user
	 * @return 1 if success, 0 if fail
	 */
    public Integer addUser(User user);
    /**
     * add list of user into table user
     * @param user username and password
     * @return positive integer if success, 0 if not for unknown reason
     */
    public Integer batchAddUser(List<User> user);
    /**
     * check whether username and password match in table user
     * @param user username and password of user
     * @return ture if username and password match, null if not
     */
    public Boolean matchPassword(User user);
    /**
     * change password of given username in table user
     * @param user username and password of user
     * @return 1 if success, 0 if fail
     */
    public Integer changePassword(User user);
    /**
     * delete given username from table user
     * @param user username
     * @return 1 if success, 0 if fail
     */
    public Integer deleteUser(String username);
    /**
     * delete all user from table user
     * @return positive integer if success, 0 if not
     */
    public Integer deleteAllUser();
    /**
     * get one user in table user
     * @return (username, password)
     */
    User getOneUser(String username);
    /**
     * get all user in table user
     * @return list of user
     */
    List<User> getAllUser();
//    
//    public List<IPanFile> selectAllFilesByUsername(String username);
//    
//    public int addFileByUsername(IPanFile file);
//    
//    public int deleteFile(IPanFile file);

//    
//    
//    
//    public int clearFile(IPanUser user);
//    
//    public List<IPanUser> selectAllUser();
//    
//    public List<IPanFile> selectAllFiles();
//    
//    public IPanUser selectOneUser(IPanUser user);
//
//    
//    
//    public int reset();
//    
//    public Boolean checkFile(IPanFile file);
//    
//    public int renameFile(Map map);
    
    
    
    
    
//    public int updateUser(MyUser user);
//
//    public int deleteUser(Integer uid);
}