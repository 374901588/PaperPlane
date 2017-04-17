package com.hut.zero.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by Zero on 2017/4/3.
 * 对应与知乎内容的表
 */

public class ZhihuCache extends DataSupport {
    //由于LitePal默认含有主键，所以在这里不另外设置了
    private int zhihu_id;//知乎日报消息id
    private String zhihu_news;//知乎日报消息内容
    private float zhihu_time;//知乎日报消息发布的时间
    private String zhihu_content;//知乎日报消息详细内容
    private boolean bookmark=false;//是否被收藏

    public boolean isBookmark() {
        return bookmark;
    }

    public void setBookmark(boolean bookmark) {
        this.bookmark = bookmark;
    }

    public int getZhihu_id() {
        return zhihu_id;
    }

    public void setZhihu_id(int zhihu_id) {
        this.zhihu_id = zhihu_id;
    }

    public String getZhihu_news() {
        return zhihu_news;
    }

    public void setZhihu_news(String zhihu_news) {
        this.zhihu_news = zhihu_news;
    }

    public float getZhihu_time() {
        return zhihu_time;
    }

    public void setZhihu_time(float zhihu_time) {
        this.zhihu_time = zhihu_time;
    }

    public String getZhihu_content() {
        return zhihu_content;
    }

    public void setZhihu_content(String zhihu_content) {
        this.zhihu_content = zhihu_content;
    }

    @Override
    public String toString() {
        return "zhihu_id="+zhihu_id+",bookmark="+bookmark;
    }
}
