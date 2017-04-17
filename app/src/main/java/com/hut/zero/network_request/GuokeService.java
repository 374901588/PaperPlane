package com.hut.zero.network_request;

import com.google.gson.Gson;
import com.hut.zero.bean.GuokeHandpickNews;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Zero on 2017/4/5.
 */

public interface GuokeService {

    GuokeService SERVICE=new Retrofit.Builder()
            .baseUrl("http://apis.guokr.com/")
            .client(new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)//设置失败重试
                    .build())
            .addConverterFactory(GsonConverterFactory.create(new Gson()))
            .build().create(GuokeService.class);

    GuokeService SERVICE_JINGXUAN=new Retrofit.Builder()
            .baseUrl("http://jingxuan.guokr.com/")
            .client(new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)//设置失败重试
                    .build())
//            .addConverterFactory(GsonConverterFactory.create(new Gson()))
            .build().create(GuokeService.class);

    @GET("handpick/article.json?retrieve_type=by_since&category=all&limit=25&ad=1")
    Call<GuokeHandpickNews> load();

    @GET("pick/{id}")
    Call<ResponseBody> loadJingxuan(@Path("id") String id);
}
