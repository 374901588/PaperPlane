package com.hut.zero.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by Zero on 2017/4/5.
 */

public class GuokeCache extends DataSupport {
    private int guoke_id;
    private String guoke_news;
    private float guoke_time;
    private String guoke_content;
    private boolean bookmark=false;

    public int getGuoke_id() {
        return guoke_id;
    }

    public void setGuoke_id(int guoke_id) {
        this.guoke_id = guoke_id;
    }

    public String getGuoke_news() {
        return guoke_news;
    }

    public void setGuoke_news(String guoke_news) {
        this.guoke_news = guoke_news;
    }

    public float getGuoke_time() {
        return guoke_time;
    }

    public void setGuoke_time(float guoke_time) {
        this.guoke_time = guoke_time;
    }

    public String getGuoke_content() {
        return guoke_content;
    }

    public void setGuoke_content(String guoke_content) {
        this.guoke_content = guoke_content;
    }

    public boolean isBookmark() {
        return bookmark;
    }

    public void setBookmark(boolean bookmark) {
        this.bookmark = bookmark;
    }

    @Override
    public String toString() {
        return "guoke_id="+guoke_id+",bookmark="+bookmark;
    }
}
