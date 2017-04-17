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

package com.hut.zero.bookmarks;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.hut.zero.adapter.BookmarksAdapter;
import com.hut.zero.bean.BeanType;
import com.hut.zero.bean.DoubanCache;
import com.hut.zero.bean.DoubanMomentNews;
import com.hut.zero.bean.GuokeCache;
import com.hut.zero.bean.GuokeHandpickNews;
import com.hut.zero.bean.ZhihuCache;
import com.hut.zero.bean.ZhihuDailyNews;
import com.hut.zero.detail.DetailActivity;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rx.Observable;

import static com.hut.zero.adapter.BookmarksAdapter.TYPE_DOUBAN_NORMAL;
import static com.hut.zero.adapter.BookmarksAdapter.TYPE_DOUBAN_WITH_HEADER;
import static com.hut.zero.adapter.BookmarksAdapter.TYPE_GUOKE_NORMAL;
import static com.hut.zero.adapter.BookmarksAdapter.TYPE_GUOKE_WITH_HEADER;
import static com.hut.zero.adapter.BookmarksAdapter.TYPE_ZHIHU_NORMAL;
import static com.hut.zero.adapter.BookmarksAdapter.TYPE_ZHIHU_WITH_HEADER;

/**
 * Created by lizhaotailang on 2016/12/23.
 */

public class BookmarksPresenter implements BookmarksContract.Presenter {
    private BookmarksContract.View view;
    private Context context;
    private Gson gson;

    private ArrayList<DoubanMomentNews.Posts> doubanList;
    private ArrayList<GuokeHandpickNews.Result> guokeList;
    private ArrayList<ZhihuDailyNews.Question> zhihuList;

    private ArrayList<Integer> types;

    public BookmarksPresenter(Context context, BookmarksContract.View view) {
        this.context = context;
        this.view = view;
        this.view.setPresenter(this);
        gson = new Gson();

        zhihuList = new ArrayList<>();
        guokeList = new ArrayList<>();
        doubanList = new ArrayList<>();

        types = new ArrayList<>();
    }

    /**
     * 请求结果
     *
     * @param refresh
     */
    @Override
    public void loadResults(boolean refresh) {
        if (!refresh) {
            view.showLoading();
        } else {
            zhihuList.clear();
            guokeList.clear();
            doubanList.clear();
            types.clear();
        }

        checkForFreshData();
        view.showResults(zhihuList, guokeList, doubanList, types);
        view.stopLoading();
    }

    @Override
    public void checkForFreshData() {
        // every first one of the 3 lists is with header
        // add them in advance

        //如果zhihuList、guokeList、doubanList分别在三个单独的线程添加数据，会因线程不同步使得使得types的数据与list不对应，从而在适配器在加载子项时出错
        //但是在同一个线程中顺序添加数据时，就不会导致错误(如下)
        //LitePal在存储布尔值的时候是以1和0的值存储的，不是以true或者false，所以在以布尔值为字段存储为查询条件时应该为 fieldName = 1 or 0

        types.add(TYPE_ZHIHU_WITH_HEADER);
        List<ZhihuCache> list1 = DataSupport.where("bookmark = 1").find(ZhihuCache.class);
        Observable.from(list1)
                .subscribe(zhihuCache -> {
                    ZhihuDailyNews.Question question = gson.fromJson(zhihuCache.getZhihu_news(), ZhihuDailyNews.Question.class);
                    zhihuList.add(question);
                    types.add(TYPE_ZHIHU_NORMAL);
                });

        types.add(TYPE_GUOKE_WITH_HEADER);
        List<GuokeCache> list2 = DataSupport.where("bookmark = 1").find(GuokeCache.class);
        Observable.from(list2)
                .subscribe(guokeCache -> {
                    GuokeHandpickNews.Result result = gson.fromJson(guokeCache.getGuoke_news(), GuokeHandpickNews.Result.class);
                    guokeList.add(result);
                    types.add(TYPE_GUOKE_NORMAL);
                });


        types.add(TYPE_DOUBAN_WITH_HEADER);
        List<DoubanCache> list3 = DataSupport.where("bookmark = 1").find(DoubanCache.class);
        Observable.from(list3)
                .subscribe(doubanCache -> {
                    DoubanMomentNews.Posts posts = gson.fromJson(doubanCache.getDouban_news(), DoubanMomentNews.Posts.class);
                    doubanList.add(posts);
                    types.add(TYPE_DOUBAN_NORMAL);
                });
    }

    @Override
    public void feelLucky() {
        Random random = new Random();
        int p = new Random().nextInt(types.size());
        while (true) {
            if (types.get(p) == BookmarksAdapter.TYPE_ZHIHU_NORMAL) {
                startReading(BeanType.TYPE_ZHIHU, p);
                break;
            } else if (types.get(p) == BookmarksAdapter.TYPE_GUOKE_NORMAL) {
                startReading(BeanType.TYPE_GUOKE, p);
                break;
            } else if (types.get(p) == BookmarksAdapter.TYPE_DOUBAN_NORMAL) {
                startReading(BeanType.TYPE_DOUBAN, p);
                break;
            } else {
                p = random.nextInt(types.size());
            }
        }
    }

    @Override
    public void startReading(BeanType type, int position) {
        Intent intent = new Intent(context, DetailActivity.class);
        switch (type) {
            case TYPE_ZHIHU:
                ZhihuDailyNews.Question q = zhihuList.get(position - 1);
                intent.putExtra("type", BeanType.TYPE_ZHIHU);
                intent.putExtra("id", q.getId());
                intent.putExtra("title", q.getTitle());
                intent.putExtra("coverUrl", q.getImages().get(0));
                break;

            case TYPE_GUOKE:
                GuokeHandpickNews.Result r = guokeList.get(position - zhihuList.size() - 2);
                intent.putExtra("type", BeanType.TYPE_GUOKE);
                intent.putExtra("id", r.getId());
                intent.putExtra("title", r.getTitle());
                intent.putExtra("coverUrl", r.getHeadline_img());
                break;
            case TYPE_DOUBAN:
                DoubanMomentNews.Posts p = doubanList.get(position - zhihuList.size() - guokeList.size() - 3);
                intent.putExtra("type", BeanType.TYPE_DOUBAN);
                intent.putExtra("id", p.getId());
                intent.putExtra("title", p.getTitle());
                if (p.getThumbs().size() == 0) {
                    intent.putExtra("coverUrl", "");
                } else {
                    intent.putExtra("image", p.getThumbs().get(0).getMedium().getUrl());
                }
                break;
            default:
                break;
        }
        context.startActivity(intent);
    }

    @Override
    public void start() {
    }
}
