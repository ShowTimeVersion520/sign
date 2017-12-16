package com.showtime.sign.service;

import com.showtime.sign.constant.CourseFieldConstant;
import com.showtime.sign.constant.CourseSignStateConstant;
import com.showtime.sign.constant.SignDetailFieldConstant;
import com.showtime.sign.constant.SignDetailStateConstant;
import com.showtime.sign.enums.SignStateEnum;
import com.showtime.sign.mapper.CoursesMapper;
import com.showtime.sign.mapper.SignDetilMapper;
import com.showtime.sign.model.base.HostHolder;
import com.showtime.sign.model.base.ViewObject;
import com.showtime.sign.model.entity.Courses;
import com.showtime.sign.model.entity.SignDetil;
import com.showtime.sign.model.entity.Students;
import com.showtime.sign.utils.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private StudentService studentService;

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
                if(SignDetailStateConstant.FINISH_SIGN.equals(signDetils.get(0).getSignState())){
                    return "你已经签到";
                }else if(CourseSignStateConstant.NOT_SIGN.equals(signDetils.get(0).getSignState())){
                    SignDetil s1 = signDetils.get(0);
                    s1.setSignState(SignDetailStateConstant.AFTER_SIGN);
                    signDetilMapper.updateByPrimaryKey(s1);
                    return "补签成功";
                }
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

    public List<SignDetil> getByStudentId(Long id) {
        Example example = new Example(SignDetil.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(SignDetailFieldConstant.STUDENT_ID, id);

        return signDetilMapper.selectByExample(example);
    }

    public void insertBatch(List<SignDetil> signDetils) {
        signDetilMapper.insertBatch(signDetils);
    }

    public String getDetailExcelByCourseId(Long courseId) {
        List<ViewObject> vos = new ArrayList<>();
        Courses course = coursesMapper.selectByPrimaryKey(courseId);
        List<Students> students = studentService.getStudentsByClassName(course.getClasses());
        List<SignDetil> signDetils = getByCourseId(courseId);

        SignUtil.JoinStudentAndSignDetail(vos, students, signDetils);

        HSSFWorkbook wb = new HSSFWorkbook();//创建Excel工作簿对象
        HSSFSheet sheet = wb.createSheet("detail");//创建Excel工作表对象

        //写入标题
        HSSFRow row0 = sheet.createRow((short)0);
        row0.createCell((short)0).setCellValue("签到学生");
        row0.createCell((short)1).setCellValue("班级名称");
        row0.createCell((short)2).setCellValue("学生姓名");
        row0.createCell((short)3).setCellValue("签到的课程");
        row0.createCell((short)4).setCellValue("签到时间");
        row0.createCell((short)5).setCellValue("签到状态");
        for(int i=0;i<vos.size();++i){
            HSSFRow row = sheet.createRow((short)i+1); //创建Excel工作表的行
            Students student = (Students) vos.get(i).get("student");
            SignDetil signDetil = (SignDetil) vos.get(i).get("signDetail");

            for(int j=0;j<6;++j){
                switch (j){
                    case 0: //签到学生
                        row.createCell((short)j).setCellValue(student.getAccount());
                        break;
                    case 1: //班级名称
                        row.createCell((short)j).setCellValue(student.getClassName());
                        break;
                    case 2: //学生姓名
                        row.createCell((short)j).setCellValue(student.getName());
                        break;
                    case 3: //签到的课程
                        row.createCell((short)j).setCellValue(course.getName());
                        break;
                    case 4: //签到时间
                        row.createCell((short)j).setCellValue(
                                new SimpleDateFormat("yyyy-MM-dd").format(signDetil.getSignTime()));
                        break;
                    case 5: //签到状态
                        String state = ((SignStateEnum)SignUtil.getEnumByCode(signDetil.getSignState(), SignStateEnum.class)).getMsg();
                        row.createCell((short)j).setCellValue(state);
                        break;
                }
            }
        }
        String fileName = SignUtil.EXCEL_DETAIL_DIR + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "-" + courseId.toString() + ".xls";
        File file = new File(fileName);

        try {
            FileOutputStream stream = new FileOutputStream(file);
            wb.write(stream);
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName;
    }



}
