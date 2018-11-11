package com.jyj.video.jyjplayer.module.download;

import com.jyj.video.jyjplayer.download.film.bean.DownLoadFilmInfo;

import java.util.List;

/**
 * Created by 74215 on 2018/11/11.
 */

public interface DownloadTasksContract {

    interface View {
        void setDownLoadRecyclerView(List<DownLoadFilmInfo> downloadList);
        void notifyDownListItem(int index);
        void notifyDownListItem(int index, List<DownLoadFilmInfo> downloadList);
        void notifyAllList();
        void checkEmptyView(List<DownLoadFilmInfo> downloadList);
    }

    interface Presenter {
        void initDownLoadData();
        void destroy();
    }

    interface Model {
        void loadDataFromDB();
        void updateDownLoadData();
        void addOrReplaceDownload(DownLoadFilmInfo download);
        int getIndexFromData(DownLoadFilmInfo downLoadFilmInfo);
        List<DownLoadFilmInfo> getDownLoadList();
    }

}
