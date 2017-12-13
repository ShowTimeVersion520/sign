package com.showtime.sign.model.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "sign_detil")
public class SignDetil {
    /**
     * 表主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 签到学生
     */
    @Column(name = "student_id")
    private Long studentId;

    /**
     * 班级名称
     */
    @Column(name = "class_name")
    private String className;

    /**
     * 学生姓名
     */
    @Column(name = "student_name")
    private String studentName;

    /**
     * 签到的课程
     */
    @Column(name = "course_id")
    private Long courseId;

    /**
     * 签到时间
     */
    @Column(name = "sign_time")
    private Date signTime;

    /**
     * 签到状态 0:未签到 1:签到成功 2：补签
     */
    @Column(name = "sign_state")
    private Byte signState;

    /**
     * 获取表主键
     *
     * @return id - 表主键
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置表主键
     *
     * @param id 表主键
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取签到学生
     *
     * @return student_id - 签到学生
     */
    public Long getStudentId() {
        return studentId;
    }

    /**
     * 设置签到学生
     *
     * @param studentId 签到学生
     */
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    /**
     * 获取班级名称
     *
     * @return class_name - 班级名称
     */
    public String getClassName() {
        return className;
    }

    /**
     * 设置班级名称
     *
     * @param className 班级名称
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * 获取学生姓名
     *
     * @return student_name - 学生姓名
     */
    public String getStudentName() {
        return studentName;
    }

    /**
     * 设置学生姓名
     *
     * @param studentName 学生姓名
     */
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    /**
     * 获取签到的课程
     *
     * @return course_id - 签到的课程
     */
    public Long getCourseId() {
        return courseId;
    }

    /**
     * 设置签到的课程
     *
     * @param courseId 签到的课程
     */
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    /**
     * 获取签到时间
     *
     * @return sign_time - 签到时间
     */
    public Date getSignTime() {
        return signTime;
    }

    /**
     * 设置签到时间
     *
     * @param signTime 签到时间
     */
    public void setSignTime(Date signTime) {
        this.signTime = signTime;
    }

    /**
     * 获取签到状态 0:未签到 1:签到成功 2：补签
     *
     * @return sign_state - 签到状态 0:未签到 1:签到成功 2：补签
     */
    public Byte getSignState() {
        return signState;
    }

    /**
     * 设置签到状态 0:未签到 1:签到成功 2：补签
     *
     * @param signState 签到状态 0:未签到 1:签到成功 2：补签
     */
    public void setSignState(Byte signState) {
        this.signState = signState;
    }
}