package com.hut.zero.other;

import android.databinding.ViewDataBinding;
import android.view.View;

/**
 * Created by Zero on 2017/4/5.
 */

public class WithListenerViewHolder extends BaseViewHolder implements View.OnClickListener {
    private final OnRecyclerViewOnClickListener listener;

    public WithListenerViewHolder(ViewDataBinding binding,OnRecyclerViewOnClickListener listener) {
        super(binding);
        this.listener=listener;
        binding.getRoot().setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (listener != null){
            listener.onItemClick(v,getLayoutPosition());
        }
    }
}
