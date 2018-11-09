package com.example.zjy.player.utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.util.Log;

/**
 * Created by 74215 on 2018/5/26.
 */

public class VideoUtils {


    public static final String TAG = "VideoUtils";

    public static Bitmap getThumbFromVideo(String videoPath) {
        Bitmap bitmap = null;
        try{
            String dataPath = videoPath;
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(dataPath);
            bitmap = retriever.getFrameAtTime(1*1000*1000,MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        }catch (Exception e){
            e.printStackTrace();
        }

        return bitmap;
    }

    public static Bitmap getThumbFromVideo(String videoPath, long time) {
        String dataPath = videoPath;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(dataPath);
        Bitmap bitmap = retriever.getFrameAtTime(time*1000,MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        return bitmap;
    }

    //获取视频总时长
    public static int getVideoDuration(String path){
        String duration = "0";
        try{
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(path);
            duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            Log.e(TAG, "获取视频时长为: " + duration);
        }catch (Exception e){
            e.printStackTrace();
        }

        return Integer.parseInt(duration);
    }




    /** * @param videoPath 视频路径 *
     * @param width 图片宽度
     * @param height 图片高度 *
     * @param kind eg:MediaStore.Video.Thumbnails.MICRO_KIND MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96 * @return */
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height,int kind) {
        // 获取视频的缩略图
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        // extractThumbnail 方法二次处理,以指定的大小提取居中的图片,获取最终我们想要的图片
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }


}
