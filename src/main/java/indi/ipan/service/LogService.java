package indi.ipan.service;

import indi.ipan.model.UserOperationLog;

public interface LogService {
    /**
     * add operation log into database
     * @param userOperationLog operation name and count
     */
    public void addOperationLog(UserOperationLog userOperationLog);
    /**
     * store result status of operation into database
     * @param userOperationLog operation log id
     * @param status operation result
     */
    public void addOperationResult(UserOperationLog userOperationLog, Boolean status);
    /**
     * store result of operation into database
     * @param userOperationLog operation log id
     * @param status operation result
     * @param count the number of item affected in this operation
     */
    public void addOperationResult(UserOperationLog userOperationLog, Boolean status, Long count);
}
