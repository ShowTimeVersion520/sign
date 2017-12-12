package com.showtime.sign.service;

import com.showtime.sign.constant.LoginTicketFieldConstant;
import com.showtime.sign.constant.TicketRoleConstant;
import com.showtime.sign.mapper.AdminMapper;
import com.showtime.sign.mapper.LoginTicketMapper;
import com.showtime.sign.mapper.StudentsMapper;
import com.showtime.sign.model.entity.Admin;
import com.showtime.sign.model.entity.LoginTicket;
import com.showtime.sign.model.entity.Students;
import com.showtime.sign.utils.SignUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class StudentService {
    @Autowired
    private StudentsMapper studentsMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    public Map<String, Object> register(String username, String password) {
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
}
