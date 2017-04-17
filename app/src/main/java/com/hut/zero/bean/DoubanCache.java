package com.hut.zero.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by Zero on 2017/4/5.
 */

public class DoubanCache extends DataSupport {
    private int douban_id;
    private String douban_news;
    private float douban_time;
    private String douban_content;
    private boolean bookmark=false;

    public int getDouban_id() {
        return douban_id;
    }

    public void setDouban_id(int douban_id) {
        this.douban_id = douban_id;
    }

    public String getDouban_news() {
        return douban_news;
    }

    public void setDouban_news(String douban_news) {
        this.douban_news = douban_news;
    }

    public float getDouban_time() {
        return douban_time;
    }

    public void setDouban_time(float douban_time) {
        this.douban_time = douban_time;
    }

    public String getDouban_content() {
        return douban_content;
    }

    public void setDouban_content(String douban_content) {
        this.douban_content = douban_content;
    }

    public boolean isBookmark() {
        return bookmark;
    }

    public void setBookmark(boolean bookmark) {
        this.bookmark = bookmark;
    }

    @Override
    public String toString() {
        return "douban_id="+douban_id+",bookmark="+bookmark;
    }
}
