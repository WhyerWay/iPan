package indi.ipan.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import indi.ipan.exception.CustomizedExcption;
import indi.ipan.exception.ResultEnum;
import indi.ipan.mapper.LogMapper;
import indi.ipan.model.UserOperationLog;
import indi.ipan.service.LogService;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class LogSeriveImpl extends ServiceImpl<LogMapper, UserOperationLog> implements LogService{
    @Autowired
    private LogMapper logMapper;

    @Override
    public void addOperationLog(UserOperationLog userOperationLog) {
        if (logMapper.insert(userOperationLog) != 1) {
            throw new CustomizedExcption(ResultEnum.UNEXPECTED_DATABASE_LOGGING_OPERATION_RESULT);
        }
    }

    @Override
    public void addOperationResult(UserOperationLog userOperationLog, Boolean status) {
        userOperationLog.setStatus(status);
        if (logMapper.updateById(userOperationLog) != 1) {
            throw new CustomizedExcption(ResultEnum.UNEXPECTED_DATABASE_LOGGING_OPERATION_RESULT);
        }
    }

    @Override
    public void addOperationResult(UserOperationLog userOperationLog, Boolean status, Long count) {
        userOperationLog.setStatus(status);
        userOperationLog.setCount(count);
        if (logMapper.updateById(userOperationLog) != 1) {
            throw new CustomizedExcption(ResultEnum.UNEXPECTED_DATABASE_LOGGING_OPERATION_RESULT);
        }
    }


}
