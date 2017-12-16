package com.showtime.sign.controller.teacher;

import com.showtime.sign.constant.SignDetailStateConstant;
import com.showtime.sign.mapper.CoursesMapper;
import com.showtime.sign.model.base.HostHolder;
import com.showtime.sign.model.base.ViewObject;
import com.showtime.sign.model.entity.Courses;
import com.showtime.sign.model.entity.SignDetil;
import com.showtime.sign.model.entity.Students;
import com.showtime.sign.service.CourseService;
import com.showtime.sign.service.SignService;
import com.showtime.sign.service.StudentService;
import com.showtime.sign.utils.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.swing.text.html.ObjectView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@Slf4j
@RequestMapping(value = "/teacher/sign")
public class TeacherSignController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentService studentService;
    @Autowired
    private SignService signService;

    @Autowired
    private CoursesMapper coursesMapper;

    @Autowired
    private HostHolder hostHolder;

    @GetMapping(value = {"/list"})
    public String signList(Model model){

        List<Courses> courses = courseService.getCourseByTeacherNameAndDate(hostHolder.getTeacher().getAccount(),
                new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        model.addAttribute("courses", courses);
        return "teacher/signList";
    }

    @GetMapping(value = {"/detail"})
    public String signDetail(@Param("id") Long id, Model model){
        List<ViewObject> vos = new ArrayList<>();
        Courses course = coursesMapper.selectByPrimaryKey(id);
        List<Students> students = studentService.getStudentsByClassName(course.getClasses());
        List<SignDetil> signDetils = signService.getByCourseId(id);
        SignUtil.JoinStudentAndSignDetail(vos, students, signDetils);
        model.addAttribute("vos", vos);
        model.addAttribute("course", course);
        return "teacher/signDetail";
    }

    @GetMapping(value = {"/start"})
    public String startSign(@Param("courseId") Long courseId){
        log.info("courseId : {}", courseId);
        courseService.startSign(courseId);
        return"redirect:detail?id=" + courseId;
    }

    @GetMapping(value = {"/reset"})
    public String resetSign(@Param("courseId") Long courseId){
        log.info("courseId : {}", courseId);
        courseService.resetSign(courseId);
        return "redirect:detail?id=" + courseId;
    }

    @GetMapping(value = {"/aftersign"})
    public String afterSign(@Param("courseId") Long courseId){
        log.info("courseId : {}", courseId);
        courseService.afterSign(courseId);
        return "redirect:detail?id=" + courseId;
    }

    @GetMapping(value = {"/end"})
    public String endSign(@Param("courseId") Long courseId){
        log.info("courseId : {}", courseId);
        courseService.endSign(courseId);

        return "redirect:detail?id=" + courseId;
    }
}
