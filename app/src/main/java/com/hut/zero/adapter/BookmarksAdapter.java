package com.hut.zero.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hut.zero.BR;
import com.hut.zero.R;
import com.hut.zero.bean.DoubanMomentNews;
import com.hut.zero.bean.GuokeHandpickNews;
import com.hut.zero.bean.ZhihuDailyNews;
import com.hut.zero.other.OnRecyclerViewOnClickListener;
import com.hut.zero.other.BaseViewHolder;
import com.hut.zero.other.WithListenerViewHolder;

import java.util.List;

/**
 * Created by Zero on 2017/4/12.
 */

public class BookmarksAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;

    public static final int TYPE_ZHIHU_NORMAL = 0;
    public static final int TYPE_ZHIHU_WITH_HEADER = 1;
    public static final int TYPE_GUOKE_NORMAL = 2;
    public static final int TYPE_GUOKE_WITH_HEADER = 3;
    public static final int TYPE_DOUBAN_NORMAL = 4;
    public static final int TYPE_DOUBAN_WITH_HEADER = 5;

    private List<DoubanMomentNews.Posts> doubanList;
    private List<GuokeHandpickNews.Result> guokeList;
    private List<ZhihuDailyNews.Question> zhihuList;

    private OnRecyclerViewOnClickListener listener;

    private List<Integer> types; // to store which type the item is.

    public BookmarksAdapter(@NonNull Context context, List<DoubanMomentNews.Posts> doubanList, List<GuokeHandpickNews.Result> guokeList, List<ZhihuDailyNews.Question> zhihuList, List<Integer> types) {
        this.context=context;
        this.doubanList = doubanList;
        this.guokeList = guokeList;
        this.zhihuList = zhihuList;
        this.types = types;//types.size = zhihuList.size + guokrList.size + doubanList.size
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ZHIHU_NORMAL:
            case TYPE_GUOKE_NORMAL:
            case TYPE_DOUBAN_NORMAL:
                return new WithListenerViewHolder(
                        DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.home_list_item_layout, parent, false)
                        , this.listener);
            default:break;
        }
        return new BaseViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.bookmark_header, parent, false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        switch (types.get(position)) {
            case TYPE_ZHIHU_WITH_HEADER:
                holder.getBinding().setVariable(BR.viewType,context.getResources().getString(R.string.zhihu_daily));
                break;
            case TYPE_ZHIHU_NORMAL:
                if (!zhihuList.isEmpty()) {
                    ZhihuDailyNews.Question question = zhihuList.get(position - 1);
                    holder.getBinding().setVariable(BR.imgUrl, question.getImages().get(0));
                    holder.getBinding().setVariable(BR.title, question.getTitle());
                }
                break;
            case TYPE_GUOKE_WITH_HEADER:
                holder.getBinding().setVariable(BR.viewType,context.getResources().getString(R.string.guokr_handpick));
                break;
            case TYPE_GUOKE_NORMAL:
                if (!guokeList.isEmpty()) {
                    GuokeHandpickNews.Result result = guokeList.get(position - zhihuList.size() - 2);
                    holder.getBinding().setVariable(BR.imgUrl, result.getHeadline_img_tb());
                    holder.getBinding().setVariable(BR.title, result.getTitle());
                }
                break;
            case TYPE_DOUBAN_WITH_HEADER:
                holder.getBinding().setVariable(BR.viewType,context.getResources().getString(R.string.douban_moment));
                break;
            case TYPE_DOUBAN_NORMAL:
                if (!doubanList.isEmpty()){
                    DoubanMomentNews.Posts posts=doubanList.get(position - zhihuList.size() - guokeList.size() - 3);
                    if (posts.getThumbs().size()==0) {
                        holder.getBinding().getRoot().findViewById(R.id.imageViewCover).setVisibility(View.INVISIBLE);
                    } else {
                        holder.getBinding().setVariable(BR.imgUrl, posts.getThumbs().get(0).getMedium().getUrl());
                    }
                    holder.getBinding().setVariable(BR.title, posts.getTitle());
                }
                break;
            default:break;
        }
    }

    public void setItemListener(OnRecyclerViewOnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return types.get(position);
    }

    @Override
    public int getItemCount() {
        return types.size();
    }
}
