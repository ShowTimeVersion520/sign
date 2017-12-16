package com.showtime.sign.service;

import com.showtime.sign.constant.*;
import com.showtime.sign.enums.ResultEnum;
import com.showtime.sign.exception.SignException;
import com.showtime.sign.mapper.CoursesMapper;
import com.showtime.sign.model.entity.Courses;
import com.showtime.sign.model.entity.SignDetil;
import com.showtime.sign.model.entity.Students;
import com.showtime.sign.utils.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class CourseService {

    @Autowired
    private CoursesMapper coursesMapper;

    @Autowired
    private SignService signService;

    @Autowired
    private StudentService studentService;

    public void InsertCourseByExcel(MultipartFile file) throws Exception {
        if (SignUtil.checkExcelExt(file)) throw new SignException(ResultEnum.ERROR_EXCEL_EXT);


        POIFSFileSystem fs=new POIFSFileSystem((FileInputStream)file.getInputStream());
        //得到Excel工作簿对象
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        //得到Excel工作表对象
        HSSFSheet sheet = wb.getSheetAt(0);

        int nowTeacher = 0; //用于记录现在是哪个老师
        List<Courses> courses = new ArrayList<>();  //存储course

        //得到Excel工作表的行
        for(int i=1; i<sheet.getLastRowNum(); ++i){
            HSSFRow row = sheet.getRow(i);

            Courses course = new Courses();

            //查看授课教师的人数
            String teachers = row.getCell(6).getStringCellValue();
            String[] teacher = teachers.split(",");

            for(int j=0; j<=13; ++j){
                switch (j){
                    case 0 :    //学年学期
                        course.setSemester(row.getCell(j).getStringCellValue());
                        break;
                    case 1 :    //开课编号
                        course.setNumber(Integer.parseInt(row.getCell(j).getStringCellValue()));
                        break;
                    case 2 :    //课程名称
                        course.setName(row.getCell(j).getStringCellValue());
                        break;
                    case 3 :    //教学班名称
                        course.setClasses(row.getCell(j).getStringCellValue());
                        break;
                    case 6 :    //授课教师
                        course.setTeacher(teacher[nowTeacher]);
                        break;
                    case 8 :    //节次
                        course.setJieci(row.getCell(j).getStringCellValue());
                        break;
                    case 13 :   //日期
                        course.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(row.getCell(j).getStringCellValue()));
                        break;
                }
            }

            //签到默认状态为没签到
            course.setSignState(CourseSignStateConstant.NOT_SIGN);

            courses.add(course);

            if(nowTeacher < teacher.length - 1){
                --i;    //再打印一次这一行
                ++nowTeacher;
            }else {
                nowTeacher = 0;
            }

            log.info("course : {}, i : {}, nowTeacher : {}", course.toString(), i, nowTeacher);
        }

        InsertBatch(courses);
    }

    private void InsertBatch(List<Courses> courses) {
        //验证数据库中是否已经被注册,把已经注册的删除
        for(int i=0; i<courses.size(); ++i){
            Example example = new Example(Courses.class);
            Example.Criteria criteria = example.createCriteria();

            criteria.andEqualTo(CourseFieldConstant.NUMBER, courses.get(i).getNumber());
            criteria.andEqualTo(CourseFieldConstant.JIECI, courses.get(i).getJieci());
            criteria.andEqualTo(CourseFieldConstant.DATE,
                    new SimpleDateFormat("yyyy-MM-dd").format(courses.get(i).getDate()));

            List<Courses> fromDataBase = coursesMapper.selectByExample(example);

            if(fromDataBase.size() != 0){
                courses.remove(i);
                --i;
                continue;
            }
        }

        if(courses.size() != 0) {
            coursesMapper.InsertBatch(courses);
        }
    }


    public List<Courses> getCourseByTeacherNameAndDate(String name, String date) {
        Example example = new Example(Courses.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo(CourseFieldConstant.TEACHER, name);
        criteria.andEqualTo(CourseFieldConstant.DATE, date);
        return coursesMapper.selectByExample(example);
    }

    public void startSign(Long courseId) {
        Courses course = coursesMapper.selectByPrimaryKey(courseId);
        course.setSignState(CourseSignStateConstant.ING_SIGN);
        coursesMapper.updateByPrimaryKey(course);
    }

    @Transactional
    public void resetSign(Long courseId) {
        Courses course = coursesMapper.selectByPrimaryKey(courseId);
        course.setSignState(CourseSignStateConstant.NOT_SIGN);
        coursesMapper.updateByPrimaryKey(course);

        //删除同学们的签到状态
        signService.deleteSignDetailByCourseId(courseId);
    }


    public void afterSign(Long courseId) {
        Courses course = coursesMapper.selectByPrimaryKey(courseId);
        course.setSignState(CourseSignStateConstant.AFTER_SIGN);
        coursesMapper.updateByPrimaryKey(course);
    }

    @Transactional
    public void endSign(Long courseId) {
        log.info("=======================into endSign==========================");
        Courses course = coursesMapper.selectByPrimaryKey(courseId);
        course.setSignState(CourseSignStateConstant.FINISH_SIGN);
        coursesMapper.updateByPrimaryKey(course);

        //添加在结束时insert未签到的人的信息入SignDetail
        List<Students> students = studentService.getStudentsByClassName(course.getClasses());
        List<SignDetil> signDetils = signService.getByCourseId(courseId);
        List<SignDetil> insertContent = new ArrayList<>();
        //对已经有记录的同学进行剔除
        for(int i=0;i<students.size();++i){
            boolean falg = false;   //用于识别是否已经break过
            for(SignDetil signDetil:signDetils){
                if(students.get(i).getId().equals(signDetil.getStudentId())){
                    log.info("student: {}", students.get(i).getName());
                    students.remove(i);
                    --i;
                    falg = true;
                    break;
                }
            }

            if(falg){
                log.info("flag is true");
               continue;
            }
            Students student = students.get(i);
            SignDetil signDetil = new SignDetil();
            signDetil.setCourseId(courseId);
            signDetil.setSignState(SignDetailStateConstant.NOT_SIGN);
            signDetil.setStudentName(student.getName());
            signDetil.setStudentId(student.getId());
            signDetil.setSignTime(new Date());
            signDetil.setClassName(student.getClassName());
            log.info("add signDetil name is {}", student.getName());
            insertContent.add(signDetil);
        }

        if(insertContent.size() != 0){
            signService.insertBatch(insertContent);
        }
    }

    public List<Courses> getCourseBySignDetils(List<SignDetil> signDetils) {
        return coursesMapper.getCourseBySignDetils(signDetils);
    }

}
