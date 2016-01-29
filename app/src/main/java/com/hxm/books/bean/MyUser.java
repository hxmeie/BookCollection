package com.hxm.books.bean;

import cn.bmob.v3.BmobUser;

/**
 * 用户表
 * Created by hxm on 2016/1/13.
 */
public class MyUser extends BmobUser {
    private Integer age;
    private Boolean sex;
    private String nickName;

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
