package com.jyj.video.jyjplayer.module.local.model;

import com.jyj.video.jyjplayer.filescan.model.FileVideoModel;
import com.jyj.video.jyjplayer.filescan.model.bean.FolderInfo;
import com.jyj.video.jyjplayer.module.local.LocalTasksContract;
import com.zjyang.base.base.BaseModel;

import java.util.List;

/**
 * Created by zhengjiayang on 2018/11/9.
 */

public class LocalModel extends BaseModel implements LocalTasksContract.Model{

    FileVideoModel mFileVideoModel;

    public LocalModel() {
        mFileVideoModel = new FileVideoModel();
    }

    @Override
    public void getAllVideoFolderData(boolean isUseCache){
        if(isUseCache){
            mFileVideoModel.loadFolderInfosAsync();
            return;
        }
        mFileVideoModel.reLoadFolderInfos();
    }

    @Override
    public List<FolderInfo> getCachedOnlyFileInfos() {
        return mFileVideoModel.getCachedOnlyFileInfos();
    }

    @Override
    public List<FolderInfo> sortVideos2FolderInfo() {
        return mFileVideoModel.sortVideos2FolderInfo();
    }
}
