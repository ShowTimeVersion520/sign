package com.showtime.sign.mapper;

import com.showtime.sign.model.entity.Teachers;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface TeachersMapper extends Mapper<Teachers> {
    void InsertBatch(List<Teachers> teachers);
}