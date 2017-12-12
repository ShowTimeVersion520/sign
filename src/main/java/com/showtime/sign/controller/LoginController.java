package com.showtime.sign.controller;


import com.showtime.sign.constant.LoginTicketFieldConstant;
import com.showtime.sign.constant.ParamRoleConstant;
import com.showtime.sign.service.AdminService;
import com.showtime.sign.service.StudentService;
import com.showtime.sign.service.TeacherService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
public class LoginController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private TeacherService teacherService;

    @PostMapping(value = {"/login"})
    public String login(@Param("username") String username,
                        @Param("password") String password,
                        @Param("role") String role,
                        Model model, HttpServletResponse response) {
        log.info("username: {}, password: {}, role: {}", username, password, role);
        Map<String, Object> map = new HashMap<>();
        if(ParamRoleConstant.STUDENT.equals(role)){
            map = studentService.login(username, password);
        }else if(ParamRoleConstant.TEACHER.equals(role)){
            map = teacherService.login(username, password);
        }

        if(map.get("ticket") != null){
            Cookie cookie = new Cookie(LoginTicketFieldConstant.TICKET, map.get("ticket").toString());
            cookie.setPath("/");
            response.addCookie(cookie);
            if(ParamRoleConstant.STUDENT.equals(role)){
                return "redirect:student/index";
            }else if(ParamRoleConstant.TEACHER.equals(role)){
                return "redirect:teacher/index";
            }
        }
        model.addAllAttributes(map);
        return "login";
    }

    @GetMapping(value = {"/", "/login"})
    public String index() {
        return "login";
    }

}
