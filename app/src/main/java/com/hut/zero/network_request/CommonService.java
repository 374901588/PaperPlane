package com.hut.zero.network_request;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Zero on 2017/4/4.
 */

public interface CommonService {
    CommonService SERVICE_DYNAMIC_URL =new Retrofit.Builder()
            .baseUrl("https://www.baidu.com/")
            .client(new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)//设置失败重试
                    .build())
            .addConverterFactory(GsonConverterFactory.create(new Gson()))
            .build().create(CommonService.class);

    /**
     * 根据动态的URL加载内容
     * @return
     */
    @GET
    Call<ResponseBody> loadDynamic(@Url String url);
}
