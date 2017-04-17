package com.hut.zero.detail;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hut.zero.R;
import com.hut.zero.databinding.ReadingActionsSheetBinding;
import com.hut.zero.databinding.UniversalReadLayoutBinding;

/**
 * Created by Zero on 2017/4/6.
 */

public class DetailFragment extends Fragment implements DetailContract.View {
    private UniversalReadLayoutBinding mBinding;

    private DetailContract.Presenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding= DataBindingUtil.inflate(inflater, R.layout.universal_read_layout, container, false);

        initViews();

        setHasOptionsMenu(true);

        mPresenter.requestData();

        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_more, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getActivity().onBackPressed();
        } else if (id == R.id.action_more) {

            final BottomSheetDialog dialog = new BottomSheetDialog(getActivity());

            ReadingActionsSheetBinding binding = DataBindingUtil.inflate(getActivity().getLayoutInflater(), R.layout.reading_actions_sheet, null, false);

            if (mPresenter.queryIfIsBookmarked()) {
                binding.textView.setText(R.string.action_delete_from_bookmarks);
                binding.imageView.setColorFilter(ContextCompat.getColor(getContext(),R.color.colorPrimary));
            }

            binding.layoutBookmark.setOnClickListener(v -> {
                dialog.dismiss();
                mPresenter.addToOrDeleteFromBookmarks();
            });
            binding.layoutCopyLink.setOnClickListener(v -> {
                dialog.dismiss();
                mPresenter.copyLink();
            });
            binding.layoutOpenInBrowser.setOnClickListener(v -> {
                dialog.dismiss();
                mPresenter.openInBrowser();
            });
            binding.layoutCopyText.setOnClickListener(v -> {
                dialog.dismiss();
                mPresenter.copyText();
            });
            binding.layoutShareText.setOnClickListener(v -> {
                dialog.dismiss();
                mPresenter.shareAsText();
            });
            dialog.setContentView(binding.getRoot());
            dialog.show();
        }
        return true;
    }

    @Override
    public void setPresenter(DetailContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void initViews() {
        mBinding.refreshLayout.setColorSchemeResources(R.color.colorPrimary);

        mBinding.toolbar.setOnClickListener(v -> mBinding.scrollView.smoothScrollTo(0, 0));

        mBinding.refreshLayout.setOnRefreshListener(() -> mPresenter.requestData());

        DetailActivity activity = (DetailActivity) getActivity();
        activity.setSupportActionBar(mBinding.toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBinding.webView.setScrollbarFadingEnabled(true);

        //能够和js交互
        mBinding.webView.getSettings().setJavaScriptEnabled(true);
        //缩放,设置为不能缩放可以防止页面上出现放大和缩小的图标
        mBinding.webView.getSettings().setBuiltInZoomControls(false);
        //缓存
        mBinding.webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        //开启DOM storage API功能
        mBinding.webView.getSettings().setDomStorageEnabled(true);
        //开启application Cache功能
        mBinding.webView.getSettings().setAppCacheEnabled(false);

        mBinding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mPresenter.openUrl(view, url);
                return true;
            }

        });
    }

    @Override
    public void showLoading() {
        mBinding.refreshLayout.post(() -> mBinding.refreshLayout.setRefreshing(true));
    }

    @Override
    public void stopLoading() {
        mBinding.refreshLayout.post(() -> mBinding.refreshLayout.setRefreshing(false));
    }

    @Override
    public void showLoadingError() {
        Snackbar.make(mBinding.imageView,R.string.loaded_failed,Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, view -> mPresenter.requestData())
                .show();
    }

    @Override
    public void showSharingError() {
        Snackbar.make(mBinding.imageView,R.string.share_error,Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showResult(String result) {
        mBinding.webView.loadDataWithBaseURL("x-data://base",result,"text/html","utf-8",null);
    }

    @Override
    public void showResultWithoutBody(String url) {
        mBinding.webView.loadUrl(url);
    }

    @Override
    public void showCover(String url) {
        mBinding.setImageUrl(url);
    }

    @Override
    public void setTitle(String title) {
        setCollapsingToolbarLayoutTitle(title);
    }

    @Override
    public void setImageMode(boolean showImage) {
        mBinding.webView.getSettings().setBlockNetworkImage(showImage);
    }

    @Override
    public void showBrowserNotFoundError() {
        Snackbar.make(mBinding.imageView, R.string.no_browser_found,Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showTextCopied() {
        Snackbar.make(mBinding.imageView, R.string.copied_to_clipboard,Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showCopyTextError() {
        Snackbar.make(mBinding.imageView, R.string.copied_to_clipboard_failed,Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showAddedToBookmarks() {
        Snackbar.make(mBinding.imageView, R.string.added_to_bookmarks,Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showDeletedFromBookmarks() {
        Snackbar.make(mBinding.imageView, R.string.deleted_from_bookmarks,Snackbar.LENGTH_SHORT).show();
    }

    private void setCollapsingToolbarLayoutTitle(String title) {
        mBinding.toolbarLayout.setTitle(title);
        mBinding.toolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        mBinding.toolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        mBinding.toolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBarPlus1);
        mBinding.toolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarPlus1);
    }
}
