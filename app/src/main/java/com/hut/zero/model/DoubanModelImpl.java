package com.hut.zero.model;

import com.hut.zero.bean.DoubanMomentNews;
import com.hut.zero.bean.DoubanMomentStory;
import com.hut.zero.network_request.DoubanService;

/**
 * Created by Zero on 2017/4/5.
 */

public class DoubanModelImpl {

    public void load(String date, retrofit2.Callback<DoubanMomentNews> callback) {
        DoubanService.SERVICE.load(date).enqueue(callback);
    }

    public void loadArticleDetail(String id, retrofit2.Callback<DoubanMomentStory> callback) {
        DoubanService.SERVICE_ARTICLE_DETAIL.loadArticleDetail(id).enqueue(callback);
    }
}
