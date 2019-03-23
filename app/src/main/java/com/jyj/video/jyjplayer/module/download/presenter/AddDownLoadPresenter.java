package com.jyj.video.jyjplayer.module.download.presenter;

import com.jyj.video.jyjplayer.module.download.AddDownLoadTasksContract;
import com.jyj.video.jyjplayer.module.download.model.AddDownLoadModel;
import com.jyj.video.jyjplayer.module.download.model.DownLoadModel;
import com.zjyang.base.base.BasePresenter;
import com.zjyang.base.utils.LogUtil;

import java.util.List;

/**
 * Created by 74215 on 2018/11/10.
 */

public class AddDownLoadPresenter implements AddDownLoadTasksContract.Presenter{

    private AddDownLoadTasksContract.View mView;
    private AddDownLoadTasksContract.Model mModel;

    public AddDownLoadPresenter(AddDownLoadTasksContract.View view) {
        mView = view;
        mModel =  new AddDownLoadModel();
    }

    @Override
    public void addUrlToDownList(String url) {
        if(mModel == null){
            return;
        }
        LogUtil.d("zjy", "--->");
        List<String> urlList = mModel.addDownUrl(url);
        LogUtil.d("zjy", "urlList : " + urlList.size());
        mView.notifyHistoryListView(urlList);
    }

    @Override
    public void initHistory() {
        List<String> urlList = mModel.getDownUrlList();
        if(urlList == null){
            return;
        }
        mView.notifyHistoryListView(urlList);
    }

    @Override
    public void clearHistory() {
        mModel.clearDownUrl();
        mView.notifyHistoryListView(null);
    }
}
