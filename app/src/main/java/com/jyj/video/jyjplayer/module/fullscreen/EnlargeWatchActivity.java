package com.jyj.video.jyjplayer.module.fullscreen;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.zjy.player.ui.PlayerListener;
import com.example.zjy.player.ui.VideoFrame;
import com.example.zjy.player.ui.YPlayerView;
import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.filescan.model.bean.VideoInfo;
import com.jyj.video.jyjplayer.manager.VideoPlayDataManager;
import com.jyj.video.jyjplayer.subtitle.SubTitleContainer;
import com.zjyang.base.base.BaseActivity;
import com.zjyang.base.base.BasePresenter;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by 74215 on 2018/9/9.
 */

public class EnlargeWatchActivity extends BaseActivity implements EnlargeTasksContract.View, PlayerListener, YPlayerView.OnInfoListener {

    public Unbinder unbinder;

    @BindView(R.id.root_view)
    FrameLayout mRootView;

    @BindView(R.id.player_view)
    YPlayerView mPlayerView;
    @BindView(R.id.menu_panel)
    SubTitleContainer mMenuPanel;

    VideoFrame mVideoFrame;

    private VideoInfo mVideoInfo;


    public static void start(Context context){
        Intent intent = new Intent(context, EnlargeWatchActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);// 允许使用transitions
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_enlarge_watch);
        unbinder = ButterKnife.bind(this);

        mVideoInfo = VideoPlayDataManager.getInstance().getCurPlayVideoInfo();
        if(mVideoInfo != null){
            mPlayerView.setVideoUrl(mVideoInfo.getPath());
            mPlayerView.setVideoTitle(mVideoInfo.getName());
        }
        mPlayerView.attachActivity(this);
        mPlayerView.setGestureEnable(true);
        mPlayerView.setPlayerListener(this);
        mPlayerView.setOnInfoListener(this);
        mPlayerView.start();
        mVideoFrame = mPlayerView.getVideoFrame();
    }

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    public void onInfo(IMediaPlayer imp, MediaPlayer mp, int what, int extra) {
        if (what == 10001 && extra == 0) {
            setOrientationLandScape();
        } else if (what == 10001 && extra == 90) {
            setOrientationLandScape();
        }
    }

    @Override
    public void clickNarrow() {
        finish();
    }

    @Override
    public void clickBack() {
        //finish();
        mMenuPanel.initMenuPanel(this, Configuration.ORIENTATION_LANDSCAPE);
        mMenuPanel.setVisibility(View.VISIBLE);
        mMenuPanel.startEnterAnimation();
    }

    private void setOrientationPortrait() {
        //竖屏录制的视频
        //videoView.setPlayerRotation(90);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void setOrientationLandScape() {
        //横屏录制的视频
        //videoView.setPlayerRotation(0);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        //Log.d("zjy", "横屏position: " + mTimedTextTv.getY() + "  " + videoView.getRenderViewWidth() + "  " + videoView.getRenderViewHeight());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayerView.stopPlayback();
        if(unbinder != null){
            unbinder.unbind();
        }
        //EventBus.getDefault().post(new FullScreenExitEvent());
    }
}
