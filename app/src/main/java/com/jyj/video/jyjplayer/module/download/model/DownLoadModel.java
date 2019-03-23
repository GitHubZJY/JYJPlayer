package com.jyj.video.jyjplayer.module.download.model;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.jyj.video.jyjplayer.AppApplication;
import com.jyj.video.jyjplayer.constant.SpConstant;
import com.jyj.video.jyjplayer.download.film.bean.DownLoadFilmInfo;
import com.jyj.video.jyjplayer.filescan.model.FileVideoModel;
import com.jyj.video.jyjplayer.module.download.DownloadTasksContract;
import com.jyj.video.jyjplayer.subtitle.bean.SubLanguage;
import com.zjyang.base.base.BaseModel;
import com.zjyang.base.utils.LogUtil;
import com.zjyang.base.utils.SpUtils;

import java.util.ArrayList;
import java.util.List;

import static com.jyj.video.jyjplayer.constant.SpConstant.DOWNLOAD_URL_LIST;

/**
 * Created by 74215 on 2018/11/10.
 */

public class DownLoadModel extends BaseModel implements DownloadTasksContract.Model{

    public static final String TAG = "DownLoadModel";

    private List<DownLoadFilmInfo> mDownFilmList;


    @Override
    public void loadDataFromDB() {
        mDownFilmList = new ArrayList<>();
        FileVideoModel.loadDownFilmInfoFromDB();
    }

    @Override
    public void updateDownLoadData(){
        mDownFilmList.clear();
        List<DownLoadFilmInfo> cachelist = FileVideoModel.getDownFilmInfoCached();
        LogUtil.d(TAG, "cache size: "+cachelist.size());
        for(DownLoadFilmInfo downLoadFilmInfo : cachelist){
            LogUtil.d(TAG, "film name: "+downLoadFilmInfo.getPath());
            //mDownFilmList.add(downLoadFilmInfo);
        }
        mDownFilmList.addAll(cachelist);
    }

    @Override
    public int getIndexFromData(DownLoadFilmInfo downLoadFilmInfo){
        return mDownFilmList.indexOf(downLoadFilmInfo);
    }


    /**
     * 避免继续下载时有多个相同item
     * @param download
     */
    @Override
    public void addOrReplaceDownload(DownLoadFilmInfo download) {
        if (download == null) {
            return;
        }

        DownLoadFilmInfo pre = getVideoInfoDownload(download.getUrl());
        int index = -1;
        if (pre != null) { // 文件存在
            index = mDownFilmList.indexOf(pre);
        }

        if(index == -1){
            mDownFilmList.add(0, download);
        } else {
            mDownFilmList.set(index, download);
        }
    }

    /**
     * ！结果可能为空
     */
    public DownLoadFilmInfo getVideoInfoDownload(String url) {
        if (TextUtils.isEmpty(url) || mDownFilmList == null || mDownFilmList.isEmpty()) {
            return null;
        }
        for (DownLoadFilmInfo d : mDownFilmList) {
            if (url.equals(d.getUrl())) {
                return d;
            }
        }
        return null;
    }

    @Override
    public List<DownLoadFilmInfo> getDownLoadList(){
        return mDownFilmList;
    }


}
