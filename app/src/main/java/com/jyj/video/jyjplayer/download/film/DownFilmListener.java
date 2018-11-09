package com.jyj.video.jyjplayer.download.film;


import com.jyj.video.jyjplayer.download.film.bean.DownLoadFilmInfo;

/**
 * Created by zhengjiayang on 2018/5/7.
 */

public interface DownFilmListener {

    void onStart(DownLoadFilmInfo download);

    void onProgress(DownLoadFilmInfo download);

    void onPause(DownLoadFilmInfo download);

    void onCancel(DownLoadFilmInfo download);

    void onFinish(DownLoadFilmInfo download);

    void onWait(DownLoadFilmInfo download);

    void onError(int error, DownLoadFilmInfo download);

    void onRetry(DownLoadFilmInfo download);

}
