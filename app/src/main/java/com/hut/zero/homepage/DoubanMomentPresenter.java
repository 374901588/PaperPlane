package com.hut.zero.homepage;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hut.zero.bean.BeanType;
import com.hut.zero.bean.DoubanCache;
import com.hut.zero.bean.DoubanMomentNews;
import com.hut.zero.detail.DetailActivity;
import com.hut.zero.model.DoubanModelImpl;
import com.hut.zero.service.CacheService;
import com.hut.zero.util.DateFormatter;
import com.hut.zero.util.NetworkState;

import org.litepal.crud.DataSupport;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Zero on 2017/4/2.
 */

public class DoubanMomentPresenter implements DoubanMomentContract.Presenter {
    private Context mContext;

    private DoubanMomentContract.View mView;

    private Gson gson=new Gson();

    private DoubanModelImpl model;

    private ArrayList<DoubanMomentNews.Posts> dataForPosts = new ArrayList<>();

    public DoubanMomentPresenter(Context context, DoubanMomentContract.View view) {
        mContext=context;
        mView=view;
        mView.setPresenter(this);
        model=new DoubanModelImpl();
    }


    @Override
    public void start() {
        refresh();
    }

    @Override
    public void startReading(int position) {
        DoubanMomentNews.Posts item = dataForPosts.get(position);
        Intent intent = new Intent(mContext, DetailActivity.class);

        intent.putExtra("type", BeanType.TYPE_DOUBAN);
        intent.putExtra("id", item.getId());
        intent.putExtra("title", item.getTitle());
        if (item.getThumbs().size() == 0){
            intent.putExtra("coverUrl", "");
        } else {
            intent.putExtra("coverUrl", item.getThumbs().get(0).getMedium().getUrl());
        }
        mContext.startActivity(intent);
    }

    @Override
    public void loadPosts(long date, boolean clearing) {
        if (clearing) {
            mView.startLoading();
        }
        if (NetworkState.networkConnected(mContext)) {
            try {
                model.load(new DateFormatter().DoubanDateFormat(date), new Callback<DoubanMomentNews>() {
                    @Override
                    public void onResponse(Call<DoubanMomentNews> call, Response<DoubanMomentNews> response) {
                        if (clearing) {
                            dataForPosts.clear();
                        }
                        for (DoubanMomentNews.Posts item: response.body().getPosts() ){
                            dataForPosts.add(item);
                            if ( !isQuestionIdExist(item.getId())) {
                                try {
                                    DoubanCache temp=new DoubanCache();
                                    temp.setDouban_id(item.getId());
                                    temp.setDouban_news(gson.toJson(item));
                                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                    Date date = format.parse(item.getPublished_time());
                                    temp.setDouban_time(date.getTime()/1000);
                                    temp.setDouban_content("");
                                    if (temp.save()) {
                                        Intent intent = new Intent("com.hut.zero.LOCAL_BROADCAST");
                                        intent.putExtra("type", CacheService.TYPE_DOUBAN);
                                        intent.putExtra("id", item.getId());
                                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                                    } else {
                                        Log.e("DoubanMomentPresenter","loadPosts->数据保存失败");
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            mView.showResults(dataForPosts);
                            mView.stopLoading();
                        }
                    }

                    @Override
                    public void onFailure(Call<DoubanMomentNews> call, Throwable t) {
                        mView.stopLoading();
                        mView.showLoadingError();
                    }
                });
            } catch (JsonSyntaxException e) {
                mView.showLoadingError();
                e.printStackTrace();
            }
        } else {
            if (clearing) {
                dataForPosts.clear();

                for (DoubanCache doubanCache :DataSupport.findAll(DoubanCache.class)) {
                    DoubanMomentNews.Posts posts = gson.fromJson(doubanCache.getDouban_news(), DoubanMomentNews.Posts.class);
                    dataForPosts.add(posts);
                }
                mView.stopLoading();
                mView.showResults(dataForPosts);
            }
        }
    }

    @Override
    public void refresh() {
        loadPosts(Calendar.getInstance().getTimeInMillis(), true);
    }

    @Override
    public void loadMore(long date) {
        loadPosts(date, false);
    }

    @Override
    public void feelLucky() {
        if (dataForPosts.isEmpty()) {
            mView.showLoadingError();
            return;
        }
        startReading(new Random().nextInt(dataForPosts.size()));
    }

    private boolean isQuestionIdExist(int douban_id) {
        return DataSupport.isExist(DoubanCache.class, "douban_id = ?", douban_id+"");
    }
}
