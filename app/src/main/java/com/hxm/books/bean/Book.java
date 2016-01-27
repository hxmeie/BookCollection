package com.hxm.books.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by hxm on 2016/1/26.
 */
public class Book extends BmobObject {
    //书名
    private String title;
    private String price;
    private String pages;
    private String author;
    private String summary;
    private String bookImage;
    private String publisher;
    private String pubdate;
    private String catalog;

    public String getPubdate() {
        return pubdate;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getBookImage() {
        return bookImage;
    }

    public void setBookImage(String bookImage) {
        this.bookImage = bookImage;
    }

    @Override
    public String toString() {
        return "title=" + title + "\n" + "price=" + price + "\n" + "pages=" + pages + "\n" + "author=" + author + "\n" + "summary=" + summary + "\n" + "bookImage=" + bookImage + "\n" + "publisher=" + publisher;
    }
}
