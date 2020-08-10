package indi.ipan.service;

import indi.ipan.model.UserOperationLog;

public interface LogService {
    /**
     * add operation log into database
     * @param username username of user
     * @param operation operation name e.g. search file by username
     * @param count number of data referenced
     * @return true if success, false if not
     */
    public Boolean addOperationLog(UserOperationLog userOperationLog);
    /**
     * add result status of operation into database
     * @param id id of operation in table user_operation_log
     * @param status true if commit, false if rollback
     * @return true if success, false if not
     */
    public Boolean addOperationResult(UserOperationLog userOperationLog);
}
