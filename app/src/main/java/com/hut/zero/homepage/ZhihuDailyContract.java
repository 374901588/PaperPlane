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

package com.hut.zero.homepage;

import com.hut.zero.BasePresenter;
import com.hut.zero.BaseView;
import com.hut.zero.bean.ZhihuDailyNews;

import java.util.ArrayList;

/**
 * Created by Lizhaotailang on 2016/9/16.
 */

public interface ZhihuDailyContract {

    interface View extends BaseView<Presenter> {

        // 显示加载或其他类型的错误
        void showError();

        // 显示正在加载
        void showLoading();

        // 停止显示正在加载
        void stopLoading();

        // 成功获取到数据后，在界面中显示
        void showResults(ArrayList<ZhihuDailyNews.Question> list);

        void showPickDialog();
    }

    interface Presenter extends BasePresenter {

        // 请求数据
        void loadPosts(long date, boolean clearing);

        // 刷新数据
        void refresh();

        // 加载更多文章
        void loadMore(long date);

        // 显示详情
        void startReading(int position);

        // 随便看看
        void feelLucky();

    }
}
