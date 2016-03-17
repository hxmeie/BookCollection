package com.hxm.books.bean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * 用户表
 * Created by hxm on 2016/1/13.
 */
public class MyUser extends BmobUser {
    private Integer age;
    private Boolean sex;
    private String nickName;
    private BmobFile userAvatar;//用户头像
    private BmobRelation starBooks;//多对多关系:用户收藏的图书

    public BmobFile getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(BmobFile userAvatar) {
        this.userAvatar = userAvatar;
    }


    public BmobRelation getStarBooks() {
        return starBooks;
    }

    public void setStarBooks(BmobRelation starBooks) {
        this.starBooks = starBooks;
    }

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
