package com.showtime.sign.controller.admin;


import com.showtime.sign.constant.LoginTicketFieldConstant;
import com.showtime.sign.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@Slf4j
@RequestMapping(value = "/admin")
public class AdminLoginController {

    @Autowired
    private AdminService adminService;

    @PostMapping(value = {"/login"})
    public String login(@Param("username") String username, @Param("password") String password,
                        Model model, HttpServletResponse response) {
        log.info("username: {}, password: {}", username, password);
        Map<String, Object> map = adminService.login(username, password);
        if(map.get("ticket") != null){
            Cookie cookie = new Cookie(LoginTicketFieldConstant.TICKET, map.get("ticket").toString());
            cookie.setPath("/");
            response.addCookie(cookie);
            return "redirect:index";
        }
        model.addAllAttributes(map);
        return "admin/login";
    }

    @GetMapping(value = {"/login"})
    public String index() {
        return "admin/login";
    }

}
