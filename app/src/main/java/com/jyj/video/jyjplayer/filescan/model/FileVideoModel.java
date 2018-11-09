package com.jyj.video.jyjplayer.filescan.model;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.download.bean.DownloadSidecar;
import com.jyj.video.jyjplayer.utils.FileUtils;
import com.jyj.video.jyjplayer.utils.SdcardUtil;
import com.jyj.video.jyjplayer.utils.VideoUtil;
import com.jyj.video.jyjplayer.AppApplication;
import com.jyj.video.jyjplayer.constant.InfoType;
import com.jyj.video.jyjplayer.db.helper.DownloadHelper;
import com.jyj.video.jyjplayer.db.helper.FolderHelper;
import com.jyj.video.jyjplayer.db.helper.SubtitleHelper;
import com.jyj.video.jyjplayer.db.helper.VideoHelper;
import com.jyj.video.jyjplayer.download.Constant;
import com.jyj.video.jyjplayer.download.bean.DownloadInfo;
import com.jyj.video.jyjplayer.download.film.DownFilmHelper;
import com.jyj.video.jyjplayer.download.film.FilmDownLoadManager;
import com.jyj.video.jyjplayer.download.film.bean.DownLoadFilmInfo;
import com.jyj.video.jyjplayer.filescan.ScannerAsyncTask;
import com.jyj.video.jyjplayer.filescan.SingleFolderScannerAsyncTask;
import com.jyj.video.jyjplayer.filescan.SortComparators;
import com.jyj.video.jyjplayer.filescan.model.bean.FolderInfo;
import com.jyj.video.jyjplayer.filescan.model.bean.SubtitleBase;
import com.jyj.video.jyjplayer.filescan.model.bean.SubtitleInfo;
import com.jyj.video.jyjplayer.filescan.model.bean.VideoInfo;
import com.zjyang.base.utils.HandlerUtils;
import com.zjyang.base.utils.LogUtil;

import org.greenrobot.greendao.annotation.NotNull;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.jyj.video.jyjplayer.download.Constant.Status.FINISH;

/**
 * @author denglongyun
 * @date 17-10-30
 */

public class FileVideoModel {
    private static final String TAG = "FileVideoModel";

    private static LinkedList<FolderInfo> sFolderInfoList = new LinkedList<FolderInfo>();
    private static LinkedList<VideoInfo> sVideoInfoList = new LinkedList<VideoInfo>();
    private static LinkedList<VideoInfo> sLatestVideoInfoList = new LinkedList<VideoInfo>();
    private static LinkedList<SubtitleInfo> sSubtitleInfoList = new LinkedList<SubtitleInfo>();
    private static LinkedList<DownloadInfo> sDownloadList = new LinkedList<DownloadInfo>();
    private static LinkedList<VideoInfo> sDownloadVideoList = new LinkedList<VideoInfo>();
    private static LinkedList<DownLoadFilmInfo> sDownFilmList = new LinkedList<>();

    ScannerAsyncTask scannerAsyncTask = null;

    SingleFolderScannerAsyncTask singleFolderScannerAsyncTask = null;

    public static int sLatestTodayVideoCnt = 0;
    public static int sLatestWeekVideoCnt = 0;
    public static int sLatestEarlyVideoCnt = 0;

    private static long scanAndSortTime = 0;


    /**
     * 优先从缓存中获取视频文件夹列表
     * 然后从数据库
     */
    public List<FolderInfo> getCachedOnlyFileInfos() {

        if (sFolderInfoList == null || sFolderInfoList.isEmpty()) {
            loadFolderInfosFromDB();
        }
        if (sFolderInfoList != null && !sFolderInfoList.isEmpty()) {
            filterInvalidData();
            LogUtil.e(TAG, "getCachedOnlyFileInfos: " + sFolderInfoList.size());
            return sFolderInfoList;
        }
        LogUtil.e(TAG, "getCachedOnlyFileInfos: NULL");
        return null;
    }

    /**
     * 优先从缓存中获取视频文件夹列表
     * 然后从数据库
     * 最后即时扫描
     */
    public List<FolderInfo> getCachedFileInfos() {

        if (sFolderInfoList == null || sFolderInfoList.isEmpty()) {
            loadFolderInfosFromDB();
        }
        if (sFolderInfoList != null && !sFolderInfoList.isEmpty()) {
            filterInvalidData();
            LogUtil.e(TAG, "getCachedFileInfos: " + sFolderInfoList.size());
            return sFolderInfoList;
        } else {
            return loadFolderInfosAsync();
        }
    }


    /**
     * 同步从数据库 获取视频文件夹列表
     */
    public List<FolderInfo> loadFolderInfosFromDB() {
        refreshAllFileInfos(FolderHelper.getInstance().query());
        return sFolderInfoList;
    }

    /**
     * 异步扫描文件目录 获取视频文件夹列表
     */
    public List<FolderInfo> loadFolderInfosAsync() {
        LogUtil.e(TAG, "loadFolderInfosAsync: " + sFolderInfoList.size());
        HandlerUtils.postThread(new Runnable() {
            @Override
            public void run() {
                startLoadFileInfosTask();
            }
        });
        return null;
    }

    /**
     * 根据路径扫描单个文件夹 不遍历内文件夹
     * @param path
     */
    public void loadSingleFolderAsync(@NotNull String path) {
        startLoadSingleFolderInfoTask(path);
    }

    /**
     * 同步从数据库 获取下载视频/图片列表
     */
    public static List<DownloadInfo> loadDownloadInfoFromDBClone() {
        List<DownloadInfo> list = new ArrayList<DownloadInfo>();
        list.addAll(loadDownloadInfoFromDB());
        return list;
    }

    /**
     * 同步从数据库 获取下载视频/图片列表
     */
    public static List<DownloadInfo> loadDownloadInfoFromDB() {
        sDownloadList.clear();
        sDownloadList.addAll(DownloadHelper.getInstance().query());
        filterInvalidDownloadData();
        Collections.sort(sDownloadList, SortComparators.getInstance().mCreateTimeDesc);
        return sDownloadList;
    }

    /**
     * 从数据库 获取下载数据 过滤出非完成非取消
     */
    public static List<DownloadInfo> loadUnfinishDownloadFromDB() {
        List<DownloadInfo> downloadList = loadDownloadInfoFromDBClone();
        Iterator<DownloadInfo> iterator = downloadList.iterator();
        while (iterator.hasNext()) {
            int status = iterator.next().getStatus();
            if (status == Constant.Status.ALL_FINISH || status == Constant.Status.CANCEL) {
                iterator.remove();
            }
        }
        return downloadList;
    }

    public static void addOrReplaceDownload(DownloadInfo download) {
        if (download == null) {
            return;
        }

        DownloadInfo pre = getDownloadInfo(download.getUrl(), false);
        if (pre != null) { // 文件存在
            sDownloadList.remove(pre);
        }
        sDownloadList.add(download);
        Collections.sort(sDownloadList, SortComparators.getInstance().mCreateTimeDesc);
        if (download.getType() != InfoType.VIDEO) {
            return;
        }

        addOrReplaceDownloadVideo(download);
    }

    public static void addOrReplaceDownloadVideo(DownloadInfo download) {
        if (download == null) {
            return;
        }
        if (download.getType() != InfoType.VIDEO) {
            return;
        }

        VideoInfo pre = getVideoInfoDownload(download.getPath());
        if (pre != null) { // 文件存在
            sDownloadVideoList.remove(pre);
        }
        sDownloadVideoList.addAll(0, DownloadInfo.getVideoInfo(download));
    }

    public static List<DownloadInfo> getDownloadInfoCached() {
        return sDownloadList;
    }

    public static List<DownloadInfo> getDownloadInfoCachedClone() {
        return (List<DownloadInfo>) sDownloadList.clone();
    }

    public static List<VideoInfo> getDownloadVideoInfoCached() {
        return sDownloadVideoList;
    }

    public static List<VideoInfo> getDownloadVideoInfoCachedClone() {
        return (List<VideoInfo>) sDownloadVideoList.clone();
    }

    /**
     * 从视频db读取单个视频文件 并组合成为FolderInfo list 存入文件db
     * @return
     */
    public List<FolderInfo> sortVideos2FolderInfo() {
        long a = System.currentTimeMillis();
        List<VideoInfo> allVideo = sVideoInfoList /*VideoHelper.getInstance().query()*/;
        if (allVideo == null || allVideo.size() < 1) {
            return null;
        }
        LogUtil.e("JniScan", "分文件夹开始 ===  视频数： " + allVideo.size());
        List<FolderInfo> resultList = new ArrayList<FolderInfo>();
        Map<String, List<VideoInfo>> temp = new HashMap<String, List<VideoInfo>>();
        Set<String> folderPaths = new HashSet<String>();


        for (VideoInfo v : allVideo) {
            if (v == null) {
                continue;
            }
            folderPaths.add(VideoUtil.getParentPath(v.getPath()));
        }

        for (String f : folderPaths) {
            List<VideoInfo> videoInfos = new ArrayList<VideoInfo>();
            for (VideoInfo v : allVideo) {
                if (v == null) {
                    continue;
                }
                File file = new File(v.getPath());
                if (file.exists() && file.getParentFile().getAbsolutePath().equals(f)) {
                    videoInfos.add(v);
                }
            }
            if (videoInfos.size() > 0) {
                temp.put(f, videoInfos);
            }
        }

        if (temp.size() < 1) {
            return null;
        }
        for (String p : temp.keySet()) {
            File file = new File(p);
            FolderInfo folderInfo = new FolderInfo();
            folderInfo.setPath(p);
            allVideo = temp.get(p);
            Collections.sort(allVideo, SortComparators.getInstance().mCreateTimeDesc);
            folderInfo.setVideoList(allVideo);
            folderInfo.setDisplayName(file.getName());
            folderInfo.setName(file.getName());
            folderInfo.setLastModify(file.lastModified());
            folderInfo.setCreateTime(folderInfo.getLastModify());
            long size = 0L;
            for (VideoInfo v : temp.get(p)) {
                size += v.getSize();
            }
            folderInfo.setSize(size);

            resultList.add(folderInfo);
        }
        long b = System.currentTimeMillis();

        refreshAllFileInfos(resultList);
        LogUtil.e("JniScan", "分文件夹完毕 ===  耗时：" + (b - a) + " 文件夹数： " + temp.size());
        LogUtil.e("JniScan", "分文件夹(split_folder_time) ===  耗时：" + (b - a) + " 文件夹数： " + temp.size());
        return resultList;
    }

    public static void uploadScantimeStatistic() {
        if (scanAndSortTime <= 0) {
            return;
        }

        float scanTime = (float) (System.currentTimeMillis() - scanAndSortTime) / 1000;
        LogUtil.e("JniScanFileScanner", "扫描 & 分文件夹 完毕 ===  总总耗时：" + scanTime + " 秒");
    }

    /**
     * 过滤失效的文件
     * 如果文件已经不在 删除数据库对应数据 更新对应的videoList
     */
    private static void filterInvalidDownloadData() {
        if (!SdcardUtil.hasSdcardPermissions()) {
            return;
        }
        File temp;
        String path;
        sDownloadVideoList.clear();
        Iterator<DownloadInfo> iterator = getDownloadInfoCachedClone().iterator();
        while (iterator.hasNext()) {
            DownloadInfo download = iterator.next();
            path = download.getPath();
            if (!TextUtils.isEmpty(path)) {
                temp = new File(path);
                if (temp.exists()) {
                    if (download.getType() != InfoType.VIDEO) { // 影响：搜索不搜索图片
                        continue;
                    }
                    sDownloadVideoList.addAll(DownloadInfo.getVideoInfo(download));
                }
            }
        }
    }


    /**
     * 过滤失效的文件夹
     */
    private void filterInvalidData() {
        File temp;
        String path;
        List<FolderInfo> tempFolderList = new ArrayList<FolderInfo>();

        for (FolderInfo f : getAllFolderListClone()) {
            if (f == null) {
                LogUtil.e(TAG, "失效的文件夹 : NULL");
                continue;
            }
            path = f.getPath();

            if (!TextUtils.isEmpty(path)) {
                temp = new File(path);
                if (!isFileHasVideo(temp)) {
                    tempFolderList.add(f);
                    LogUtil.e(TAG, "失效的文件夹 : " + path);
                }
            } else {
                tempFolderList.add(f);
                LogUtil.e(TAG, "失效的文件夹路径 : isEmpty");
            }
        }

        sFolderInfoList.removeAll(tempFolderList);
    }

    private List<FolderInfo> startLoadFileInfosTask() {
        LogUtil.e("JniScanFileScanner", "扫描 & 分文件夹 开始计时 ===  总总耗时：??? 秒");
        scanAndSortTime = System.currentTimeMillis();
        scannerAsyncTask = new ScannerAsyncTask();
        scannerAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return sFolderInfoList;
    }

    private void startLoadSingleFolderInfoTask(@NotNull String path) {
        singleFolderScannerAsyncTask = new SingleFolderScannerAsyncTask(path);
        singleFolderScannerAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void refreshAllFileInfos(List<FolderInfo> newFileList) {
        if (newFileList == null /*|| newFileList.isEmpty()*/) {
            return;
        }

//        Iterator<FolderInfo> iteratorNew = newFileList.iterator();
//        Iterator<FolderInfo> iterator = sFolderInfoList.iterator();
//
//        for (FolderInfo folderInfoNew : newFileList) {
//            LogUtil.e(TAG, "扫描完毕添加至list： " + folderInfoNew.getPath());
//            while (iterator.hasNext()) {
//                FolderInfo folderInfo = iterator.next();
//
//                // 已有的文件夹先删除 后面统一加
//                if (folderInfo.getPath().equals(folderInfoNew.getPath())) {
//                    LogUtil.e(TAG, "已有的文件夹： " + folderInfoNew.getPath());
//                    iterator.remove();
//                }
//            }
//        }
        sFolderInfoList.clear();
        sFolderInfoList.addAll(newFileList);
    }

    public static void addOrReplaceFolderInfo(FolderInfo folderInfo) {
        if (folderInfo == null) {
            return;
        }
        FolderInfo pre = getFileInfo(folderInfo.getPath());
        if (pre != null) { // 文件存在
            sFolderInfoList.remove(pre);
        }
        sFolderInfoList.add(folderInfo);

    }

    public static void refreshAllVideoInfo(List<VideoInfo> videoInfos) {
        if (videoInfos == null /*|| videoInfos.size() < 1*/) {
            return;
        }
        sVideoInfoList.clear();
        sVideoInfoList.addAll(videoInfos);

        if (sVideoInfoList.isEmpty()) {
            sFolderInfoList.clear();

            sLatestVideoInfoList.clear();
            sLatestWeekVideoCnt = 0;
            sLatestEarlyVideoCnt = 0;
            sLatestTodayVideoCnt = 0;
        }
    }

    public static void addOrReplaceVideoInfo(VideoInfo videoInfo) {
        if (videoInfo == null) {
            return;
        }
        VideoInfo pre = getVideoInfo(videoInfo.getPath());
        if (pre != null) { // 文件存在
            sVideoInfoList.remove(pre);
        }
        sVideoInfoList.add(videoInfo);

    }

    /**
     * 遍历文件夹内所有非dir文件 判断是否有支持类型的视频文件
     *
     */
    public static boolean isFileHasVideo(File file) {
        if (file == null || !file.exists()) {
            return false;
        }

        File[] files = file.listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {

                String name = file.getName();
                int i = name.lastIndexOf('.');

                //判断是不是目录
                if (!file.isDirectory() && i != -1) {
                    name = name.substring(i);
                    if (VideoUtil.isVideoFile(name) && file.length() > 0L) {
                        return true;
                    }
                }
                return false;
            }
        });

        return files != null && files.length > 0;
    }

    /**
     * 根据路径path查询文件信息
     * ！结果可能为空
     */
    public static FolderInfo getFileInfo(String path) {
        if (TextUtils.isEmpty(path) || sFolderInfoList == null || sFolderInfoList.size() < 1) {
            return null;
        }
        for (FolderInfo folderInfo : getAllFolderListClone()) {
            if (path.equals(folderInfo.getPath())) {
                return folderInfo;
            }
        }
        return null;
    }

    /**
     * 根据路径path查询视频信息
     * ！结果可能为空
     */
    public static VideoInfo getVideoInfo(String path) {
        if (TextUtils.isEmpty(path) || sVideoInfoList == null || sVideoInfoList.size() < 1) {
            return null;
        }
        for (VideoInfo folderInfo : sVideoInfoList) {
            if (path.equals(folderInfo.getPath())) {
                return folderInfo;
            }
        }
        return null;
    }

    /**
     * 根据路径path查询视频信息
     * ！结果可能为空
     */
    public static VideoInfo getVideoInfoDownload(String path) {
        if (TextUtils.isEmpty(path) || sDownloadVideoList == null || sDownloadVideoList.isEmpty()) {
            LogUtil.e(TAG, "getVideoInfoDownload0 : NULL");
            return null;
        }
        for (VideoInfo v : sDownloadVideoList) {
            if (path.equals(v.getPath())) {
                return v;
            }
        }
        LogUtil.e(TAG, "getVideoInfoDownload : NULL");
        return null;
    }

    /**
     * 根据资源下载url查询下载信息
     * ！结果可能为空
     */
    public static DownloadInfo getDownloadInfo(String url, boolean isLoadDB) {

        if (TextUtils.isEmpty(url)) {
            return null;
        }
        List<DownloadInfo> downloadList = getDownloadInfoCachedClone();
        if(downloadList != null){
            for (DownloadInfo download : downloadList) {

                if (url.equals(download.getUrl())) {
                    return download;
                }
            }
        }

        if (!isLoadDB) {
            return null;
        }
        // 缓存没找到再load数据库
        loadDownloadInfoFromDB();
        downloadList = getDownloadInfoCachedClone();
        for (DownloadInfo download : downloadList) {
            if (url.equals(download.getUrl())) {
                return download;
            }
        }
        return null;
    }

    public static void putFolderInfos2DB() {
        if (sFolderInfoList == null) {
            return;
        }
        if (sFolderInfoList.isEmpty()) {
            FolderHelper.getInstance().deleteAll();
        }

        List<FolderInfo> temp = (List<FolderInfo>) sFolderInfoList.clone();
        List<FolderInfo> preData = FolderHelper.getInstance().query();

        for (FolderInfo f : temp) {
            FolderHelper.getInstance().addOrReplace(f);

            Iterator<FolderInfo> it = preData.iterator();
            while (it.hasNext()) {
                FolderInfo folder = it.next();
                if (folder.getPath().equals(f.getPath())) {
                    it.remove();
                }
            }
        }

        if (preData.size() > 0) {
            for (FolderInfo f : preData) {
                LogUtil.e("JniScan", "数据库数据比较=> 已被删除文件夹: " + f.getPath());
                FolderHelper.getInstance().deleteByPath(f.getPath());
            }
        }
    }

    public static void putVideoInfos2DB() {
        if (sVideoInfoList == null) {
            return;
        }
        if (sVideoInfoList.isEmpty()) {
            VideoHelper.getInstance().deleteAll();
        }
        List<VideoInfo> temp = (List<VideoInfo>) sVideoInfoList.clone();
        List<VideoInfo> preData = VideoHelper.getInstance().query();

        for (VideoInfo v : temp) {
            VideoHelper.getInstance().addOrReplace(v);
            Iterator<VideoInfo> it = preData.iterator();
            while (it.hasNext()) {
                VideoInfo video = it.next();
                if (video.getPath().equals(v.getPath())) {
                    it.remove();
                }
            }
        }

        if (preData.size() > 0) {
            for (VideoInfo v : preData) {
                LogUtil.e("JniScan", "数据库数据比较=> 已被删除视频: " + v.getPath());
                VideoHelper.getInstance().deleteByPath(v.getPath());
            }
        }


    }


    /**
     * 获取最近一周所有视频 & 一周前的5个视频
     * @return
     */
    public static List<VideoInfo> loadLatestVideosDesc() {
        sLatestVideoInfoList.clear();
        final List<VideoInfo> videoInfos = new ArrayList<VideoInfo>();

        if (sVideoInfoList.size() < 1) {
            videoInfos.addAll(VideoHelper.getInstance().query());
        } else {
            videoInfos.addAll(sVideoInfoList);
        }
        if (videoInfos.size() < 1) {
            return sLatestVideoInfoList;
        }

        Collections.sort(videoInfos, SortComparators.getInstance().mInfoTimeDesc);

        sLatestTodayVideoCnt = 0;
        sLatestWeekVideoCnt = 0;
        sLatestEarlyVideoCnt = 0;

        int cnt = 5;
        for (VideoInfo v : videoInfos) {
            v.setTag(VideoUtil.getTagResId(v.getLastModify()));

            switch (v.getTag()) {
                case R.string.latest_today:
                    sLatestVideoInfoList.add(v);
                    sLatestTodayVideoCnt++;
                    break;
                case R.string.latest_week:
                    sLatestVideoInfoList.add(v);
                    sLatestWeekVideoCnt++;
                    break;
                case R.string.latest_early:
                    if (sLatestTodayVideoCnt < 1 && sLatestWeekVideoCnt < 1 && cnt > 0) {
                        sLatestVideoInfoList.add(v);
                        sLatestEarlyVideoCnt++;
                        cnt --;
                    } else {
                        return sLatestVideoInfoList;
                    }
                    break;
                default:
                    break;
            }
        }

        return sLatestVideoInfoList;
    }

    public static List<VideoInfo> getLatestVideosCached() {
        return sLatestVideoInfoList;
    }

    public static String getLastestVideosSize(){
        if(sLatestVideoInfoList == null){
            return "";
        }
        long sumSize = 0;
        for (VideoInfo videoInfo : sLatestVideoInfoList){
            sumSize = sumSize + videoInfo.getSize();
        }
        return VideoUtil.getSizeAsStringWithUnit(sumSize);
    }

    public static String getDownLoadVideosSize(){
        if(sDownloadVideoList == null){
            return "";
        }
        long sumSize = 0;
        for(DownLoadFilmInfo downLoadFilmInfo : sDownFilmList){
            if(downLoadFilmInfo.getStatus() == FINISH){
                sumSize = sumSize + downLoadFilmInfo.getTotalLength();
            }
        }
        return VideoUtil.getSizeAsStringWithUnit(sumSize);
    }

    public static List<VideoInfo> getAllVideoListClone() {
        return (List<VideoInfo>) sVideoInfoList.clone();
    }

    public static List<FolderInfo> getAllFolderList() {
        return sFolderInfoList;
    }

    public static List<FolderInfo> getAllFolderListClone() {
        return (List<FolderInfo>) sFolderInfoList.clone();
    }

    public static int getFolderInfoCntFromDB() {
        return FolderHelper.getInstance().queryFolderDaoData().size();
    }

    /**
     * 添加单个视频文件
     * 需处理cache & folderInfo 中相应数据 & DB & 最近list
     * @param videoInfo 被添加视频
     */
    public static void addSingleVideo(VideoInfo videoInfo) {
        if (videoInfo == null) {
            return;
        }
        String path = videoInfo.getPath();

        // DB
        VideoHelper.getInstance().addOrReplace(videoInfo);
        // video list cache
        addOrReplaceVideoInfo(videoInfo);

        // folder list cache & db
        String folderPath = VideoUtil.getParentPath(path);
        FolderInfo targetFolder = getFileInfo(folderPath);
        if (targetFolder != null) {
            long size = targetFolder.getSize();
            List<VideoInfo> tempList = targetFolder.getVideoList();
            if (tempList != null) {
                tempList.add(videoInfo);
            } else {
                List<VideoInfo> vList = new ArrayList<VideoInfo>();
                vList.add(videoInfo);
                targetFolder.setVideoList(vList);
            }
            targetFolder.setSize(size + videoInfo.getSize());
            FolderHelper.getInstance().addOrReplace(targetFolder);
        } else {
            targetFolder = FolderHelper.getInstance().queryFolderInfoByPath(folderPath);
            if (targetFolder == null) {
                targetFolder = new FolderInfo();
                targetFolder.setPath(folderPath);
                targetFolder.setDisplayName(VideoUtil.getVideoName(folderPath));
                targetFolder.setName(targetFolder.getDisplayName());
                targetFolder.setLastModify(System.currentTimeMillis());
                targetFolder.setCreateTime(targetFolder.getLastModify());
                targetFolder.setType(InfoType.DIRECTORY);
                targetFolder.setSize(0L);
            }

            List<VideoInfo> tempList = targetFolder.getVideoList();
            if (tempList != null) {
                tempList.add(videoInfo);
            } else {
                List<VideoInfo> vList = new ArrayList<VideoInfo>();
                vList.add(videoInfo);
                targetFolder.setVideoList(vList);
            }

            targetFolder.setSize(targetFolder.getSize() + videoInfo.getSize());
            addOrReplaceFolderInfo(targetFolder);
            FolderHelper.getInstance().addOrReplace(targetFolder);
        }

    }

    /**
     * 删除单个视频文件
     * 需处理cache & folderInfo 中相应数据 & DB & 最近list
     * @param path 被删视频路径
     */
    public static void deleteSingleVideo(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        // DB
        VideoHelper.getInstance().deleteByPath(path);
        // video list cache
        VideoInfo delVideo = getVideoInfo(path);
        if (delVideo != null) {
            // latest list
            deleteVideoFromLatestCached(delVideo, true);
            // all video list cache
            deleteVideoCached(delVideo);
        }
        // folder list cache
        String folderPath = VideoUtil.getParentPath(path);
        FolderInfo targetFolder = getFileInfo(folderPath);
        if (targetFolder != null) {
            long size = targetFolder.getSize();
            List<VideoInfo> tempList = targetFolder.getVideoList();
            if (tempList != null && !tempList.isEmpty()) {
                for (VideoInfo v : tempList) {
                    if (v.getPath().equals(path)) {
                        size -= v.getSize();
                        tempList.remove(v);
                        break;
                    }
                }
            }
            targetFolder.setSize(size);
            if (tempList == null || tempList.isEmpty()) {
                boolean isSuccess = sFolderInfoList.remove(targetFolder);
                FolderHelper.getInstance().deleteByPath(folderPath);
            } else {
                FolderHelper.getInstance().addOrReplace(targetFolder);
            }
        } else {
            targetFolder = FolderHelper.getInstance().queryFolderInfoByPath(folderPath);
            if (targetFolder == null) {
                return;
            }
            long size = targetFolder.getSize();
            if (targetFolder != null) {
                List<VideoInfo> tempList = targetFolder.getVideoList();
                if (tempList != null && !tempList.isEmpty()) {
                    for (VideoInfo v : tempList) {
                        if (v.getPath().equals(path)) {
                            size -= v.getSize();
                            tempList.remove(v);
                            break;
                        }
                    }
                }
                targetFolder.setSize(size);
                if (tempList == null || tempList.isEmpty()) {
                    boolean isSuccess = sFolderInfoList.remove(targetFolder);
                    FolderHelper.getInstance().deleteByPath(folderPath);
                } else {
                    FolderHelper.getInstance().addOrReplace(targetFolder);
                }
            }
        }

    }

    /**
     *
     * @param v
     * @param isReCount 是否要更新最近list数目
     * @return
     */
    public static boolean deleteVideoFromLatestCached(final VideoInfo v, boolean isReCount) {
        if (v == null) {
            return false;
        }
        boolean isDelete = sLatestVideoInfoList.remove(v);
        if (isDelete && isReCount) {
            v.setTag(VideoUtil.getTagResId(v.getLastModify()));

            switch (v.getTag()) {
                case R.string.latest_today:
                    sLatestTodayVideoCnt--;
                    break;
                case R.string.latest_week:
                    sLatestWeekVideoCnt--;
                    break;
                case R.string.latest_early:
                    sLatestEarlyVideoCnt--;
                    break;
                default:
                    break;
            }
        }
        return isDelete;
    }

    public static void deleteVideoCached(final VideoInfo v) {
        if (v == null) {
            return;
        }
        sVideoInfoList.remove(v);
    }

    public static void deleteDownloadCached(final DownloadInfo d) {
        if (d == null) {
            return;
        }
        sDownloadList.remove(d);
    }

    public static void deleteDownloadVideoCached(final DownloadInfo d) {
        if (d == null) {
            return;
        }
        VideoInfo v = getVideoInfoDownload(d.getPath());
        if (v == null) {
            return;
        }
        sDownloadVideoList.remove(v);
    }


    /**
     * 重命名单个视频文件
     * 需处理cache & folderInfo 中相应数据 & DB & 最近list
     * @param path 被重命名视频路径
     * @param newName 视频新名(同一文件夹下
     */
    public static void renameSingleVideo(String path, String newName) {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(newName)) {
            return;
        }
        String newPath = VideoUtil.getRenamedNewPath(path, newName);
        if (newPath == null || TextUtils.isEmpty(newPath)) {
            return;
        }
        // DB
        VideoInfo dbVideo = VideoHelper.getInstance().queryVideoInfoByPath(path);
        if (dbVideo != null) {
            VideoHelper.getInstance().deleteByPath(path);
            dbVideo.setPath(newPath);
            dbVideo.setDisplayName(newName);
            dbVideo.setName(newName);
            VideoHelper.getInstance().addOrReplace(dbVideo);
        }
        // video list cache
        VideoInfo delVideo = getVideoInfo(path);
        if (delVideo != null) {
            // latest list
            boolean isLatest = deleteVideoFromLatestCached(delVideo, false);
            // all video list cache
            delVideo.setPath(newPath);
            delVideo.setDisplayName(newName);
            delVideo.setName(newName);
            if (isLatest) {
                sLatestVideoInfoList.add(delVideo);
                Collections.sort(sLatestVideoInfoList, SortComparators.getInstance().mInfoTimeDesc);
            }
        }

        // folder list cache
        String folderPath = VideoUtil.getParentPath(path);
        FolderInfo targetFolder = getFileInfo(folderPath);
        if (targetFolder != null) {
            List<VideoInfo> tempList = targetFolder.getVideoList();
            if (tempList != null && !tempList.isEmpty()) {
                for (VideoInfo v : tempList) {
                    if (v.getPath().equals(path)) {
                        v.setPath(newPath);
                        v.setDisplayName(newName);
                        v.setName(newName);
                        break;
                    }
                }
            }
        } else {
            targetFolder = FolderHelper.getInstance().queryFolderInfoByPath(folderPath);
            if (targetFolder != null) {
                List<VideoInfo> tempList = targetFolder.getVideoList();
                if (tempList != null && !tempList.isEmpty()) {
                    for (VideoInfo v : tempList) {
                        if (v.getPath().equals(path)) {
                            v.setPath(newPath);
                            v.setDisplayName(newName);
                            v.setName(newName);
                            break;
                        }
                    }
                }
                FolderHelper.getInstance().addOrReplace(targetFolder);
            }
        }

    }


    /**
     * 删除单个视频文件
     * 需处理cache & DB & file
     * @param url 被删资源下载url
     */
    public static boolean deleteDownload(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        DownloadInfo download = getDownloadInfo(url, false);
        if (download == null) {
            return false;
        }

        boolean isSuccess = true;

        int type = download.getType();
        switch (type) {
            case InfoType.SIDECAR:
                // File
                List<DownloadSidecar> downloadList = download.getDownloadList();
                if (downloadList == null || downloadList.isEmpty()) {
                    LogUtil.e(TAG, "deleteDownload: 失败 downloadList 为空 ");
                    return false;
                }
                for (DownloadSidecar sidecar : downloadList) {
                    if (sidecar == null) {
                        continue;
                    }

                    if (!FileUtils.deleteSingleFile(sidecar.getPath())) {
                        LogUtil.e(TAG, "deleteDownload: 失败 未删除完全 " + sidecar.getPath() + "  " + sidecar.getUrl());
                        isSuccess = false;
                    }
                }
                break;
            case InfoType.IMAGE:
            case InfoType.VIDEO:
            default:
                // File
                isSuccess = FileUtils.deleteSingleFile(download.getPath());
                break;
        }

        if (isSuccess) {
            // DB
            DownloadHelper.getInstance().deleteByUrl(url);
            // Download list cache
            deleteDownloadCached(download);
            deleteDownloadVideoCached(download);
            // handler
            //DownloadManger.getInstance(AppApplication.getContext()).destroy(url);
            LogUtil.e(TAG, "deleteDownload: 成功 ");
            return true;
        }
        return false;
    }


    //////////////////////////////////////字幕数据//////////////////////////////////////////

    /**
     * 优先从缓存中获取字幕信息列表
     * 然后从数据库
     */
    public List<SubtitleInfo> getCachedOnlySubtitleList() {

        if (sSubtitleInfoList == null || sSubtitleInfoList.isEmpty()) {
            loadSubtitleInfosFromDB();
        }
        if (sSubtitleInfoList != null && !sSubtitleInfoList.isEmpty()) {
            LogUtil.e(TAG, "getCachedOnlySubtitleList: " + sSubtitleInfoList.size());
            return sSubtitleInfoList;
        }
        LogUtil.e(TAG, "getCachedOnlySubtitleList: NULL");
        return null;
    }


    /**
     * 同步从数据库 获取字幕信息夹列表
     */
    public static List<SubtitleInfo> loadSubtitleInfosFromDB() {
        sSubtitleInfoList.clear();
        sSubtitleInfoList.addAll(SubtitleHelper.getInstance().query());
        return sSubtitleInfoList;
    }


    /**
     * 根据路径path查询字幕信息
     * ！结果可能为空
     */
    public static SubtitleInfo getSubtitleInfo(String videoPath) {
        boolean loadDb = false;
        if (sSubtitleInfoList == null || sSubtitleInfoList.isEmpty()) {
            loadSubtitleInfosFromDB();
            loadDb = true;
        }
        if (TextUtils.isEmpty(videoPath) || sSubtitleInfoList == null || sSubtitleInfoList.isEmpty()) {
            return null;
        }
        // cache
        for (SubtitleInfo subtitle : sSubtitleInfoList) {
            if (videoPath.equals(subtitle.getVideoPath())) {
                filterUnExistSubFile(subtitle);
                return subtitle;
            }
        }
        if (loadDb) {
            return null;
        }
        // DB
        loadSubtitleInfosFromDB();
        for (SubtitleInfo subtitle : sSubtitleInfoList) {
            if (videoPath.equals(subtitle.getVideoPath())) {
                filterUnExistSubFile(subtitle);
                return subtitle;
            }
        }
        return null;
    }

    public static void filterUnExistSubFile(SubtitleInfo subtitle){
        List<SubtitleBase> newSubBaseList = new ArrayList<>();
        List<SubtitleBase> subtitleBaseList = subtitle.getAllSubtitles();
        for(SubtitleBase subtitleBase : subtitleBaseList){
            LogUtil.d(TAG, "遍历字幕数据，检查文件是否存在");
            String path = subtitleBase.getPath();
            boolean isExist = SdcardUtil.isFileExist(path);
            if(isExist){
                LogUtil.d(TAG, "字幕文件依旧存在于文件夹");
                newSubBaseList.add(subtitleBase);
            }
        }
        subtitle.setAllSubtitles(newSubBaseList);
        addOrReplaceSubtitleInfo(subtitle);
    }

    /**
     * 已有的字幕数据 添加单个字幕到字幕list 同时更新数据库
     * @param videoPath
     * @param base
     * @return 如果缓存及DB无此字幕数据 则添加失败
     */
    public static boolean addSingleSubtitle(String videoPath, SubtitleBase base) {
        SubtitleInfo subtitleInfo = getSubtitleInfo(videoPath);

        if (subtitleInfo == null) {
            return false;
        }
        subtitleInfo.addSubtitle(base);

        addOrReplaceSubtitleInfo(subtitleInfo);

        return false;
    }

    /**
     * 创建字幕数据 并设置此字幕为当前使用字幕 同时更新数据库
     * @param videoPath
     * @param curSubtitlePath
     */
    public static SubtitleInfo createSubtitle(String videoPath, String curSubtitlePath) {
        if (TextUtils.isEmpty(videoPath) || TextUtils.isEmpty(curSubtitlePath)) {
            return null;
        }
        File file = new File(curSubtitlePath);
        if (!file.exists()) {
            return null;
        }
        SubtitleBase base = new SubtitleBase();
        base.setPath(curSubtitlePath);
        base.setName(VideoUtil.getVideoName(curSubtitlePath));
        base.setCreateTime(System.currentTimeMillis());
        base.setSize(file.length());

        return createSubtitle(videoPath, base);
    }

    /**
     * 创建字幕数据 并设置此字幕为当前使用字幕 同时更新数据库
     * @param videoPath
     * @param curSubtitle
     */
    public static SubtitleInfo createSubtitle(String videoPath, SubtitleBase curSubtitle) {
        if (TextUtils.isEmpty(videoPath) || curSubtitle == null) {
            return null;
        }
        SubtitleInfo subtitleInfo = getSubtitleInfo(videoPath);

        if (subtitleInfo == null) {
            subtitleInfo = new SubtitleInfo();
        }
        subtitleInfo.setVideoPath(videoPath);
        subtitleInfo.setCurSubtitle(curSubtitle);

        addOrReplaceSubtitleInfo(subtitleInfo);
        return subtitleInfo;
    }

    /**
     * 更新字幕 同时更新数据库
     * @param subtitleInfo
     */
    public static void addOrReplaceSubtitleInfo(SubtitleInfo subtitleInfo) {
        if (subtitleInfo == null || TextUtils.isEmpty(subtitleInfo.getVideoPath())) {
            return;
        }
        if (sSubtitleInfoList.isEmpty()) {
            sSubtitleInfoList.add(subtitleInfo);
        } else {
            for (SubtitleInfo s : sSubtitleInfoList) {
                String path = s.getVideoPath();
                if (!TextUtils.isEmpty(path) && path.equals(s.getVideoPath())) {
                    sSubtitleInfoList.remove(s);
                    break;
                }
            }
            sSubtitleInfoList.add(subtitleInfo);
        }
        SubtitleHelper.getInstance().addOrReplace(subtitleInfo);
    }

    //////////////////////////////////////字幕END//////////////////////////////////////////


    ///在线电影下载///
    /**
     * 同步从数据库 获取下载视频/图片列表
     */
    public static List<DownLoadFilmInfo> loadDownFilmInfoFromDBClone() {
        List<DownLoadFilmInfo> list = new ArrayList<DownLoadFilmInfo>();
        list.addAll(loadDownFilmInfoFromDB());
        return list;
    }

    /**
     * 同步从数据库 获取下载视频/图片列表
     */
    public static List<DownLoadFilmInfo> loadDownFilmInfoFromDB() {
        sDownFilmList.clear();
        sDownFilmList.addAll(DownFilmHelper.getInstance().query());
        //filterInvalidDownFilmData();
        Collections.sort(sDownFilmList, SortComparators.getInstance().mCreateTimeDesc);
        return sDownFilmList;
    }

    /**
     * 从数据库 获取下载数据 过滤出非完成非取消
     */
    public static List<DownLoadFilmInfo> loadUnfinishDownFilmFromDB() {
        List<DownLoadFilmInfo> downloadList = loadDownFilmInfoFromDBClone();
        Iterator<DownLoadFilmInfo> iterator = downloadList.iterator();
        while (iterator.hasNext()) {
            int status = iterator.next().getStatus();
            if (status == Constant.Status.ALL_FINISH || status == Constant.Status.CANCEL) {
                iterator.remove();
            }
        }
        return downloadList;
    }

    public static void addOrReplaceDownloadFilm(DownLoadFilmInfo download) {
        if (download == null) {
            return;
        }

        DownLoadFilmInfo pre = getDownloadFilmInfo(download.getUrl(), false);
        if (pre != null) { // 文件存在
            sDownFilmList.remove(pre);
        }
        LogUtil.d(TAG, "addOrReplaceDownloadFilm");
        sDownFilmList.add(download);
        Collections.sort(sDownFilmList, SortComparators.getInstance().mCreateTimeDesc);

        addOrReplaceDownFilmVideo(download);
    }

    public static void addOrReplaceDownFilmVideo(DownLoadFilmInfo download) {
        if (download == null) {
            return;
        }

        VideoInfo pre = getVideoInfoDownload(download.getPath());
        if (pre != null) { // 文件存在
            sDownloadVideoList.remove(pre);
        }
        sDownloadVideoList.add(0, DownLoadFilmInfo.getVideoInfo(download));
    }

    public static List<DownLoadFilmInfo> getDownFilmInfoCached() {
        LogUtil.d(TAG, "getDownFilmInfoCached: " + sDownFilmList.size());
        return sDownFilmList;
    }

    public static List<DownLoadFilmInfo> getDownFilmInfoCachedClone() {
        return (List<DownLoadFilmInfo>) sDownFilmList.clone();
    }

    public static List<DownLoadFilmInfo> getFinishDownFilmInfoList() {
        List<DownLoadFilmInfo> cacheList = new ArrayList<>();
        if(sDownFilmList == null){
            return cacheList;
        }
        for(DownLoadFilmInfo downLoadFilmInfo : (List<DownLoadFilmInfo>) sDownFilmList.clone()){
            if(downLoadFilmInfo.getStatus() == FINISH){
                cacheList.add(downLoadFilmInfo);
            }
        }
        return cacheList;
    }

    /**
     * 根据资源下载url查询下载信息
     * ！结果可能为空
     */
    public static DownLoadFilmInfo getDownloadFilmInfo(String url, boolean isLoadDB) {

        if (TextUtils.isEmpty(url)) {
            return null;
        }
        List<DownLoadFilmInfo> downloadList = getDownFilmInfoCachedClone();
        if(downloadList != null){
            for (DownLoadFilmInfo download : downloadList) {

                if (url.equals(download.getUrl())) {
                    return download;
                }
            }
        }

        if (!isLoadDB) {
            return null;
        }
        // 缓存没找到再load数据库
        loadDownloadInfoFromDB();
        downloadList = getDownFilmInfoCachedClone();
        for (DownLoadFilmInfo download : downloadList) {
            if (url.equals(download.getUrl())) {
                return download;
            }
        }
        return null;
    }

    /**
     * 过滤失效的文件
     * 如果文件已经不在 删除数据库对应数据 更新对应的videoList
     */
    private static void filterInvalidDownFilmData() {
        if (!SdcardUtil.hasSdcardPermissions()) {
            return;
        }
        File temp;
        String path;
        sDownloadVideoList.clear();
        Iterator<DownLoadFilmInfo> iterator = getDownFilmInfoCachedClone().iterator();
        while (iterator.hasNext()) {
            DownLoadFilmInfo download = iterator.next();
            path = download.getPath();
            if (!TextUtils.isEmpty(path)) {
                temp = new File(path);
                if (temp.exists()) {
                    sDownloadVideoList.add(DownLoadFilmInfo.getVideoInfo(download));
                }else{
                    LogUtil.d(TAG, "文件实际不存在，从下载列表中移除: " + path);
                    sDownFilmList.remove(download);
                }
            }
        }
    }

    /**
     * 删除单个视频文件
     * 需处理cache & DB & file
     * @param url 被删资源下载url
     */
    public static boolean deleteDownloadFilm(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        DownLoadFilmInfo download = getDownLoadFilmInfo(url, false);
        if (download == null) {
            return true;
        }

        boolean isSuccess = true;
        String filePath = download.getPath();
        File file = new File(filePath);
        if(file.isFile() && file.exists()){
            isSuccess = FileUtils.deleteSingleFile(filePath);
        }else{
            isSuccess = true;
        }


        if (isSuccess) {
            // DB
            DownFilmHelper.getInstance().deleteByUrl(url);
            // Download list cache
            deleteDownFilmCached(download);
            deleteDownFilmVideoCached(download);
            // handler
            FilmDownLoadManager.getInstance(AppApplication.getContext()).destroy(url);
            LogUtil.e(TAG, "deleteDownload: 成功 :" + url);
            return true;
        }
        return false;
    }

    /**
     * 删除下载数据（不包括文件）
     * @param path
     * @return
     */
    public static void deleteDownloadFilmData(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        DownLoadFilmInfo download = null;
        List<DownLoadFilmInfo> downloadList = getDownFilmInfoCachedClone();
        if(downloadList != null){
            for (DownLoadFilmInfo d : downloadList) {

                if (path.equals(d.getPath())) {
                    download = d;
                }
            }
        }
        if (download == null) {
            return;
        }
        DownFilmHelper.getInstance().deleteByUrl(download.getUrl());
        // Download list cache
        deleteDownFilmCached(download);
        deleteDownFilmVideoCached(download);
        // handler
        FilmDownLoadManager.getInstance(AppApplication.getContext()).destroy(download.getUrl());
        LogUtil.e(TAG, "deleteDownloadFilmData: 成功 ");
    }

    /**
     * 根据资源下载url查询下载信息
     * ！结果可能为空
     */
    public static DownLoadFilmInfo getDownLoadFilmInfo(String url, boolean isLoadDB) {

        if (TextUtils.isEmpty(url)) {
            return null;
        }
        List<DownLoadFilmInfo> downloadList = getDownFilmInfoCachedClone();
        if(downloadList != null){
            for (DownLoadFilmInfo download : downloadList) {

                if (url.equals(download.getUrl())) {
                    return download;
                }
            }
        }

        if (!isLoadDB) {
            return null;
        }
        // 缓存没找到再load数据库
        loadDownFilmInfoFromDB();
        downloadList = getDownFilmInfoCachedClone();
        for (DownLoadFilmInfo download : downloadList) {
            if (url.equals(download.getUrl())) {
                return download;
            }
        }
        return null;
    }

    public static void deleteDownFilmCached(final DownLoadFilmInfo d) {
        if (d == null) {
            return;
        }
        sDownFilmList.remove(d);
    }

    public static void deleteDownFilmVideoCached(final DownLoadFilmInfo d) {
        if (d == null) {
            return;
        }
        VideoInfo v = getVideoInfoDownload(d.getPath());
        if (v == null) {
            return;
        }
        sDownloadVideoList.remove(v);
    }


}
