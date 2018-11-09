package com.example.zjy.player.controller;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.example.zjy.player.R;


/**
 * Created by zhengjiayang on 2017/10/20.
 * 管理上下面板的自动收缩
 */

public class PanelManager {

    private static PanelManager mPanelManager = null;

    private Activity mActivity;

    private QVMediaController mediaController;
    private RelativeLayout mToolbarTb;
    private ImageView mLockIv;
    private SeekBar mProgressBar;

    /**
     * 是否显示控制面板，默认为隐藏，true为显示false为隐藏
     */
    private boolean isShowControlPanl;
    /**
     * 是否显示底部进度条和锁按钮
     */
    private boolean isShowBottomBar;
    /**
     * 控制面板收起或者显示的轮询监听
     */
    private AutoPlayRunnable mAutoPlayRunnable;
    private LockDismissRunnable mLockDismissRunnable;

    private boolean mIsLockScreen;

    private PanelManager(Activity activity){
        this.mActivity = activity;
        mediaController = (QVMediaController) mActivity.findViewById(R.id.media_controller);
        mToolbarTb = (RelativeLayout) mActivity.findViewById(R.id.toolbar);
        mLockIv = (ImageView) mActivity.findViewById(R.id.lock_iv);
        mProgressBar = (SeekBar) mActivity.findViewById(R.id.media_controller_progress_bottom);
        mAutoPlayRunnable = new AutoPlayRunnable();
        mLockDismissRunnable = new LockDismissRunnable();
    }

    public static PanelManager getInstance(Activity activity){
        if(mPanelManager == null){
            mPanelManager = new PanelManager(activity);
        }
        return mPanelManager;
    }


    /**
     * 显示或隐藏操作面板
     */
    public void operatorPanl() {
        if(mIsLockScreen){
            isShowBottomBar = !isShowBottomBar;
            if (isShowBottomBar) {
                mLockIv.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                mLockDismissRunnable.start();
            } else {
                mLockIv.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                mLockDismissRunnable.stop();
            }
        }else{
            isShowControlPanl = !isShowControlPanl;
            if (isShowControlPanl) {
                mLockIv.setVisibility(View.VISIBLE);
                mediaController.setVisibility(View.VISIBLE);
                mToolbarTb.setVisibility(View.VISIBLE);
                mAutoPlayRunnable.start();
            } else {
                mLockIv.setVisibility(View.INVISIBLE);
                mediaController.setVisibility(View.INVISIBLE);
                mToolbarTb.setVisibility(View.INVISIBLE);
                mAutoPlayRunnable.stop();
            }
        }
    }

    public void startAutoStretch(boolean isFromScroll){
        if(mIsLockScreen){
            if(mLockDismissRunnable != null){
                mLockDismissRunnable.start();
            }
        }else{
            if(isFromScroll){
                //如果是滑动操作触发的，需显示面板
                isShowControlPanl = true;
                mLockIv.setVisibility(View.VISIBLE);
                mediaController.setVisibility(View.VISIBLE);
                mToolbarTb.setVisibility(View.VISIBLE);
            }
            if (mAutoPlayRunnable != null) {
                mAutoPlayRunnable.start();
            }
        }

    }

    public void stopAutoStretch(){
        if (mLockDismissRunnable != null) {
            mLockDismissRunnable.stop();
        }
        if (mAutoPlayRunnable != null) {
            mAutoPlayRunnable.stop();
        }
    }

    public void setPanelEnable(boolean isEnable){
        mToolbarTb.setVisibility(!mIsLockScreen ? View.VISIBLE : View.INVISIBLE);
        mediaController.setVisibility(!mIsLockScreen ? View.VISIBLE : View.INVISIBLE);
        mProgressBar.setVisibility(mIsLockScreen ? View.VISIBLE : View.INVISIBLE);
        stopAutoStretch();
        if(mIsLockScreen){
            isShowBottomBar = true;
            mLockIv.setVisibility(isEnable ? View.VISIBLE : View.INVISIBLE);
            mProgressBar.setVisibility(isEnable ? View.VISIBLE : View.INVISIBLE);
            if(isEnable){
                startAutoStretch(false);
            }else{
                stopAutoStretch();
            }
        }else{
            mLockIv.setVisibility(!isEnable ? View.VISIBLE : View.INVISIBLE);
            mToolbarTb.setVisibility(!isEnable ? View.VISIBLE : View.INVISIBLE);
            mediaController.setVisibility(!isEnable ? View.VISIBLE : View.INVISIBLE);
            if(!isEnable){
                startAutoStretch(false);
            }else{
                stopAutoStretch();
            }
        }
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());
    /**
     * 收起控制面板轮询，默认5秒无操作，收起控制面板，
     */
    private class AutoPlayRunnable implements Runnable {
        private int AUTO_PLAY_INTERVAL = 5000;

        /**
         * 五秒无操作，收起控制面板
         */
        public AutoPlayRunnable() {
        }

        public void start() {
            mHandler.removeCallbacks(this);
            mHandler.postDelayed(this, AUTO_PLAY_INTERVAL);
        }

        public void stop() {
            mHandler.removeCallbacks(this);
        }

        @Override
        public void run() {
            mHandler.removeCallbacks(this);
            if (isShowControlPanl) {
                operatorPanl();
            }
        }
    }

    private class LockDismissRunnable implements Runnable {
        private int AUTO_PLAY_INTERVAL = 2000;

        /**
         * 五秒无操作，收起控制面板
         */
        public LockDismissRunnable() {
        }

        public void start() {
            mHandler.removeCallbacks(this);
            mHandler.postDelayed(this, AUTO_PLAY_INTERVAL);
        }

        public void stop() {
            mHandler.removeCallbacks(this);
        }

        @Override
        public void run() {
            mHandler.removeCallbacks(this);
            if (isShowBottomBar) {
                operatorPanl();
            }
        }
    }

//    private Runnable mAutoDismissRunnable = new Runnable() {
//        @Override
//        public void run() {
//            mLockIv.setVisibility(View.INVISIBLE);
//            mProgressBar.setVisibility(View.INVISIBLE);
//        }
//    };

    public boolean getIsLockScreen() {
        return mIsLockScreen;
    }

    public void setLockScreen(boolean mIsLockScreen) {
        this.mIsLockScreen = mIsLockScreen;
    }

    public void reset(){
        isShowControlPanl = false;
        mHandler.removeCallbacks(mAutoPlayRunnable);
        mAutoPlayRunnable = null;
        mPanelManager = null;
    }


}
