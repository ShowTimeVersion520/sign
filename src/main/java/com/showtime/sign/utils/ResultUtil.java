package com.showtime.sign.utils;


import com.showtime.sign.model.base.Result;

public class ResultUtil {

    public static Result success(Object object){
        return new Result(0, "成功",object);
    }

    public static Result error(Integer code, String msg){
        return new Result(code, msg, null);
    }
}
