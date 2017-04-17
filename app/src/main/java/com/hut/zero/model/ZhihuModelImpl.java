package com.hut.zero.model;

import com.hut.zero.bean.ZhihuDailyNews;
import com.hut.zero.bean.ZhihuDailyStory;
import com.hut.zero.network_request.ZhihuService;

/**
 * Created by Zero on 2017/4/5.
 */

public class ZhihuModelImpl {
    public void loadHistory(String date, retrofit2.Callback<ZhihuDailyNews> callback) {
        ZhihuService.SERVICE_HISTORY.loadHistory(date).enqueue(callback);
    }

    public void loadStory(String zhihu_id, retrofit2.Callback<ZhihuDailyStory> callback) {
        ZhihuService.SERVICE_STORY.loadStory(zhihu_id).enqueue(callback);
    }
}
