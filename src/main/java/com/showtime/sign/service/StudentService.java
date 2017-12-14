package com.showtime.sign.service;

import com.showtime.sign.constant.*;
import com.showtime.sign.enums.ResultEnum;
import com.showtime.sign.exception.SignException;
import com.showtime.sign.mapper.AdminMapper;
import com.showtime.sign.mapper.LoginTicketMapper;
import com.showtime.sign.mapper.StudentsMapper;
import com.showtime.sign.model.base.HostHolder;
import com.showtime.sign.model.entity.*;
import com.showtime.sign.utils.SignUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class StudentService {
    @Autowired
    private StudentsMapper studentsMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private HostHolder hostHolder;

    public Map<String, Object> register(String username, String password, String name, String className) {
        Map<String, Object> map = SignUtil.checkUsernameAndPassword(username,password);

        if(map.size() != 0){
            return map;
        }

        Students s1 = new Students();
        s1.setAccount(username);
        Students student = studentsMapper.selectOne(s1);

        if (student != null) {
            map.put("msgname", "用户名已经被注册");
            return map;
        }

        // 密码强度
        student = new Students();
        student.setAccount(username);
        student.setName(name);
        student.setClassName(className);
        student.setSalt(UUID.randomUUID().toString().substring(0, 5));
        student.setPassword(SignUtil.MD5(password+student.getSalt()));
        studentsMapper.insert(student);
        // 登陆
        String ticket = addLoginTicket(student.getId());
        map.put("ticket", ticket);
        return map;
    }


    public Map<String, Object> login(String username, String password) {

        Map<String, Object> map = SignUtil.checkUsernameAndPassword(username,password);

        if(map.size() != 0){
            return map;
        }

        Students s1 = new Students();
        s1.setAccount(username);
        Students student = studentsMapper.selectOne(s1);

        if (student == null) {
            map.put("msgname", "用户名不存在");
            return map;
        }

        if (!SignUtil.MD5(password+student.getSalt()).equals(student.getPassword())) {
            map.put("msgpwd", "密码不正确");
            return map;
        }

        map.put("userId", student.getId());

        String ticket = addLoginTicket(student.getId());
        map.put("ticket", ticket);
        return map;
    }

    private String addLoginTicket(Long accountId) {
        LoginTicket ticket = SignUtil.InitLoginTicket(accountId);
        ticket.setRole(TicketRoleConstant.STUDENT);
        loginTicketMapper.insert(ticket);
        return ticket.getTicket();
    }



    public Students getStudents(Long id) {
        return studentsMapper.selectByPrimaryKey(id);
    }

    public void logout(String ticket) {
        Example example = SignUtil.logout(ticket);

        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setStatus((byte)0);
        loginTicketMapper.updateByExample(loginTicket, example);
    }

    public List<Students> getStudentsByClassName(String classes) {
        String[] classNames = classes.split(",");
        List<Students> students = new ArrayList<>();
        for(String className:classNames){
            Example example = new Example(Students.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo(StudentFieldConstant.CLASS_NAME, className);

            List<Students> students1 = studentsMapper.selectByExample(example);
            students.addAll(students1);
        }

        return students;
    }

    public void updatePassword(String password) {
        if (StringUtils.isBlank(password)) {
            throw new SignException(ResultEnum.EMPTY_PASSWORD);
        }
        Students student = hostHolder.getStudent();
        student.setPassword(SignUtil.MD5(password+student.getSalt()));
        studentsMapper.updateByPrimaryKey(student);
    }

    public void InsertStudentByExcel(MultipartFile file) throws Exception {
        if (SignUtil.checkExcelExt(file)) throw new SignException(ResultEnum.ERROR_EXCEL_EXT);


        POIFSFileSystem fs=new POIFSFileSystem((FileInputStream)file.getInputStream());
        //得到Excel工作簿对象
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        //得到Excel工作表对象
        HSSFSheet sheet = wb.getSheetAt(0);

        List<Students> students = new ArrayList<>();  //存储student

        //得到Excel工作表的行
        for(int i=1; i<sheet.getLastRowNum(); ++i){
            HSSFRow row = sheet.getRow(i);

            Students student = new Students();

            for(int j=0; j<=2; ++j){
                switch (j){
                    case 0 :    //学号
                        student.setAccount(row.getCell(j).getStringCellValue());
                        break;
                    case 1 :    //姓名
                        student.setName(row.getCell(j).getStringCellValue());
                        break;
                    case 2 :    //班级
                        student.setClassName(row.getCell(j).getStringCellValue());
                        break;
                }
            }

            students.add(student);
        }

        InsertBatch(students);
    }

    private void InsertBatch(List<Students> students) {
        //验证数据库中是否已经被注册,把已经注册的删除
        for(int i=0; i<students.size(); ++i){
            Example example = new Example(Students.class);
            Example.Criteria criteria = example.createCriteria();

            criteria.andEqualTo(StudentFieldConstant.ACCOUNT, students.get(i).getAccount());
            List<Students> fromDataBase = studentsMapper.selectByExample(example);

            if(fromDataBase.size() != 0){
                students.remove(i);
                --i;
                continue;
            }

            //通过验证的给密码加密
            students.get(i).setSalt(UUID.randomUUID().toString().substring(0, 5));
            students.get(i).setPassword(SignUtil.MD5(AccountPasswordConstant.PASSWORD + students.get(i).getSalt()));
        }

        if(students.size() != 0){
            studentsMapper.InsertBatch(students);
        }
    }
}
