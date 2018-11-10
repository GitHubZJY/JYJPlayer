package com.jyj.video.jyjplayer.download.callback;

import com.jyj.video.jyjplayer.download.bean.DownloadInfo;

/**
 * @author zjyang
 * @date 17-12-16
 */

public interface DownloadCallback {

    void onStart(DownloadInfo download);

    void onProgress(DownloadInfo download);

    void onPause(DownloadInfo download);

    void onCancel(DownloadInfo download);

    void onFinish(DownloadInfo download);

    void onAllFinish(DownloadInfo download);

    void onWait(DownloadInfo download);

    void onError(int error, DownloadInfo download);
}
