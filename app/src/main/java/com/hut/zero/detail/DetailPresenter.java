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

package com.hut.zero.detail;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.text.Html;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hut.zero.R;
import com.hut.zero.bean.BeanType;
import com.hut.zero.bean.DoubanCache;
import com.hut.zero.bean.DoubanMomentNews;
import com.hut.zero.bean.DoubanMomentStory;
import com.hut.zero.bean.GuokeCache;
import com.hut.zero.bean.ZhihuCache;
import com.hut.zero.bean.ZhihuDailyStory;
import com.hut.zero.customtabs.CustomFallback;
import com.hut.zero.customtabs.CustomTabActivityHelper;
import com.hut.zero.model.DoubanModelImpl;
import com.hut.zero.model.GuokeModelImpl;
import com.hut.zero.model.ZhihuModelImpl;
import com.hut.zero.other_pages.SettingsPreferenceActivity;
import com.hut.zero.util.Api;
import com.hut.zero.util.NetworkState;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static org.litepal.crud.DataSupport.select;

/**
 * Created by lizhaotailang on 2016/12/27.
 */

public class DetailPresenter implements DetailContract.Presenter {

    private DetailContract.View view;
    private Context context;

    private ZhihuDailyStory zhihuDailyStory;
    private String guokeStory;
    private DoubanMomentStory doubanMomentStory;

    private Gson gson;

    // the four data come from the intent extra
    private BeanType type;
    private int id;
    private String title;
    private String coverUrl;

    private SharedPreferences sp;

    public void logMsg() {
        Log.d("测试-->", "type=" + type + ",id=" + id + ",title=" + title + ",coverUrl=" + coverUrl);
    }

    public void setType(BeanType type) {
        this.type = type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public DetailPresenter(Context context, DetailContract.View view) {
        this.view = view;
        view.setPresenter(this);
        this.context = context;
        sp = context.getSharedPreferences(SettingsPreferenceActivity.SETTINGS_CONFIG_FILE_NAME, MODE_PRIVATE);
        gson = new Gson();
    }


    @Override
    public void openInBrowser() {
        //因为在DetailPresenter展示详细内容时，是在一个WebView中展示
        //就相当于在内置浏览器中展示，因此当选择“在浏览器中打开”时，默认用外置浏览器
        //而不需要考虑在Chrome Custom Tabs中显示了
        if (checkNull()) {
            view.showLoadingError();
            return;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String uriStr = null;
            switch (type) {
                case TYPE_ZHIHU:
                    uriStr=zhihuDailyStory.getShare_url();
                    break;
                case TYPE_GUOKE:
                    uriStr= Api.GUOKE_ARTICLE_LINK_V1 + id;
                    break;
                case TYPE_DOUBAN:
                    uriStr = doubanMomentStory.getShort_url();
            }
            intent.setData(Uri.parse(uriStr));
            context.startActivity(intent);

        } catch (android.content.ActivityNotFoundException ex) {
            view.showBrowserNotFoundError();
        }
    }

    @Override
    public void shareAsText() {
        if (checkNull()) {
            view.showSharingError();
            return;
        }

        try {
            Intent shareIntent = new Intent().setAction(Intent.ACTION_SEND).setType("text/plain");
            String shareText = "" + title + " ";

            switch (type) {
                case TYPE_ZHIHU:
                    shareText += zhihuDailyStory.getShare_url();
                    break;
                case TYPE_GUOKE:
                    shareText += Api.GUOKE_ARTICLE_LINK_V1 + id;
                    break;
                case TYPE_DOUBAN:
                    shareText += doubanMomentStory.getShort_url();
            }

            shareText = shareText + "\t\t\t" + context.getString(R.string.share_extra);

            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_to)));
        } catch (android.content.ActivityNotFoundException ex) {
            view.showLoadingError();
        }
    }

    @Override
    public void openUrl(WebView webView, String url) {
        if (sp.getBoolean("in_app_browser", true)) {
            CustomTabsIntent.Builder customTabsIntent = new CustomTabsIntent.Builder()
                    .setToolbarColor(context.getResources().getColor(R.color.colorAccent))
                    .setShowTitle(true);
            CustomTabActivityHelper.openCustomTab(
                    (Activity) context,
                    customTabsIntent.build(),
                    Uri.parse(url),
                    new CustomFallback() {
                        @Override
                        public void openUri(Activity activity, Uri uri) {
                            super.openUri(activity, uri);
                        }
                    }
            );
        } else {

            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)));
            } catch (android.content.ActivityNotFoundException ex) {
                view.showBrowserNotFoundError();
            }

        }
    }

    @Override
    public void copyText() {
        if (checkNull()) {
            view.showCopyTextError();
            return;
        }

        ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = null;
        switch (type) {
            case TYPE_ZHIHU:
                clipData = ClipData.newPlainText("text", Html.fromHtml(title + "\n" + zhihuDailyStory.getBody()).toString());
                break;
            case TYPE_GUOKE:
                clipData = ClipData.newPlainText("text", Html.fromHtml(guokeStory).toString());
                break;
            case TYPE_DOUBAN:
                clipData = ClipData.newPlainText("text", Html.fromHtml(title + "\n" + doubanMomentStory.getContent()).toString());
        }
        manager.setPrimaryClip(clipData);
        view.showTextCopied();
    }

    @Override
    public void copyLink() {
        if (checkNull()) {
            view.showCopyTextError();
            return;
        }

        ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = null;
        switch (type) {
            case TYPE_ZHIHU:
                clipData = ClipData.newPlainText("text", Html.fromHtml(zhihuDailyStory.getShare_url()).toString());
                break;
            case TYPE_GUOKE:
                clipData = ClipData.newPlainText("text", Html.fromHtml(Api.GUOKE_ARTICLE_LINK_V1 + id).toString());
                break;
            case TYPE_DOUBAN:
                clipData = ClipData.newPlainText("text", Html.fromHtml(doubanMomentStory.getOriginal_url()).toString());
        }
        manager.setPrimaryClip(clipData);
        view.showTextCopied();
    }

    @Override
    public void addToOrDeleteFromBookmarks() {

        Class tmpClass = null;
        String tmpStr = null;
        switch (type) {
            case TYPE_ZHIHU:
                tmpClass = ZhihuCache.class;
                tmpStr = "zhihu_id";
                break;
            case TYPE_GUOKE:
                tmpClass = GuokeCache.class;
                tmpStr = "guoke_id";
                break;
            case TYPE_DOUBAN:
                tmpClass = DoubanCache.class;
                tmpStr = "douban_id";
                break;
            default:
                break;
        }

        ContentValues values = new ContentValues();
        if (queryIfIsBookmarked()) {
            // delete
            // update Zhihu set bookmark = false where zhihu_id = id
            values.put("bookmark", false);
            DataSupport.updateAll(tmpClass, values, tmpStr + " = ?", id + "");
            view.showDeletedFromBookmarks();
        } else {
            // add
            // update Zhihu set bookmark = true where zhihu_id = id
            values.put("bookmark", true);
            DataSupport.updateAll(tmpClass, values, tmpStr + " = ?", id + "");
            view.showAddedToBookmarks();
        }
    }

    @Override
    public boolean queryIfIsBookmarked() {
        if (id == 0 || type == null) {
            view.showLoadingError();
            return false;
        }

        switch (type) {
            case TYPE_ZHIHU:
                return DataSupport.select("bookmark").where("zhihu_id = ?", "" + id).findFirst(ZhihuCache.class).isBookmark();
            case TYPE_GUOKE:
                return DataSupport.select("bookmark").where("guoke_id = ?", "" + id).findFirst(GuokeCache.class).isBookmark();
            case TYPE_DOUBAN:
                return DataSupport.select("bookmark").where("douban_id = ?", "" + id).findFirst(DoubanCache.class).isBookmark();
            default:
                return false;
        }
    }

    @Override
    public void requestData() {
        if (id == 0 || type == null) {
            view.showLoadingError();
            return;
        }

        view.showLoading();
        view.setTitle(title);
        view.showCover(coverUrl);

        view.setImageMode(sp.getBoolean("no_picture_mode", false));

        switch (type) {
            case TYPE_ZHIHU:
                if (NetworkState.networkConnected(context)) {
                    try {
                        new ZhihuModelImpl().loadStory("" + id, new Callback<ZhihuDailyStory>() {
                            @Override
                            public void onResponse(Call<ZhihuDailyStory> call, Response<ZhihuDailyStory> response) {
                                zhihuDailyStory = response.body();//这里记得给zhihuDailyStory赋值，否则zhihuDailyStory为空，不能正常在浏览器中打开
                                if (zhihuDailyStory.getBody() == null) {
                                    view.showResultWithoutBody(zhihuDailyStory.getShare_url());
                                } else {
                                    view.showResult(convertZhihuContent(response.body().getBody()));
                                }
                                view.stopLoading();
                            }

                            @Override
                            public void onFailure(Call<ZhihuDailyStory> call, Throwable t) {
                                view.stopLoading();
                                view.showLoadingError();
                            }
                        });
                    } catch (JsonSyntaxException e) {
                        view.showLoadingError();
                        view.stopLoading();
                        e.printStackTrace();
                    }
                } else {
                    ZhihuCache cache = DataSupport.select("zhihu_content").where("zhihu_id = ?", id + "").findFirst(ZhihuCache.class);
                    try {
                        zhihuDailyStory = gson.fromJson(cache.getZhihu_content(), ZhihuDailyStory.class);
                        view.showResult(convertZhihuContent(zhihuDailyStory.getBody()));
                        view.stopLoading();
                    } catch (JsonSyntaxException e) {
                        view.showResult(cache.getZhihu_content());
                        view.stopLoading();
                    } catch (NullPointerException e) {
                        Toast.makeText(context,"无法从本地获取详细内容",Toast.LENGTH_SHORT).show();
                        view.stopLoading();
                    }
                }
                break;

            case TYPE_GUOKE:
                if (NetworkState.networkConnected(context)) {
                    try {
                        new GuokeModelImpl().loadJingxuan("" + id, new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {
                                    convertGuokeContent(response.body().string());//该方法会为guokrStory赋值
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                view.showResult(guokeStory);
                                view.stopLoading();
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                view.stopLoading();
                                view.showLoadingError();
                            }
                        });
                    } catch (JsonSyntaxException e) {
                        view.showLoadingError();
                        view.stopLoading();
                        e.printStackTrace();
                    }
                } else {
                    try {
                        GuokeCache cache = select("guoke_content").where("guoke_id = ?", id + "").findFirst(GuokeCache.class);
                        convertGuokeContent(cache.getGuoke_content());
                        view.showResult(guokeStory);
                        view.stopLoading();
                    } catch (NullPointerException e) {
                        Toast.makeText(context,"无法从本地获取详细内容",Toast.LENGTH_SHORT).show();
                        view.stopLoading();
                    }
                }
                break;

            case TYPE_DOUBAN:
                if (NetworkState.networkConnected(context)) {
                    try {
                        new DoubanModelImpl().loadArticleDetail("" + id, new Callback<DoubanMomentStory>() {
                            @Override
                            public void onResponse(Call<DoubanMomentStory> call, Response<DoubanMomentStory> response) {
                                doubanMomentStory = response.body();
                                view.showResult(convertDoubanContent());
                                view.stopLoading();
                            }

                            @Override
                            public void onFailure(Call<DoubanMomentStory> call, Throwable t) {
                                view.showLoadingError();
                                view.stopLoading();
                            }
                        });
                    } catch (JsonSyntaxException e) {
                        view.showLoadingError();
                        view.stopLoading();
                        e.printStackTrace();
                    }
                } else {
                    try {
                        DoubanCache cache = select("douban_content").where("douban_id = ?", id + "").findFirst(DoubanCache.class);
                        doubanMomentStory = gson.fromJson(cache.getDouban_content(), DoubanMomentStory.class);
                        view.showResult(convertDoubanContent());
                        view.stopLoading();
                    } catch (NullPointerException e) {
                        Toast.makeText(context,"无法从本地获取详细内容",Toast.LENGTH_SHORT).show();
                        view.stopLoading();
                    }
                }
                break;
            default:
                view.stopLoading();
                view.showLoadingError();
                break;
        }
    }

    @Override
    public void start() {

    }

    private String convertDoubanContent() {

        if (doubanMomentStory.getContent() == null) {
            return null;
        }
        String css;
        if ((context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES) {
            css = "<link rel=\"stylesheet\" href=\"file:///android_asset/douban_dark.css\" type=\"text/css\">";
        } else {
            css = "<link rel=\"stylesheet\" href=\"file:///android_asset/douban_light.css\" type=\"text/css\">";
        }
        String content = doubanMomentStory.getContent();
        ArrayList<DoubanMomentNews.Posts.thumbs> imageList = doubanMomentStory.getPhotos();
        for (int i = 0; i < imageList.size(); i++) {
            String old = "<img id=\"" + imageList.get(i).getTag_name() + "\" />";
            String newStr = "<img id=\"" + imageList.get(i).getTag_name() + "\" "
                    + "src=\"" + imageList.get(i).getMedium().getUrl() + "\"/>";
            content = content.replace(old, newStr);
        }
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html>\n");
        builder.append("<html lang=\"ZH-CN\" xmlns=\"http://www.w3.org/1999/xhtml\">\n");
        builder.append("<head>\n<meta charset=\"utf-8\" />\n");
        builder.append(css);
        builder.append("\n</head>\n<body>\n");
        builder.append("<div class=\"container bs-docs-container\">\n");
        builder.append("<div class=\"post-container\">\n");
        builder.append(content);
        builder.append("</div>\n</div>\n</body>\n</html>");

        return builder.toString();
    }

    private String convertZhihuContent(String preResult) {

        preResult = preResult.replace("<div class=\"img-place-holder\">", "");
        preResult = preResult.replace("<div class=\"headline\">", "");

        // 在api中，css的地址是以一个数组的形式给出，这里需要设置
        // in fact,in api,css addresses are given as an array
        // api中还有js的部分，这里不再解析js
        // javascript is included,but here I don't use it
        // 不再选择加载网络css，而是加载本地assets文件夹中的css
        // use the css file from local assets folder,not from network
        String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/zhihu_daily.css\" type=\"text/css\">";


        // 根据主题的不同确定不同的加载内容
        // loadMoment content judging by different theme
        String theme = "<body className=\"\" onload=\"onLoaded()\">";
        if ((context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES) {
            theme = "<body className=\"\" onload=\"onLoaded()\" class=\"night\">";
        }

        return new StringBuilder()
                .append("<!DOCTYPE html>\n")
                .append("<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">\n")
                .append("<head>\n")
                .append("\t<meta charset=\"utf-8\" />")
                .append(css)
                .append("\n</head>\n")
                .append(theme)
                .append(preResult)
                .append("</body></html>").toString();
    }

    private void convertGuokeContent(String content) {
        // 简单粗暴的去掉下载的div部分
        this.guokeStory = content.replace("<div class=\"down\" id=\"down-footer\">\n" +
                "        <img src=\"http://static.guokr.com/apps/handpick/images/c324536d.jingxuan-logo.png\" class=\"jingxuan-img\">\n" +
                "        <p class=\"jingxuan-txt\">\n" +
                "            <span class=\"jingxuan-title\">果壳精选</span>\n" +
                "            <span class=\"jingxuan-label\">早晚给你好看</span>\n" +
                "        </p>\n" +
                "        <a href=\"\" class=\"app-down\" id=\"app-down-footer\">下载</a>\n" +
                "    </div>\n" +
                "\n" +
                "    <div class=\"down-pc\" id=\"down-pc\">\n" +
                "        <img src=\"http://static.guokr.com/apps/handpick/images/c324536d.jingxuan-logo.png\" class=\"jingxuan-img\">\n" +
                "        <p class=\"jingxuan-txt\">\n" +
                "            <span class=\"jingxuan-title\">果壳精选</span>\n" +
                "            <span class=\"jingxuan-label\">早晚给你好看</span>\n" +
                "        </p>\n" +
                "        <a href=\"http://www.guokr.com/mobile/\" class=\"app-down\">下载</a>\n" +
                "    </div>", "");

        // 替换css文件为本地文件
        guokeStory = guokeStory.replace("<link rel=\"stylesheet\" href=\"http://static.guokr.com/apps/handpick/styles/d48b771f.article.css\" />",
                "<link rel=\"stylesheet\" href=\"file:///android_asset/guokr.article.css\" />");

        // 替换js文件为本地文件
        guokeStory = guokeStory.replace("<script src=\"http://static.guokr.com/apps/handpick/scripts/9c661fc7.base.js\"></script>",
                "<script src=\"file:///android_asset/guokr.base.js\"></script>");
        if ((context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES) {
            guokeStory = guokeStory.replace("<div class=\"article\" id=\"contentMain\">",
                    "<div class=\"article \" id=\"contentMain\" style=\"background-color:#212b30; color:#878787\">");
        }
    }

    private boolean checkNull() {
        return (type == BeanType.TYPE_ZHIHU && zhihuDailyStory == null)
                || (type == BeanType.TYPE_GUOKE && guokeStory == null)
                || (type == BeanType.TYPE_DOUBAN && doubanMomentStory == null);
    }

}
