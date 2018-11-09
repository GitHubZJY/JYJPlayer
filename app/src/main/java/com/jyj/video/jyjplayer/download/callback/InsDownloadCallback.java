package com.jyj.video.jyjplayer.download.callback;

/**
 * @author denglongyun
 * @date 17-12-16
 */

public interface InsDownloadCallback extends DownloadCallback {

    void onAlreadyExist();

    void onInvalidUrl();

    void onNotInsUrl();

    void onCheckSuccess();
}
