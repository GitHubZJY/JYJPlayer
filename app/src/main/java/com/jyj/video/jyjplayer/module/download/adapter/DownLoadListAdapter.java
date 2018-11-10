package com.jyj.video.jyjplayer.module.download.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.download.film.FilmDownLoadManager;
import com.jyj.video.jyjplayer.download.film.bean.DownLoadFilmInfo;
import com.jyj.video.jyjplayer.filescan.model.FileVideoModel;
import com.jyj.video.jyjplayer.filescan.model.bean.VideoInfo;
import com.jyj.video.jyjplayer.manager.FolderListPicManager;
import com.jyj.video.jyjplayer.utils.VideoUtil;
import com.zjyang.base.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.jyj.video.jyjplayer.download.Constant.Status.ERROR;
import static com.jyj.video.jyjplayer.download.Constant.Status.FINISH;
import static com.jyj.video.jyjplayer.download.Constant.Status.PAUSE;
import static com.jyj.video.jyjplayer.download.Constant.Status.PROGRESS;
import static com.jyj.video.jyjplayer.download.Constant.Status.RETRY;

/**
 * Created by zhengjiayang on 2018/5/8.
 */

public class DownLoadListAdapter extends RecyclerView.Adapter<DownLoadListAdapter.DownFilmListViewHolder>{

    private FragmentActivity mActivity;
    private Context mContext;
    private List<DownLoadFilmInfo> mDownFilmList;

    ExecutorService cachedThreadPool = Executors.newFixedThreadPool(3);

    public DownLoadListAdapter(Context mContext, FragmentActivity mActivity, List<DownLoadFilmInfo> mFilmList) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.mDownFilmList = mFilmList;
    }

    @Override
    public DownFilmListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_down_load_film, parent, false);
        return new DownFilmListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DownFilmListViewHolder holder, int position) {
        holder.setContent(position, mDownFilmList.get(position));
    }

    @Override
    public int getItemCount() {
        if(mDownFilmList == null ){
            return 0;
        }
        return mDownFilmList.size();
    }


    public class DownFilmListViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mRootView;
        private TextView mDownFilmNameTv;
        private RelativeLayout mDownStatusRlyt;
        private TextView mDownStatusTv;
        private ImageView mDownStatusIv;
        private TextView mCurSizeTv;
        private TextView mMaxSizeTv;
        private TextView mSpeedTv;
        private TextView mRemainTimeTv;
        private ImageView mSettingIv;
        private ImageView mPreviewPicIv;
        private ProgressBar mProgressBar;
        private ImageView mPauseIv;
        private ImageView mContinueIv;
        private TextView mPercentageTv;
        private ImageView mErrorIv;

        public DownFilmListViewHolder(View itemView) {
            super(itemView);

            mRootView = (RelativeLayout) itemView.findViewById(R.id.item_down_film_root_view);
            mDownFilmNameTv = (TextView) itemView.findViewById(R.id.item_down_load_name_tv);
            mDownStatusRlyt = (RelativeLayout) itemView.findViewById(R.id.item_down_load_status_rlyt);
            mDownStatusTv = (TextView) itemView.findViewById(R.id.item_down_load_status_tv);
            mDownStatusIv = (ImageView) itemView.findViewById(R.id.item_down_load_status_iv);
            mCurSizeTv = (TextView) itemView.findViewById(R.id.item_down_load_cur_size);
            mMaxSizeTv = (TextView) itemView.findViewById(R.id.item_down_load_max_size);
            mSpeedTv = (TextView) itemView.findViewById(R.id.item_down_load_speed);
            mRemainTimeTv = (TextView) itemView.findViewById(R.id.item_down_load_remain_second);
            mSettingIv = (ImageView) itemView.findViewById(R.id.setting);
            mPreviewPicIv = (ImageView) itemView.findViewById(R.id.preview_pic_iv);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.down_load_progress);
            mPauseIv = (ImageView) itemView.findViewById(R.id.pause_iv);
            mContinueIv = (ImageView) itemView.findViewById(R.id.continue_start_iv);
            mPercentageTv = (TextView) itemView.findViewById(R.id.percentage_tv);
            mErrorIv = (ImageView) itemView.findViewById(R.id.download_error_iv);
        }

        public void setContent(final int index, final DownLoadFilmInfo downLoadFilmInfo) {
            if (downLoadFilmInfo == null) {
                return;
            }
            String fileName = downLoadFilmInfo.getFileName();
            final float percentage = downLoadFilmInfo.getPercentage();
            mDownFilmNameTv.setText(TextUtils.isEmpty(fileName) ? "" : fileName);
            mPreviewPicIv.setTag(downLoadFilmInfo.getPath());
            mPreviewPicIv.setImageResource(R.drawable.bg_online_video_default);
            switch (downLoadFilmInfo.getStatus()){
                case ERROR:
                    mDownStatusTv.setText(mContext.getResources().getString(R.string.download_error));
                    mDownStatusTv.setTextColor(Color.parseColor("#fa3a3a"));
                    mDownStatusTv.setVisibility(View.VISIBLE);
                    //mDownStatusIv.setVisibility(View.VISIBLE);
                    //mDownStatusIv.setImageResource(R.drawable.icn_download_retry);
                    mCurSizeTv.setVisibility(View.VISIBLE);
                    mMaxSizeTv.setVisibility(View.VISIBLE);
                    mSpeedTv.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                    mContinueIv.setVisibility(View.GONE);
                    mPauseIv.setVisibility(View.GONE);
                    mErrorIv.setVisibility(View.VISIBLE);
                    mRemainTimeTv.setVisibility(View.GONE);
                    mRemainTimeTv.setText("");
                    break;
                case RETRY:
                    mDownStatusTv.setText(mContext.getResources().getText(R.string.download_auto_connecting));
                    mDownStatusTv.setTextColor(Color.parseColor("#00c1de"));
                    mDownStatusTv.setVisibility(View.VISIBLE);
                    //mDownStatusIv.setVisibility(View.GONE);
                    mCurSizeTv.setVisibility(View.VISIBLE);
                    mMaxSizeTv.setVisibility(View.VISIBLE);
                    mSpeedTv.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    mContinueIv.setVisibility(View.GONE);
                    mProgressBar.setProgress((int)percentage);
                    mPauseIv.setVisibility(View.VISIBLE);
                    //mPercentageTv.setText((int)percentage + "%");
                    mErrorIv.setVisibility(View.GONE);
                    mRemainTimeTv.setVisibility(View.VISIBLE);
                    mRemainTimeTv.setText(secondToTimeStr(downLoadFilmInfo.getRemainSecond()));
                    break;
                case PAUSE:
                    mDownStatusTv.setText(mContext.getResources().getString(R.string.download_pause));
                    mDownStatusTv.setTextColor(Color.parseColor("#00c1de"));
                    mDownStatusTv.setVisibility(View.VISIBLE);
                    //mDownStatusIv.setVisibility(View.GONE);
                    mCurSizeTv.setVisibility(View.VISIBLE);
                    mMaxSizeTv.setVisibility(View.VISIBLE);
                    mSpeedTv.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress((int)percentage);
                    mContinueIv.setVisibility(View.VISIBLE);
                    mPauseIv.setVisibility(View.GONE);
                    mErrorIv.setVisibility(View.GONE);
                    mRemainTimeTv.setVisibility(View.GONE);
                    mRemainTimeTv.setText("");
                    break;
                case FINISH:
                    mDownStatusTv.setText(mContext.getResources().getString(R.string.download_finish));
                    mDownStatusTv.setTextColor(Color.parseColor("#28d7f8"));
                    mDownStatusTv.setVisibility(View.VISIBLE);
                    //mDownStatusIv.setVisibility(View.VISIBLE);
                    //mDownStatusIv.setImageResource(R.drawable.icn_download_finish);
                    mCurSizeTv.setVisibility(View.GONE);
                    mMaxSizeTv.setVisibility(View.VISIBLE);
                    mSpeedTv.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                    mContinueIv.setVisibility(View.GONE);
                    mPauseIv.setVisibility(View.GONE);
                    mErrorIv.setVisibility(View.GONE);
                    mRemainTimeTv.setVisibility(View.GONE);
                    mRemainTimeTv.setText("");
                    getVideoIcon(DownLoadFilmInfo.getVideoInfo(downLoadFilmInfo));
                    break;
                default:
                    mDownStatusTv.setText("");
                    mDownStatusTv.setTextColor(Color.parseColor("#00c1de"));
                    mDownStatusTv.setVisibility(View.GONE);
                    //mDownStatusIv.setVisibility(View.GONE);
                    mCurSizeTv.setVisibility(View.VISIBLE);
                    mMaxSizeTv.setVisibility(View.VISIBLE);
                    mSpeedTv.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    mContinueIv.setVisibility(View.GONE);
                    mProgressBar.setProgress((int)percentage);
                    mPauseIv.setVisibility(View.VISIBLE);
                    //mPercentageTv.setText((int)percentage + "%");
                    mErrorIv.setVisibility(View.GONE);
                    mRemainTimeTv.setVisibility(View.VISIBLE);
                    mRemainTimeTv.setText(secondToTimeStr(downLoadFilmInfo.getRemainSecond()));
                    break;
            }
            mCurSizeTv.setText(byteToSize(downLoadFilmInfo.getCurrentLength())+" / ");
            mMaxSizeTv.setText(byteToSize(downLoadFilmInfo.getTotalLength())+"");
            mSpeedTv.setText(byteToSize(downLoadFilmInfo.getDownLoadSpeed())+"/" + "s");

            mSettingIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //showItemSettingPopup(view, downLoadFilmInfo, false);
                }
            });

            mErrorIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(downLoadFilmInfo.getStatus() == ERROR){
                        //只有在错误状态下，才可点击重试
                        FilmDownLoadManager.getInstance(mContext).retryFailTask(downLoadFilmInfo.getUrl(), FilmDownLoadManager.CLICK_RETRY);
                        mProgressBar.setVisibility(View.VISIBLE);
                        mDownStatusTv.setVisibility(View.GONE);
                        //mDownStatusIv.setVisibility(View.GONE);
                        mSpeedTv.setVisibility(View.VISIBLE);
                        mProgressBar.setProgress((int)percentage);
                        mPauseIv.setVisibility(View.VISIBLE);
                        //mPercentageTv.setText((int)percentage + "%");
                        mErrorIv.setVisibility(View.GONE);
                    }
                }
            });

            mContinueIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(downLoadFilmInfo.getStatus() == PAUSE){
                        FilmDownLoadManager.getInstance(mContext).resume(downLoadFilmInfo.getUrl());
                    }
                }
            });

            mPauseIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(downLoadFilmInfo.getStatus() == PROGRESS){
                        FilmDownLoadManager.getInstance(mContext).pause(downLoadFilmInfo.getUrl());
                        FilmDownLoadManager.getInstance(mContext).removeVideoFromAutoPauseList(downLoadFilmInfo.getUrl());
                    }
                }
            });

            mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String parentFolderPath = "";
                    if (downLoadFilmInfo != null) {
                        parentFolderPath = VideoUtil.getParentPath(downLoadFilmInfo.getPath());
                    }
//                    DownFilmPlayManager.getInstance().setmIsFromOnlineFilm(false);
//                    DownFilmPlayManager.getInstance().filmListToVideoList(mDownFilmList);
//                    DownFilmPlayManager.getInstance().setCurPlayFilm(downLoadFilmInfo);
//                    FloatVideoViewManager.getInstance().setmCurrentPlayPath("");
//                    FloatVideoViewManager.getInstance().setmPrivacyList(DownFilmPlayManager.getInstance().getVideoListPath());
//                    LogUtil.e(FloatWindowManager.TAG, "电影下载列表当前是否展示悬浮窗:" + FloatWindowManager.getInstance().getmIsShowFloating());
//                    if (FloatWindowManager.getInstance().getmIsShowFloating()) {
//                        EventBus.getDefault().post(new FloatingRefreshVideoEvent(downLoadFilmInfo.getPath()));
//                    } else {
//                        PlayVideoActivity.go(mContext, DownFilmPlayManager.getInstance().getVideoListPath(), false);
//                        LogUtil.e(FloatWindowManager.TAG,"电影下载列表拉起全屏播放视频");
//
//                        // 展示广告
//                        if (DownloadPlayAdController.getInstance().haveAdCache() && DownloadPlayAdController.getInstance().isAdLoaded()) {
//                            DownloadPlayAdController.getInstance().onAdShow();
//                        }
//                    }
                }
            });
        }

        private void getVideoIcon(final VideoInfo videoInfo) {
            if (cachedThreadPool.isShutdown()) {
                return;
            }
            cachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    final Bitmap icon = FolderListPicManager.loadVideoIcon(videoInfo);//VideoUtil.createVideoThumbnail(videoInfo.getPath());
                    if(icon == null){
                        LogUtil.d("FolderListPicManager", "film list icon is null");
                    }
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mPreviewPicIv.getTag().equals(videoInfo.getPath())) {
                                if (icon != null) {
                                    mPreviewPicIv.setImageBitmap(icon);
                                }
                            }
                        }
                    });
                }
            });
        }
    }

    public void notifyDownLoadData(){
        mDownFilmList.clear();
        List<DownLoadFilmInfo> cachelist = FileVideoModel.getDownFilmInfoCached();
        mDownFilmList.addAll(cachelist);
        notifyDataSetChanged();
    }


    public String secondToTimeStr(int second){
        if(second <= 0 || second == -1){
            return "0" + "s";
        }else if(second >= 24 * 60 * 60){
            return "more than 24 hours";
        }else{
            if(second < 60){
                return second + "s" + " " + "left";
            }else if(second >= 60 && second < 60*60){
                return second/60 + "min" + second%60 + "s" + " " + "left";
            }else{
                return second/(60*60) + "hour" + (second%(60*60))/60 + "min" + (second - 60*60)%60 + "s" + " " + "left";
            }
        }
    }


    public String byteToSize(long bytes){
        if(bytes <= 0 || bytes == -1){
            return "0kb";
        }else{
            if(bytes < 1000){
                return bytes + "b";
            }else if(bytes >= 1000 && bytes < 1000*1000){
                return bytes/1000 + "kb";
            }else if(bytes >= 1000*1000 && bytes < 1000*1000*1000){
                float mbSize = (float)bytes / (1000f*1000f);
                mbSize = (float)(Math.round(mbSize*100))/100;
                return mbSize + "M";
            }else{
                float mbSize = (float)bytes / (1000f*1000f*1000f);
                mbSize = (float)(Math.round(mbSize*100))/100;
                return mbSize + "G";
            }
        }
    }

}
