package com.hut.zero.network_request;

import com.google.gson.Gson;
import com.hut.zero.bean.DoubanMomentNews;
import com.hut.zero.bean.DoubanMomentStory;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Zero on 2017/4/5.
 */

public interface DoubanService {

    DoubanService SERVICE =new Retrofit.Builder()
            .baseUrl("https://moment.douban.com/")
            .client(new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)//设置失败重试
                    .build())
            .addConverterFactory(GsonConverterFactory.create(new Gson()))
            .build().create(DoubanService.class);

    DoubanService SERVICE_ARTICLE_DETAIL =new Retrofit.Builder()
            .baseUrl("https://moment.douban.com/")
            .client(new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)//设置失败重试
                    .build())
            .addConverterFactory(GsonConverterFactory.create(new Gson()))
            .build().create(DoubanService.class);

    @GET("api/stream/date/{date}")
    Call<DoubanMomentNews> load(@Path("date") String date);

    @GET("api/post/{id}")
    Call<DoubanMomentStory> loadArticleDetail(@Path("id") String id);
}
