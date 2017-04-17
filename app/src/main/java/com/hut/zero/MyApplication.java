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

package com.hut.zero;

import android.support.v7.app.AppCompatDelegate;

import com.hut.zero.other_pages.SettingsPreferenceActivity;

/**
 * Created by Lizhaotailang on 2016/8/23.
 */

public class MyApplication extends org.litepal.LitePalApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        // the 'theme' has two values, 0 and 1
        AppCompatDelegate.setDefaultNightMode(
                getSharedPreferences(SettingsPreferenceActivity.SETTINGS_CONFIG_FILE_NAME,MODE_PRIVATE).getInt("theme", 0) == 0 ?
                        AppCompatDelegate.MODE_NIGHT_NO:
                        AppCompatDelegate.MODE_NIGHT_YES
        );
    }

}
