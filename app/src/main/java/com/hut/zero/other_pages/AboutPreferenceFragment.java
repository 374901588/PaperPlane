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

package com.hut.zero.other_pages;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.Toolbar;

import com.hut.zero.R;
import com.hut.zero.customtabs.CustomFallback;
import com.hut.zero.customtabs.CustomTabActivityHelper;

/**
 * Created by lizhaotailang on 2016/7/26.
 */

public class AboutPreferenceFragment extends PreferenceFragmentCompat {

    private Toolbar toolbar;

    private SharedPreferences sp;

    private CustomTabsIntent.Builder customTabsIntent;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        addPreferencesFromResource(R.xml.about_preference_fragment);

        init();

        findPreference("follow_me_on_github_").setOnPreferenceClickListener(preference -> {
            returnUrl(R.string.me_github_url);
            return true;
        });

        findPreference("open_source_license").setOnPreferenceClickListener(preference -> {
            getActivity().startActivity(new Intent(getActivity(), OpenSourceLicenseActivity.class));
            return true;
        });

        findPreference("follow_he_on_github").setOnPreferenceClickListener(preference -> {
            returnUrl(R.string.github_url);
            return true;
        });

        findPreference("feedback").setOnPreferenceClickListener(preference -> {
            returnUrl(R.string.feedback_uri);
            return true;
        });

        findPreference("coffee").setOnPreferenceClickListener(preference -> {
            AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
            dialog.setTitle(R.string.donate);
            dialog.setMessage(getContext().getString(R.string.donate_content));
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, getContext().getString(R.string.positive), (dialogInterface, i) -> {
                // 将指定账号添加到剪切板
                ClipboardManager manager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", getContext().getString(R.string.donate_account));
                manager.setPrimaryClip(clipData);
            });
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getContext().getString(R.string.negative), (dialogInterface, i) -> {
            });
            dialog.show();
            return true;
        });

        findPreference("author_imitate").setOnPreferenceClickListener(preference -> {
            /**
             * System.arraycopy()实现数组之间的复制
             * 有趣的是这个函数可以实现自己到自己复制，比如：
             int[] fun ={0,1,2,3,4,5,6};
             System.arraycopy(fun,0,fun,3,3);
             则结果为：{0,1,2,0,1,2,6};
             实现过程是这样的，先生成一个长度为length的临时数组,将fun数组中srcPos
             到srcPos+length-1之间的数据拷贝到临时数组中，再执行System.arraycopy(临时数组,0,fun,3,3).
             */
            System.arraycopy(hits,1,hits,0,hits.length-1);
            hits[hits.length - 1] = SystemClock.uptimeMillis();
            //能够实现n次点击事件，我们需要定义一个n长度的数组，每点击一次将数组里的内容按序号整体向左移动一格，
            //然后给n-1出即数组的最后添加当前的时间，如果0个位置的时间大于当前时间减去500毫秒的话，那么证明在500毫秒内点击了n次
            if (hits[0] >= (SystemClock.uptimeMillis() - 500)) {
                AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
                dialog.setCancelable(false);
                dialog.setTitle(R.string.easter_egg);
                dialog.setMessage(getContext().getString(R.string.easter_egg_content));
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, getActivity().getString(R.string.yes), (dialogInterface, i) -> {});
                dialog.show();
            }
            return true;
        });

    }

    long[] hits = new long[3];

    private void returnUrl(int strId) {
        if (sp.getBoolean("in_app_browser", true)) {
            CustomTabActivityHelper.openCustomTab(
                    getActivity(),
                    customTabsIntent.build(),
                    Uri.parse(getContext().getString(strId)),
                    new CustomFallback() {
                        @Override
                        public void openUri(Activity activity, Uri uri) {
                            super.openUri(activity, uri);
                        }
                    });
        } else {
            try {
                getActivity().startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(getContext().getString(strId))));
            } catch (android.content.ActivityNotFoundException ex) {
                showBrowserNotFoundError();
            }
        }
    }

    // some problems occur when set support action bar
    // with PreferenceFragmentCompat
    // setting display home as up enable can not work either
    // so work it in activity directly
    public void init() {
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        sp = getPreferenceManager().getSharedPreferences();
        customTabsIntent = new CustomTabsIntent.Builder();
        customTabsIntent.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        customTabsIntent.setShowTitle(true);
    }


    public void showBrowserNotFoundError() {
        Snackbar.make(toolbar, R.string.no_browser_found, Snackbar.LENGTH_SHORT).show();
    }

}
