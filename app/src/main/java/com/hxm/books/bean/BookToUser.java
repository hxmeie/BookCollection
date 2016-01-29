package com.hxm.books.bean;

import cn.bmob.v3.BmobObject;

/**
 * 用户和书籍关联表
 * Created by hxm on 2016/1/29.
 */
public class BookToUser extends BmobObject {

    private String uerObjectId;
    private String isbn;

    public String getUerObjectId() {
        return uerObjectId;
    }

    public void setUerObjectId(String uerObjectId) {
        this.uerObjectId = uerObjectId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
}
