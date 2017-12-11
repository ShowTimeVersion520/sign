package com.showtime.sign.service;


import com.showtime.sign.constant.LoginTicketFieldConstant;
import com.showtime.sign.mapper.AdminMapper;
import com.showtime.sign.mapper.LoginTicketMapper;
import com.showtime.sign.model.entity.Admin;
import com.showtime.sign.model.entity.LoginTicket;
import com.showtime.sign.utils.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;



@Service
@Slf4j
public class AdminService {
    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    public Map<String, Object> register(String username, String password) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isBlank(username)) {
            map.put("msgname", "用户名不能为空");
            return map;
        }

        if (StringUtils.isBlank(password)) {
            map.put("msgpwd", "密码不能为空");
            return map;
        }

        Admin b1 = new Admin();
        b1.setAccount(username);
        Admin admin = adminMapper.selectOne(b1);

        if (admin != null) {
            map.put("msgname", "用户名已经被注册");
            return map;
        }

        // 密码强度
        admin = new Admin();
        admin.setAccount(username);
        admin.setSalt(UUID.randomUUID().toString().substring(0, 5));
        admin.setPassword(SignUtil.MD5(password+admin.getSalt()));
        adminMapper.insert(admin);
        // 登陆
        String ticket = addLoginTicket();
        map.put("ticket", ticket);
        return map;
    }


    public Map<String, Object> login(String username, String password) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isBlank(username)) {
            map.put("msgname", "用户名不能为空");
            return map;
        }

        if (StringUtils.isBlank(password)) {
            map.put("msgpwd", "密码不能为空");
            return map;
        }

        Admin b1 = new Admin();
        b1.setAccount(username);
        Admin admin = adminMapper.selectOne(b1);

        if (admin == null) {
            /** 如果博主第一次登录，则为其注册 */
            if(adminMapper.selectCount(new Admin()) == 0){
                return this.register(username, password);
            }
            map.put("msgname", "用户名不存在");
            return map;
        }

        if (!SignUtil.MD5(password+admin.getSalt()).equals(admin.getPassword())) {
            map.put("msgpwd", "密码不正确");
            return map;
        }

        map.put("userId", admin.getId());

        String ticket = addLoginTicket();
        map.put("ticket", ticket);
        return map;
    }

    private String addLoginTicket() {
        LoginTicket ticket = new LoginTicket();
        Date date = new Date();
        date.setTime(date.getTime() + 1000*3600*24);
        //超时时间
        ticket.setExpired(date);
        ticket.setStatus((byte) 1);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        loginTicketMapper.insert(ticket);
        return ticket.getTicket();
    }

    public Admin getAdmin() {
        return adminMapper.selectByPrimaryKey(1L);
    }

    public void logout(String ticket) {
        Example example = new Example(LoginTicket.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo(LoginTicketFieldConstant.TICKET, ticket);

        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setStatus((byte)0);
        loginTicketMapper.updateByExample(loginTicket, example);
    }
}
