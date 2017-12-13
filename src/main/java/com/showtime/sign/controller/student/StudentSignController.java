package com.showtime.sign.controller.student;

import com.showtime.sign.service.SignService;
import com.showtime.sign.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
@RequestMapping(value = "/student")
public class StudentSignController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private SignService signService;

    @ResponseBody
    @PostMapping(value = {"/sign"})
    public String sign(@Param("courseId") Long courseId,
                        @Param("studentAccount") String studentAccount){
        log.info("courseId : {}, studentAccount : {}", courseId, studentAccount);
        String result = signService.studentSign(courseId,studentAccount);
        return result;
    }
}
