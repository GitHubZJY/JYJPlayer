package com.jyj.video.jyjplayer.download.film;

import android.content.Context;

import com.jyj.video.jyjplayer.AppApplication;
import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.constant.TimeConstant;
import com.jyj.video.jyjplayer.download.DownloadUtils;
import com.jyj.video.jyjplayer.download.ErrorCode;
import com.jyj.video.jyjplayer.download.film.bean.DownLoadFilmInfo;
import com.jyj.video.jyjplayer.filescan.model.FileVideoModel;
import com.jyj.video.jyjplayer.utils.FileUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.zjyang.base.utils.HandlerUtils;
import com.zjyang.base.utils.LogUtil;
import com.zjyang.base.utils.ToastUtils;

import java.security.InvalidParameterException;

import static com.jyj.video.jyjplayer.download.Constant.CallbackType.ON_EEROR;
import static com.jyj.video.jyjplayer.download.Constant.CallbackType.ON_FINISH;
import static com.jyj.video.jyjplayer.download.Constant.CallbackType.ON_PAUSE;
import static com.jyj.video.jyjplayer.download.Constant.CallbackType.ON_PROGRESS;
import static com.jyj.video.jyjplayer.download.Constant.CallbackType.ON_RETRY;
import static com.jyj.video.jyjplayer.download.Constant.CallbackType.ON_START;
import static com.jyj.video.jyjplayer.download.Constant.Status.ERROR;
import static com.jyj.video.jyjplayer.download.Constant.Status.FINISH;
import static com.jyj.video.jyjplayer.download.Constant.Status.NONE;
import static com.jyj.video.jyjplayer.download.Constant.Status.PAUSE;
import static com.jyj.video.jyjplayer.download.Constant.Status.PROGRESS;
import static com.jyj.video.jyjplayer.download.Constant.Status.START;

/**
 * Created by zhengjiayang on 2018/5/7.
 */

public class DownFilmTask {

    public static final String TAG = "DownFilmTask";

    private String url;
    private String path;
    private String name;
    private String suffix;
    private String lastFinishPath;
    private int mRetryType = FilmDownLoadManager.CLICK_RETRY;//重试类型

    private Context context;

    private DownLoadFilmInfo downloadData;

    private int mCurrentState = NONE;

    //记录已经下载的大小
    private int currentLength = 0;
    //记录文件总大小
    private int totalLength = 0;

    private long lastProgressTime;

    private BaseDownloadTask downloadTask;

    private static long NET_ANR_PAUSE_MAX_LIMIT = 30 * TimeConstant.ONE_SEC;

    private boolean isResponsed = false;

    String mDownloadPath;

    public DownFilmTask(final Context context, final DownLoadFilmInfo downloadData, final String downloadingUrl) {
        init(context, downloadData, downloadingUrl);
    }


    private void init(final Context context, final DownLoadFilmInfo downloadData, final String downloadingUrl) {
        this.context = context;

        this.url = downloadingUrl;
        this.path = downloadData.getPath();
        this.name = downloadData.getFileName();
        this.suffix = downloadData.getFileType();
        LogUtil.e(TAG, "DownloadProgressHandler " + url);

        DownLoadFilmInfo dbData = DownFilmHelper.getInstance().queryInfoByUrl(url);
        this.downloadData = dbData == null ? downloadData : dbData;
        //this.downloadData = downloadData;
    }

    public void startDownload() {
        if (downloadData == null) {
            LogUtil.e(TAG, "startDownload ERROR !! downloadData == null");
            return;
        }
        if(downloadTask != null){
            downloadTask.pause();
        }
        downloadTask = FileDownloader.getImpl().create(url);
        LogUtil.d(TAG, ""+downloadTask);
        mDownloadPath = path;
        if(path.endsWith(".temp")){
            mDownloadPath = FileUtils.getReallyFileName(path);
        }
        downloadTask.setWifiRequired(false);
        downloadTask.setPath(mDownloadPath);
        downloadTask.setCallbackProgressMinInterval(1000);
        downloadTask.setCallbackProgressTimes(Integer.MAX_VALUE);
        downloadTask.setAutoRetryTimes(5);
        downloadTask.setSyncCallback(true);
        downloadTask.setForceReDownload(true);
        downloadTask.setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        LogUtil.e(TAG, "pending soFarBytes=" + soFarBytes + "  totalBytes=" + totalBytes + "  " + task);

                        LogUtil.e(TAG, "START url= " + url);

                        totalLength = totalBytes;
                        currentLength = soFarBytes;
                        LogUtil.e(TAG, " totalLength " + totalLength + " currentLength " + currentLength);

                        // 多图多视频组合
                        updateProgress2DB(START);

                        HandlerUtils.post(new Runnable() {
                            @Override
                            public void run() {
                                FilmDownLoadManager.getInstance(context).onCallback(ON_START, downloadData);
                            }
                        });

                        HandlerUtils.postDelay(new Runnable() {
                            @Override
                            public void run() {
                                if (!isResponsed && downloadData.getStatus() != ERROR) {
                                    updateProgress2DB(ERROR);
                                    FilmDownLoadManager.getInstance(context).onCallback(ON_EEROR, downloadData, downloadData.getUrl(), ErrorCode.NET_ERROR);
                                }
                            }
                        }, NET_ANR_PAUSE_MAX_LIMIT);
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        LogUtil.e(TAG, "connected etag=" + etag + "  isContinue=" + isContinue + "  soFarBytes=" + soFarBytes + "  totalBytes=" + totalBytes + "  " + task);

                        LogUtil.e(TAG, "START url= " + url);

                        totalLength = totalBytes;
                        currentLength = soFarBytes;
                        LogUtil.e(TAG, " totalLength " + totalLength + " currentLength " + currentLength);

                        updateProgress2DB(START);

                        HandlerUtils.post(new Runnable() {
                            @Override
                            public void run() {
                                FilmDownLoadManager.getInstance(context).onCallback(ON_START, downloadData);
                            }
                        });

                    }

                    @Override
                    protected void started(BaseDownloadTask task) {
                        super.started(task);
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        isResponsed = true;
                        int percent = (int) ((double) task.getSmallFileSoFarBytes() / (double) task.getSmallFileTotalBytes() * 100);
                        LogUtil.e(TAG, "progress soFarBytes=" + soFarBytes + "  totalBytes=" + totalBytes + " percent=" + percent);
                        int speed = soFarBytes - currentLength;
                        int remainSize = totalBytes - soFarBytes;
                        int second = 0;
                        if(speed != 0){
                            second = remainSize / speed;
                        }
                        downloadData.setDownLoadSpeed(downloadTask.getSpeed()*1000);
                        downloadData.setRemainSecond(second);
                        currentLength = soFarBytes;

                        synchronized (this) {

                            lastProgressTime = System.currentTimeMillis();
                            updateProgress2DB(PROGRESS);

                            HandlerUtils.post(new Runnable() {
                                @Override
                                public void run() {
                                    FilmDownLoadManager.getInstance(context).onCallback(ON_PROGRESS, downloadData);
                                }
                            });
                        }
                    }

                    @Override
                    protected void blockComplete(BaseDownloadTask task) {
                        LogUtil.e(TAG, "blockComplete " + task);
                    }

                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                        LogUtil.e(TAG, "retry soFarBytes=" + soFarBytes + "  retryingTimes=" + retryingTimes + "  Throwable=" + ex + "  " + task);
                        HandlerUtils.post(new Runnable() {
                            @Override
                            public void run() {
                                FilmDownLoadManager.getInstance(context).onCallback(ON_RETRY, downloadData);
                            }
                        });
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        LogUtil.e(TAG, "completed " + task);
                        downloadData.setStatus(FINISH);
                        lastFinishPath = path;

                        downloadData.setTotalLength(totalLength);
                        downloadData.setCurrentLength(currentLength);
                        downloadData.setLastModify(lastProgressTime);
                        downloadData.setPercentage(100f);
                        downloadData.setStatus(FINISH);
                        downloadData.setLastFinishPath(lastFinishPath);
                        downloadData.setFileName(name+suffix);
                        downloadData.setDisplayName(name+suffix);
                        downloadData.setPath(mDownloadPath+suffix);
                        LogUtil.e(TAG, "FINISH lastFinishPath = " + downloadData.getLastFinishPath());

                        LogUtil.e(TAG, "FINISH " + downloadData.getStatus());
                        DownFilmHelper.getInstance().addOrReplace(downloadData);
                        FileVideoModel.addOrReplaceDownloadFilm(downloadData);
                        FileUtils.renameSingleFile(mDownloadPath, mDownloadPath+suffix);
                        HandlerUtils.post(new Runnable() {
                            @Override
                            public void run() {
                                FilmDownLoadManager.getInstance(context).onCallback(ON_FINISH, downloadData);
                            }
                        });
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        LogUtil.e(TAG, "paused soFarBytes=" + soFarBytes + "  totalBytes=" + totalBytes + "  " + task);

                        synchronized (this) {
                            if(DownFilmHelper.getInstance().queryInfoByUrl(url) != null){
                                updateProgress2DB(PAUSE);
                            }
                            FilmDownLoadManager.getInstance(context).onCallback(ON_PAUSE, downloadData);
                        }
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        LogUtil.e(TAG, "paused Throwable=" + e + "  " + task);
                        if(e instanceof InvalidParameterException){
                            DownFilmHelper.getInstance().deleteByUrl(url);
                            ToastUtils.showToast(context, context.getResources().getString(R.string.cannot_download_tip));
                            return;
                        }
                        updateProgress2DB(ERROR);
                        // TODO 其他失败类型
                        HandlerUtils.post(new Runnable() {
                            @Override
                            public void run() {
                                FilmDownLoadManager.getInstance(context).onCallback(ON_EEROR, downloadData, downloadData.getUrl(), ErrorCode.REQUEST_FAILURE);
                            }
                        });
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        LogUtil.e(TAG, "warn " + task);
                        continueDownLoad(task);
                    }
                })
                .start();
    }

    private void continueDownLoad(BaseDownloadTask task) {
        while (task.getSmallFileSoFarBytes() != task.getSmallFileTotalBytes()) {
            int percent = (int) ((double) task.getSmallFileSoFarBytes() / (double) task.getSmallFileTotalBytes() * 100);

            LogUtil.e(TAG, "continueDownLoad percent=" + percent + task);

        }
    }

    public void updateProgress2DB(int status) {
        if(!FilmDownLoadManager.getInstance(AppApplication.getContext()).isExistInDownloadMap(downloadData)){
            return;
        }
        downloadData.setStatus(status);
        downloadData.setPercentage(DownloadUtils.getPercentage(currentLength, totalLength));
        downloadData.setCurrentLength(currentLength);
        downloadData.setTotalLength(totalLength);
        downloadData.setLastModify(lastProgressTime);
        downloadData.setDisplayName(name);


        DownFilmHelper.getInstance().addOrReplace(downloadData);
        FileVideoModel.addOrReplaceDownloadFilm(downloadData);
    }


    public int getCurrentState() {
        return mCurrentState;
    }

    public DownLoadFilmInfo getDownloadData() {
        return downloadData;
    }

    public void pause() {
        if (downloadTask != null) {
            LogUtil.e(TAG, "暂停任务: " + downloadData.getUrl());
            downloadTask.pause();
        }
    }

    public int getRetryType() {
        return mRetryType;
    }

    public void setRetryType(int mRetryType) {
        this.mRetryType = mRetryType;
    }
}
