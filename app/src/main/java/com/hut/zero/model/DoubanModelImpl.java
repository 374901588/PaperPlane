package com.hut.zero.model;

import com.hut.zero.bean.DoubanMomentNews;
import com.hut.zero.bean.DoubanMomentStory;
import com.hut.zero.network_request.DoubanService;

import okhttp3.ResponseBody;

/**
 * Created by Zero on 2017/4/5.
 */

public class DoubanModelImpl {

    public void load(String date, retrofit2.Callback<DoubanMomentNews> callback) {
        DoubanService.SERVICE.loadMoment(date).enqueue(callback);
    }

    public void loadArticleDetail(String id, retrofit2.Callback<DoubanMomentStory> callback) {
        DoubanService.SERVICE.loadArticleDetail(id).enqueue(callback);
    }

    public void loadArticleDetailForResponeBody(String id, retrofit2.Callback<ResponseBody> callback) {
        DoubanService.SERVICE.loadArticleDetailForResponseBody(id).enqueue(callback);
    }
}
