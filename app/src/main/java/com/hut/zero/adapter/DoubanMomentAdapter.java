package com.hut.zero.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hut.zero.BR;
import com.hut.zero.R;
import com.hut.zero.bean.DoubanMomentNews;
import com.hut.zero.other.OnRecyclerViewOnClickListener;
import com.hut.zero.other.BaseViewHolder;
import com.hut.zero.other.WithListenerViewHolder;

import java.util.ArrayList;

/**
 * Created by Zero on 2017/4/5.
 */

public class DoubanMomentAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private OnRecyclerViewOnClickListener listener;

    private ArrayList<DoubanMomentNews.Posts> dataForPosts;

    private static final int TYPE_NORMAL = 0x00;
    private static final int TYPE_FOOTER = 0x02;
    private static final int TYPE_NO_IMG = 0x03;

    public DoubanMomentAdapter(ArrayList<DoubanMomentNews.Posts> list) {
        this.dataForPosts = list;
    }

    public void setItemClickListener(OnRecyclerViewOnClickListener listener) {
        this.listener=listener;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_NORMAL:
                return new WithListenerViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.home_list_item_layout, parent, false), listener);
            case TYPE_NO_IMG:
                return new NoImgViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.home_list_item_without_image, parent, false), listener);
            case TYPE_FOOTER:
                return new BaseViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.list_footer, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if ( holder instanceof WithListenerViewHolder) {
            DoubanMomentNews.Posts item = dataForPosts.get(position);
            if (! (holder instanceof NoImgViewHolder)) {
                holder.getBinding().setVariable(BR.imgUrl,item.getThumbs().get(0).getMedium().getUrl());
            }
            holder.getBinding().setVariable(BR.title,item.getTitle());
        }
    }

    @Override
    public int getItemCount() {
        return dataForPosts.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == dataForPosts.size()) {
            return TYPE_FOOTER;
        }
        if (dataForPosts.get(position).getThumbs().size() == 0) {
            return TYPE_NO_IMG;
        }
        return TYPE_NORMAL;
    }

    //因为TYPE_NORMAL、TYPE_NO_IMG实际上都是WithListenerViewHolder类型
    //但为了在onBindViewHolder用以区分来判断是否设置图片的url，所以这里单独包装出一个NoImgViewHolder类型
    private class NoImgViewHolder extends WithListenerViewHolder {

        NoImgViewHolder(ViewDataBinding binding, OnRecyclerViewOnClickListener listener) {
            super(binding, listener);
        }
    }
}
