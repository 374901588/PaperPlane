package com.hut.zero;

/**
 * Created by Zero on 2017/3/19.
 */

public interface BaseView<T> {
    // 为View设置Presenter
    void setPresenter(T presenter);
    // 初始化界面控件
    void initViews();
}
