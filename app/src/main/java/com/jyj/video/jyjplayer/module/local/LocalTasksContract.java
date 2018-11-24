package com.jyj.video.jyjplayer.module.local;

import com.jyj.video.jyjplayer.filescan.model.FileVideoModel;
import com.jyj.video.jyjplayer.filescan.model.bean.FolderInfo;

import java.util.List;

/**
 * Created by zhengjiayang on 2018/11/9.
 */

public interface LocalTasksContract {

    interface View {
        void notifyFolderListView(List<FolderInfo> scanList);
        void showEmptyView();
        void hideEmptyView();
    }

    interface Model {
        void getAllVideoFolderData();
        List<FolderInfo> getCachedOnlyFileInfos();
        List<FolderInfo> sortVideos2FolderInfo();
    }

    interface Presenter {
        void scanSystemFolderData();
        void destroy();
    }
}
