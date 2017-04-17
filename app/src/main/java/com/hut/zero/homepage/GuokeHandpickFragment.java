package com.hut.zero.homepage;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hut.zero.R;
import com.hut.zero.adapter.GuokeNewsAdapter;
import com.hut.zero.bean.GuokeHandpickNews;
import com.hut.zero.databinding.FragmentListBinding;

import java.util.ArrayList;

/**
 * Created by Zero on 2017/4/2.
 */

public class GuokeHandpickFragment extends Fragment implements GuokeHandpickContract.View {
    private FragmentListBinding mBinding;

    private GuokeHandpickContract.Presenter mPresenter;

    private GuokeNewsAdapter mAdapter;

    public GuokeHandpickFragment(){}

    public static GuokeHandpickFragment newInstance() {
        return new GuokeHandpickFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding= DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false);
        initViews();
        mPresenter.start();
        return mBinding.getRoot();
    }

    @Override
    public void setPresenter(GuokeHandpickContract.Presenter presenter) {
        if (presenter!=null)
            mPresenter=presenter;
    }

    @Override
    public void initViews() {
        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mBinding.refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mBinding.refreshLayout.setOnRefreshListener(()->mPresenter.refresh());

    }

    @Override
    public void showError() {
        Snackbar.make(mBinding.refreshLayout, R.string.loaded_failed,Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, v -> mPresenter.refresh())
                .show();
    }

    @Override
    public void showResults(ArrayList<GuokeHandpickNews.Result> list) {
        if (mAdapter == null) {
            mAdapter = new GuokeNewsAdapter(list);
            mAdapter.setItemClickListener((v, position) -> mPresenter.startReading(position));
            mBinding.recyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
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
