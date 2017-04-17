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

package com.hut.zero.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hut.zero.BR;
import com.hut.zero.R;
import com.hut.zero.bean.GuokeHandpickNews;
import com.hut.zero.other.OnRecyclerViewOnClickListener;
import com.hut.zero.other.BaseViewHolder;
import com.hut.zero.other.WithListenerViewHolder;

import java.util.List;

/**
 * Created by lizhaotailang on 2016/6/14.
 */
public class GuokeNewsAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private OnRecyclerViewOnClickListener mListener;

    private List<GuokeHandpickNews.Result> data;


    public GuokeNewsAdapter(List<GuokeHandpickNews.Result> list) {
        data=list;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WithListenerViewHolder(
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.home_list_item_layout, parent, false)
                ,mListener);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        GuokeHandpickNews.Result item=data.get(position);

        holder.getBinding().setVariable(BR.imgUrl,item.getHeadline_img_tb());
        holder.getBinding().setVariable(BR.title,item.getTitle());
    }

    public void setItemClickListener(OnRecyclerViewOnClickListener listener){
        this.mListener = listener;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private class GuokePostViewHolder extends WithListenerViewHolder {
        public GuokePostViewHolder(ViewDataBinding binding, OnRecyclerViewOnClickListener listener) {
            super(binding,listener);
        }
    }
}
