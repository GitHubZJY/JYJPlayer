package com.jyj.video.jyjplayer.filescan;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.jyj.video.jyjplayer.AppApplication;
import com.jyj.video.jyjplayer.event.SystemMediaScanFinishEvent;
import com.jyj.video.jyjplayer.filescan.model.FileVideoModel;
import com.jyj.video.jyjplayer.filescan.model.bean.VideoInfo;
import com.jyj.video.jyjplayer.utils.SdcardUtil;
import com.jyj.video.jyjplayer.utils.StorageAddressUtil;
import com.jyj.video.jyjplayer.utils.VideoUtil;
import com.zjyang.base.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zjyang
 * @date 17-11-13
 */

public class SystemVideoScanner {

    private static final String TAG = "SystemFileScanner";

    public static final String URI_SUFFIX = "file://";

    static void getSystemVideoInfos() {
        sysVideoList.clear();
        setVideoList();
        if (sysVideoList != null && !sysVideoList.isEmpty()) {
            FileVideoModel.refreshAllVideoInfo(sysVideoList);
            EventBus.getDefault().post(new SystemMediaScanFinishEvent());
        }
    }

    private static List<VideoInfo> sysVideoList = new ArrayList<VideoInfo>();// 视频信息集合

    private static void setVideoList() {
        // MediaStore.Video.Thumbnails.DATA: 视频缩略图的文件路径
//        String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA,
//                MediaStore.Video.Thumbnails.VIDEO_ID};

        // MediaStore.Video.Media.DATA：视频文件路径；
        // MediaStore.Video.Media.DISPLAY_NAME : 视频文件名，如 testVideo.mp4
        // MediaStore.Video.Media.TITLE: 视频标题 : testVideo
        String[] mediaColumns = {MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DATE_MODIFIED, // 秒
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.DISPLAY_NAME};

        Cursor cursor = null;
        ContentResolver resolver = AppApplication.getContext().getContentResolver();
        try{
            cursor = resolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    mediaColumns, null, null, null);
        }catch (Exception e){
            cursor = null;
        }

        if (cursor == null) {
            LogUtil.e(TAG, "系统--没有找到可播放视频文件");
            return;
        }
        if (cursor.moveToFirst()) {
            do {
                VideoInfo info = new VideoInfo();
//                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));
//                Cursor thumbCursor = resolver.query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
//                        thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID + "=" + id, null, null);
//                if (thumbCursor.moveToFirst()) {
//                    info.setThumbPath(thumbCursor.getString(thumbCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA)));
//                }
//                thumbCursor.close();
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                info.setPath(path);
                info.setName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                info.setSize(size);
                info.setDuration(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)));
                info.setLastModify(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED)) * 1000);
                info.setCreateTime(info.getLastModify());
                info.setDisplayName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)));
//                info.setMimeType(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)));

                // 视频路径
                if (!TextUtils.isEmpty(path)) {
                    path = uriToTruthPath(path);
                    info.setPath(path);
                } else {
                    return;
                }


                LogUtil.d(TAG, "DisplayName: " + info.getDisplayName()
                        + " Path: " + info.getPath()
                        + " size: " + info.getSize()
                        + " duration: " + info.getDuration()
                        + " lastModify: " + info.getLastModify()
                        + " createTime: " + info.getCreateTime()
                        + " Name: " + info.getName()
                        + " MimeType: " + cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)));

                // 过滤广告文件夹： 非 ad-cache adscache video-cache 非ins下载文件夹
                if (size > 0
                        //&& !"Camera".equals(VideoUtil.getVideoName(VideoUtil.getParentPath(path)))
                        // other
                        && !path.toLowerCase().contains("cache")
                        && !path.toLowerCase().contains("temp")
                        && !path.toLowerCase().contains("wandoujia")
                        // 社交app同名文件夹
                        && !path.toLowerCase().contains("musically")
                        && !path.toLowerCase().contains("facebook")
                        && !path.toLowerCase().contains("instagram")
                        && !path.toLowerCase().contains("twitter")
                        && !path.toLowerCase().contains("vine")
                        && !path.toLowerCase().contains("youtube")
                        && !path.toLowerCase().contains("snapchat")
                        && !path.toLowerCase().contains("wechat")
                        && !path.toLowerCase().contains("tencent")
                        && !path.toLowerCase().contains("messager")
                        && !path.toLowerCase().contains("whatsapp")) {
                    sysVideoList.add(info);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
    }


    /**
     * 将内存备份地址转化为真实/sdcard 地址
     */
    public static String uriToTruthPath(String path) {

        if (TextUtils.isEmpty(path) || !StorageAddressUtil.isLocalSdCardFile(path)) {
            return path;
        }

        File file = null;
        try {
            file = new File(path);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (file != null && file.exists()) {
            String filePath = file.getAbsolutePath();
            String[] dataStr = filePath.split("/");
            String fileTruePath = "/sdcard";

            int startIndex = 4;
            if (path.contains(StorageAddressUtil.SDCARD_NAME_3)) {
                startIndex = 3;
            }
            for (int i = startIndex; i < dataStr.length; i++) {
                fileTruePath = fileTruePath + "/" + dataStr[i];
            }
            return fileTruePath;
        }
        return path;
    }
}
