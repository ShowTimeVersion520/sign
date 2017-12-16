package com.showtime.sign.controller.teacher;

import com.showtime.sign.model.entity.SignDetil;
import com.showtime.sign.service.CourseService;
import com.showtime.sign.service.SignService;
import com.showtime.sign.service.StudentService;
import com.showtime.sign.service.TeacherService;
import com.showtime.sign.utils.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

@Controller
@Slf4j
@RequestMapping(value = "/teacher")
public class TeacherExcelController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private SignService signService;

    @RequestMapping(path = {"/detail/download"}, method = {RequestMethod.GET})
    @ResponseBody
    public void detailDownload(@Param("courseId") Long courseId,
            HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.ms-excel");
            String fileName = signService.getDetailExcelByCourseId(courseId);
            log.info("----------------------->fileName");
            StreamUtils.copy(new FileInputStream(new
                    File(fileName)), response.getOutputStream());
        } catch (Exception e) {
            log.error("读取文件错误" + e.getMessage());
        }
    }



}
