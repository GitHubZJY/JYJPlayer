package com.jyj.video.jyjplayer.manager;

import android.text.TextUtils;

import com.jyj.video.jyjplayer.db.helper.DurationHelper;
import com.jyj.video.jyjplayer.utils.VideoUtil;
import com.zjyang.base.utils.LogUtil;

import java.util.LinkedHashMap;
import java.util.Vector;

/**
 * Created by vincent on 2017/11/13.
 */

public class VideoListDurManager {
    private static final String TAG = "VideoListDurManager";

    private static final LinkedHashMap<String, Long> sVideoDurMap = new LinkedHashMap<String, Long>();
    public static final Vector<String> sPicErrVideoVector = new Vector<String>();  // 多线程, 用vector来确保add, contain, clear之间的线程安全


    public static Long checkVideoIconCached(String path) {
        if (TextUtils.isEmpty(path)) {
            return 0L;
        }

        Long duration = sVideoDurMap.get(path);

        if (duration != null && duration > 0) {
            LogUtil.d(TAG, "读取cached 时长: " + duration + "  " + path);
            return duration;
        }
        return 0L;
    }


    public static Long loadVideoDuration(String path) {
        if (TextUtils.isEmpty(path)) {
            return 0L;
        }

        Long duration = checkVideoIconCached(path);

        if (duration > 0) {
            return duration;
        }

        if (sPicErrVideoVector.contains(path)) {
            return 0L;
        }

        duration = DurationHelper.getInstance().queryByPath(path);
        LogUtil.d(TAG, "读取DB 时长: " + duration + "  " + path);
        if (duration > 0) {
            sVideoDurMap.put(path, duration);
            return duration;
        }

        LogUtil.d(TAG, "读取视频文件时长:  " + path);
        duration = VideoUtil.getDuration(path);
        if (duration > 0) {
            LogUtil.d(TAG, "video 时长: " + duration);
            sVideoDurMap.put(path, duration);
            DurationHelper.getInstance().addOrReplace(path, duration);
            return duration;
        }
        /**
         *  无法获取时长, 丢到排除名单里面, 避免每次滚动都尝试去加载
         */
        sPicErrVideoVector.add(path);

        return 0L;
    }

    public static void clearPicErrVideoVector() {
        sPicErrVideoVector.clear();
    }
}
