package com.showtime.sign.controller.student;

import com.showtime.sign.service.AdminService;
import com.showtime.sign.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 学生可以签到、查看历史签到记录
 */
@Controller
@Slf4j
@RequestMapping(value = "/student")
public class StudentIndexController {


    @Autowired
    private StudentService studentService;

    @GetMapping(value = {"/index"})
    public String index(){
        return "student/index";
    }

}
