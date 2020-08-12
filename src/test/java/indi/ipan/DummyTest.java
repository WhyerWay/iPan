package indi.ipan;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import indi.ipan.controller.FileController;
import indi.ipan.controller.UserController;
import indi.ipan.mapper.FileMapperTest;
import indi.ipan.model.File;
import indi.ipan.model.User;
import indi.ipan.result.Result;
import indi.ipan.service.FileService;
import indi.ipan.service.UserService;

@SpringBootTest(classes = IPanApplication.class) //启动类
public class DummyTest {
    
    @Autowired
    FileController fileController;
    
//    @Test
//    public void test() {
//    }
}
