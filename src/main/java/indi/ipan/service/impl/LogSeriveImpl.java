package indi.ipan.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import indi.ipan.mapper.LogMapperTest;
import indi.ipan.model.UserOperationLog;
import indi.ipan.service.LogService;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class LogSeriveImpl extends ServiceImpl<LogMapperTest, UserOperationLog> implements LogService{
    @Autowired
    LogMapperTest logMapperTest;

    @Override
    public Boolean addOperationLog(UserOperationLog userOperationLog) {
        return logMapperTest.insert(userOperationLog) == 1;
    }

    @Override
    public Boolean addOperationResult(UserOperationLog userOperationLog) {
        return logMapperTest.updateById(userOperationLog) == 1;
    }


}
