package com.showtime.sign.model.entity;

import java.util.Date;
import javax.persistence.*;

public class Courses {
    /**
     * 表主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 学年学期
     */
    private String semester;

    /**
     * 开课编号
     */
    private Integer number;

    /**
     * 课程名称
     */
    private String name;

    /**
     * 教学班名称
     */
    private String classes;

    /**
     * 授课教师
     */
    private String teacher;

    /**
     * 节次
     */
    private Integer jieci;

    /**
     * 签到状态， 0:未开始 1：已完成 2：签到中
     */
    @Column(name = "sign_state")
    private Byte signState;

    /**
     * 日期
     */
    private Date date;

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
     * 获取学年学期
     *
     * @return semester - 学年学期
     */
    public String getSemester() {
        return semester;
    }

    /**
     * 设置学年学期
     *
     * @param semester 学年学期
     */
    public void setSemester(String semester) {
        this.semester = semester;
    }

    /**
     * 获取开课编号
     *
     * @return number - 开课编号
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * 设置开课编号
     *
     * @param number 开课编号
     */
    public void setNumber(Integer number) {
        this.number = number;
    }

    /**
     * 获取课程名称
     *
     * @return name - 课程名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置课程名称
     *
     * @param name 课程名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取教学班名称
     *
     * @return classes - 教学班名称
     */
    public String getClasses() {
        return classes;
    }

    /**
     * 设置教学班名称
     *
     * @param classes 教学班名称
     */
    public void setClasses(String classes) {
        this.classes = classes;
    }

    /**
     * 获取授课教师
     *
     * @return teacher - 授课教师
     */
    public String getTeacher() {
        return teacher;
    }

    /**
     * 设置授课教师
     *
     * @param teacher 授课教师
     */
    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    /**
     * 获取节次
     *
     * @return jieci - 节次
     */
    public Integer getJieci() {
        return jieci;
    }

    /**
     * 设置节次
     *
     * @param jieci 节次
     */
    public void setJieci(Integer jieci) {
        this.jieci = jieci;
    }

    /**
     * 获取签到状态， 0:未开始 1：已完成 2：签到中
     *
     * @return sign_state - 签到状态， 0:未开始 1：已完成 2：签到中
     */
    public Byte getSignState() {
        return signState;
    }

    /**
     * 设置签到状态， 0:未开始 1：已完成 2：签到中
     *
     * @param signState 签到状态， 0:未开始 1：已完成 2：签到中
     */
    public void setSignState(Byte signState) {
        this.signState = signState;
    }

    /**
     * 获取日期
     *
     * @return date - 日期
     */
    public Date getDate() {
        return date;
    }

    /**
     * 设置日期
     *
     * @param date 日期
     */
    public void setDate(Date date) {
        this.date = date;
    }
}