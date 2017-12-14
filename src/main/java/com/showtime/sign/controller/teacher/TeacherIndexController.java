package com.showtime.sign.controller.teacher;

import com.showtime.sign.service.AdminService;
import com.showtime.sign.service.CourseService;
import com.showtime.sign.service.TeacherService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 老师可以发起签到、查看签到记录、导出签到记录到Excel
 */
@Controller
@Slf4j
@RequestMapping(value = "/teacher")
public class TeacherIndexController {


    @Autowired
    private TeacherService teacherService;

    @Autowired
    private CourseService courseService;

    @GetMapping(value = {"/index"})
    public String index(){
        return "teacher/index";
    }

    @GetMapping(value = {"/change/password"})
    public String IntoChangePassword(){
        return "teacher/changePassword";
    }

    @ResponseBody
    @PostMapping(value = {"/change/password"})
    public String updatePassword(@Param("password") String password){
        teacherService.updatePassword(password);
        return "密码修改成功";
    }

}
