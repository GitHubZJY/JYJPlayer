package com.jyj.video.jyjplayer.module.download.model;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.jyj.video.jyjplayer.AppApplication;
import com.jyj.video.jyjplayer.constant.SpConstant;
import com.jyj.video.jyjplayer.module.download.AddDownLoadTasksContract;
import com.zjyang.base.utils.SpUtils;

import java.util.ArrayList;
import java.util.List;

import static com.jyj.video.jyjplayer.constant.SpConstant.DOWNLOAD_URL_LIST;

/**
 * Created by 74215 on 2019/3/22.
 */

public class AddDownLoadModel implements AddDownLoadTasksContract.Model {
    private List<String> mDownUrlList = new ArrayList<>();

    @Override
    public List<String> addDownUrl(String url){
        if(mDownUrlList.size() >= 5){
            mDownUrlList.remove(mDownUrlList.get(mDownUrlList.size() - 1));
        }
        mDownUrlList.add(0, url);
        SpUtils.obtain(SpConstant.DEFAULT_SP_FILE).save(DOWNLOAD_URL_LIST, AppApplication.getGson().toJson(mDownUrlList));
        return mDownUrlList;
    }

    @Override
    public List<String> getDownUrlList(){
        String json = SpUtils.obtain(SpConstant.DEFAULT_SP_FILE).getString(DOWNLOAD_URL_LIST);
        if(TextUtils.isEmpty(json)){
            return mDownUrlList;
        }
        List<String> downHistoryList = AppApplication.getGson().fromJson(json, new TypeToken<List<String>>() {}.getType());
        mDownUrlList.clear();
        mDownUrlList.addAll(downHistoryList);
        return mDownUrlList;
    }

    @Override
    public void clearDownUrl() {
        if(mDownUrlList == null){
            return;
        }
        mDownUrlList.clear();
        SpUtils.obtain(SpConstant.DEFAULT_SP_FILE).save(DOWNLOAD_URL_LIST, AppApplication.getGson().toJson(mDownUrlList));
    }
}
