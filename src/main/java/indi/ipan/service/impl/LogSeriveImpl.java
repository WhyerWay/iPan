package indi.ipan.service.impl;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import indi.ipan.dao.LogDao;
import indi.ipan.model.UserOperationLog;
import indi.ipan.service.LogService;

@Service
@MapperScan("indi.ipan.dao")
public class LogSeriveImpl implements LogService{
    @Autowired
    private LogDao logDao;

    @Override
    public Boolean addOperationLog(UserOperationLog userOperationLog) {
        return logDao.addLog(userOperationLog) == 1;
    }

    @Override
    public Boolean addOperationResult(UserOperationLog userOperationLog) {
        return logDao.addResult(userOperationLog) == 1;
    }


}
