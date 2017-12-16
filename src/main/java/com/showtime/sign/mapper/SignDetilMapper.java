package com.showtime.sign.mapper;

import com.showtime.sign.model.entity.SignDetil;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SignDetilMapper extends Mapper<SignDetil> {
    void insertBatch(List<SignDetil> signDetils);
}