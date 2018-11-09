package com.jyj.video.jyjplayer.filescan;

import android.text.TextUtils;


import com.jyj.video.jyjplayer.AppApplication;
import com.jyj.video.jyjplayer.db.helper.FolderHelper;
import com.jyj.video.jyjplayer.db.helper.VideoHelper;
import com.jyj.video.jyjplayer.event.JniScanFoundEvent;
import com.jyj.video.jyjplayer.event.SingleFolderScanFinishEvent;
import com.jyj.video.jyjplayer.filescan.model.FileVideoModel;
import com.jyj.video.jyjplayer.filescan.model.bean.FolderInfo;
import com.jyj.video.jyjplayer.filescan.model.bean.VideoInfo;
import com.jyj.video.jyjplayer.manager.SpManager;
import com.jyj.video.jyjplayer.utils.SdcardUtil;
import com.jyj.video.jyjplayer.utils.VideoUtil;
import com.zjyang.base.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import tv.danmaku.ijk.media.player.PlugInSoHelper;

/**
 * @author denglongyun
 * @date 17-11-2
 */

public class JniUtils {
    public static final long FILTER_FLAG_NONE = 0x0;
    public static final long FILTER_FLAG_NOMEDIA = 0x1;
    public static final long FILTER_FLAG_HIDDEN = 0x2;
    public static final long FILTER_FLAG_WITHOUT_RECURSIVE = 0x4; // 不递归遍历文件夹
    public static final long FILTER_FLAG_ADS_CACHE = 0x8; // 不扫描广告缓存文件夹
    private static final String TAG = "JniScan"; // 和scanner.c logtag 一致

    private static final Set<Long> videoDateSet = new HashSet<Long>();

    private static JniUtils sJniUtils;
    private final static Object LOCK = new Object();

    private static LinkedList<VideoInfo> sVideoInfoList = new LinkedList<VideoInfo>();
    private static LinkedList<VideoInfo> sFolderVideoList = new LinkedList<VideoInfo>();

    private JniUtils() {

    }

    public static JniUtils getInstance() {
        if (sJniUtils == null) {
            synchronized (LOCK) {
                if (sJniUtils == null) {
                    sJniUtils = new JniUtils();
                }
            }
        }
        return sJniUtils;
    }

    static {
        try {
            PlugInSoHelper.loadLibrary("scan");//与build.gradle里面设置的so名字，必须一致
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public native void getDocumentFiles(String[] rootPath, long filterFlag);

    /**
     * 文件夹扫描 不进行深度扫描遍历子文件夹
     */
    public void onFolderScaned(final VideoInfo[] files) {
        LogUtil.d(TAG, "onFolderScanned");
        if (files == null) {
            LogUtil.d(TAG, "JNI 单层文件夹扫描完毕 文件数=  null");
            EventBus.getDefault().post(new SingleFolderScanFinishEvent());
            return;
        }

        if (files.length == 0) {
            LogUtil.d(TAG, "JNI 单层文件夹扫描完毕 文件数=  0");
            EventBus.getDefault().post(new SingleFolderScanFinishEvent());
            return;
        }

        LogUtil.d(TAG, "JNI 单层文件夹扫描完毕 文件数= " + files.length);

        LogUtil.d(TAG, "JNI 单层文件夹处理完毕 耗时？");
        long a = System.currentTimeMillis();
        sFolderVideoList.clear();
        long folderSize = 0;
        for (VideoInfo video : files) {
            if (video == null || TextUtils.isEmpty(video.getPath()) || video.getSize() < 1 || video.getPath().contains(SdcardUtil.DOWNLOAD_SAVE_PATH)) {
                continue;
            }

            LogUtil.d(TAG, "files-> video db " + video.getPath());
            String name = VideoUtil.getVideoName(video.getPath());
            video.setDisplayName(name);
            video.setName(name);
            sFolderVideoList.add(video);
            folderSize += video.getSize();
            FileVideoModel.addOrReplaceVideoInfo(video);
        }

        if (sFolderVideoList.isEmpty()) {
            LogUtil.d(TAG, "files-> video db 可用列表为空！！");
            EventBus.getDefault().post(new SingleFolderScanFinishEvent());
            return;
        }
        VideoInfo oneVideo = sFolderVideoList.get(0);
        String folderPath = VideoUtil.getParentPath(oneVideo.getPath());
        if (TextUtils.isEmpty(folderPath)) {
            LogUtil.d(TAG, "获取父文件夹路径为空！！");
            EventBus.getDefault().post(new SingleFolderScanFinishEvent());
            return;
        }
        // 仅替换视频list
        FolderInfo folderInfo = FileVideoModel.getFileInfo(folderPath);
        if (folderInfo == null) {
            folderInfo = new FolderInfo();
            folderInfo.setPath(folderPath);
            File file = new File(folderPath);
            if (file.exists() && file.isDirectory()) {
                folderInfo.setCreateTime(file.lastModified());
                folderInfo.setLastModify(file.lastModified());
            } else {
                long time = System.currentTimeMillis();
                folderInfo.setCreateTime(time);
                folderInfo.setLastModify(time);
            }
            folderInfo.setName(oneVideo.getParentFileName());
            folderInfo.setDisplayName(oneVideo.getParentFileName());
            folderInfo.setIsNew(false);
        }
        folderInfo.setSize(folderSize);
        Collections.sort(sFolderVideoList, SortComparators.getInstance().mCreateTimeDesc);
        folderInfo.setVideoList(sFolderVideoList);

        FileVideoModel.addOrReplaceFolderInfo(folderInfo);
        long b = System.currentTimeMillis();
        LogUtil.d(TAG, "JNI 单层文件夹处理完毕 耗时" + (b - a));

        // 发车之后不宜再用原始数据 备份先
        LinkedList<VideoInfo> tempList = (LinkedList<VideoInfo>) sFolderVideoList.clone();

        EventBus.getDefault().post(new SingleFolderScanFinishEvent());

        long c = System.currentTimeMillis();
        FolderHelper.getInstance().addOrReplace(folderInfo);
        for (VideoInfo v : tempList) {
            VideoHelper.getInstance().addOrReplace(v);
        }
        long d = System.currentTimeMillis();
        LogUtil.e(TAG, "塞入缓存完毕 ===  耗时：" + (d - c));
    }

    /**
     * 文件夹深度遍历扫描
     * @param files
     */
    public void onFileScaned(final VideoInfo[] files) {
        LogUtil.d(TAG, "onFileScaned");
        sVideoInfoList.clear();
        videoDateSet.clear();

        if (files == null) {
            EventBus.getDefault().post(new JniScanFoundEvent());
            LogUtil.d(TAG, "JNI 扫描完毕 文件数=  null");

            FileVideoModel.refreshAllVideoInfo(sVideoInfoList);
            return;
        }

        if (files.length == 0) {
            EventBus.getDefault().post(new JniScanFoundEvent());
            LogUtil.d(TAG, "JNI 扫描完毕 文件数=  0");

            FileVideoModel.refreshAllVideoInfo(sVideoInfoList);
            return;
        }

        LogUtil.d(TAG, "JNI 扫描完毕 文件数= " + files.length);

//        if (MainActivity2.getCachedThreadPool().isShutdown()) {
//            EventBus.getDefault().post(new JniScanFoundEvent());
//            return;
//        }
//        MainActivity2.getCachedThreadPool().execute(new Runnable() {
//            @Override
//            public void run() {
//
//                long a = System.currentTimeMillis();
//                for (VideoInfo video : files) {
//                    if (video == null || TextUtils.isEmpty(video.getPath()) || video.getSize() < 1 || video.getPath().contains(SdcardUtil.DOWNLOAD_SAVE_PATH)) {
//                        continue;
//                    }
//
////                  LogUtil.d(TAG, "files-> video db " + video.getPath());
//                    String name = VideoUtil.getVideoName(video.getPath());
//                    video.setDisplayName(name);
//                    video.setName(name);
//                    // LogUtil.d(TAG, "files-> video db " + video);
//                    sVideoInfoList.add(video);
//                    videoDateSet.add(VideoUtil.time2date(video.getLastModify()));
//                }
//                FileVideoModel.refreshAllVideoInfo(sVideoInfoList);
//                long b = System.currentTimeMillis();
//
//                LogUtil.e(TAG, "塞入缓存完毕 ===  耗时：" + (b - a));
//
//                if (sVideoInfoList.isEmpty()) {
//                    FolderHelper.getInstance().deleteAll();
//                    VideoHelper.getInstance().deleteAll();
//                }
//
//                // 发车之后不宜再用原始数据 备份先
//                Set<Long> tempVideoDateSet = new HashSet<Long>();
//                tempVideoDateSet.addAll(videoDateSet);
//
//                EventBus.getDefault().post(new JniScanFoundEvent());
//                LogUtil.e("JniScan", "统计 videoCnt ===============" + sVideoInfoList.size());
//
//                // ----------------每周几新增 统计 START----------------------------------------------//
//                if (tempVideoDateSet.isEmpty()) {
//                    return;
//                }
//                Set<Long> preSet = null;
//                String preDates = SpManager.getInstance().getAllVideoDate();
//                if (!TextUtils.isEmpty(preDates)) {
//                    preSet = VideoUtil.getVideoDateSetFromJson(preDates);
//                }
//                SpManager.getInstance().setAllVideoDate(AppApplication.getGson().toJson(tempVideoDateSet));
//
//                if (preSet != null && !preSet.isEmpty()) {
//                    for (Long d : preSet) {
//                        LogUtil.e("t000_download_week", "统计 已上传过的date ===============" + d);
//                        tempVideoDateSet.remove(d);
//                    }
//                }
//                for (Long date : tempVideoDateSet) {
//                    LogUtil.e("t000_download_week", "统计 新date ===============" + date);
//                }
//                // ----------------每周几新增 统计 END----------------------------------------------//
//
//            }
//        });
    }
}
