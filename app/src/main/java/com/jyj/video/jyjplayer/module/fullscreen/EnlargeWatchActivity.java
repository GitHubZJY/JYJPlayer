package com.jyj.video.jyjplayer.module.fullscreen;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zjy.player.ui.PlayerListener;
import com.example.zjy.player.ui.VideoFrame;
import com.example.zjy.player.ui.YPlayerView;
import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.constant.SpConstant;
import com.jyj.video.jyjplayer.event.PlaySettingCloseEvent;
import com.jyj.video.jyjplayer.event.SubtitleAsyncEvent;
import com.jyj.video.jyjplayer.event.SubtitleSwitchEvent;
import com.jyj.video.jyjplayer.filescan.model.FileVideoModel;
import com.jyj.video.jyjplayer.filescan.model.bean.FolderInfo;
import com.jyj.video.jyjplayer.filescan.model.bean.SubtitleInfo;
import com.jyj.video.jyjplayer.filescan.model.bean.VideoInfo;
import com.jyj.video.jyjplayer.manager.SpManager;
import com.jyj.video.jyjplayer.manager.VideoPlayDataManager;
import com.jyj.video.jyjplayer.module.fullscreen.widget.SpeedPanel;
import com.jyj.video.jyjplayer.subtitle.SRTUtils;
import com.jyj.video.jyjplayer.subtitle.SubTitleContainer;
import com.jyj.video.jyjplayer.utils.FileUtils;
import com.jyj.video.jyjplayer.utils.VideoUtil;
import com.zjyang.base.base.BaseActivity;
import com.zjyang.base.base.BasePresenter;
import com.zjyang.base.utils.SpUtils;
import com.zjyang.base.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
    @BindView(R.id.speed_panel)
    SpeedPanel mSpeedPanel;
    @BindView(R.id.subtitle_text_tv)
    TextView mSubtitleTv;

    VideoFrame mVideoFrame;

    private VideoInfo mVideoInfo;
    private FolderInfo mFolderInfo;

    private boolean mIsShowSubTitle;


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
        EventBus.getDefault().register(this);

        mVideoInfo = VideoPlayDataManager.getInstance().getCurPlayVideoInfo();
        if(mVideoInfo != null){
            mVideoInfo.setIsPlaying(true);
           initVideoParams();
        }
        mPlayerView.attachActivity(this);
        mPlayerView.setGestureEnable(true);
        mPlayerView.setPlayerListener(this);
        mPlayerView.setOnInfoListener(this);
        mVideoFrame = mPlayerView.getVideoFrame();



        String folderPath = VideoUtil.getParentPath(mVideoInfo.getPath());
        mFolderInfo = FileVideoModel.getFileInfo(folderPath);

        initVideoParams();
    }

    public void initVideoParams(){

        if(mVideoInfo == null){
            return;
        }

        mPlayerView.setVideoUrl(mVideoInfo.getPath());
        mPlayerView.setVideoTitle(mVideoInfo.getName());
        mPlayerView.start();
        mSubtitleTv.setText("");
        SubtitleInfo subtitleInfo = FileVideoModel.getSubtitleInfo(mVideoInfo.getPath());
        SRTUtils.clear();

        if(subtitleInfo != null && subtitleInfo.getAllSubtitles() != null && !subtitleInfo.getAllSubtitles().isEmpty()){
            String cacheSubPath = subtitleInfo.getCurSubtitle().getPath();
            if(!TextUtils.isEmpty(cacheSubPath)){
                //有字幕文件
                SRTUtils.parseSrt(cacheSubPath, true);
                mIsShowSubTitle = SpUtils.obtain(SpConstant.DEFAULT_SP_FILE).getBoolean(SpConstant.IS_OPEN_SUBTITLE, true);
                if(mIsShowSubTitle){
                    mSubtitleTv.setVisibility(View.VISIBLE);
                    SRTUtils.showSRT(mSubtitleTv, mPlayerView.getVideoFrame());
                }
            }
        }else{
            initDefaultSrt();
        }
    }

    /**
     * 初始化寻找同目录下是否有同名字幕文件
     */
    public void initDefaultSrt(){
        if(mVideoInfo == null){
            return;
        }
        String defaultSubtitle = SRTUtils.getRandomSrtInFolder(FileUtils.getFileDirPath(mVideoInfo.getPath()), mVideoInfo.getDisplayName());
        if(!TextUtils.isEmpty(defaultSubtitle)){
            //同目录下找到字幕文件
            FileVideoModel.createSubtitle(mVideoInfo.getPath(), defaultSubtitle);

            SRTUtils.parseSrt(defaultSubtitle, true);
            mIsShowSubTitle = SpUtils.obtain(SpConstant.DEFAULT_SP_FILE).getBoolean(SpConstant.IS_OPEN_SUBTITLE, true);
            if(mIsShowSubTitle){
                mSubtitleTv.setVisibility(View.VISIBLE);
                SRTUtils.showSRT(mSubtitleTv, mPlayerView.getVideoFrame());
            }
        }
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
    public void progress(int progress) {
        if(mIsShowSubTitle){
            SRTUtils.showSRT(mSubtitleTv, mPlayerView.getVideoFrame());
        }
    }

    @Override
    public void clickPre() {
        VideoInfo videoInfo = null;

        if (mFolderInfo != null) {
            videoInfo = mFolderInfo.getPre();
        }
        if(videoInfo != null){
            mVideoFrame.setRender(0);
            mVideoFrame.initRenders();
            mVideoFrame.stopPlayback();
            //先置当前视频为false
            mVideoInfo.setIsPlaying(false);
            //赋值新的视频
            mVideoInfo = videoInfo;
            //将新的视频设置为true
            mVideoInfo.setIsPlaying(true);
            initVideoParams();
        }else{
            ToastUtils.showToast(EnlargeWatchActivity.this, "Already the first video");
        }
    }

    @Override
    public void clickNext() {
        VideoInfo videoInfo = null;

        if (mFolderInfo != null) {
            videoInfo = mFolderInfo.getNext();
        }
        if(videoInfo != null){
            mVideoFrame.setRender(0);
            mVideoFrame.initRenders();
            mVideoFrame.stopPlayback();
            //先置当前视频为false
            mVideoInfo.setIsPlaying(false);
            //赋值新的视频
            mVideoInfo = videoInfo;
            //将新的视频设置为true
            mVideoInfo.setIsPlaying(true);
            initVideoParams();
        }else{
            ToastUtils.showToast(EnlargeWatchActivity.this, "Already the last video");
        }
    }

    @Override
    public void playComplete() {
        if(SpManager.getInstance().getAutoPlayNext()){
            clickNext();
        }
    }

    @Override
    public void clickNarrow() {
        finish();
    }

    @Override
    public void clickSubtitle() {
        mMenuPanel.initMenuPanel(this, Configuration.ORIENTATION_LANDSCAPE);
        mMenuPanel.setVisibility(View.VISIBLE);
        mMenuPanel.setVideoPath(mVideoInfo.getPath());
        mMenuPanel.startEnterAnimation();
    }

    @Override
    public void clickSpeed() {
        mVideoFrame.setSpeed(2);
        mSpeedPanel.initSpeedPanel(this, Configuration.ORIENTATION_LANDSCAPE);
        mSpeedPanel.setVisibility(View.VISIBLE);
        mSpeedPanel.startEnterAnimation();
    }

    @Override
    public void clickBack() {
        finish();
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


    @Subscribe
    public void onEvent(PlaySettingCloseEvent event){
        int type = event.getPanelType();
        switch (type){
            case PlaySettingCloseEvent.SUBTITLE_PANEL:
                //AndroidDevice.hideSoftInput(this);
                onWindowFocusChanged(true);
                String srtFileUrl = event.getSrtFileUrl();
                if(!TextUtils.isEmpty(srtFileUrl)){
                    //不为空路径，说明选择了某个字幕文件
                    //获取当前播放的视频信息，将选中的字幕信息一同存入数据库
                    VideoInfo videoInfo = VideoPlayDataManager.getInstance().getCurPlayVideoInfo();
                    if(videoInfo != null){
//                videoInfo.setSubtitleName(FileUtils.getFileNameInPath(srtFileUrl));
//                videoInfo.setSubtitlePath(srtFileUrl);
//                VideoHelper.getInstance().addOrReplace(videoInfo);
                        FileVideoModel.createSubtitle(videoInfo.getPath(), srtFileUrl);
                    }
                    SRTUtils.showSRT(mSubtitleTv, mPlayerView.getVideoFrame());
                    mIsShowSubTitle = true;
                }
                //收起菜单面板
                mMenuPanel.startExitAnimation();
                break;
            case PlaySettingCloseEvent.SPEED_PANEL:
                mSpeedPanel.startExitAnimation();
                break;
        }


    }

    @Subscribe
    public void onEvent(SubtitleAsyncEvent event){
        if(!event.isSuccess()){
            ToastUtils.showToast(this, getResources().getString(R.string.fail_load_tip));
        }else{
            if(!event.isAuto()){
                ToastUtils.showToast(this, getResources().getString(R.string.already_apply));
            }
        }
    }

    @Subscribe
    public void onSubtitleSwitchEvent(SubtitleSwitchEvent event){
        mIsShowSubTitle = SpUtils.obtain(SpConstant.DEFAULT_SP_FILE).getBoolean(SpConstant.IS_OPEN_SUBTITLE, true);
        if(mIsShowSubTitle){
            mSubtitleTv.setVisibility(View.VISIBLE);
        }else{
            mSubtitleTv.setVisibility(View.GONE);
        }
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) { //表示按返回键 时的操作
                //  return true;    //已处理
                if(mMenuPanel.getVisibility() == View.VISIBLE){
                    mMenuPanel.startExitAnimation();
                    return true;
                }else if(mSpeedPanel.getVisibility() == View.VISIBLE){
                    mSpeedPanel.startExitAnimation();
                    return true;
                }else{
                    onBackPressed();
                }
            }

        }

        return super.onKeyDown(keyCode, event);

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayerView.stopPlayback();
        if(unbinder != null){
            unbinder.unbind();
        }
        mVideoInfo.setIsPlaying(false);
        EventBus.getDefault().unregister(this);
        //EventBus.getDefault().post(new FullScreenExitEvent());
    }
}
