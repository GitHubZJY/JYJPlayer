package com.jyj.video.jyjplayer.module.download.presenter;

import com.jyj.video.jyjplayer.module.download.AddDownLoadTasksContract;
import com.jyj.video.jyjplayer.module.download.model.DownLoadModel;
import com.zjyang.base.base.BasePresenter;

/**
 * Created by 74215 on 2018/11/10.
 */

public class AddDownLoadPresenter extends BasePresenter<AddDownLoadTasksContract.View, DownLoadModel>{

    @Override
    public DownLoadModel createModel() {
        return new DownLoadModel();
    }
}
