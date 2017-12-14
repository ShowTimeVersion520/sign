package com.showtime.sign.mapper;

import com.showtime.sign.model.entity.Students;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface StudentsMapper extends Mapper<Students> {
    void InsertBatch(List<Students> students);
}