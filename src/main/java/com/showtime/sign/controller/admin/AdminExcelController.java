package com.showtime.sign.controller.admin;

import com.showtime.sign.service.CourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@Slf4j
@RequestMapping(value = "/admin")
public class AdminExcelController {
    @Autowired
    private CourseService courseService;

    @ResponseBody
    @PostMapping(value = {"/upload/course"})
    public String uploadCourse(@RequestParam("file") MultipartFile file) throws Exception {
        courseService.InsertCourseByExcel(file);
        return "上传成功";
    }
}
