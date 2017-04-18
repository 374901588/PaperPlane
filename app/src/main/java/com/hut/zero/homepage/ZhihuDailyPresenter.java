package com.hut.zero.homepage;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hut.zero.bean.BeanType;
import com.hut.zero.bean.ZhihuCache;
import com.hut.zero.bean.ZhihuDailyNews;
import com.hut.zero.detail.DetailActivity;
import com.hut.zero.model.ZhihuModelImpl;
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

public class ZhihuDailyPresenter implements ZhihuDailyContract.Presenter {
    private Context mContext;

    private ZhihuDailyContract.View mView;

    private ZhihuModelImpl mModel;

    private Gson gson = new Gson();

    private ArrayList<ZhihuDailyNews.Question> dataForQuestion = new ArrayList<>();


    private DateFormatter formatter = new DateFormatter();

    public ZhihuDailyPresenter(Context context, ZhihuDailyContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
        mModel = new ZhihuModelImpl();
    }

    @Override
    public void start() {
        loadPosts(Calendar.getInstance().getTimeInMillis(), true);
    }

    @Override
    public void loadPosts(long date, boolean clearing) {
        if (clearing) mView.showLoading();
        if (NetworkState.networkConnected(mContext)) {
            try {
                mModel.loadHistory(formatter.ZhihuDailyDateFormat(date), new Callback<ZhihuDailyNews>() {
                    @Override
                    public void onResponse(Call<ZhihuDailyNews> call, Response<ZhihuDailyNews> response) {
                        if (clearing) {
                            dataForQuestion.clear();
                        }

                        ZhihuDailyNews zhihuDailyNews = response.body();
                        for (ZhihuDailyNews.Question item : zhihuDailyNews.getStories()) {
                            dataForQuestion.add(item);
                            if (!isQuestionIdExist(item.getId())) {
                                try {
                                    ZhihuCache temp = new ZhihuCache();
                                    temp.setZhihu_id(item.getId());
                                    temp.setZhihu_news(gson.toJson(item));
                                    //在请求知乎消息列表的时候，并没有返回消息的详细内容。不过详细内容我们还是需要缓存的，网络请求在UI线程上进行可能会引起ANR，那更好的解决办法就是在Service里面完成了
                                    temp.setZhihu_content("");
                                    DateFormat format = new SimpleDateFormat("yyyyMMdd");
                                    Date dateTemp = format.parse(zhihuDailyNews.getDate());
                                    temp.setZhihu_time(dateTemp.getTime() / 1000);
                                    if (temp.save()) {
                                        Intent intent = new Intent("com.hut.zero.LOCAL_BROADCAST");
                                        intent.putExtra("type", CacheService.TYPE_ZHIHU);
                                        intent.putExtra("id", item.getId());
                                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                                    } else {
                                        Log.e("ZhihuDailyPresenter", "loadPosts->数据保存失败");
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                        mView.showResults(dataForQuestion);
                        mView.stopLoading();
                    }

                    @Override
                    public void onFailure(Call<ZhihuDailyNews> call, Throwable t) {
                        mView.stopLoading();
                        mView.showError();
                    }
                });
            } catch (JsonSyntaxException e) {
                mView.showError();
            }
        } else {
            if (clearing) {
                dataForQuestion.clear();

                for (ZhihuCache zhihuCache : DataSupport.findAll(ZhihuCache.class)) {
                    ZhihuDailyNews.Question question = gson.fromJson(zhihuCache.getZhihu_news(), ZhihuDailyNews.Question.class);
                    dataForQuestion.add(question);
                }
                mView.stopLoading();
                mView.showResults(dataForQuestion);
            } else
                mView.showError();
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
    public void startReading(int position) {
        ZhihuDailyNews.Question question = dataForQuestion.get(position);
        mContext.startActivity(new Intent(mContext, DetailActivity.class)
                .putExtra("type", BeanType.TYPE_ZHIHU)
                .putExtra("id", question.getId())
                .putExtra("title", question.getTitle())
                .putExtra("coverUrl", question.getImages().get(0)));
    }

    @Override
    public void feelLucky() {
        if (dataForQuestion.isEmpty()) {
            mView.showError();
            return;
        }
        startReading(new Random().nextInt(dataForQuestion.size()));
    }

    private boolean isQuestionIdExist(int zhihu_id) {
        return DataSupport.isExist(ZhihuCache.class, "zhihu_id = ?", zhihu_id + "");
    }
}
