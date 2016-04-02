package com.hxm.books.bean;

import cn.bmob.v3.BmobObject;

/**
 * 为了查询是否收藏某本书籍时，返回的数据只有isbn,减少数据流量和获取时间
 * Created by Sidney on 2016/4/2.
 */
public class BookISBN extends BmobObject {
    private String bookISBN;

    public String getBookISBN() {
        return bookISBN;
    }

    public void setBookISBN(String bookISBN) {
        this.bookISBN = bookISBN;
    }
}
