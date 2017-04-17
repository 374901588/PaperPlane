package com.hut.zero.model;

import com.hut.zero.bean.GuokeHandpickNews;
import com.hut.zero.network_request.GuokeService;

import okhttp3.ResponseBody;

/**
 * Created by Zero on 2017/4/5.
 */

public class GuokeModelImpl {
    public void load(retrofit2.Callback<GuokeHandpickNews> callback) {
        GuokeService.SERVICE.load().enqueue(callback);
    }

    public void loadJingxuan(String id,retrofit2.Callback<ResponseBody> callback) {
        GuokeService.SERVICE_JINGXUAN.loadJingxuan(id).enqueue(callback);
    }
}
