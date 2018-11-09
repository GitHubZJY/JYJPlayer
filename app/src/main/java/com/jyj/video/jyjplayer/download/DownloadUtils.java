package com.jyj.video.jyjplayer.download;

import android.text.TextUtils;

import com.jyj.video.jyjplayer.constant.InfoType;
import com.jyj.video.jyjplayer.utils.VideoUtil;
import com.zjyang.base.utils.LogUtil;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;

public class DownloadUtils {
    /**
     * 关闭流
     *
     * @param closeable
     */
    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static long stringToLong(String s) {
        if (s == null) return -1;
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }


    /**
     * 删除文件
     *
     * @param file
     * @return
     */
    public static boolean deleteFile(File file) {
        if (file != null && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 批量删除文件
     *
     * @param files
     */
    public static void deleteFile(File... files) {
        for (File file : files) {
            deleteFile(file);
        }
    }

    /**
     * 删除文件
     *
     * @param path
     * @param name
     */
    public static void deleteFile(String path, String name) {
        deleteFile(new File(path, name));
    }

    /**
     * 创建文件
     *
     * @param path
     * @return
     */
    public static synchronized boolean mkdirs(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File parentFile = new File(path);
        if (parentFile.exists() || parentFile.mkdirs()) {
            return true;
        }
        LogUtil.e("DownloadUtil", "mkdir FAILED " + path);

        return false;
    }

    /**
     * 创建文件
     *
     * @param path
     * @return
     */
    public static synchronized File createFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        String p = VideoUtil.getParentPath(path);
        if (TextUtils.isEmpty(p)) {
            return null;
        }
        File parentFile = new File(p);
        if (!parentFile.exists() && !parentFile.mkdirs()) {
            LogUtil.e("DownloadUtil", "mkdir FAILED " + p);
            return null;
        }

        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                LogUtil.e("DownloadUtil", "IOException " + e);
            }
        }

        return file;
    }

    public static boolean isFileExists(File file) {
        if (file != null && file.exists()) {
            return true;
        }
        return false;
    }

    public static float getPercentage(int currentSize, int totalSize) {
        if (currentSize > totalSize) {
            return 0;
        }

        return ((int) (currentSize * 10000.0 / totalSize)) * 1.0f / 100;
    }

    /**
     * 根据文件名解析contentType
     *
     * @param name
     * @return
     */
    public static String getMimeType(String name) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = null;
        try {
            contentTypeFor = fileNameMap.getContentTypeFor(URLEncoder.encode(name, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }


    /**
     * 默认类型: VIDEO
     */
    public static int getInfoType(String insDataTypeName) {
        // 类型:单个图片/单个视频/多图多视频组合
        if (InfoType.InsDataType.GraphVideo.toString().equals(insDataTypeName)) {
            return InfoType.VIDEO;
        } else if (InfoType.InsDataType.GraphImage.toString().equals(insDataTypeName)) {
            return InfoType.IMAGE;
        } else if (InfoType.InsDataType.GraphSidecar.toString().equals(insDataTypeName)) {
            return InfoType.SIDECAR;
        }
        return InfoType.VIDEO;
    }
}
