package com.hut.zero.homepage;

import com.hut.zero.BasePresenter;
import com.hut.zero.BaseView;
import com.hut.zero.bean.GuokeHandpickNews;

import java.util.ArrayList;

/**
 * Created by Zero on 2017/4/2.
 */

public class GuokeHandpickContract {

    interface View extends BaseView<Presenter> {

        void showError();

        void showResults(ArrayList<GuokeHandpickNews.Result> list);

        void showLoading();

        void stopLoading();

    }

    interface Presenter extends BasePresenter {

        void loadPosts();

        void refresh();

        void startReading(int position);

        void feelLucky();

    }
}
