package com.sunilpaulmathew.researchgateclient.fragments;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sunilpaulmathew.researchgateclient.R;
import com.sunilpaulmathew.researchgateclient.activities.SettingsActivity;
import com.sunilpaulmathew.researchgateclient.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 12, 2021
 */

public class ResearchGateFragment extends Fragment {

    private boolean mExit;
    private Handler mHandler = new Handler();
    private ValueCallback<Uri[]> mPhoto;
    private WebView mWebView;

    @SuppressLint({"SetJavaScriptEnabled", "UseCompatLoadingForDrawables"})
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_layout, container, false);

        AppCompatImageButton mMenu = mRootView.findViewById(R.id.menu_button);
        AppCompatImageButton mAdd = mRootView.findViewById(R.id.add_button);
        AppCompatImageView mSplashImage = mRootView.findViewById(R.id.splash_image);
        FrameLayout mMenuLayout = mRootView.findViewById(R.id.menu_layout);
        LinearLayout mHome = mRootView.findViewById(R.id.home_button);
        LinearLayout mMore = mRootView.findViewById(R.id.more_button);
        LinearLayout mNotifications = mRootView.findViewById(R.id.notifications_button);
        LinearLayout mSplashScreen = mRootView.findViewById(R.id.splash_screen);
        ProgressBar mProgress = mRootView.findViewById(R.id.progress);
        mWebView = mRootView.findViewById(R.id.webview);
        SwipeRefreshLayout mSwipeRefreshLayout = mRootView.findViewById(R.id.swipe_layout);
        mSplashImage.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));

        mSplashScreen.setVisibility(View.VISIBLE);

        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setPluginState(WebSettings.PluginState.ON);

        mWebView.setWebViewClient(new WebViewClient());

        mWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                mSplashScreen.setVisibility(View.GONE);
                mMenuLayout.setVisibility(View.VISIBLE);
                mAdd.setVisibility(View.VISIBLE);
                mWebView.loadUrl("javascript:(function() { " + "document.getElementsByClassName('header-logged-in__logo')[0].style.display='none'; })()");
            }
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mWebView.loadUrl(url);
                return true;
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                mProgress.setProgress(progress);
            }
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> uploadMsg,
                                             FileChooserParams fileChooserParams) {
                mPhoto = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), 0);
                return true;
            }
        });

        mWebView.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setMimeType(mimeType);
            String cookies = CookieManager.getInstance().getCookie(url);
            request.addRequestHeader("cookie", cookies);
            request.addRequestHeader("User-Agent", userAgent);
            request.setDescription(getString(R.string.downloading));
            request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(
                            url, contentDisposition, mimeType));
            DownloadManager dm = (DownloadManager) requireActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            dm.enqueue(request);
        });

        mWebView.loadUrl("https://www.researchgate.net/");

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mWebView.reload();
            mWebView.setWebViewClient(new WebViewClient() {
                public void onPageFinished(WebView view, String url) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    mWebView.loadUrl("javascript:(function() { " + "document.getElementsByClassName('header-logged-in__logo')[0].style.display='none'; })()");
                }
            });
        });

        mMenu.setOnClickListener(v -> {
            Intent settings = new Intent(requireActivity(), SettingsActivity.class);
            startActivity(settings);
        });

        mHome.setOnClickListener(v -> reloadPage("https://www.researchgate.net/"));
        mMore.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(requireActivity(), mMore);
            Menu menu = popupMenu.getMenu();
            menu.add(Menu.NONE, 0, Menu.NONE, R.string.search);
            menu.add(Menu.NONE, 1, Menu.NONE, R.string.questions);
            menu.add(Menu.NONE, 2, Menu.NONE, R.string.jobs);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 0:
                        reloadPage("https://www.researchgate.net/search/");
                        break;
                    case 1:
                        reloadPage("https://www.researchgate.net/topics/");
                        break;
                    case 2:
                        reloadPage("https://www.researchgate.net/jobs/");
                        break;
                }
                return false;
            });
            popupMenu.show();
        });
        mNotifications.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(requireActivity(), mNotifications);
            Menu menu = popupMenu.getMenu();
            menu.add(Menu.NONE, 0, Menu.NONE, R.string.updates);
            menu.add(Menu.NONE, 1, Menu.NONE, R.string.messages);
            menu.add(Menu.NONE, 2, Menu.NONE, R.string.requests);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 0:
                        reloadPage("https://www.researchgate.net/updates/");
                        break;
                    case 1:
                        reloadPage("https://www.researchgate.net/messages/");
                        break;
                    case 2:
                        reloadPage("https://www.researchgate.net/requests/");
                        break;
                }
                return false;
            });
            popupMenu.show();
        });

        mAdd.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(requireActivity(), mAdd);
            Menu menu = popupMenu.getMenu();
            menu.add(Menu.NONE, 0, Menu.NONE, R.string.publish_research);
            menu.add(Menu.NONE, 1, Menu.NONE, R.string.publish_project);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 0:
                        reloadPage("https://www.researchgate.net/publications/create");
                        break;
                    case 1:
                        reloadPage("https://www.researchgate.net/projects/create");
                        break;
                }
                return false;
            });
            popupMenu.show();
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else if (mExit) {
                    mExit = false;
                    this.remove();
                    requireActivity().onBackPressed();
                } else {
                    Utils.showSnackbar(mRootView, getString(R.string.press_back));
                    mExit = true;
                    mHandler.postDelayed(() -> mExit = false, 2000);
                }
            }
        });

        return mRootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (null == mPhoto) return;
            mPhoto.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
            mPhoto = null;
        }
    }

    private void reloadPage(String url) {
        if (url.equals(mWebView.getUrl())) return;
        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                mWebView.loadUrl("javascript:(function() { " + "document.getElementsByClassName('header-logged-in__logo')[0].style.display='none'; })()");
            }
        });
    }

}