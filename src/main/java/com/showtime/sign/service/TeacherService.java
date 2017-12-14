package com.showtime.sign.service;

import com.showtime.sign.constant.AccountPasswordConstant;
import com.showtime.sign.constant.StudentFieldConstant;
import com.showtime.sign.constant.TeacherFieldConstant;
import com.showtime.sign.constant.TicketRoleConstant;
import com.showtime.sign.enums.ResultEnum;
import com.showtime.sign.exception.SignException;
import com.showtime.sign.mapper.LoginTicketMapper;
import com.showtime.sign.mapper.StudentsMapper;
import com.showtime.sign.mapper.TeachersMapper;
import com.showtime.sign.model.base.HostHolder;
import com.showtime.sign.model.entity.LoginTicket;
import com.showtime.sign.model.entity.Students;
import com.showtime.sign.model.entity.Teachers;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class TeacherService {
    @Autowired
    private TeachersMapper teachersMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private HostHolder hostHolder;

    public Map<String, Object> register(String username, String password) {
        Map<String, Object> map = SignUtil.checkUsernameAndPassword(username,password);

        if(map.size() != 0){
            return map;
        }

        Teachers s1 = new Teachers();
        s1.setAccount(username);
        Teachers teacher = teachersMapper.selectOne(s1);

        if (teacher != null) {
            map.put("msgname", "用户名已经被注册");
            return map;
        }

        // 密码强度
        teacher = new Teachers();
        teacher.setAccount(username);
        teacher.setSalt(UUID.randomUUID().toString().substring(0, 5));
        teacher.setPassword(SignUtil.MD5(password+teacher.getSalt()));
        teachersMapper.insert(teacher);
        // 登陆
        String ticket = addLoginTicket(teacher.getId());
        map.put("ticket", ticket);
        return map;
    }


    public Map<String, Object> login(String username, String password) {

        Map<String, Object> map = SignUtil.checkUsernameAndPassword(username,password);

        if(map.size() != 0){
            return map;
        }

        Teachers b1 = new Teachers();
        b1.setAccount(username);
        Teachers teacher = teachersMapper.selectOne(b1);

        if (teacher == null) {
            map.put("msgname", "用户名不存在");
            return map;
        }

        if (!SignUtil.MD5(password+teacher.getSalt()).equals(teacher.getPassword())) {
            map.put("msgpwd", "密码不正确");
            return map;
        }

        map.put("userId", teacher.getId());

        String ticket = addLoginTicket(teacher.getId());
        map.put("ticket", ticket);
        return map;
    }

    private String addLoginTicket(Long accountId) {
        LoginTicket ticket = SignUtil.InitLoginTicket(accountId);
        ticket.setRole(TicketRoleConstant.TEACHER);
        loginTicketMapper.insert(ticket);
        return ticket.getTicket();
    }



    public Teachers getTeachers(Long id) {
        return teachersMapper.selectByPrimaryKey(id);
    }

    public void logout(String ticket) {
        Example example = SignUtil.logout(ticket);

        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setStatus((byte)0);
        loginTicketMapper.updateByExample(loginTicket, example);
    }

    public void updatePassword(String password) {
        if (StringUtils.isBlank(password)) {
            throw new SignException(ResultEnum.EMPTY_PASSWORD);
        }
        Teachers teacher = hostHolder.getTeacher();
        teacher.setPassword(SignUtil.MD5(password+teacher.getSalt()));
        teachersMapper.updateByPrimaryKey(teacher);
    }

    public void InsertTeacherByExcel(MultipartFile file) throws Exception {
        if (SignUtil.checkExcelExt(file)) throw new SignException(ResultEnum.ERROR_EXCEL_EXT);


        POIFSFileSystem fs=new POIFSFileSystem((FileInputStream)file.getInputStream());
        //得到Excel工作簿对象
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        //得到Excel工作表对象
        HSSFSheet sheet = wb.getSheetAt(0);

        List<Teachers> teachers = new ArrayList<>();  //存储student

        //得到Excel工作表的行
        for(int i=1; i<sheet.getLastRowNum(); ++i){
            HSSFRow row = sheet.getRow(i);

            Teachers teacher = new Teachers();

            for(int j=0; j<=0; ++j){
                switch (j){
                    case 0 :    //账号
                        teacher.setAccount(row.getCell(j).getStringCellValue());
                        break;
                }
            }

            teachers.add(teacher);
        }

        InsertBatch(teachers);
    }

    private void InsertBatch(List<Teachers> teachers) {
        //验证数据库中是否已经被注册,把已经注册的删除
        for(int i=0; i<teachers.size(); ++i){
            Example example = new Example(Teachers.class);
            Example.Criteria criteria = example.createCriteria();

            criteria.andEqualTo(TeacherFieldConstant.ACCOUNT, teachers.get(i).getAccount());
            List<Teachers> fromDataBase = teachersMapper.selectByExample(example);

            if(fromDataBase.size() != 0){
                teachers.remove(i);
                --i;
                continue;
            }

            //通过验证的给密码加密
            teachers.get(i).setSalt(UUID.randomUUID().toString().substring(0, 5));
            teachers.get(i).setPassword(SignUtil.MD5(AccountPasswordConstant.PASSWORD + teachers.get(i).getSalt()));
        }

        if(teachers.size() != 0) {
            teachersMapper.InsertBatch(teachers);
        }
    }
}
