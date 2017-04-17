package com.hut.zero.other;

import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hut.zero.R;

/**
 * Created by Zero on 2017/4/3.
 */

public class DataBindingCustomAttr {
    public static final String NO_LOAD="NO_LOAD";

    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView view, String url) {
        //在设置imageUrl的值之前，会存在null的情况，所以需要加上判空的操作
        if (TextUtils.isEmpty(url)||url.equals(NO_LOAD)) view.setImageResource(R.drawable.placeholder);
        else Glide.with(view.getContext()).load(url)
                .asBitmap()
                .placeholder(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .error(R.drawable.placeholder)
                .centerCrop()
                .into(view);
    }
}
