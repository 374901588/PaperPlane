package com.hut.zero.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hut.zero.BR;
import com.hut.zero.R;
import com.hut.zero.bean.ZhihuDailyNews;
import com.hut.zero.other.OnRecyclerViewOnClickListener;
import com.hut.zero.other.BaseViewHolder;
import com.hut.zero.other.DataBindingCustomAttr;
import com.hut.zero.other.WithListenerViewHolder;

import java.util.List;

/**
 * Created by Zero on 2017/4/3.
 */

public class ZhihuDailyNewsAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_FOOTER = 1;

    private OnRecyclerViewOnClickListener mListener;

    private List<ZhihuDailyNews.Question> data;

    public ZhihuDailyNewsAdapter(List<ZhihuDailyNews.Question> data) {
        this.data = data;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_NORMAL:
                return new WithListenerViewHolder(
                        DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.home_list_item_layout, parent, false),
                        mListener);
            case TYPE_FOOTER:
                return new BaseViewHolder(
                        DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.home_list_item_layout, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof WithListenerViewHolder) {//holder instanceof NormalViewHolder
            ZhihuDailyNews.Question item = data.get(position);

            //DataBindingCustomAttr.DataBindingCustomAttr()
            holder.getBinding().setVariable(BR.imgUrl,(item.getImages().get(0) == null? DataBindingCustomAttr.NO_LOAD:item.getImages().get(0)));
            holder.getBinding().setVariable(BR.title,item.getTitle());
        }
    }

    // 因为含有footer，返回值需要 + 1
    @Override
    public int getItemCount() {
        return data.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        return position==data.size()?TYPE_FOOTER:TYPE_NORMAL;
    }

    public void setItemClickListener(OnRecyclerViewOnClickListener listener) {
        mListener=listener;
    }

    private class NormalViewHolder extends WithListenerViewHolder {
        NormalViewHolder(ViewDataBinding binding, OnRecyclerViewOnClickListener listener) {
            super(binding, listener);
        }
    }

    private class FooterViewHolder extends BaseViewHolder {

        FooterViewHolder(ViewDataBinding binding) {
            super(binding);
        }
    }


    public List<ZhihuDailyNews.Question> getData() {
        return data;
    }

    public void setData(List<ZhihuDailyNews.Question> data) {
        this.data=data;
    }
}
