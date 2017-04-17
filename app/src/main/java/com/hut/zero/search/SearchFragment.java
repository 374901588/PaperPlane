package com.hut.zero.search;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.hut.zero.R;
import com.hut.zero.adapter.BookmarksAdapter;
import com.hut.zero.bean.BeanType;
import com.hut.zero.bean.DoubanMomentNews;
import com.hut.zero.bean.GuokeHandpickNews;
import com.hut.zero.bean.ZhihuDailyNews;
import com.hut.zero.databinding.FragmentSearchBookmarksBinding;

import java.util.ArrayList;

/**
 * Created by Zero on 2017/4/16.
 */

public class SearchFragment extends Fragment implements SearchContract.View {
    private SearchContract.Presenter presenter;

    private FragmentSearchBookmarksBinding mBinding;

    private BookmarksAdapter adapter;


    public SearchFragment() {
    }
    
    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_bookmarks, container, false);

        setHasOptionsMenu(true);

        initViews();

        presenter.loadResults("");

        return mBinding.getRoot();
    }

    @Override
    public void setPresenter(SearchContract.Presenter presenter) {
        if (presenter != null) {
            this.presenter = presenter;
        }
    }

    @Override
    public void initViews() {
        ((SearchActivity)(getActivity())).setSupportActionBar(mBinding.toolbar);
        ((SearchActivity)(getActivity())).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBinding.searchView.setIconified(false);

        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //TODO 实现拼音也能匹配
        mBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                presenter.loadResults(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                presenter.loadResults(newText);
                return false;
            }
        });
    }

    @Override
    public void showResults(ArrayList<ZhihuDailyNews.Question> zhihuList, ArrayList<GuokeHandpickNews.Result> guokeList, ArrayList<DoubanMomentNews.Posts> doubanList, ArrayList<Integer> types) {
        if (adapter == null) {
            adapter = new BookmarksAdapter(getActivity(), doubanList, guokeList, zhihuList, types);
            adapter.setItemListener((v, position) -> {
                int type = mBinding.recyclerView.findViewHolderForLayoutPosition(position).getItemViewType();
                if (type == BookmarksAdapter.TYPE_ZHIHU_NORMAL) {
                    presenter.startReading(BeanType.TYPE_ZHIHU, position);
                } else if (type == BookmarksAdapter.TYPE_GUOKE_NORMAL) {
                    presenter.startReading(BeanType.TYPE_GUOKE, position);
                } else if (type == BookmarksAdapter.TYPE_DOUBAN_NORMAL) {
                    presenter.startReading(BeanType.TYPE_DOUBAN, position);
                }
            });
            mBinding.recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showLoading() {
        mBinding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopLoading() {
        mBinding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
