package com.showtime.sign.service;

import com.showtime.sign.constant.TicketRoleConstant;
import com.showtime.sign.model.entity.Courses;
import com.showtime.sign.model.entity.LoginTicket;
import com.showtime.sign.utils.SignUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    @Autowired
    private CourseService courseService;

    public String InsertCourse(List<Courses> courses) {
        return null;
    }
}
