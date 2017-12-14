package com.showtime.sign.service;


import com.showtime.sign.constant.LoginTicketFieldConstant;
import com.showtime.sign.constant.TicketRoleConstant;
import com.showtime.sign.enums.ResultEnum;
import com.showtime.sign.exception.SignException;
import com.showtime.sign.mapper.AdminMapper;
import com.showtime.sign.mapper.LoginTicketMapper;
import com.showtime.sign.model.base.HostHolder;
import com.showtime.sign.model.entity.Admin;
import com.showtime.sign.model.entity.LoginTicket;
import com.showtime.sign.model.entity.Students;
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

    @Autowired
    private HostHolder hostHolder;


    public Map<String, Object> register(String username, String password) {
        Map<String, Object> map = SignUtil.checkUsernameAndPassword(username,password);

        if(map.size() != 0){
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
        String ticket = addLoginTicket(admin.getId());
        map.put("ticket", ticket);
        return map;
    }


    public Map<String, Object> login(String username, String password) {
        Map<String, Object> map = SignUtil.checkUsernameAndPassword(username,password);

        if(map.size() != 0){
            return map;
        }

        Admin b1 = new Admin();
        b1.setAccount(username);
        Admin admin = adminMapper.selectOne(b1);

        if (admin == null) {
            /** 如果管理员第一次登录，则为其注册 */
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

        String ticket = addLoginTicket(admin.getId());
        map.put("ticket", ticket);
        return map;
    }

    private String addLoginTicket(Long accountId) {
        LoginTicket ticket = SignUtil.InitLoginTicket(accountId);
        ticket.setRole(TicketRoleConstant.ADMIN);
        loginTicketMapper.insert(ticket);
        return ticket.getTicket();
    }

    public Admin getAdmin() {
        return adminMapper.selectByPrimaryKey(1L);
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
        Admin admin = hostHolder.getAdmin();
        admin.setPassword(SignUtil.MD5(password+admin.getSalt()));
        adminMapper.updateByPrimaryKey(admin);
    }
}
