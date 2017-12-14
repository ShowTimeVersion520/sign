package com.showtime.sign.controller.student;

import com.showtime.sign.model.base.HostHolder;
import com.showtime.sign.model.base.ViewObject;
import com.showtime.sign.model.entity.Courses;
import com.showtime.sign.model.entity.SignDetil;
import com.showtime.sign.service.AdminService;
import com.showtime.sign.service.CourseService;
import com.showtime.sign.service.SignService;
import com.showtime.sign.service.StudentService;
import javafx.geometry.HorizontalDirection;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.swing.text.View;
import java.util.ArrayList;
import java.util.List;

/**
 * 学生可以签到、查看历史签到记录
 */
@Controller
@Slf4j
@RequestMapping(value = "/student")
public class StudentIndexController {


    @Autowired
    private StudentService studentService;

    @Autowired
    private SignService signService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private HostHolder hostHolder;

    @GetMapping(value = {"/index"})
    public String index(){
        return "student/index";
    }

    @GetMapping(value = {"/change/password"})
    public String IntoChangePassword(){
        return "student/changePassword";
    }

    @ResponseBody
    @PostMapping(value = {"/change/password"})
    public String updatePassword(@Param("password") String password){
        studentService.updatePassword(password);
        return "密码修改成功";
    }

    @GetMapping(value = {"/history"})
    public String history(Model model){
        List<ViewObject> vos = new ArrayList<>();
        List<SignDetil> signDetils = signService.getByStudentId(hostHolder.getStudent().getId());

        //如果没有记录，则跳过以下两步骤
        if(signDetils.size() == 0){
            model.addAttribute("msg", "无签到记录");
            model.addAttribute("vos", vos);
            return "student/history";
        }

        List<Courses> courses = courseService.getCourseBySignDetils(signDetils);

        for (SignDetil signDetil:signDetils){
            for (Courses course:courses){
                if(signDetil.getCourseId().equals(course.getId())){
                    ViewObject vo = new ViewObject();
                    vo.set("signDetail", signDetil);
                    vo.set("course", course);

                    vos.add(vo);
                }
            }
        }
        model.addAttribute("vos", vos);
        return "student/history";
    }

}
