package indi.ipan.dao;

import org.springframework.stereotype.Repository;

import indi.ipan.model.UserOperationLog;

@Repository
public interface LogDao {
    /**
     * add operation log into table user_operation_log
     * @param username username of user
     * @param operation operation name e.g. search file by username
     * @param count number of data referenced
     * @return 1 if success, 0 if not
     */
    public Integer addLog(UserOperationLog userOperationLog);
    /**
     * add result status of operation into table user_operation_log
     * @param id id of operation in table user_operation_log
     * @param status true if commit, false if rollback
     * @return 1 if success, 0 if not
     */
    public Integer addResult(UserOperationLog userOperationLog);
}
