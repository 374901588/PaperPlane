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

package com.hut.zero.bookmarks;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.hut.zero.R;
import com.hut.zero.adapter.BookmarksAdapter;
import com.hut.zero.bean.BeanType;
import com.hut.zero.bean.DoubanMomentNews;
import com.hut.zero.bean.GuokeHandpickNews;
import com.hut.zero.bean.ZhihuDailyNews;
import com.hut.zero.databinding.FragmentListBinding;
import com.hut.zero.search.SearchActivity;

import java.util.ArrayList;

/**
 * Created by lizhaotailang on 2016/12/20.
 */

public class BookmarksFragment extends Fragment implements BookmarksContract.View {
    private FragmentListBinding mBinding;

    private BookmarksContract.Presenter presenter;

    private BookmarksAdapter adapter;

    //增加一个判断标志
    //因为如果从收藏界面进入文章详细界面，把该文章的收藏取消，再返回收藏界面
    //如果不加判断标志，则需要手动刷新才能去掉前文取消收藏的文章item
    //将在onCreateView与返回收藏界面时的presenter.loadResults逻辑统一放到onResume中
    private boolean isOnCreatedView = false;

    public BookmarksFragment() {
    }

    public static BookmarksFragment newInstance() {
        return new BookmarksFragment();
    }

    @Override
    public void setPresenter(BookmarksContract.Presenter presenter) {
        if (presenter != null) this.presenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false);

        initViews();

        setHasOptionsMenu(true);

        return mBinding.getRoot();
    }

    @Override
    public void initViews() {
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBinding.refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mBinding.refreshLayout.setOnRefreshListener(() -> presenter.loadResults(true));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_bookmarks, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                startActivity(new Intent(getActivity(), SearchActivity.class));
                break;
            case R.id.action_feel_lucky:
                presenter.feelLucky();
                break;
        }
        return true;
    }

    @Override
    public void showResults(ArrayList<ZhihuDailyNews.Question> zhihuList, ArrayList<GuokeHandpickNews.Result> guokeList, ArrayList<DoubanMomentNews.Posts> doubanList, ArrayList<Integer> types) {
        if (adapter == null) {
            adapter = new BookmarksAdapter(getActivity(), doubanList, guokeList, zhihuList, types);
            adapter.setItemListener((v, position) -> {
                int type = mBinding.recyclerView.findViewHolderForLayoutPosition(position).getItemViewType();

                BeanType beanType = null;
                switch (type) {
                    case BookmarksAdapter.TYPE_ZHIHU_NORMAL:
                        beanType = BeanType.TYPE_ZHIHU;
                        break;
                    case BookmarksAdapter.TYPE_GUOKE_NORMAL:
                        beanType = BeanType.TYPE_GUOKE;
                        break;
                    case BookmarksAdapter.TYPE_DOUBAN_NORMAL:
                        beanType = BeanType.TYPE_DOUBAN;
                        break;
                    default:
                        break;
                }
                presenter.startReading(beanType, position);
            });
            mBinding.recyclerView.setAdapter(adapter);
        } else
            adapter.notifyDataSetChanged();
    }

    @Override
    public void notifyDataChanged() {
        presenter.loadResults(true);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isOnCreatedView) {
            presenter.loadResults(false);
            isOnCreatedView = true;
        } else
            presenter.loadResults(true);
    }

    @Override
    public void showLoading() {
        mBinding.refreshLayout.setRefreshing(true);
    }

    @Override
    public void stopLoading() {
        mBinding.refreshLayout.setRefreshing(false);
    }
}
