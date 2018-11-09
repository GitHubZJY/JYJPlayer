package com.jyj.video.jyjplayer.manager;

import android.graphics.Bitmap;
import android.text.TextUtils;


import com.jyj.video.jyjplayer.constant.InfoType;
import com.jyj.video.jyjplayer.db.helper.ThumbnailHelper;
import com.jyj.video.jyjplayer.download.Constant;
import com.jyj.video.jyjplayer.download.bean.DownloadInfo;
import com.jyj.video.jyjplayer.filescan.model.bean.FolderInfo;
import com.jyj.video.jyjplayer.filescan.model.bean.VideoInfo;
import com.jyj.video.jyjplayer.utils.BitmapUtility;
import com.jyj.video.jyjplayer.utils.ImageUtil;
import com.jyj.video.jyjplayer.utils.VideoUtil;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.zjyang.base.utils.DrawUtils;
import com.zjyang.base.utils.LogUtil;

import java.util.Vector;

/**
 * Created by vincent on 2017/11/13.
 */

public class FolderListPicManager {
    private static final String TAG = "FolderListPicManager";
    public static final LruMemoryCache sPicFolderLru = new LruMemoryCache(12 * 1024 * 1024);
    public static final LruMemoryCache sPicVideoLru = new LruMemoryCache(12 * 1024 * 1024);
    public static final Vector<String> sPicErrFolderVector = new Vector<String>();  // 多线程, 用vector来确保add, contain, clear之间的线程安全
    public static final Vector<String> sPicErrVideoVector = new Vector<String>();  // 多线程, 用vector来确保add, contain, clear之间的线程安全

    public static final int THUMBNAIL_WIDTH = DrawUtils.dp2px(96);
    public static final int THUMBNAIL_HEIGHT = DrawUtils.dp2px(70);
    public static final float THUMBNAIL_RATE = (float) THUMBNAIL_WIDTH / THUMBNAIL_HEIGHT;


    public static Bitmap checkFolderIconCached(FolderInfo folderInfo) {
        if (folderInfo == null || folderInfo.getVideoList() == null || folderInfo.getVideoList().size() < 1) {
            return null;
        }
        String path = folderInfo.getPath();

        Bitmap bitmap = sPicFolderLru.get(path);
        if (bitmap != null) {
            if (!bitmap.isRecycled()) {
                LogUtil.d(TAG, "找到文件夹缓存:  " + path);
                return bitmap;
            } else {
                sPicFolderLru.remove(path);
            }
        }
        return null;
    }

    public static Bitmap checkVideoIconCached(VideoInfo videoInfo) {
        if (videoInfo == null) {
            return null;
        }
        String path = videoInfo.getPath();
        if (TextUtils.isEmpty(path)) {
            return null;
        }

        Bitmap bitmap = sPicVideoLru.get(path);

        if (bitmap != null) {
            if (!bitmap.isRecycled()) {
                LogUtil.d(TAG, "找到文件预览图缓存:  " + path);
                return bitmap;
            } else {
                sPicVideoLru.remove(path);
            }
        }
        return null;
    }

    public static Bitmap checkDownLoadIconCached(DownloadInfo videoInfo) {
        if (videoInfo == null) {
            return null;
        }
        String path;
        if(videoInfo.getType() == InfoType.SIDECAR){
            if(TextUtils.isEmpty(videoInfo.getLastFinishPath())){
                path = videoInfo.getPath();
            }else{
                path = videoInfo.getLastFinishPath();
            }
        }else{
            path = videoInfo.getPath();
        }
        if (TextUtils.isEmpty(path)) {
            return null;
        }

        Bitmap bitmap = sPicVideoLru.get(path);

        if (bitmap != null) {
            if (!bitmap.isRecycled()) {
                LogUtil.d(TAG, "找到文件预览图缓存:  " + path);
                return bitmap;
            } else {
                sPicVideoLru.remove(path);
            }
        }
        return null;
    }


    public static Bitmap loadFolderIcon(FolderInfo folderInfo) {
        if (folderInfo == null || folderInfo.getVideoList() == null || folderInfo.getVideoList().size() < 1) {
            return null;
        }

        Bitmap bitmap = checkFolderIconCached(folderInfo);
        if (bitmap != null) {
            return bitmap;
        }

        String path = folderInfo.getPath();

        if (sPicErrFolderVector.contains(path)) {
            return null;
        }

        LogUtil.d(TAG, "读取视频文件夹预览图1:  " + path);
        bitmap = loadVideoIcon(folderInfo.getVideoList().get(0));
        if (bitmap == null || bitmap.isRecycled()) {
            if (folderInfo.getVideoList().size() > 1) {
                LogUtil.d(TAG, "读取视频文件夹预览图2:  " + path);
                bitmap = loadVideoIcon(folderInfo.getVideoList().get(1));
            }
        }
        if (bitmap != null && !bitmap.isRecycled()) {
            //bitmap = ImageBlurUtil.blur(bitmap, 1f, 1f, 0f, 0f, 1);
            /** bitmap是从{@link sPicVideoLru } 里面拿出来的bitmap, 不要recycle */
            //if (bitmap != null && !bitmap.isRecycled()) {
                sPicFolderLru.put(path, bitmap);
                return bitmap;
            //}
        }

        /**
         *  无法加载到图, 丢到排除名单里面, 避免每次滚动都尝试去加载
         */
        sPicErrFolderVector.add(path);

        return null;
    }

    public static Bitmap loadVideoIcon(VideoInfo videoInfo) {
        if (videoInfo == null) {
            return null;
        }
        String path = videoInfo.getPath();
        if (TextUtils.isEmpty(path)) {
            return null;
        }

        Bitmap bitmap = checkVideoIconCached(videoInfo);

        if (bitmap != null) {
            return bitmap;
        }

        if (sPicErrVideoVector.contains(path)) {
            return null;
        }

        bitmap = ThumbnailHelper.getInstance().queryByPath(path);
        LogUtil.d(TAG, "读取DB预览图:  " + path + " ===========BM : " + bitmap);
        if (bitmap != null) {
            if (!bitmap.isRecycled()) {
                sPicVideoLru.put(path, bitmap);
                return bitmap;
            }
        }

        LogUtil.d(TAG, "读取视频文件预览图:  " + path);
        bitmap = VideoUtil.createVideoThumbnail(path);
        if (bitmap != null) {
            if (!bitmap.isRecycled()) {
                sPicErrVideoVector.remove(path);
//                LogUtil.d(TAG, "video预览区大小: " + bitmap.getWidth() + ", " + bitmap.getHeight() + ", size= " + (bitmap.getRowBytes() * bitmap.getHeight()));
                bitmap = fixThumbnailSize(bitmap, true);
//                LogUtil.d(TAG, "video预览区大小fixed: " + bitmap.getWidth() + ", " + bitmap.getHeight() + ", size= " + (bitmap.getRowBytes() * bitmap.getHeight()));
                sPicVideoLru.put(path, bitmap);
                ThumbnailHelper.getInstance().addOrReplace(path, bitmap);
                return bitmap;
            }
        }
        /**
         *  无法加载到图, 丢到排除名单里面, 避免每次滚动都尝试去加载
         */
        sPicErrVideoVector.add(path);

        return null;
    }

    public static Bitmap loadDownLoadIcon(DownloadInfo videoInfo, int type) {
        if (videoInfo == null) {
            return null;
        }
        String path;
        if(videoInfo.getType() == InfoType.SIDECAR){
            if(TextUtils.isEmpty(videoInfo.getLastFinishPath())){
                path = videoInfo.getPath();
            }else{
                path = videoInfo.getLastFinishPath();
            }
        }else{
            path = videoInfo.getPath();
        }

        if (TextUtils.isEmpty(path)) {
            return null;
        }

        Bitmap bitmap = checkDownLoadIconCached(videoInfo);

        if (bitmap != null) {
            return bitmap;
        }

        if (sPicErrVideoVector.contains(path)) {
            LogUtil.d("zjy", "type: " + type + "  " + "sPicErrVideoVector = null");
            //return null;
        }

        bitmap = ThumbnailHelper.getInstance().queryByPath(path);
        LogUtil.d(TAG, "读取DB预览图:  " + path + " ===========BM : " + bitmap);
        if (bitmap != null) {
            if (!bitmap.isRecycled()) {
                sPicVideoLru.put(path, bitmap);
                return bitmap;
            }
        }

        LogUtil.d(TAG, "读取视频文件预览图:  " + path);
        if(type == InfoType.VIDEO){
            bitmap = VideoUtil.createVideoThumbnail(path);
        }else if(type == InfoType.IMAGE){
            bitmap = ImageUtil.createImgBitmap(path);
        }else if(type == InfoType.SIDECAR){
            String lastFinishPath = videoInfo.getLastFinishPath();
            if (TextUtils.isEmpty(lastFinishPath)) {
                return null;
            }
            if (videoInfo.getStatus() == Constant.Status.ALL_FINISH){
                lastFinishPath = videoInfo.getDownloadList().get(0).getPath();
            }
            if(lastFinishPath.endsWith(".mp4")){
                //当前最后下载完成的子文件类型是视频
                LogUtil.d(TAG, "当前最后下载完成的子文件类型是视频");
                bitmap = VideoUtil.createVideoThumbnail(lastFinishPath);
            }else{
                LogUtil.d(TAG, "当前最后下载完成的子文件类型是图片");
                //当前最后下载完成的子文件类型是图片
                bitmap = ImageUtil.createImgBitmap(lastFinishPath);
            }
        }

        if (bitmap != null) {
            if (!bitmap.isRecycled()) {
                LogUtil.d(TAG, "video预览区大小: " + bitmap.getWidth() + ", " + bitmap.getHeight() + ", size= " + (bitmap.getRowBytes() * bitmap.getHeight()));
                bitmap = fixThumbnailSize(bitmap, true);
                if (bitmap != null) {
                    LogUtil.d(TAG, "video预览区大小fixed: " + bitmap.getWidth() + ", " + bitmap.getHeight() + ", size= " + (bitmap.getRowBytes() * bitmap.getHeight()));
                    sPicVideoLru.put(path, bitmap);
                    ThumbnailHelper.getInstance().addOrReplace(path, bitmap);
                }
                return bitmap;
            }
        }
        /**
         *  无法加载到图, 丢到排除名单里面, 避免每次滚动都尝试去加载
         */
        sPicErrVideoVector.add(path);

        return null;
    }

    public static Bitmap fixThumbnailSize(Bitmap bitmap, boolean recycleOriBitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return bitmap;
        }
        float rate = (float) bitmap.getWidth() / bitmap.getHeight();
        int width;
        int height;
        if (rate > THUMBNAIL_RATE) {
            height = THUMBNAIL_HEIGHT;
            width = Math.min(THUMBNAIL_WIDTH, (int) (THUMBNAIL_HEIGHT * rate));
        } else {
            width = THUMBNAIL_WIDTH;
            height = Math.min(THUMBNAIL_HEIGHT, (int) (width / rate));
        }
        Bitmap temp = BitmapUtility.createScaledBitmap(bitmap, width, height);
        if (recycleOriBitmap && temp != bitmap) {
            bitmap.recycle();
        }
        return temp;
    }

    public static void clearPicErrFolderVector() {
        sPicErrFolderVector.clear();
    }

    public static void clearPicErrVideoVector() {
        sPicErrVideoVector.clear();
    }
}
