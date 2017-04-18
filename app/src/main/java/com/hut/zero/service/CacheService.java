/*
 * Copyright 2017 lizhaotailang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hut.zero.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.hut.zero.bean.DoubanCache;
import com.hut.zero.bean.GuokeCache;
import com.hut.zero.bean.ZhihuCache;
import com.hut.zero.bean.ZhihuDailyStory;
import com.hut.zero.network_request.CommonService;
import com.hut.zero.network_request.DoubanService;
import com.hut.zero.network_request.GuokeService;
import com.hut.zero.network_request.ZhihuService;
import com.hut.zero.other_pages.SettingsPreferenceActivity;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Lizhaotailang on 2016/9/18.
 * 定义了内部类LocalReceiver， 用于接收内部广播并获取传递的数据
 * 可以将三种请求内容的方法合并为一个，但是这样会使代码可读性变差
 */

public class CacheService extends Service {
    public static final int TYPE_ZHIHU = 0x00;
    public static final int TYPE_GUOKE = 0x01;
    public static final int TYPE_DOUBAN = 0x02;

    private LocalBroadcastManager manager;
    private LocalReceiver localReceiver = new LocalReceiver();

    @Override
    public void onCreate() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.hut.zero.LOCAL_BROADCAST");
        manager = LocalBroadcastManager.getInstance(this);
        localReceiver = new LocalReceiver();
        manager.registerReceiver(localReceiver, filter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    /**
     * 网络请求id对应的知乎日报的内容主体
     * 当type为0时，存储body中的数据
     * 当type为1时，再次请求share url中的内容并储存
     *
     * @param id 所要获取的知乎日报消息内容对应的id
     */
    private void startZhihuCache(final int id) {
        ZhihuCache cache=DataSupport.select("zhihu_content").where("zhihu_id = ?",""+id).findFirst(ZhihuCache.class);
        if (cache!=null && TextUtils.isEmpty(cache.getZhihu_content())) {
            ZhihuService.SERVICE_NEWS.loadNews(id+"").enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response1) {
                    try {
                        String s1=response1.body().string();
                        ZhihuDailyStory zhihuDailyStory= new Gson().fromJson(s1, ZhihuDailyStory.class);
                        if (zhihuDailyStory.getType()==1) {
                            CommonService.SERVICE_DYNAMIC_URL.loadDynamic(zhihuDailyStory.getShare_url()).enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response2) {
                                    try {
                                        String s2 = response2.body().string();
                                        ContentValues values = new ContentValues();
                                        values.put("zhihu_content",s2);
                                        DataSupport.updateAll(ZhihuCache.class, values, "zhihu_id = ?", ""+id);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Log.d("CacheService===>\n","startZhihuCache()->CommonService.SERVICE_DYNAMIC_URL.loadDynamic()请求失败!");
                                }
                            });
                        } else {
                            ContentValues values = new ContentValues();
                            values.put("zhihu_content",s1);
                            DataSupport.updateAll(ZhihuCache.class, values, "zhihu_id = ?", "" + id);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("CacheService===>\n","startZhihuCache()->ZhihuService.SERVICE_NEWS.loadNews()请求失败!");
                }
            });
        }
    }

    class LocalReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int id = intent.getIntExtra("id", 0);
            switch (intent.getIntExtra("type", -1)) {
                case TYPE_ZHIHU:
                    startZhihuCache(id);
                    break;
                case TYPE_GUOKE:
                    startGuokeCache(id);
                    break;
                case TYPE_DOUBAN:
                    startDoubanCache(id);
                    break;
                default:
                case -1:
                    break;
            }
        }


    }

    private void startDoubanCache(final int id) {
        DoubanCache cache=DataSupport.select("douban_content").where("douban_id = ?",""+id).findFirst(DoubanCache.class);
        if (cache!=null && TextUtils.isEmpty(cache.getDouban_content())) {
            new Retrofit.Builder()
                    .baseUrl("https://moment.douban.com/")
                    .client(new OkHttpClient.Builder()
                            .retryOnConnectionFailure(true)//设置失败重试
                            .build())
                    .build().create(DoubanService.class)
                    .loadArticleDetailForResponseBody("" + id)
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                String s = response.body().string();
                                ContentValues values = new ContentValues();
                                values.put("douban_content", s);
                                DataSupport.updateAll(DoubanCache.class, values, "douban_id = ?", id + "");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.d("CacheService===>\n","startDoubanCache()->DoubanService-loadArticleDetailForResponseBody()请求失败!");
                        }
                    });
        }
    }

    private void deleteTimeoutPosts() {

        SharedPreferences sp = getSharedPreferences(SettingsPreferenceActivity.SETTINGS_CONFIG_FILE_NAME, MODE_PRIVATE);

        long timeStamp = (Calendar.getInstance().getTimeInMillis() / 1000) - Long.parseLong(sp.getString("time_of_saving_articles", "7")) * 24 * 60 * 60;
        DataSupport.deleteAll(ZhihuCache.class, "zhihu_time < ? and bookmark != 1", ""+timeStamp);
        DataSupport.deleteAll(DoubanCache.class, "douban_time < ? and bookmark != 1", ""+timeStamp);
        DataSupport.deleteAll(GuokeCache.class, "guoke_time < ? and bookmark != 1", ""+timeStamp);
    }

    private void startGuokeCache(final int id) {
        GuokeCache cache=DataSupport.select("guoke_content").where("guoke_id = ?",""+id).findFirst(GuokeCache.class);
        if (cache!=null && TextUtils.isEmpty(cache.getGuoke_content())) {
            GuokeService.SERVICE_JINGXUAN.loadJingxuan(id+"").enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String s = response.body().string();
                        ContentValues values = new ContentValues();
                        values.put("guoke_content", s);
                        DataSupport.updateAll(GuokeCache.class, values, "guoke_id = ?", id + "");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("CacheService===>\n","startGuokeCache()->GuokeService.SERVICE_JINGXUAN.loadJingxuan()请求失败!");
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        manager.unregisterReceiver(localReceiver);
        deleteTimeoutPosts();
    }

}
