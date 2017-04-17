package com.hut.zero.search;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
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

import rx.Observable;

import static com.hut.zero.adapter.BookmarksAdapter.TYPE_DOUBAN_NORMAL;
import static com.hut.zero.adapter.BookmarksAdapter.TYPE_DOUBAN_WITH_HEADER;
import static com.hut.zero.adapter.BookmarksAdapter.TYPE_GUOKE_NORMAL;
import static com.hut.zero.adapter.BookmarksAdapter.TYPE_GUOKE_WITH_HEADER;
import static com.hut.zero.adapter.BookmarksAdapter.TYPE_ZHIHU_NORMAL;
import static com.hut.zero.adapter.BookmarksAdapter.TYPE_ZHIHU_WITH_HEADER;


/**
 * Created by Zero on 2017/4/16.
 */

public class SearchPresenter implements SearchContract.Presenter {
    private Context context;
    private SearchContract.View view;

    private Gson gson;

    private ArrayList<DoubanMomentNews.Posts> doubanList;
    private ArrayList<GuokeHandpickNews.Result> guokeList;
    private ArrayList<ZhihuDailyNews.Question> zhihuList;

    private ArrayList<Integer> types;

    public SearchPresenter(Context context, SearchContract.View view) {
        this.context = context;
        this.view = view;
        view.setPresenter(this);
        gson = new Gson();

        zhihuList = new ArrayList<>();
        guokeList = new ArrayList<>();
        doubanList = new ArrayList<>();
        types = new ArrayList<>();
    }

    @Override
    public void start() {

    }

    @Override
    public void loadResults(String queryWords) {
        zhihuList.clear();
        guokeList.clear();
        doubanList.clear();
        types.clear();

        //TODO
        types.add(TYPE_ZHIHU_WITH_HEADER);
        List<ZhihuCache> list1 = DataSupport.where("bookmark = 1 and zhihu_news like ?", "%"+queryWords+"%").find(ZhihuCache.class);
        Observable.from(list1)
                .subscribe(zhihuCache -> {
                    ZhihuDailyNews.Question question = gson.fromJson(zhihuCache.getZhihu_news(), ZhihuDailyNews.Question.class);
                    zhihuList.add(question);
                    types.add(TYPE_ZHIHU_NORMAL);
                });

        types.add(TYPE_GUOKE_WITH_HEADER);
        List<GuokeCache> list2 = DataSupport.where("bookmark = 1 and guoke_news like ?", "%"+queryWords+"%").find(GuokeCache.class);
        Observable.from(list2)
                .subscribe(guokeCache -> {
                    GuokeHandpickNews.Result result = gson.fromJson(guokeCache.getGuoke_news(), GuokeHandpickNews.Result.class);
                    guokeList.add(result);
                    types.add(TYPE_GUOKE_NORMAL);
                });


        types.add(TYPE_DOUBAN_WITH_HEADER);
        List<DoubanCache> list3 = DataSupport.where("bookmark = 1 and douban_news like ?", "%"+queryWords+"%").find(DoubanCache.class);
        Observable.from(list3)
                .subscribe(doubanCache -> {
                    DoubanMomentNews.Posts posts = gson.fromJson(doubanCache.getDouban_news(), DoubanMomentNews.Posts.class);
                    doubanList.add(posts);
                    types.add(TYPE_DOUBAN_NORMAL);
                });
        view.showResults(zhihuList, guokeList, doubanList, types);
    }

    @Override
    public void startReading(BeanType type, int position) {
        Intent intent = new Intent(context, DetailActivity.class);
        switch (type) {
            case TYPE_ZHIHU:
                ZhihuDailyNews.Question q = zhihuList.get(position - 1);
                intent.putExtra("type", BeanType.TYPE_ZHIHU);
                intent.putExtra("id",q.getId());
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
                if (p.getThumbs().size() == 0){
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
}
