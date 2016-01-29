package com.hxm.books;

/**
 * Created by hxm on 2016/1/29.
 */
public class Constants {
    //图书接口
    public static final String GET_BOOK_BASE_URL="https://api.douban.com/v2/book/isbn/:";
    public static final String APP_ID="aeb5c4b32145065bb54ea83c40734e88";

    //-----------------------------------------SQL语句---------------------------------------

    //查询扫描到的书是否已存在在服务器上
    public static final String CHECK_ISBN_EXIST_OR_NOT="select isbn from Book where isbn=";

}
