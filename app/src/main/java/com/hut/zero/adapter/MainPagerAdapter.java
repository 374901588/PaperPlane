package com.hut.zero.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hut.zero.R;
import com.hut.zero.homepage.DoubanMomentFragment;
import com.hut.zero.homepage.GuokeHandpickFragment;
import com.hut.zero.homepage.ZhihuDailyFragment;

/**
 * Created by Zero on 2017/4/2.
 */

public class MainPagerAdapter extends FragmentPagerAdapter {
    private String[] mTitles;

    private ZhihuDailyFragment zhihuDailyFragment;
    private GuokeHandpickFragment guokeHandpickFragment;
    private DoubanMomentFragment doubanMomentFragment;

    public MainPagerAdapter(FragmentManager fm, Context context, ZhihuDailyFragment zhihuDailyFragment, GuokeHandpickFragment guokeHandpickFragment, DoubanMomentFragment doubanMomentFragment) {
        super(fm);

        mTitles = new String[]{context.getResources().getString(R.string.zhihu_daily),
                context.getResources().getString(R.string.guokr_handpick),
                context.getResources().getString(R.string.douban_moment)};

        this.zhihuDailyFragment=zhihuDailyFragment;
        this.doubanMomentFragment=doubanMomentFragment;
        this.guokeHandpickFragment=guokeHandpickFragment;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:return guokeHandpickFragment;
            case 2:return doubanMomentFragment;
            default:return zhihuDailyFragment;
        }
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    public ZhihuDailyFragment getZhihuDailyFragment() {
        return zhihuDailyFragment;
    }

    public GuokeHandpickFragment getGuokeHandpickFragment() {
        return guokeHandpickFragment;
    }

    public DoubanMomentFragment getDoubanMomentFragment() {
        return doubanMomentFragment;
    }
}
