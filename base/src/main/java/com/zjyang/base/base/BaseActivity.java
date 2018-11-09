package com.zjyang.base.base;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

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
        LogUtil.d(this.getClass().getSimpleName(), "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(this.getClass().getSimpleName(), "onResume: ");
    }


    @Override
    protected void onPause() {
        super.onPause();
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
        LogUtil.d(this.getClass().getSimpleName(), "onDestroy: ");
    }





}
