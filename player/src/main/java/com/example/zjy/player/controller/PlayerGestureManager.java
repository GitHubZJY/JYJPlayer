package com.example.zjy.player.controller;

/**
 * Created by zhengjiayang on 2017/10/20.
 */

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.example.zjy.player.R;
import com.example.zjy.player.ui.VideoFrame;


/**
 * 播放器的手势监听
 */
public class PlayerGestureManager {

    /**
     * 当前亮度大小
     */
    private float brightness;
    /**
     * 当前声音大小
     */
    private int volume = -1;
    /**
     * 设备最大音量
     */
    private final int mMaxVolume;
    /**
     * 音频管理器
     */
    private AudioManager audioManager;
    /**
     * 手势管理器
     */
    GestureDetector gestureDetector;

    private Activity mActivity;
    private RelativeLayout rootView;
    private VideoFrame mVideoView;
    private ItemVideoController mediaController;
    private ImageView mCenterPauseIv;
    //滑动的起点位置
    int mStartX,mStartY;
    //滑动的结束位置
    int mCurrentX,mCurrentY;
    //是否锁定屏幕
    private boolean mGestureEnable = true;
    //统计所用，判断当前滑动类型
    private int mCurScrollType = 0;
    private final static int SCROLL_TYPE_PROGRESS = 1;
    private final static int SCROLL_TYPE_VOLUME = 3;
    private final static int SCROLL_TYPE_BRIGHT = 2;

    public PlayerGestureManager(Activity activity) {
        this.mActivity = activity;
        rootView = (RelativeLayout) activity.findViewById(R.id.root_view);
        mVideoView = (VideoFrame) activity.findViewById(R.id.video_frame);
        mediaController = (ItemVideoController) activity.findViewById(R.id.item_controller);
        mCenterPauseIv = (ImageView) activity.findViewById(R.id.center_pause_iv);
        audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        gestureDetector = new GestureDetector(activity, new PlayerGestureListener());

        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        mStartX=(int)motionEvent.getX();
                        mStartY=(int)motionEvent.getY();
                        PanelManager.getInstance(mActivity).stopAutoStretch();
                        break;
                }
                if (gestureDetector.onTouchEvent(motionEvent)){
                    return true;
                }
                // 处理手势结束
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        mCurrentX=(int)motionEvent.getX();
                        mCurrentY=(int)motionEvent.getY();
                        int upX = Math.abs(mCurrentX - mStartX);
                        int upY = Math.abs(mCurrentY - mStartY);
                        double distance = Math.sqrt(Math.pow(upX, 2)+ Math.pow(upY, 2));
                        if(distance <= 4){
                            //点击事件结束
                            //切换面板状态
                            PanelManager.getInstance(mActivity).operatorPanl();
                        }else{
                            //滑动事件结束
                            endGesture();
                        }
                        break;
                }
                return false;
            }


        });


    }



    /**
     * 手势结束
     */
    public void endGesture() {
        volume = -1;
        brightness = -1f;
        //根据滑动的结果调整进度条的位置
        mediaController.scrollEnd();
        //重置面板自动伸缩轮轮询线程
        PanelManager.getInstance(mActivity).startAutoStretch(true);
    }


    public void setAllGestureEnable(boolean isEnable){
        mGestureEnable = isEnable;
    }


    /**
     * 通过传参直接绑定视频View（适用于没有Activity绑定的时候）
     * @param videoView
     */
    public void attachVideoView(VideoFrame videoView){
        if(mVideoView == null){
            mVideoView = videoView;
        }
    }


    /**
     * 亮度滑动改变亮度
     *
     * @param percent
     */
    private void onBrightnessSlide(float percent) {
        if (brightness < 0) {
            brightness = mActivity.getWindow().getAttributes().screenBrightness;
            if (brightness <= 0.00f) {
                brightness = 0.50f;
            } else if (brightness < 0.01f) {
                brightness = 0.01f;
            }
        }
        //query.id(R.id.app_video_brightness_box).visible();
        WindowManager.LayoutParams lpa = mActivity.getWindow().getAttributes();
        lpa.screenBrightness = brightness + percent;
        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f;
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f;
        }
       // query.id(R.id.app_video_brightness).text(((int) (lpa.screenBrightness * 100)) + "%");
        mActivity.getWindow().setAttributes(lpa);

        // 变更进度条
        int i = ((int) (lpa.screenBrightness * 100));
        //亮度调动进度展示
//        mBrightChangeView.setVisibility(View.VISIBLE);
//        mBrightChangeView.setBrightProgress(i);

//        BaseApp.remove(mBrightDismissRunnable);
//        BaseApp.postDelay(mBrightDismissRunnable , 2000);
    }

    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    public void onVolumeSlide(float percent) {
        //静音
        if(percent == -1){
            volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            return;
        }
        //不改变音量
        if(percent != 0){
            volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
        //正常调节音量
        if (volume < 0)
            volume = 0;
        int index = (int) (percent * mMaxVolume) + volume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;

        // 变更声音
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

        if(percent != 0){
            // 变更进度条
            int i = (int) (index * 1.0 / mMaxVolume * 100);
            //声音调动进度展示
//            mVolumeChangeView.setVisibility(View.VISIBLE);
//            mVolumeChangeView.setVolumeProgress(i);

//            BaseApp.remove(mVolumeDismissRunnable);
//            BaseApp.postDelay(mVolumeDismissRunnable , 2000);
        }
    }

    private Runnable mVolumeDismissRunnable = new Runnable() {
        @Override
        public void run() {
            //mVolumeChangeView.setVisibility(View.GONE);
        }
    };

    private Runnable mBrightDismissRunnable = new Runnable() {
        @Override
        public void run() {
            //mBrightChangeView.setVisibility(View.GONE);
        }
    };


    public class PlayerGestureListener extends GestureDetector.SimpleOnGestureListener{
        /**
         * 是否是按下的标识，默认为其他动作，true为按下标识，false为其他动作
         */
        private boolean isDownTouch;
        /**
         * 是否声音控制,默认为亮度控制，true为声音控制，false为亮度控制
         */
        private boolean isVolume;
        /**
         * 是否横向滑动，默认为纵向滑动，true为横向滑动，false为纵向滑动
         */
        private boolean isLandscape;


        /**
         * 按下
         */
        @Override
        public boolean onDown(MotionEvent e) {
            isDownTouch = true;
            return true;
        }


        /**
         * 滑动
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if(mGestureEnable){
            float mOldX = e1.getX(), mOldY = e1.getY();
            float deltaY = mOldY - e2.getY();
            float deltaX = mOldX - e2.getX();
            if (isDownTouch) {
                //如果滑动的横向距离比纵向距离大，定义为横向滑动操作
                isLandscape = Math.abs(distanceX) >= Math.abs(distanceY);
                //如果滑动的起点位置超过屏幕一半，定义为音量调整，否则为亮度调整
                isVolume = mOldX > 1080 * 0.5f; //1080改为获取屏幕宽度方法
                isDownTouch = false;
            }
            if (isLandscape) {
                /**进度设置*/
//                if(mProgressDialog != null){
//                    mProgressDialog.setIsShow(true);
//                }
                mediaController.onProgressSlide(-deltaX / mVideoView.getWidth());
                mCurScrollType = SCROLL_TYPE_PROGRESS;
            } else {
                float percent = deltaY*0.5f / mVideoView.getHeight();
                if (isVolume) {
                    /**声音设置*/
                    onVolumeSlide(percent);
                    mCurScrollType = SCROLL_TYPE_VOLUME;
                } else {
                    /**亮度设置*/
                    onBrightnessSlide(percent);
                    mCurScrollType = SCROLL_TYPE_BRIGHT;
                }


            }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        /**
         * 单击触发
         */
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if(mGestureEnable){
                if(mVideoView.isPlaying()){
                    mVideoView.pause();
                    mediaController.setPauseStatus();
                    mCenterPauseIv.setVisibility(View.VISIBLE);
                    mCenterPauseIv.setImageResource(R.drawable.ic_play_play);
                    mCenterPauseIv.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mCenterPauseIv.setVisibility(View.GONE);
                        }
                    }, 1000);
                }else{
                    mVideoView.start();
                    mediaController.resetPlayStatus();
                    //mCenterPauseIv.setImageResource(R.drawable.ic_play_pause);
                }
            }
            return super.onDoubleTap(e);
        }
    }
}
