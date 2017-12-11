package com.showtime.sign.interceptor;



import com.showtime.sign.constant.AttributeNameConstant;
import com.showtime.sign.constant.RoleConstant;
import com.showtime.sign.enums.ResultEnum;
import com.showtime.sign.exception.SignException;
import com.showtime.sign.mapper.AdminMapper;
import com.showtime.sign.mapper.LoginTicketMapper;
import com.showtime.sign.mapper.StudentsMapper;
import com.showtime.sign.mapper.TeachersMapper;
import com.showtime.sign.model.base.HostHolder;
import com.showtime.sign.model.entity.Admin;
import com.showtime.sign.model.entity.LoginTicket;
import com.showtime.sign.model.entity.Students;
import com.showtime.sign.model.entity.Teachers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by nowcoder on 2016/7/3.
 */
@Component
public class PassportInterceptor implements HandlerInterceptor {

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private StudentsMapper studentsMapper;

    @Autowired
    private TeachersMapper teachersMapper;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String ticket = null;
        if (httpServletRequest.getCookies() != null) {
            for (Cookie cookie : httpServletRequest.getCookies()) {
                if (cookie.getName().equals("ticket")) {
                    ticket = cookie.getValue();
                    break;
                }
            }
        }

        if (ticket != null) {

            LoginTicket t1 = new LoginTicket();
            t1.setTicket(ticket);
            LoginTicket loginTicket = loginTicketMapper.selectOne(t1);

            if (loginTicket == null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus() == 0) {
                return true;
            }

            if(RoleConstant.ADMIN.equals(loginTicket.getRole())){
                Admin admin = adminMapper.selectByPrimaryKey(loginTicket.getAccountId());
                hostHolder.setAdmin(admin);
            }else if(RoleConstant.STUDENT.equals(loginTicket.getRole())){
                Students student = studentsMapper.selectByPrimaryKey(loginTicket.getAccountId());
                hostHolder.setStudent(student);
            }else if(RoleConstant.TEACHER.equals(loginTicket.getRole())){
                Teachers teacher = teachersMapper.selectByPrimaryKey(loginTicket.getAccountId());
                hostHolder.setTeacher(teacher);
            }else {
                throw new SignException(ResultEnum.UNKNOW_ROLE);
            }

        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            if(hostHolder.getAdmin() != null){
                modelAndView.addObject(AttributeNameConstant.ADMIN, hostHolder.getAdmin());
            }

            if(hostHolder.getStudent() != null){
                modelAndView.addObject(AttributeNameConstant.STUDENT, hostHolder.getStudent());
            }

            if(hostHolder.getTeacher() != null){
                modelAndView.addObject(AttributeNameConstant.TEACHER, hostHolder.getTeacher());
            }

        }
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        hostHolder.clear();
    }
}
