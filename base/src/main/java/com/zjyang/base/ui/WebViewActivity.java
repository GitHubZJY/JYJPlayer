package com.zjyang.base.ui;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import com.zjyang.base.R;
import com.zjyang.base.broadcast.NetBroadcastReceiver;
import com.zjyang.base.utils.HandlerUtils;
import com.zjyang.base.utils.LogUtil;
import com.zjyang.base.utils.NetworkUtils;
import com.zjyang.base.utils.ShapeUtils;


/**
 * 布局有 前进/后退/刷新/关闭按钮的webview
 *
 * @author denglongyun
 * @date 18-3-5
 */

public class WebViewActivity extends AppCompatActivity implements NetBroadcastReceiver.NetEvent, View.OnClickListener{


    public static final String TAG = "WebViewActivity";

    public static final String AD_URL_DATA_KEY = "web_url_data_key";

    private WebView mWebView;
    private ImageView mHomeView;
    private ImageView mPreView;
    private ImageView mNextView;
    private ImageView mCloseView;
    private ImageView mRefreshView;
    private ViewGroup rootLayout;
    private SeekBar mProgressBar;
    private LinearLayout mTipsContainer;
    private ImageView mTipsClose;
    private TextView mLoadingTv;

    ValueAnimator valueAnimator;
    String mLoading;

    /**
     * 监控网络的广播
     */
    private NetBroadcastReceiver netBroadcastReceiver;

    private long mBackKeyTimestamp = -1;

    private long mEnterTimestamp = -1;
    private String mLastUrl;

    private boolean mIsUpdateView = true;
    private String mLastLoadingUrl;
    private int mPageType;
    private int mCureentOrientation = Configuration.ORIENTATION_PORTRAIT;
    private String mCurrentUrl;

    //记录chrome client上次load的网址
    private String mLastChromeClientUrl = "";

    private String mCurTitle;

    public static void go(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(AD_URL_DATA_KEY, url);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        mWebView = findViewById(R.id.web_view);
        mHomeView = (ImageView) findViewById(R.id.home);
        mPreView = (ImageView) findViewById(R.id.pre);
        mNextView = (ImageView) findViewById(R.id.next);
        mCloseView = (ImageView) findViewById(R.id.close);
        mRefreshView = (ImageView) findViewById(R.id.refresh);
        mTipsClose = (ImageView) findViewById(R.id.webView_loading_close_iv);
        mLoadingTv = (TextView) findViewById(R.id.webView_loading_tips_tv);
        mTipsContainer = (LinearLayout) findViewById(R.id.ll_webview_loading_tips);
        mProgressBar = (SeekBar) findViewById(R.id.web_load_progress);
        rootLayout = (ViewGroup) findViewById(R.id.root_view);

        mHomeView.setOnClickListener(this);
        mPreView.setOnClickListener(this);
        mNextView.setOnClickListener(this);
        mCloseView.setOnClickListener(this);
        mRefreshView.setOnClickListener(this);
        mTipsClose.setOnClickListener(this);

        mHomeView.setImageDrawable(ShapeUtils.drawColor(getResources().getDrawable(R.drawable.web_home), Color.BLACK));
        mPreView.setImageDrawable(ShapeUtils.drawColor(getResources().getDrawable(R.drawable.web_pre), Color.BLACK));
        mNextView.setImageDrawable(ShapeUtils.drawColor(getResources().getDrawable(R.drawable.web_next), Color.BLACK));
        mCloseView.setImageDrawable(ShapeUtils.drawColor(getResources().getDrawable(R.drawable.web_close), Color.BLACK));
        mRefreshView.setImageDrawable(ShapeUtils.drawColor(getResources().getDrawable(R.drawable.web_refresh), Color.BLACK));

        mWebView.setWebViewClient(new CustomWebClient());
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        //mWebView.setInitialScale(100);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        int screenDensity = getResources().getDisplayMetrics().densityDpi ;
        WebSettings.ZoomDensity zoomDensity = WebSettings.ZoomDensity.MEDIUM ;
        switch (screenDensity){
            case DisplayMetrics.DENSITY_LOW :
                zoomDensity = WebSettings.ZoomDensity.CLOSE;
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                zoomDensity = WebSettings.ZoomDensity.MEDIUM;
                break;
            case DisplayMetrics.DENSITY_HIGH:
                zoomDensity = WebSettings.ZoomDensity.FAR;
                break ;
        }
        mWebView.getSettings().setDefaultZoom(zoomDensity);
        mProgressBar.setMax(100);
        mLoading = getResources().getString(R.string.webView_loading_tips);
        if (NetworkUtils.isNetworkOK(this)) {
            mTipsContainer.setVisibility(View.GONE);
            startNumAnim();
        } else {
            mTipsContainer.setVisibility(View.GONE);
        }
        //注册广播
        if (netBroadcastReceiver == null) {
            netBroadcastReceiver = new NetBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(netBroadcastReceiver, filter);
            /**
             * 设置监听
             */
            netBroadcastReceiver.setNetEvent(this);
        }

        mWebView.setWebViewClient(new CustomWebClient());
        mWebView.reload();

        initView();
        initData(savedInstanceState);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config=new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config,res.getDisplayMetrics() );
        return res;
    }




    public void initView() {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.BLACK);
        }

    }

    protected void initData(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mWebView.loadUrl(getIntent().getStringExtra(AD_URL_DATA_KEY));
        }
        mCurrentUrl = getIntent().getStringExtra(AD_URL_DATA_KEY);
        refreshBthState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mWebView != null){
            mWebView.onResume();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mWebView.clearView();
        mWebView.loadUrl(getIntent().getStringExtra(AD_URL_DATA_KEY));
        refreshBthState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogUtil.e(TAG, "onConfigurationChanged");
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mCureentOrientation = Configuration.ORIENTATION_PORTRAIT;
        }
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mCureentOrientation = Configuration.ORIENTATION_LANDSCAPE;
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.pre){
            onPreClick();
        }else if(id == R.id.next){
            onNextClick();
        }else if(id == R.id.close){
            onCloseClick();
        }else if(id == R.id.refresh){
            onRefreshClick();
        }else if(id == R.id.home){
            onHomeClick();
        }else if(id == R.id.webView_loading_close_iv){
            onTipsClose();
        }
    }

    private void onPreClick() {
        refreshBthState();

        if (mBackKeyTimestamp == -1) {
            mBackKeyTimestamp = SystemClock.elapsedRealtime();
        }

        if (mWebView.canGoBack()) {
            mWebView.goBack();
        }
    }


    private void onNextClick() {
        refreshBthState();
        if (mWebView.canGoForward()) {
            mWebView.goForward();
        }
    }


    private void onCloseClick() {
        finish();
    }


    private void onRefreshClick() {
        mWebView.reload();
        refreshBthState();
        if (valueAnimator != null) {
            valueAnimator.start();
        }
    }

    void onHomeClick() {
        mWebView.loadUrl(getIntent().getStringExtra(AD_URL_DATA_KEY));
        refreshBthState();
    }

    void onTipsClose() {
        if (mTipsContainer != null) {
            mTipsContainer.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mWebView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mWebView.restoreState(savedInstanceState);
    }

    private void refreshBthState() {
        if(mWebView == null){
            return;
        }
        if (mWebView.canGoBack()) {
            mPreView.setEnabled(true);
            mPreView.setClickable(true);
            mPreView.setAlpha(1.0f);
        } else {
            mPreView.setEnabled(false);
            mPreView.setClickable(false);
            mPreView.setAlpha(0.4f);
        }

        if (mWebView.canGoForward()) {
            mNextView.setEnabled(true);
            mNextView.setClickable(true);
            mNextView.setAlpha(1.0f);
        } else {
            mNextView.setEnabled(false);
            mNextView.setClickable(false);
            mNextView.setAlpha(0.4f);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mWebView != null){
            mWebView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.clearView();

        if (netBroadcastReceiver != null) {
            //注销广播
            unregisterReceiver(netBroadcastReceiver);
        }
    }

    @Override
    public void onNetChange(int netMobile) {
        if (!NetworkUtils.isNetworkOK(this) && mTipsContainer.getVisibility() == View.VISIBLE) {
            mTipsContainer.setVisibility(View.GONE);
        }
    }


    class CustomWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            mIsUpdateView = true;
            refreshBthState();
            mProgressBar.setProgress(mWebView.getProgress());
            mProgressBar.setVisibility(View.VISIBLE);
            mEnterTimestamp = System.currentTimeMillis();
            if (mWebView.getProgress() >= 80) {
                setmTipsContainerStatus();
            }
            mLastLoadingUrl = url;
            LogUtil.e(TAG, "onPageStarted 开始加载 WebView.getProgress()= " + mWebView.getProgress() + "   " + url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mLastUrl = url;
            int progress = mWebView.getProgress();
            mProgressBar.setProgress(progress);
            LogUtil.e(TAG, "onPageFinished 加载结束 WebView.getProgress()= " + progress + "   " + url);
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);
            LogUtil.e(TAG, "onPageCommitVisible " + url);
            if (mLastUrl != null && !url.equals(mLastUrl)) {
                if (mTipsContainer.getVisibility() == View.VISIBLE) {
                    HandlerUtils.postDelay(new Runnable() {
                        @Override
                        public void run() {
                            if (mTipsContainer != null) {
                                mTipsContainer.setVisibility(View.GONE);
                            }
                        }
                    }, 300);
                }
            }
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            // 请求资源 比如超链接、JS文件、CSS文件、图片等
            refreshBthState();
            if(mWebView == null){
                return;
            }

            int progress = mWebView.getProgress();
            mProgressBar.setProgress(progress);
            if (progress >= 80) {
                setmTipsContainerStatus();
            }
            LogUtil.d(TAG, "onLoadResource 请求资源 WebView.getProgress()= " + mWebView.getProgress() + "   " + url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            LogUtil.d(TAG, "shouldOverrideUrlLoading 加载超链接 url= " + url);
            if (url.startsWith("http:") || url.startsWith("https:")) {
                return super.shouldOverrideUrlLoading(view, url);
            }
            if (mTipsContainer.getVisibility() == View.VISIBLE) {
                mTipsContainer.setVisibility(View.GONE);
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            LogUtil.e(TAG, "onReceivedError 加载错误 " + error);
//            ToastUtil.showShort(WebViewActivity.this, R.string.network_error);
        }

        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
//            super.onReceivedSslError(view, handler, error);
            final AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this);
            builder.setMessage("Received Ssl Error");
            builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.proceed();
                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.cancel();
                }
            });
            final AlertDialog dialog = builder.create();
            //dialog.show();
            handler.proceed();
            LogUtil.e(TAG, "onReceivedSslError 收到https错误 忽略错误 继续请求");
        }

    }


    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private void setmTipsContainerStatus() {
        if (mTipsContainer.getVisibility() == View.VISIBLE) {
            setLoadingProgress(100);
        }
    }

    public void startNumAnim() {
        valueAnimator = ValueAnimator.ofInt(0, 99);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (mLoadingTv != null) {
                    mLoadingTv.setText(mLoading + valueAnimator.getAnimatedValue() + "%");
                }
                if (!NetworkUtils.isNetworkOK(getApplicationContext())) {
                    valueAnimator.cancel();
                }
            }
        });
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.setDuration(25 * 1000);
        valueAnimator.start();
    }

    public void setLoadingProgress(int percent) {
        if (mLoadingTv != null) {
            //mLoadingTv.setText(mLoading + percent + "%");
            mLoadingTv.setText(getResources().getText(R.string.webView_loading_tips));
            HandlerUtils.postDelay(new Runnable() {
                @Override
                public void run() {
                    if (mTipsContainer != null) {
                        mTipsContainer.setVisibility(View.GONE);
                    }
                }
            }, 3000);
        }
        valueAnimator.cancel();
    }

}
