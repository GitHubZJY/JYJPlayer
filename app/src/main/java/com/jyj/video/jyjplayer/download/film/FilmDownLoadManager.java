package com.jyj.video.jyjplayer.download.film;


import android.content.Context;
import android.text.TextUtils;

import com.jyj.video.jyjplayer.utils.FileUtils;
import com.jyj.video.jyjplayer.utils.SdcardUtil;
import com.jyj.video.jyjplayer.AppApplication;
import com.jyj.video.jyjplayer.constant.SpConstant;
import com.jyj.video.jyjplayer.download.Constant;
import com.jyj.video.jyjplayer.download.ErrorCode;
import com.jyj.video.jyjplayer.download.ThreadPool;
import com.jyj.video.jyjplayer.download.callback.InsDownloadCallback;
import com.jyj.video.jyjplayer.download.film.bean.DownLoadFilmInfo;
import com.jyj.video.jyjplayer.filescan.model.FileVideoModel;
import com.google.gson.reflect.TypeToken;
import com.liulishuo.filedownloader.FileDownloadList;
import com.liulishuo.filedownloader.FileDownloader;
import com.zjyang.base.utils.LogUtil;
import com.zjyang.base.utils.SpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.jyj.video.jyjplayer.download.Constant.CallbackType.ON_ALREADY_EXIST;
import static com.jyj.video.jyjplayer.download.Constant.CallbackType.ON_CANCEL;
import static com.jyj.video.jyjplayer.download.Constant.CallbackType.ON_CHECK_SUCCESS;
import static com.jyj.video.jyjplayer.download.Constant.CallbackType.ON_DESTROY;
import static com.jyj.video.jyjplayer.download.Constant.CallbackType.ON_EEROR;
import static com.jyj.video.jyjplayer.download.Constant.CallbackType.ON_FINISH;
import static com.jyj.video.jyjplayer.download.Constant.CallbackType.ON_INVALID_URL;
import static com.jyj.video.jyjplayer.download.Constant.CallbackType.ON_NOT_INS_URL;
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
 * Created by zhengjiayang on 2018/5/2.
 */
public class FilmDownLoadManager {
    private static final String TAG = "FilmDownLoadManager";
    public static final int CLICK_RETRY = 1000;
    public static final int SCREEN_ON_RETRY = 1001;
    public static final int WIFI_CONNECT_RETRY = 1002;

    private Context context;
    private List<String> mAutoPauseList = new ArrayList<>();

    private Map<String, DownLoadFilmInfo> downloadDataMap = new HashMap<>(); // 保存任务数据
    private Map<String, DownFilmTask> downloadTaskMap = new HashMap<>(); // 保存下载线程

    private static final List<DownFilmListener> callbackList = new ArrayList<DownFilmListener>(); // 保存任务回调 包含InsDownloadCallback

    private volatile static FilmDownLoadManager downloadManager;

    public static FilmDownLoadManager getInstance(Context context) {
        if (downloadManager == null) {
            synchronized (FilmDownLoadManager.class) {
                if (downloadManager == null) {
                    downloadManager = new FilmDownLoadManager(context);
                }
            }
        }
        if(FileDownloader.getImpl().isServiceConnected()){
            FileDownloader.getImpl().setMaxNetworkThreadCount(10);
        }
        return downloadManager;
    }

    private FilmDownLoadManager(Context context) {
        this.context = context;
    }

    public void addDownloadCallback(DownFilmListener downloadCallback) {
        if (downloadCallback == null) {
            return;
        }
        synchronized (callbackList) {
            if (!callbackList.contains(downloadCallback)) {
                callbackList.add(downloadCallback);
            }
        }
    }

    public void removeDownloadCallback(DownFilmListener downloadCallback) {
        if (downloadCallback == null) {
            return;
        }
        synchronized (callbackList) {
            callbackList.remove(downloadCallback);
        }
    }

    public void onCallback(int type, int errorCode) {
        if (callbackList.isEmpty()) {
            LogUtil.e(TAG, "onCallback0 !!!!!!!!!!!callbackList Empty  ");
        }
        onCallback(type, null, "", errorCode);
    }

    public void onCallback(int type, String insUrl) {
        if (callbackList.isEmpty()) {
            LogUtil.e(TAG, "onCallback1 !!!!!!!!!!!callbackList Empty  ");
        }
        onCallback(type, null, insUrl, -1);
    }

    public void onCallback(int type, String insUrl, int errorCode) {
        if (callbackList.isEmpty()) {
            LogUtil.e(TAG, "onCallback2 !!!!!!!!!!!callbackList Empty  ");
        }
        onCallback(type, null, insUrl, errorCode);
    }

    public void onCallback(int type, DownLoadFilmInfo download) {
        if (callbackList.isEmpty()) {
            LogUtil.e(TAG, "onCallback3 !!!!!!!!!!!callbackList Empty  ");
        }
        onCallback(type, download, download.getUrl(), -1);
    }

    public void onCallback(int type, DownLoadFilmInfo download, int errorCode) {
        if (callbackList.isEmpty()) {
            LogUtil.e(TAG, "onCallback4 !!!!!!!!!!!callbackList Empty  ");
        }
        onCallback(type, download, download.getUrl(), errorCode);
    }

    public void onCallback(final int type, final DownLoadFilmInfo download, final String insUrl, final int errorCode) {
        if (callbackList.isEmpty()) {
            LogUtil.e(TAG, "onCallback5 !!!!!!!!!!!callbackList Empty  ");
        }

        synchronized (callbackList) {
            for (DownFilmListener c : callbackList) {
                if (c == null) {
                    LogUtil.e(TAG, "onCallback for !!!!!!!!!!!NULL  ");
                    continue;
                }
                if(!isExistInDownloadMap(download)){
                    return;
                }
                switch (type) {
                    case ON_START:
                        LogUtil.e(TAG, "onCallback onStart  ");
                        c.onStart(download);
                        break;
                    case ON_PROGRESS:
                        LogUtil.e(TAG, "onCallback onProgress  " + (download == null ? - 9 : download.getPercentage()));
                        c.onProgress(download);
                        break;
                    case ON_PAUSE:
                        LogUtil.e(TAG, "onCallback onPause  ");
                        c.onPause(download);
                        break;
                    case ON_FINISH:
                        LogUtil.e(TAG, "onCallback onFinish  ");
                        c.onFinish(download);
                        break;
                    case ON_CANCEL:
                        LogUtil.e(TAG, "onCallback onCancel  ");
                        c.onCancel(download);
                        break;
                    case ON_DESTROY:
                        LogUtil.e(TAG, "onCallback ON_DESTROY  ");
                        break;
                    case ON_EEROR:
                        LogUtil.e(TAG, "onCallback onError  " + errorCode);
                        c.onError(errorCode, download);
                        LogUtil.e(TAG, "onCallback status  " + (download == null ? "-1" : download.getStatus()));
                        if (download != null && download.getStatus() == NONE) {
                            destroy(insUrl);
                        }
                        break;
                    case ON_ALREADY_EXIST:
                        LogUtil.e(TAG, "onCallback onAlreadyExist  " + errorCode);
                        if (c instanceof InsDownloadCallback) {
                            ((InsDownloadCallback) c).onAlreadyExist();
                        }
                        break;
                    case ON_RETRY:
                        LogUtil.e(TAG, "onCallback onRetry");
                        c.onRetry(download);
                        break;
                    case ON_INVALID_URL:
                        LogUtil.e(TAG, "onCallback onInvalidUrl  ");
                        if (c instanceof InsDownloadCallback) {
                            ((InsDownloadCallback) c).onInvalidUrl();
                        }
                        break;
                    case ON_NOT_INS_URL:
                        LogUtil.e(TAG, "onCallback onNotInsUrl  ");
                        if (c instanceof InsDownloadCallback) {
                            ((InsDownloadCallback) c).onNotInsUrl();
                        }
                        break;
                    case ON_CHECK_SUCCESS:
                        LogUtil.e(TAG, "onCallback onCheckSuccess  ");
                        if (c instanceof InsDownloadCallback) {
                            ((InsDownloadCallback) c).onCheckSuccess();
                        }
                        break;
                }
            }
        }
    }

    /**
     * 配置线程池
     *
     * @param corePoolSize
     * @param maxPoolSize
     */
    public void setTaskPoolSize(int corePoolSize, int maxPoolSize) {
        if (maxPoolSize > corePoolSize && maxPoolSize * corePoolSize != 0) {
            ThreadPool.getInstance().setCorePoolSize(corePoolSize);
            ThreadPool.getInstance().setMaxPoolSize(maxPoolSize);
        }
    }
    /**
     * data + callback 形式直接开始下载
     *
     * @param
     * @return
     */
    public FilmDownLoadManager start(String url, String fileName, String suffix, int entrance, String webUrl) {
        DownLoadFilmInfo downloadData = new DownLoadFilmInfo();
        downloadData.setUrl(url);
        downloadData.setFileName(fileName);
        downloadData.setFileType(suffix);
        downloadData.setDownEntrance(entrance);
        downloadData.setmWebViewUrl(webUrl);
        String checkedFilePath = FileUtils.checkFilmDuplication((SdcardUtil.DOWNLOAD_FILM_SAVE_PATH + downloadData.getFileName() + suffix), downloadData.getFileName());
        LogUtil.d("zjy", "checkDuplication: " + checkedFilePath);
        String checkedFileName = FileUtils.getReallyFileName(FileUtils.getFileNameInPath(checkedFilePath));
        downloadData.setFileName(checkedFileName);
        downloadData.setPath(SdcardUtil.DOWNLOAD_FILM_SAVE_PATH + downloadData.getFileName() + ".temp");
        DownFilmHelper.getInstance().addOrReplace(downloadData);
        execute(downloadData, false, 0);
        return downloadManager;
    }

    public boolean isDownloaded(String url) {
        List<DownLoadFilmInfo> downloadedFiles = DownFilmHelper.getInstance().query();
        for (DownLoadFilmInfo f : downloadedFiles) {
            if (f != null && url.equals(f.getUrl()) && f.getStatus() == Constant.Status.FINISH) {
                return true;
            }
        }

        return false;
    }

    public DownLoadFilmInfo getDownloadingInfo(String insUrl) {
        List<DownLoadFilmInfo> fileList = DownFilmHelper.getInstance().query();
        if (fileList == null || fileList.isEmpty()) {
            return null;
        }

        for (DownLoadFilmInfo f : fileList) {
            if (f != null && (insUrl.equals(f.getUrl()) && f.getStatus() != Constant.Status.FINISH)) {
                return f;
            }
        }
        return null;
    }

    public boolean isDownloading(String url) {
//        if (progressHandlerMap.containsKey(url)) {
//            return true;
//        }

        DownLoadFilmInfo i = getDownloadingInfo(url);
        if (i != null/* && progressHandlerMap.containsKey(i.getUrl())*/) {
            return true;
        }
        return false;
    }

    /**
     * 执行下载任务
     */
    private synchronized void execute(DownLoadFilmInfo downloadData, boolean isRetry, int retryType) {
        if (downloadData == null) {
            onCallback(ON_EEROR, ErrorCode.INVALID_URL);
            return;
        }
//        //防止同一个任务多次下载
//        if (progressHandlerMap.get(downloadData.getUrl()) != null && downloadData.getType() != InfoType.SIDECAR) {
//            onCallback(ON_ALREADY_EXIST, downloadData, downloadData.getOriginalUrl(), ErrorCode.ALREADY_EXIST);
//            return;
//        }

        if (downloadData.getStatus() == FINISH) {
            LogUtil.e(TAG, "execute EEROR : ON_FINISH");
            return;
        }

        LogUtil.e(TAG, "execute -------------------------------------------------- ins url = " + downloadData.getUrl());

        String downloadingUrl = downloadData.getUrl();

        //默认每个任务不通过多个异步任务下载


        DownFilmTask task = downloadTaskMap.get(downloadData.getUrl());
        if(task == null){
            task = new DownFilmTask(context, downloadData, downloadingUrl);
            downloadTaskMap.put(downloadData.getUrl(), task);

        }
        task.setIsManualRetry(isRetry);
        task.setRetryType(retryType);
        task.startDownload();

    }

    /**
     * 暂停
     *
     * @param url
     */
    public void pause(String url) {
        DownFilmTask d = downloadTaskMap.get(url);
        if (d != null) {
            d.pause();
        } else {
            LogUtil.e(TAG, "pause : 无进行" + url);
        }

    }

    /**
     * 继续
     *
     * @param url
     */
    public void resume(String url) {
        DownLoadFilmInfo data = getDownloadingInfo(url);
        if (data != null) {
            execute(data, false, 0);
        }
    }

    public void retryFailTask(String url, int retryType) {
        DownLoadFilmInfo data = getDownloadingInfo(url);
        if (data != null) {
            execute(data, true, retryType);
        }
    }

    /**
     * 取消
     *
     * @param url
     */
    public void cancel(String url) {
        pause(url);
    }

    //wifi切换为4g时，播放视频时
    public void pauseAll() {
        LogUtil.d(DownFilmTask.TAG, "主动暂停所有正在下载的任务");
        mAutoPauseList.clear();
        for(DownFilmTask task : downloadTaskMap.values()){
            DownLoadFilmInfo downLoadFilmInfo = task.getDownloadData();
            if(downLoadFilmInfo.getStatus() == START || downLoadFilmInfo.getStatus() == PROGRESS || downLoadFilmInfo.getStatus() == ERROR){
                mAutoPauseList.add(downLoadFilmInfo.getUrl());
            }
        }
        SpUtils.obtain().save(SpConstant.DOWNLOAD_AUTO_PAUSE_LIST, AppApplication.getGson().toJson(mAutoPauseList));
        for (DownFilmTask d :downloadTaskMap.values()) {
            d.pause();
        }
    }

    public void addVideoToAutoPauseList(String url){
        if(mAutoPauseList == null || url == null){
            return;
        }
        if(mAutoPauseList.contains(url)){
            return;
        }
        mAutoPauseList.add(url);
        SpUtils.obtain().save(SpConstant.DOWNLOAD_AUTO_PAUSE_LIST, AppApplication.getGson().toJson(mAutoPauseList));
    }

    public void removeVideoFromAutoPauseList(String url){
        if(mAutoPauseList == null || url == null){
            return;
        }
        if(!mAutoPauseList.contains(url)){
            return;
        }
        mAutoPauseList.remove(url);
        SpUtils.obtain().save(SpConstant.DOWNLOAD_AUTO_PAUSE_LIST, AppApplication.getGson().toJson(mAutoPauseList));
    }

    /**
     * 启动所有自动暂停的下载任务
     */
    public void continueAutoPauseTaskList(){
        LogUtil.d(DownFilmTask.TAG, "继续下载所有自动暂停的任务");
        List<String> downloadList = getAutoDownFilmList();
        if(downloadList == null){
            LogUtil.d(DownFilmTask.TAG, "当前无自动暂停的任务");
            return;
        }
        LogUtil.d(DownFilmTask.TAG, "当前自动暂停的任务数: " + downloadList.size());
        for(String url : downloadList){
            resume(url);
        }
    }

    /**
     * 获取后台自动暂停下载的电影列表总入口
     * @return
     */
    public static List<String> getAutoDownFilmList() {
        String spString = SpUtils.obtain().getString(SpConstant.DOWNLOAD_AUTO_PAUSE_LIST, null);
        if (TextUtils.isEmpty(spString)) {
            return null;
        }
        return getAutoDownFilmListFromJson(spString);
    }

    public static List<String> getAutoDownFilmListFromJson(String filmJson) {
        return AppApplication.getGson().fromJson(filmJson, new TypeToken<List<String>>() {
        }.getType());
    }

    public void continueAllUnfinishTask(boolean isLoadDB) {
        List<DownLoadFilmInfo> downloadList;
        if (isLoadDB) {
            downloadList = FileVideoModel.loadDownFilmInfoFromDBClone();
        } else {
            downloadList = FileVideoModel.getDownFilmInfoCachedClone();
        }
        Iterator<DownLoadFilmInfo> iterator = downloadList.iterator();
        while (iterator.hasNext()) {
            final DownLoadFilmInfo download = iterator.next();
            if (download == null) {
                continue;
            }
            int status = download.getStatus();

            // 处理补充升级前的下载数据
//            if (status == FINISH && download.getPercentage() >= 100f) {
//                download.setStatus(FINISH);
//                FileVideoModel.addOrReplaceDownloadFilm(download);
//                DownFilmHelper.getInstance().addOrReplace(download);
//            } else {
//                LogUtil.e(TAG, "continueAllUnfinishTask 不满足 升级数据条件 " + download.toString());
//            }


            if (status == FINISH || status == Constant.Status.CANCEL || status == PAUSE || status == ERROR) {
                iterator.remove();
                continue;
            }
            resume(download.getUrl());
        }
    }

    /**
     * 自动重试失败的下载任务(且当前正在下载的任务少于4个，只自动重试最近的4个失败任务)
     */
    public void autoRetryFailTask(int retryType){
        List<DownLoadFilmInfo> downloadList = FileVideoModel.loadDownFilmInfoFromDBClone();
        if(downloadList == null || downloadList.isEmpty()){
            return;
        }
        Iterator<DownLoadFilmInfo> iterator = downloadList.iterator();

        int downloadingCount = 0;

        while (iterator.hasNext()) {
            final DownLoadFilmInfo download = iterator.next();
            if (download == null) {
                continue;
            }
            int status = download.getStatus();

            if(status == START || status == PROGRESS){
                downloadingCount++;
            }
        }
        if(downloadingCount >= 4){
            LogUtil.e(TAG, "当前正在下载的电影数为"+downloadingCount+",大于等于4，不再进行重试操作");
            return;
        }
        LogUtil.e(TAG, "当前正在下载的电影数为"+downloadingCount+",小于4，进行重试操作");
        int errorTaskCount = 0;
        for(int i=0; i < downloadList.size(); i++){
            if(errorTaskCount >= 4){
                return;
            }
            DownLoadFilmInfo downInfo = downloadList.get(i);
            if (downInfo.getStatus() == ERROR) {
                errorTaskCount++;
                retryFailTask(downInfo.getUrl(), retryType);
            }
        }

    }

    public boolean isExistInDownloadMap(DownLoadFilmInfo download){
        if (downloadTaskMap.containsKey(download.getUrl())) {
            return true;
        }
        return false;
    }

    /**
     * 退出时释放资源
     *
     * @param url 包含insUrl
     */
    public void destroy(String url) {
        if (downloadTaskMap.containsKey(url)) {
            DownFilmTask d = downloadTaskMap.get(url);
            downloadDataMap.remove(url);
            downloadTaskMap.remove(url);
            d.pause();
        }
    }

    public void destroy(String... urls) {
        if (urls != null) {
            for (String url : urls) {
                destroy(url);
            }
        }
    }
}

