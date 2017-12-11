package com.showtime.sign.model.base;

import com.showtime.sign.model.entity.Admin;
import com.showtime.sign.model.entity.Students;
import com.showtime.sign.model.entity.Teachers;
import org.springframework.stereotype.Component;

@Component
public class HostHolder {
    private static ThreadLocal<Admin> admins = new ThreadLocal<>();

    private static ThreadLocal<Students> students = new ThreadLocal<>();

    private static ThreadLocal<Teachers> teachers = new ThreadLocal<>();

    public Admin getAdmin() {
        return admins.get();
    }

    public void setAdmin(Admin admin) {
        admins.set(admin);
    }

    public Students getStudent() {
        return students.get();
    }

    public void setStudent(Students student) {
        students.set(student);
    }

    public Teachers getTeacher() {
        return teachers.get();
    }

    public void setTeacher(Teachers teacher) {
        teachers.set(teacher);
    }

    public void clear() {
        admins.remove();
        students.remove();
        teachers.remove();
    }
}
