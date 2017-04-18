package com.hut.zero.network_request;

import com.google.gson.Gson;
import com.hut.zero.bean.ZhihuDailyNews;
import com.hut.zero.bean.ZhihuDailyStory;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Zero on 2017/4/3.
 */

public interface ZhihuService {
    ZhihuService SERVICE_HISTORY =new Retrofit.Builder()
            .baseUrl("http://news.at.zhihu.com/")
            .client(new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)//设置失败重试
                    .build())
            .addConverterFactory(GsonConverterFactory.create(new Gson()))
            .build().create(ZhihuService.class);

    ZhihuService SERVICE_STORY =new Retrofit.Builder()
            .baseUrl("http://news-at.zhihu.com/")
            .client(new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)//设置失败重试
                    .build())
            .addConverterFactory(GsonConverterFactory.create(new Gson()))
            .build().create(ZhihuService.class);

    @GET("api/4/news/before/{date}")
    Call<ZhihuDailyNews> loadHistory(@Path("date") String date);

    ZhihuService SERVICE_NEWS =new Retrofit.Builder()
            .baseUrl("http://news-at.zhihu.com/")
            .client(new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)//设置失败重试
                    .build())
            .build().create(ZhihuService.class);
    @GET("api/4/news/{zhihu_id}")
    Call<ResponseBody> loadNews(@Path("zhihu_id") String zhihuId);

    @GET("api/4/news/{zhihu_id}")
    Call<ZhihuDailyStory> loadStory(@Path("zhihu_id") String zhihuId);
}
