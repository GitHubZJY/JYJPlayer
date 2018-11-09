package com.jyj.video.jyjplayer.filescan;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.jyj.video.jyjplayer.filescan.model.bean.FolderInfo;
import com.jyj.video.jyjplayer.manager.SpManager;
import com.zjyang.base.utils.LogUtil;
import com.zjyang.base.utils.SpUtils;

/**
 * @author denglongyun
 * @date 17-10-19
 */

public class SingleFolderScannerAsyncTask extends AsyncTask<Void, Integer, FolderInfo> {
    private static final String TAG = "FileScannerSingle";

    private FolderInfo mFolderInfo = new FolderInfo();

    private String mPath = "";

    boolean mIsCheckNoMedia = false;
    boolean mIsScanHiddenFolder = false;

    public SingleFolderScannerAsyncTask(String path) {
        mPath = path;
    }

    @Override
    protected FolderInfo doInBackground(Void... params) {
        if (TextUtils.isEmpty(mPath)) {
            LogUtil.i(TAG, "单个扫描 path 为空 ！！");
            cancel(true);
            return null;
        }

        String[] sdcardList = new String[] {mPath};

        mIsCheckNoMedia = SpManager.getInstance().getIsCheckNoMediaFile();
        mIsScanHiddenFolder = SpManager.getInstance().getIsShowHiddenFolder();

        long a = System.currentTimeMillis();

        long filter = JniUtils.FILTER_FLAG_NONE;
        if (mIsScanHiddenFolder) {
            filter |= JniUtils.FILTER_FLAG_HIDDEN;
        }
        if (mIsCheckNoMedia) {
            filter |= JniUtils.FILTER_FLAG_NOMEDIA;
        }
        filter |= JniUtils.FILTER_FLAG_WITHOUT_RECURSIVE;
        JniUtils.getInstance().getDocumentFiles(sdcardList, filter);
        long b = System.currentTimeMillis();

        LogUtil.e(TAG, "单个扫描完毕 " + "  耗时：" + (b - a));
        return mFolderInfo;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(FolderInfo folderInfo) {
        super.onPostExecute(folderInfo);
    }

}
