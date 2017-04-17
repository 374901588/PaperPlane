package com.hut.zero.other_pages;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.hut.zero.R;

/**
 * Created by Zero on 2017/4/11.
 */

public class SettingsPreferenceFragment extends PreferenceFragmentCompat {

    private Toolbar toolbar;

    private SharedPreferences sp;

    private Handler handler = new Handler();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings_preference_fragment);

        //通过PreferenceFragmentCompat或者PreferenceActivityCompat设置的参数，会自动在/data/data/<packagename>/shared_prefs/目录生成一个默认文件
        //通过getPreferenceManager().getSharedPreferences()得到相应的SharedPreferences引用
        sp = getPreferenceManager().getSharedPreferences();

        initViews();

        findPreference("clear_glide_cache").setOnPreferenceClickListener(preference -> {
            new Thread(() -> {
                Glide.get(getContext()).clearDiskCache();
                handler.post(SettingsPreferenceFragment.this::showCleanGlideCacheDone);
            }).start();
            Glide.get(getContext()).clearMemory();
            return true;
        });

        Preference timePreference=findPreference("time_of_saving_articles");
        timePreference.setSummary(getTimeSummary());
        timePreference.setOnPreferenceChangeListener((preference, newValue) -> {
            preference.setSummary(newValue.toString()+"天");
            return true;
        });
    }

    public void initViews() {
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
    }

    public void showCleanGlideCacheDone() {
        Snackbar.make(toolbar, R.string.clear_image_cache_successfully, Snackbar.LENGTH_SHORT).show();
    }

    public SettingsPreferenceFragment() {}

    public static SettingsPreferenceFragment newInstance() {
        return new SettingsPreferenceFragment();
    }

    public String getTimeSummary() {
        return sp.getString("time_of_saving_articles", "7")+"天";
    }
}
