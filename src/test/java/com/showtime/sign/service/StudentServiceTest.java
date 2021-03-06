package com.showtime.sign.service;

import com.showtime.sign.SignApplication;
import com.showtime.sign.constant.AccountPasswordConstant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SignApplication.class)
public class StudentServiceTest {

    @Autowired
    private StudentService studentService;

    @Test
    public void register() throws Exception {
        studentService.register("3114001458", AccountPasswordConstant.PASSWORD, "张三", "物联网14(4)");
    }

    @Test
    public void login() throws Exception {
    }

    @Test
    public void getStudents() throws Exception {
    }

    @Test
    public void logout() throws Exception {
    }

}