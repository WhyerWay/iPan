package indi.ipan;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import indi.ipan.controller.FileController;
import indi.ipan.mapper.FileMapperTest;
import indi.ipan.model.File;
import indi.ipan.model.User;
import indi.ipan.result.Result;
import indi.ipan.service.FileService;
import indi.ipan.service.UserService;

@SpringBootTest(classes = IPanApplication.class) //启动类
public class DummyTest {
    
    @Autowired
    FileController fillController;
    @Autowired
    FileService fileService;
    
    @Test
    public void test() {
        Result result = fillController.renameFile("1234", "6", "7");
        System.out.println(result);
    }

}
