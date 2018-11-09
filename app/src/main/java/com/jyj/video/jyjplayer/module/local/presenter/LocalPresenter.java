package com.jyj.video.jyjplayer.module.local.presenter;

import com.jyj.video.jyjplayer.event.JniScanFoundEvent;
import com.jyj.video.jyjplayer.event.SingleFolderScanFinishEvent;
import com.jyj.video.jyjplayer.event.SystemMediaScanFinishEvent;
import com.jyj.video.jyjplayer.filescan.model.FileVideoModel;
import com.jyj.video.jyjplayer.filescan.model.bean.FolderInfo;
import com.jyj.video.jyjplayer.module.local.LocalTasksContract;
import com.jyj.video.jyjplayer.module.local.model.LocalModel;
import com.zjyang.base.base.BasePresenter;
import com.zjyang.base.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by zhengjiayang on 2018/11/9.
 */

public class LocalPresenter implements LocalTasksContract.Presenter{

    private LocalTasksContract.View mView;
    private LocalTasksContract.Model mModel;

    public LocalPresenter(LocalTasksContract.View view) {
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        mView = view;
        mModel = new LocalModel();
    }

    @Override
    public void scanSystemFolderData() {
        mModel.getAllVideoFolderData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSingleFileScanFinishEvent(SingleFolderScanFinishEvent event) {
        List<FolderInfo> scanList = mModel.getCachedOnlyFileInfos();
        FileVideoModel.loadLatestVideosDesc();
        LogUtil.d("zjy", "获取扫描结果-单个文件夹扫描: " + (scanList == null ? -1 : scanList.size()));
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onJniScanFoundEvent(JniScanFoundEvent event) {
        final List<FolderInfo> scanList = mModel.sortVideos2FolderInfo();
        FileVideoModel.loadLatestVideosDesc();
        FileVideoModel.uploadScantimeStatistic();
        LogUtil.d("zjy", "获取扫描结果-JNI: " + (scanList == null ? -1 : scanList.size()));


        // 存入数据库------------------------------------------------------------------------

    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onSystemMediaScanFoundEvent(SystemMediaScanFinishEvent event) {
        final List<FolderInfo> scanList = mModel.sortVideos2FolderInfo();
        FileVideoModel.loadLatestVideosDesc();
        for(FolderInfo folderInfo : scanList){
            LogUtil.d("zjy", folderInfo.getName());
        }
        mView.notifyFolderListView(scanList);
        LogUtil.d("zjy", "获取扫描结果-系统媒体文件: " + (scanList == null ? -1 : scanList.size()));
    }

    @Override
    public void destroy() {
        EventBus.getDefault().unregister(this);
    }
}
