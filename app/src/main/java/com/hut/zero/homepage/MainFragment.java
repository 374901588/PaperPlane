package com.hut.zero.homepage;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.hut.zero.R;
import com.hut.zero.adapter.MainPagerAdapter;
import com.hut.zero.databinding.FragmentMainBinding;

import java.util.Random;

/**
 * Created by Zero on 2017/4/2.
 */

public class MainFragment extends Fragment {
    private Context mContext;
    private MainPagerAdapter mAdapter;

    private FragmentMainBinding mBinding;

    private ZhihuDailyFragment zhihuDailyFragment;
    private GuokeHandpickFragment guokeHandpickFragment;
    private DoubanMomentFragment doubanMomentFragment;

    private ZhihuDailyPresenter zhihuDailyPresenter;
    private GuokeHandpickPresenter guokeHandpickPresenter;
    private DoubanMomentPresenter doubanMomentPresenter;

    public MainFragment() {
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();

        // Fragment状态恢复
        if (savedInstanceState != null) {
            FragmentManager manager = getChildFragmentManager();
            zhihuDailyFragment = (ZhihuDailyFragment) manager.getFragment(savedInstanceState, "zhihu");
            guokeHandpickFragment = (GuokeHandpickFragment) manager.getFragment(savedInstanceState, "guoke");
            doubanMomentFragment = (DoubanMomentFragment) manager.getFragment(savedInstanceState, "douban");
        } else {
            // 创建View实例
            zhihuDailyFragment = ZhihuDailyFragment.newInstance();
            guokeHandpickFragment = GuokeHandpickFragment.newInstance();
            doubanMomentFragment = DoubanMomentFragment.newInstance();
        }

        //创建Presenter实例,在Presenter实例中会对传入的View设置Presenter(即view.setPresenter(this))
        //但是在view(即XXXFragment)中onCreateView时会进行mPresenter.start()操作，此时不会报空指针异常
        //是因为mPresenter.start()的执行顺序在view.setPresenter(this)之后
        //这里是由于Presenter实例是紧接着View的实例之后创建的
        //因此在其他情况下，需要考虑一下实际情况，防止空指针异常
        zhihuDailyPresenter = new ZhihuDailyPresenter(mContext, zhihuDailyFragment);
        guokeHandpickPresenter = new GuokeHandpickPresenter(mContext, guokeHandpickFragment);
        doubanMomentPresenter = new DoubanMomentPresenter(mContext, doubanMomentFragment);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);

        init();

        //对于MainFragment建立起与OptionsMenu的联系（即右上角的"随机看看"图标）
        //然后还需要在该Fragment中初始化Menu与其点击事件
        setHasOptionsMenu(true);

        return mBinding.getRoot();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FragmentManager manager = getChildFragmentManager();
        manager.putFragment(outState, "zhihu", zhihuDailyFragment);
        manager.putFragment(outState, "guoke", guokeHandpickFragment);
        manager.putFragment(outState, "douban", doubanMomentFragment);
    }

    private void init() {
        mBinding.viewPager.setOffscreenPageLimit(3);
        mAdapter = new MainPagerAdapter(getChildFragmentManager(), mContext, zhihuDailyFragment, guokeHandpickFragment, doubanMomentFragment);
        mBinding.viewPager.setAdapter(mAdapter);
        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager);
        // 当tab layout位置为果壳精选时，隐藏fab
        mBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition()==1) mBinding.fab.hide();
                else mBinding.fab.show();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        //原来是在zhihuDailyFragment中绑定点击事件，现在直接放到MainActivity中，因为fab是属于activity的view
        //根据tab layout的位置选择显示不同的dialog
        mBinding.fab.setOnClickListener(v -> {
            switch (mBinding.tabLayout.getSelectedTabPosition()) {
                case 0:zhihuDailyFragment.showPickDialog();break;
                case 2:doubanMomentFragment.showPickDialog();break;
                default:break;
            }
        });

    }

    //设置Menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_feel_lucky) {
            feelLucky();
        }
        return true;
    }

    private void feelLucky() {
        int type=new Random().nextInt(3);
        switch (type) {
            case 1:zhihuDailyPresenter.feelLucky();break;
            case 2:guokeHandpickPresenter.feelLucky();break;
            case 3:doubanMomentPresenter.feelLucky();break;
        }
    }
}
