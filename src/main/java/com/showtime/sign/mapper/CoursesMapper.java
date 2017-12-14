package com.showtime.sign.mapper;

import com.showtime.sign.model.entity.Courses;
import com.showtime.sign.model.entity.SignDetil;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CoursesMapper extends Mapper<Courses> {
    void InsertBatch(List<Courses> courses);

    List<Courses> getCourseBySignDetils(List<SignDetil> signDetils);
}