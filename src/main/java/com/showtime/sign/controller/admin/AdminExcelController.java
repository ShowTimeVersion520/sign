package com.showtime.sign.controller.admin;

import com.showtime.sign.service.CourseService;
import com.showtime.sign.service.StudentService;
import com.showtime.sign.service.TeacherService;
import com.showtime.sign.utils.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Controller
@Slf4j
@RequestMapping(value = "/admin")
public class AdminExcelController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private TeacherService teacherService;

    @ResponseBody
    @PostMapping(value = {"/upload/course"})
    public String uploadCourse(@RequestParam("file") MultipartFile file) throws Exception {
        courseService.InsertCourseByExcel(file);
        return "上传成功";
    }

    @ResponseBody
    @PostMapping(value = {"/upload/student"})
    public String uploadStudent(@RequestParam("file") MultipartFile file) throws Exception {
        studentService.InsertStudentByExcel(file);
        return "上传成功";
    }

    @ResponseBody
    @PostMapping(value = {"/upload/teacher"})
    public String uploadTeacher(@RequestParam("file") MultipartFile file) throws Exception {
        teacherService.InsertTeacherByExcel(file);
        return "上传成功";
    }

    @RequestMapping(path = {"/download/{fileName}"}, method = {RequestMethod.GET})
    @ResponseBody
    public void getTeacherDemo(@PathVariable("fileName") String fileName,
            HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.ms-excel");
            StreamUtils.copy(new FileInputStream(new
                    File(SignUtil.EXCEL_DIR + fileName+"Demo.xls")), response.getOutputStream());
        } catch (Exception e) {
            log.error("读取图片错误" + fileName+"Demo" + e.getMessage());
        }
    }



}
