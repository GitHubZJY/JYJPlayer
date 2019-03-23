package com.jyj.video.jyjplayer.module.download;

import java.util.List;

/**
 * Created by 74215 on 2018/11/10.
 */

public interface AddDownLoadTasksContract {

    interface View {
        void notifyHistoryListView(List<String> urlList);
    }

    interface Presenter {
        void addUrlToDownList(String url);
        void initHistory();
        void clearHistory();
    }

    interface Model {
        List<String> addDownUrl(String url);
        List<String> getDownUrlList();
        void clearDownUrl();
    }
}
