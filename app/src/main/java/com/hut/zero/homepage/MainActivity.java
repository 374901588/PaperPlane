package com.hut.zero.homepage;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.hut.zero.R;
import com.hut.zero.other_pages.AboutPreferenceActivity;
import com.hut.zero.bookmarks.BookmarksFragment;
import com.hut.zero.bookmarks.BookmarksPresenter;
import com.hut.zero.databinding.ActivityMain2Binding;
import com.hut.zero.service.CacheService;
import com.hut.zero.other_pages.SettingsPreferenceActivity;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMain2Binding mBinding;

    private MainFragment mainFragment;
    private BookmarksFragment bookmarksFragment;

    public static final String ACTION_BOOKMARKS = "com.hut.zero.bookmarks";

    //因为在切换主题时会recreate,如果不添加一个判断标志，每次切换主题后都直接显示mainFragment,即使是在显示BookmarkFragment时切换主题
    private int currentFragment;
    private static final int CURRENT_FRAG_MAIN=1;
    private static final int CURRENT_FRAG_BOOKMARK=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_main2);

        init();

        // 启动服务
        startService(new Intent(this, CacheService.class));

        // 恢复fragment的状态
        if (savedInstanceState != null) {
            mainFragment = (MainFragment) getSupportFragmentManager().getFragment(savedInstanceState, "MainFragment");
            bookmarksFragment = (BookmarksFragment) getSupportFragmentManager().getFragment(savedInstanceState, "BookmarksFragment");

            currentFragment = savedInstanceState.getInt("CURRENT_FRAG", CURRENT_FRAG_MAIN);
        } else {
            mainFragment = MainFragment.newInstance();
            bookmarksFragment = BookmarksFragment.newInstance();

            //默认显示MainFragment
            currentFragment = CURRENT_FRAG_MAIN;
        }

        if (!mainFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().add(R.id.layout_fragment, mainFragment, "MainFragment").commit();
        }

        if (!bookmarksFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().add(R.id.layout_fragment, bookmarksFragment, "BookmarksFragment").commit();
        }

        // 实例化BookmarksPresenter
        new BookmarksPresenter(MainActivity.this, bookmarksFragment);

        switch (currentFragment) {
            case CURRENT_FRAG_MAIN:
                showMainFragment();
                mBinding.navView.setCheckedItem(R.id.nav_home);
                break;
            case CURRENT_FRAG_BOOKMARK:
                showBookmarksFragment();
                mBinding.navView.setCheckedItem(R.id.nav_bookmarks);
                break;
            default:break;
        }
    }

    private void init() {
        setSupportActionBar(mBinding.toolbar);

        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(
                this,mBinding.drawerLayout,mBinding.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        mBinding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mBinding.navView.setNavigationItemSelectedListener(this);
    }

    // 显示MainFragment并设置Title
    private void showMainFragment() {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.show(mainFragment);
        fragmentTransaction.hide(bookmarksFragment);
        fragmentTransaction.commit();

        mBinding.toolbar.setTitle(getResources().getString(R.string.app_name));
        currentFragment = CURRENT_FRAG_MAIN;
    }

    // 显示BookmarksFragment并设置Title
    private void showBookmarksFragment() {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.show(bookmarksFragment);
        fragmentTransaction.hide(mainFragment);
        fragmentTransaction.commit();

        mBinding.toolbar.setTitle(getResources().getString(R.string.nav_bookmarks));

        //如果不加currentFragment!=CURRENT_FRAG_BOOKMARK的条件
        //如果是在显示BookmarkFragment的时候切换主题,此时currentFragment==CURRENT_FRAG_BOOKMARK
        //如果执行bookmarksFragment.notifyDataChanged()从而在执行presenter.loadResults(true)
        //此时会导致BookmarksFragment中的recyclerView和refreshLayout出现空指针异常
        //从逻辑上说,在切换主题后,由于BookmarksFragment中收藏的内容没变，所以本就不需要重新loadResults
        if (bookmarksFragment.isAdded()&&currentFragment!=CURRENT_FRAG_BOOKMARK) {
            bookmarksFragment.notifyDataChanged();
        }
        currentFragment = CURRENT_FRAG_BOOKMARK;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mBinding.drawerLayout.closeDrawer(GravityCompat.START);

        switch (item.getItemId()) {
            case R.id.nav_home:showMainFragment();break;
            case R.id.nav_bookmarks:showBookmarksFragment();break;
            case R.id.nav_change_theme:
                mBinding.drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {}

                    @Override
                    public void onDrawerOpened(View drawerView) {}

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        //切换主题
                        SharedPreferences sp =  getSharedPreferences(SettingsPreferenceActivity.SETTINGS_CONFIG_FILE_NAME,MODE_PRIVATE);
                        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                                == Configuration.UI_MODE_NIGHT_YES) {
                            sp.edit().putInt("theme", 0).apply();
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        } else {
                            sp.edit().putInt("theme", 1).apply();
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        }
                        //设置WindowAnimations淡入淡出
                        getWindow().setWindowAnimations(R.style.WindowAnimationFadeInOut);
                        MainActivity.this.recreate();//重新创建mainActivity，一些状态会重置，比如开始recyclerView滑倒了中间，重建之后会从顶部开始
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                    }
                });
                break;
            case R.id.nav_settings:startActivity(new Intent(this, SettingsPreferenceActivity.class));break;
            case R.id.nav_about:startActivity(new Intent(this, AboutPreferenceActivity.class));break;
        }
        return true;
    }

    // 存储Fragment的状态
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mainFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, "MainFragment", mainFragment);
        }
        if (bookmarksFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, "BookmarksFragment", bookmarksFragment);
        }
        outState.putInt("CURRENT_FRAG",currentFragment);
    }

    @Override
    protected void onDestroy() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        //暂时不能使用java8中与stream()相关的重构，因为JDk版本不对应
        manager.getRunningServices(Integer.MAX_VALUE).stream().filter(service -> CacheService.class.getName().equals(service.service.getClassName())).forEach(service -> {
            stopService(new Intent(this, CacheService.class));
        });
        super.onDestroy();
    }
}
