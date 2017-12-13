package com.showtime.sign.model.entity;

import javax.persistence.*;

@Table(name = "sign_statistical")
public class SignStatistical {
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
    @Column(name = "course_number")
    private Long courseNumber;

    /**
     * 已经签到次数
     */
    @Column(name = "sign_times")
    private Integer signTimes;

    /**
     * 需要签到次数
     */
    @Column(name = "sign_need_times")
    private Integer signNeedTimes;

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
     * @return course_number - 签到的课程
     */
    public Long getCourseNumber() {
        return courseNumber;
    }

    /**
     * 设置签到的课程
     *
     * @param courseNumber 签到的课程
     */
    public void setCourseNumber(Long courseNumber) {
        this.courseNumber = courseNumber;
    }

    /**
     * 获取已经签到次数
     *
     * @return sign_times - 已经签到次数
     */
    public Integer getSignTimes() {
        return signTimes;
    }

    /**
     * 设置已经签到次数
     *
     * @param signTimes 已经签到次数
     */
    public void setSignTimes(Integer signTimes) {
        this.signTimes = signTimes;
    }

    /**
     * 获取需要签到次数
     *
     * @return sign_need_times - 需要签到次数
     */
    public Integer getSignNeedTimes() {
        return signNeedTimes;
    }

    /**
     * 设置需要签到次数
     *
     * @param signNeedTimes 需要签到次数
     */
    public void setSignNeedTimes(Integer signNeedTimes) {
        this.signNeedTimes = signNeedTimes;
    }
}