package com.example.zjy.player.ui;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;

import com.example.zjy.player.setting.VideoSetting;
import com.example.zjy.player.ui.system.SystemVideoView;
import com.example.zjy.player.utils.LogUtil;

import tv.danmaku.ijk.media.example.widget.media.IjkVideoView;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by 74215 on 2018/3/26.
 * 封装播放View, 可切换系统播放器/Ijk播放器
 */

public class VideoFrame extends RelativeLayout {

    public static final String TAG = "VideoFrame";

    private SystemVideoView mAndroidVideoView;
    private IjkVideoView mIjkVideoView;

    public VideoFrame(Context context) {
        this(context, null);
    }

    public VideoFrame(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoFrame(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void initView(Context context) {

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(CENTER_IN_PARENT);
        mAndroidVideoView = new SystemVideoView(context);
        mAndroidVideoView.setLayoutParams(params);

        mIjkVideoView = new IjkVideoView(context);
        mIjkVideoView.setLayoutParams(params);

        ImageView videoBg = new ImageView(context);
        videoBg.setBackgroundColor(Color.BLACK);
        RelativeLayout.LayoutParams bgParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        videoBg.setLayoutParams(bgParams);
        addView(videoBg);


        switch (VideoSetting.getVideoType()){
            case VideoSetting.ANDROID_VIDEO_VIEW:
                LogUtil.d(TAG, "System--->初始化播放器");
                mIjkVideoView.setVisibility(GONE);
                mAndroidVideoView.setVisibility(VISIBLE);
                addView(mAndroidVideoView);
                break;
            case VideoSetting.IJK_VIDEO_VIEW:
                LogUtil.d(TAG, "IjK--->初始化播放器");
                mIjkVideoView.setVisibility(VISIBLE);
                mAndroidVideoView.setVisibility(GONE);
                addView(mIjkVideoView);
                break;
        }

    }

    public void setVideoUrl(String videoUrl){
        switch (VideoSetting.getVideoType()){
            case VideoSetting.ANDROID_VIDEO_VIEW:
                LogUtil.d(TAG, "System--->setVideoUrl");
                mAndroidVideoView.setVideoURI(Uri.parse(videoUrl));
                break;
            case VideoSetting.IJK_VIDEO_VIEW:
                LogUtil.d(TAG, "IjK--->setVideoUrl");
                mIjkVideoView.setVideoURI(Uri.parse(videoUrl));
                break;
        }

    }

    public void start(){
        switch (VideoSetting.getVideoType()){
            case VideoSetting.ANDROID_VIDEO_VIEW:
                LogUtil.d(TAG, "System--->start");
                mAndroidVideoView.start();
                break;
            case VideoSetting.IJK_VIDEO_VIEW:
                LogUtil.d(TAG, "IjK--->start");
                mIjkVideoView.start();
                break;
        }

    }

    public void setHudView(TableLayout hudView){
        switch (VideoSetting.getVideoType()){
            case VideoSetting.ANDROID_VIDEO_VIEW:
                LogUtil.d(TAG, "System--->setHudView");
                break;
            case VideoSetting.IJK_VIDEO_VIEW:
                LogUtil.d(TAG, "IjK--->setHudView");
                mIjkVideoView.setHudView(hudView);
                break;
        }
    }

    public boolean isPlaying(){
        switch (VideoSetting.getVideoType()){
            case VideoSetting.ANDROID_VIDEO_VIEW:
                return mAndroidVideoView.isPlaying();
            case VideoSetting.IJK_VIDEO_VIEW:
                return mIjkVideoView.isPlaying();
            default:
                return false;
        }
    }

    public boolean isInPlaybackState(){
        switch (VideoSetting.getVideoType()){
            case VideoSetting.ANDROID_VIDEO_VIEW:
                return mAndroidVideoView.isInPlaybackState();
            case VideoSetting.IJK_VIDEO_VIEW:
                return mIjkVideoView.isInPlaybackState();
            default:
                return false;
        }
    }

    public void pause(){
        switch (VideoSetting.getVideoType()){
            case VideoSetting.ANDROID_VIDEO_VIEW:
                LogUtil.d(TAG, "System--->pause");
                mAndroidVideoView.pause();
                break;
            case VideoSetting.IJK_VIDEO_VIEW:
                LogUtil.d(TAG, "IjK--->pause");
                mIjkVideoView.pause();
                break;
        }
    }

    public void seekTo(int position){
        switch (VideoSetting.getVideoType()){
            case VideoSetting.ANDROID_VIDEO_VIEW:
                mAndroidVideoView.seekTo(position);
                break;
            case VideoSetting.IJK_VIDEO_VIEW:
                mIjkVideoView.seekTo(position);
                break;
        }
    }

    public int getCurrentPosition(){
        switch (VideoSetting.getVideoType()){
            case VideoSetting.ANDROID_VIDEO_VIEW:
                return mAndroidVideoView.getCurrentPosition();
            case VideoSetting.IJK_VIDEO_VIEW:
                return mIjkVideoView.getCurrentPosition();
            default:
                return -1;
        }
    }

    public int getDuration(){
        switch (VideoSetting.getVideoType()){
            case VideoSetting.ANDROID_VIDEO_VIEW:
                return (int)mAndroidVideoView.getDuration();
            case VideoSetting.IJK_VIDEO_VIEW:
                return mIjkVideoView.getDuration();
            default:
                return -1;
        }
    }

    public void stopPlayback(){
        switch (VideoSetting.getVideoType()){
            case VideoSetting.ANDROID_VIDEO_VIEW:
                LogUtil.d(TAG, "System--->stopPlayback");
                mAndroidVideoView.release();
                break;
            case VideoSetting.IJK_VIDEO_VIEW:
                LogUtil.d(TAG, "IjK--->stopPlayback");
                mIjkVideoView.pause();
                mIjkVideoView.stopBackgroundPlay();
                mIjkVideoView.stopPlayback();
                break;
        }
    }

    public void setOnPrePareListener(final OnPreparedListener prePareListener){
        switch (VideoSetting.getVideoType()){
            case VideoSetting.ANDROID_VIDEO_VIEW:
                LogUtil.d(TAG, "System--->setOnPrePareListener");
                mAndroidVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        if(prePareListener != null){
                            prePareListener.onPrepared(null, mediaPlayer);
                        }
                    }
                });
                break;
            case VideoSetting.IJK_VIDEO_VIEW:
                LogUtil.d(TAG, "IjK--->setOnPreparedListener");
                mIjkVideoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(IMediaPlayer mp) {
                        if(prePareListener != null){
                            prePareListener.onPrepared(mp, null);
                        }
                    }
                });
                break;
        }
    }

    public void setOnErrorListener(final OnErrorListener errorListener){
        switch (VideoSetting.getVideoType()){
            case VideoSetting.ANDROID_VIDEO_VIEW:
                LogUtil.d(TAG, "System--->setOnErrorListener");
                mAndroidVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                        if(errorListener != null){
                            LogUtil.d(TAG, "System--->onError: " + what + ", " + extra);
                            errorListener.onError(null, mediaPlayer, what, extra);
                        }
                        return true;
                    }
                });
                break;
            case VideoSetting.IJK_VIDEO_VIEW:
                LogUtil.d(TAG, "IjK--->setOnErrorListener");
                mIjkVideoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(IMediaPlayer mediaPlayer, int what, int extra) {
                        if(errorListener != null){
                            LogUtil.d(TAG, "IjK--->onError: " + what + ", " + extra);
                            errorListener.onError(mediaPlayer, null, what, extra);
                        }
                        return true;
                    }
                });
                break;
        }
    }

    public void setOnCompletionListener(final OnCompleteListener completeListener){
        switch (VideoSetting.getVideoType()){
            case VideoSetting.ANDROID_VIDEO_VIEW:
                mAndroidVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        if(completeListener != null){
                            LogUtil.d(TAG, "System--->onCompletion");
                            completeListener.onCompletion(null, mediaPlayer);
                        }
                    }
                });
                break;
            case VideoSetting.IJK_VIDEO_VIEW:
                mIjkVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(IMediaPlayer mediaPlayer) {
                        if(completeListener != null){
                            LogUtil.d(TAG, "IjK--->onCompletion");
                            completeListener.onCompletion(mediaPlayer, null);
                        }
                    }
                });
                break;
        }
    }

    public void setOnInfoListener(final OnInfoListener infoListener){
        switch (VideoSetting.getVideoType()){
            case VideoSetting.ANDROID_VIDEO_VIEW:
                mAndroidVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                        if(infoListener != null){
                            infoListener.onInfo(null, mediaPlayer, i, i1);
                        }
                        return true;
                    }
                });
                break;
            case VideoSetting.IJK_VIDEO_VIEW:
                mIjkVideoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                        if(infoListener != null){
                            infoListener.onInfo(mp, null, what, extra);
                        }
                        return true;
                    }
                });
                break;
        }
    }


    public interface OnPreparedListener{
        void onPrepared(IMediaPlayer iMediaPlayer, MediaPlayer mediaPlayer);
    }

    public interface OnErrorListener{
        void onError(IMediaPlayer imp, MediaPlayer mp, int what, int extra);
    }

    public interface OnCompleteListener{
        void onCompletion(IMediaPlayer imp, MediaPlayer mp);
    }

    public interface OnInfoListener {
        void onInfo(IMediaPlayer imp, MediaPlayer mp, int what, int extra);
    }
}
