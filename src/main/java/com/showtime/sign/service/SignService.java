package com.showtime.sign.service;

import com.showtime.sign.constant.CourseFieldConstant;
import com.showtime.sign.constant.CourseSignStateConstant;
import com.showtime.sign.constant.SignDetailFieldConstant;
import com.showtime.sign.constant.SignDetailStateConstant;
import com.showtime.sign.mapper.CoursesMapper;
import com.showtime.sign.mapper.SignDetilMapper;
import com.showtime.sign.model.base.HostHolder;
import com.showtime.sign.model.entity.Courses;
import com.showtime.sign.model.entity.SignDetil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class SignService {

    @Autowired
    private SignDetilMapper signDetilMapper;

    @Autowired
    private CoursesMapper coursesMapper;

    @Autowired
    private HostHolder hostHolder;

    public String studentSign(Long courseId, String studentAccount) {
        Courses course = coursesMapper.selectByPrimaryKey(courseId);
        SignDetil signDetil = new SignDetil();

        if(course == null){
            return "这节课不存在";
        }
        if(course.getSignState() >= 2){
            //判断该学生是否属于该课程所对应的班级
            if(!course.getClasses().contains(hostHolder.getStudent().getClassName())){
                return "你不需要上这节课，签到失败";
            }

            //判断该学生是否已经签到
            Example example = new Example(SignDetil.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo(SignDetailFieldConstant.STUDENT_ID, hostHolder.getStudent().getId());
            criteria.andEqualTo(SignDetailFieldConstant.COURSE_ID, courseId);
            List<SignDetil> signDetils = signDetilMapper.selectByExample(example);

            if(signDetils.size() != 0){
                return "你已经签到";
            }

            signDetil.setClassName(hostHolder.getStudent().getClassName());
            signDetil.setCourseId(courseId);
            signDetil.setSignTime(new Date());
            signDetil.setStudentId(hostHolder.getStudent().getId());
            signDetil.setStudentName(hostHolder.getStudent().getName());
        }

        if(CourseSignStateConstant.NOT_SIGN.equals(course.getSignState())){
            return "这节课还没开始签到";
        }else if(CourseSignStateConstant.FINISH_SIGN.equals(course.getSignState())){
            return "签到已经结束";
        }else if(CourseSignStateConstant.ING_SIGN.equals(course.getSignState())){
            signDetil.setSignState(SignDetailStateConstant.FINISH_SIGN);
            signDetilMapper.insert(signDetil);
            return "签到成功";
        }else if(CourseSignStateConstant.AFTER_SIGN.equals(course.getSignState())){
            signDetil.setSignState(SignDetailStateConstant.AFTER_SIGN);
            signDetilMapper.insert(signDetil);
            return "补签成功";
        }

        return "签到失败";
    }

    public List<SignDetil> getByCourseId(Long courseId) {
        Example example = new Example(SignDetil.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(SignDetailFieldConstant.COURSE_ID, courseId);

        return signDetilMapper.selectByExample(example);
    }

    public void deleteSignDetailByCourseId(Long courseId) {
        Example example = new Example(SignDetil.class);
        Example.Criteria criteria = example.createCriteria();

        signDetilMapper.deleteByExample(example);
    }
}
