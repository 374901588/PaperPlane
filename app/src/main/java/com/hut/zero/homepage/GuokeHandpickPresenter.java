package com.hut.zero.homepage;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hut.zero.bean.BeanType;
import com.hut.zero.bean.GuokeCache;
import com.hut.zero.bean.GuokeHandpickNews;
import com.hut.zero.detail.DetailActivity;
import com.hut.zero.model.GuokeModelImpl;
import com.hut.zero.service.CacheService;
import com.hut.zero.util.NetworkState;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Zero on 2017/4/2.
 */

public class GuokeHandpickPresenter implements GuokeHandpickContract.Presenter {
    private GuokeHandpickContract.View mView;
    private GuokeModelImpl mModel;
    private Context mContext;

    private Gson gson=new Gson();

    private ArrayList<GuokeHandpickNews.Result> dataForResult=new ArrayList<>();

    public GuokeHandpickPresenter(Context context,GuokeHandpickContract.View view) {
        mContext=context;
        mView=view;
        mView.setPresenter(this);
        mModel=new GuokeModelImpl();
    }

    @Override
    public void start() {
        loadPosts();
    }

    @Override
    public void loadPosts() {
        mView.showLoading();
        if (NetworkState.networkConnected(mContext)) {
            try {
                // 由于果壳并没有按照日期加载的api
                // 所以不存在加载指定日期内容的操作，当要请求数据时一定是在进行刷新
                mModel.load(new Callback<GuokeHandpickNews>() {
                    @Override
                    public void onResponse(Call<GuokeHandpickNews> call, Response<GuokeHandpickNews> response) {
                        dataForResult.clear();
                        for (GuokeHandpickNews.Result re:response.body().getResult()) {//TODO
                            dataForResult.add(re);
                            if (!isQuestionIdExist(re.getId())) {
                                GuokeCache temp=new GuokeCache();
                                temp.setGuoke_id(re.getId());
                                temp.setGuoke_news(gson.toJson(re));
                                temp.setGuoke_content("");
                                temp.setGuoke_time((long)re.getDate_picked());
                                if (temp.save()) {
                                    Intent intent = new Intent("com.hut.zero.LOCAL_BROADCAST");
                                    intent.putExtra("type", CacheService.TYPE_GUOKE);
                                    intent.putExtra("id", re.getId());
                                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                                } else {
                                    Log.e("GuokeHandpickPresenter","loadPosts->数据保存失败");
                                }
                            }
                        }
                        mView.showResults(dataForResult);
                        mView.stopLoading();
                    }

                    @Override
                    public void onFailure(Call<GuokeHandpickNews> call, Throwable t) {
                        mView.stopLoading();
                        mView.showError();
                    }
                });
            } catch (JsonSyntaxException e) {
                mView.stopLoading();
                mView.showError();
            }
        } else {
            for (GuokeCache guokeCache:DataSupport.findAll(GuokeCache.class)) {
                GuokeHandpickNews.Result result=gson.fromJson(guokeCache.getGuoke_news(),GuokeHandpickNews.Result.class);
                dataForResult.add(result);
            }
            mView.stopLoading();
            mView.showResults(dataForResult);

            //TODO 在另外两个Presenter中是否都也要做相同的处理
            //当第一次安装应用，并且没有打开网络时
            //此时既无法网络加载，也无法本地加载
            if (dataForResult.isEmpty()) {
                mView.showError();
            }
        }
    }

    @Override
    public void refresh() {
        loadPosts();
    }

    @Override
    public void startReading(int position) {
        GuokeHandpickNews.Result item = dataForResult.get(position);
        mContext.startActivity(new Intent(mContext, DetailActivity.class)
                .putExtra("type", BeanType.TYPE_GUOKE)
                .putExtra("id", item.getId())
                .putExtra("coverUrl", item.getHeadline_img())
                .putExtra("title", item.getTitle())
        );
    }

    @Override
    public void feelLucky() {
        if (dataForResult.isEmpty()) {
            mView.showError();
            return;
        }
        startReading(new Random().nextInt(dataForResult.size()));
    }

    private boolean isQuestionIdExist(int guoke_id) {
        return DataSupport.isExist(GuokeCache.class, "guoke_id = ?", guoke_id+"");
    }
}
