package com.jyj.video.jyjplayer.filescan;

import android.os.AsyncTask;


import com.jyj.video.jyjplayer.filescan.model.FileVideoModel;
import com.jyj.video.jyjplayer.filescan.model.bean.FolderInfo;
import com.jyj.video.jyjplayer.manager.FolderListPicManager;
import com.jyj.video.jyjplayer.manager.SpManager;
import com.jyj.video.jyjplayer.manager.VideoListDurManager;
import com.jyj.video.jyjplayer.utils.StorageAddressUtil;
import com.zjyang.base.utils.LogUtil;

import java.util.List;

/**
 * @author denglongyun
 * @date 17-10-19
 */

public class ScannerAsyncTask extends AsyncTask<Void, Integer, List<FolderInfo>> {
    private static final String TAG = "FileScanner";

    boolean mIsCheckNoMedia = false;
    boolean mIsScanHiddenFolder = false;

    @Override
    protected List<FolderInfo> doInBackground(Void... params) {
        // 包含 多个SdCard
        String[] sdcardList = StorageAddressUtil.getAllSuitableStorage();

        mIsCheckNoMedia = SpManager.getInstance().getIsCheckNoMediaFile();
        mIsScanHiddenFolder = SpManager.getInstance().getIsShowHiddenFolder();
        FolderListPicManager.clearPicErrFolderVector();
        VideoListDurManager.clearPicErrVideoVector();

        long a = System.currentTimeMillis();
        if (FileVideoModel.getAllVideoListClone().isEmpty() && FileVideoModel.getFolderInfoCntFromDB() < 1) {
            SystemVideoScanner.getSystemVideoInfos();
        }

        long filter = JniUtils.FILTER_FLAG_NONE;
        if (mIsScanHiddenFolder) {
            filter |= JniUtils.FILTER_FLAG_HIDDEN;
        }
        if (mIsCheckNoMedia) {
            filter |= JniUtils.FILTER_FLAG_NOMEDIA;
        }
        filter |= JniUtils.FILTER_FLAG_ADS_CACHE;
        try {
            JniUtils.getInstance().getDocumentFiles(sdcardList, filter);
        } catch (Throwable t) {
            t.printStackTrace();
            LogUtil.e("JniScanFileScanner", "扫描异常中断 ===  Throwable：" + t);
        }

        long b = System.currentTimeMillis();

        float scanTime = (float) (b - a) / 1000;
        LogUtil.e("JniScanFileScanner", "扫描完毕 ===  总耗时：" + scanTime + " 秒");
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(List<FolderInfo> videoInfoList) {
        super.onPostExecute(videoInfoList);
    }
}
