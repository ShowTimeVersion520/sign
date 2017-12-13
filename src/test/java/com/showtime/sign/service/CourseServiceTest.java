package com.showtime.sign.service;

import com.showtime.sign.SignApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SignApplication.class)
public class CourseServiceTest {

    @Autowired
    private CourseService courseService;

    @Test
    public void insertCourseByExcel() throws Exception {

    }

}