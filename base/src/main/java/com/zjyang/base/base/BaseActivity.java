package com.zjyang.base.base;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.umeng.analytics.MobclickAgent;
import com.zjyang.base.utils.LogUtil;


/**
 * Created by zhengjiayang on 2018/3/1.
 */

public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity {

    public P mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(this.getClass().getSimpleName(), "onCreate: ");

        initActionBars();

        //判断当前设备版本号是否为4.4以上，如果是，则通过调用setTranslucentStatus让状态栏变透明  
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            setTranslucentStatus(true);
        }

        if(mPresenter == null){
            mPresenter = createPresenter();
        }

        if(mPresenter != null){
            mPresenter.attachV(this);
        }

    }

    public void initActionBars() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            ColorDrawable drawable = new ColorDrawable(SkinManager.getInstance().getPrimaryColor());
            actionBar.setBackgroundDrawable(drawable);
        }

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = getWindow();
            //添加Flag把状态栏设为可绘制模式
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //如果为全透明模式，取消设置Window半透明的Flag
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //设置状态栏为透明
            window.setStatusBarColor(SkinManager.getInstance().getPrimaryColor());
        }
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on){
//       Window win = getWindow();
//        WindowManager.LayoutParams winParams = win.getAttributes();
//        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//        if(on){
//            winParams.flags |= bits;
//        } else {
//            winParams.flags &= ~bits;
//        }
//        win.setAttributes(winParams);
    }

    public abstract P createPresenter();


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.d(this.getClass().getSimpleName(), "onNewIntent: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        LogUtil.d(this.getClass().getSimpleName(), "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        LogUtil.d(this.getClass().getSimpleName(), "onResume: ");
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        LogUtil.d(this.getClass().getSimpleName(), "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d(this.getClass().getSimpleName(), "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
        LogUtil.d(this.getClass().getSimpleName(), "onDestroy: ");
    }





}
