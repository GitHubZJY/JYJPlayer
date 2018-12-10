package com.jyj.video.jyjplayer.module.local;

import android.content.Context;

import com.jyj.video.jyjplayer.filescan.model.FileVideoModel;
import com.jyj.video.jyjplayer.filescan.model.bean.FolderInfo;

import java.util.List;

/**
 * Created by zhengjiayang on 2018/11/9.
 */

public interface LocalTasksContract {

    interface View {
        void startRefresh();
        void notifyFolderListView(List<FolderInfo> scanList);
        void showEmptyView();
        void hideEmptyView();
        void resume();
    }

    interface Model {
        void getAllVideoFolderData(boolean isUseCache);
        List<FolderInfo> getCachedOnlyFileInfos();
        List<FolderInfo> sortVideos2FolderInfo();
    }

    interface Presenter {
        void checkSDPermission(Context context);
        void scanSystemFolderData(boolean isUseCache);
        void destroy();
    }
}
