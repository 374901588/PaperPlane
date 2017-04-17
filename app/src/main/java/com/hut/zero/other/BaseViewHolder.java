package com.hut.zero.other;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Zero on 2017/4/3.
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {
    private final ViewDataBinding binding;


    public BaseViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding=binding;
    }

    public ViewDataBinding getBinding() {
        return binding;
    }
}
