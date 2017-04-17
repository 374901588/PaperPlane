package com.hut.zero.homepage;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hut.zero.R;
import com.hut.zero.adapter.DoubanMomentAdapter;
import com.hut.zero.bean.DoubanMomentNews;
import com.hut.zero.databinding.FragmentListBinding;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Zero on 2017/4/2.
 */

public class DoubanMomentFragment extends Fragment implements DoubanMomentContract.View {
    private FragmentListBinding mBinding;

    private FloatingActionButton fab;

    private DoubanMomentAdapter mAdapter;
    private DoubanMomentContract.Presenter mPresenter;

    private int mYear = Calendar.getInstance().get(Calendar.YEAR);
    private int mMonth = Calendar.getInstance().get(Calendar.MONTH);
    private int mDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

    public DoubanMomentFragment() {}

    public static DoubanMomentFragment newInstance() {
        return new DoubanMomentFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false);
        initViews();
        mPresenter.start();
        return mBinding.getRoot();
    }

    @Override
    public void setPresenter(DoubanMomentContract.Presenter presenter) {
        if (presenter!=null)
            mPresenter = presenter;
    }

    @Override
    public void initViews() {
        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setRippleColor(getResources().getColor(R.color.colorPrimaryDark));

        mBinding.refreshLayout.setColorSchemeResources(R.color.colorPrimary);

        mBinding.refreshLayout.setOnRefreshListener(() -> mPresenter.loadPosts(Calendar.getInstance().getTimeInMillis(), true));

        mBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            boolean isSlidingToLast = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // 当不滚动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // 获取最后一个完全显示的itemosition
                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = manager.getItemCount();

                    // 判断是否滚动到底部并且是向下滑动
                    if (lastVisibleItem == (totalItemCount - 1) && isSlidingToLast) {
                        Calendar c = Calendar.getInstance();
                        c.set(mYear, mMonth, --mDay);
                        mPresenter.loadMore(c.getTimeInMillis());
                    }
                }

                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                isSlidingToLast = dy > 0; //dy>0表示向下

                // 隐藏或者显示fab
                if(dy > 20 || dy < -20) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }
        });
    }

    @Override
    public void startLoading() {
        mBinding.refreshLayout.setRefreshing(true);
    }

    @Override
    public void stopLoading() {
        mBinding.refreshLayout.setRefreshing(false);
    }

    @Override
    public void showLoadingError() {
        Snackbar.make(fab, R.string.loaded_failed,Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, v -> mPresenter.refresh()).show();
    }

    @Override
    public void showResults(ArrayList<DoubanMomentNews.Posts> list) {
        if (mAdapter == null) {
            mAdapter = new DoubanMomentAdapter(list);
            mAdapter.setItemClickListener((v, position) -> mPresenter.startReading(position));
            mBinding.recyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void showPickDialog() {
        Calendar now = Calendar.getInstance();
        now.set(mYear, mMonth, mDay);
        DatePickerDialog dialog = DatePickerDialog.newInstance((view, year, monthOfYear, dayOfMonth) -> {
            Calendar temp = Calendar.getInstance();
            temp.clear();
            temp.set(year, monthOfYear, dayOfMonth);
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            mPresenter.loadPosts(temp.getTimeInMillis(), true);
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));

        dialog.setMaxDate(Calendar.getInstance());
        Calendar minDate = Calendar.getInstance();
        minDate.set(2014, 5, 12);
        dialog.setMinDate(minDate);
        // set the dialog not vibrate when date change, default value is true
        dialog.vibrate(false);

        dialog.show(getActivity().getFragmentManager(), "DatePickerDialog");
    }
}
