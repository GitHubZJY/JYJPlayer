package com.jyj.video.jyjplayer.module.download.presenter;

import com.jyj.video.jyjplayer.AppApplication;
import com.jyj.video.jyjplayer.download.film.DownFilmListener;
import com.jyj.video.jyjplayer.download.film.FilmDownLoadManager;
import com.jyj.video.jyjplayer.download.film.bean.DownLoadFilmInfo;
import com.jyj.video.jyjplayer.event.DownFilmDataChangeEvent;
import com.jyj.video.jyjplayer.module.download.DownloadTasksContract;
import com.jyj.video.jyjplayer.module.download.model.DownLoadModel;
import com.zjyang.base.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.jyj.video.jyjplayer.download.Constant.Status.ERROR;
import static com.jyj.video.jyjplayer.download.Constant.Status.PAUSE;
import static com.jyj.video.jyjplayer.download.Constant.Status.RETRY;

/**
 * Created by 74215 on 2018/11/11.
 */

public class DownLoadPresenter implements DownloadTasksContract.Presenter {


    public static final String TAG = "DownLoadPresenter";

    private DownloadTasksContract.View mView;
    private DownloadTasksContract.Model mModel;

    public DownLoadPresenter(DownloadTasksContract.View mView) {
        this.mView = mView;
        mModel = new DownLoadModel();
        EventBus.getDefault().register(this);
        FilmDownLoadManager.getInstance(AppApplication.getContext()).addDownloadCallback(mDownCallback);
    }

    @Override
    public void initDownLoadData() {
        mModel.loadDataFromDB();
        mView.setDownLoadRecyclerView(mModel.getDownLoadList());
        mModel.updateDownLoadData();
        mView.notifyAllList();
        mView.checkEmptyView(mModel.getDownLoadList());
    }

    public DownFilmListener mDownCallback = new DownFilmListener() {
        @Override
        public void onStart(DownLoadFilmInfo download) {
            int index = mModel.getIndexFromData(download);
            LogUtil.d(TAG, "onStart: " + index);
            //LogUtil.d("zjy", "onStart: " + download.getPath() + ", status: " + download.getStatus() + ", index: " + index);
            if(index != -1){
                //假如不等于-1，说明是失败重试触发的onStart
                mView.notifyDownListItem(index, mModel.getDownLoadList());
            }else{
                mView.notifyAllList();
            }
            mModel.addOrReplaceDownload(download);
            mView.checkEmptyView(mModel.getDownLoadList());
        }

        @Override
        public void onProgress(DownLoadFilmInfo download) {
            int index = -1;
            for(int i=0; i<mModel.getDownLoadList().size(); i++){
                if(mModel.getDownLoadList().get(i).getUrl().equals(download.getUrl())){
                    index = i;
                }
            }
            LogUtil.d(TAG, "onProgress: " + index);
            if(index == -1){
                LogUtil.e("DOWNLOAD_INS_HOME", "onProgress INDEX == -1 ERROR");
                return;
            }
            DownLoadFilmInfo d = mModel.getDownLoadList().get(index);
            d.setDownLoadSpeed(download.getDownLoadSpeed());
            d.setRemainSecond(download.getRemainSecond());
            d.setPercentage(download.getPercentage());
            d.setCurrentLength(download.getCurrentLength());
            d.setTotalLength(download.getTotalLength());
            mView.notifyDownListItem(index, mModel.getDownLoadList());
        }

        @Override
        public void onPause(DownLoadFilmInfo download) {
            LogUtil.d("FileVideoModel", "页面收到onPause");
            if(FilmDownLoadManager.getInstance(AppApplication.getContext()).isExistInDownloadMap(download)){
                LogUtil.d("FileVideoModel", "在下载队列中");
                int index = mModel.getDownLoadList().indexOf(download);
                for(int i=0; i<mModel.getDownLoadList().size(); i++){
                    if(mModel.getDownLoadList().get(i).getUrl().equals(download.getUrl())){
                        mModel.getDownLoadList().get(i).setStatus(PAUSE);
                        index = i;
                    }
                }
                mView.notifyDownListItem(index);
            }
        }

        @Override
        public void onCancel(DownLoadFilmInfo download) {

        }

        @Override
        public void onFinish(DownLoadFilmInfo download) {
            int index = mModel.getIndexFromData(download);
            mModel.addOrReplaceDownload(download);
            mView.notifyDownListItem(index);
            EventBus.getDefault().post(new DownFilmDataChangeEvent());
        }

        @Override
        public void onWait(DownLoadFilmInfo download) {

        }

        @Override
        public void onError(int error, DownLoadFilmInfo download) {
            int index = mModel.getIndexFromData(download);
            for(int i=0; i<mModel.getDownLoadList().size(); i++){
                if(mModel.getDownLoadList().get(i).getUrl().equals(download.getUrl())){
                    mModel.getDownLoadList().get(i).setStatus(ERROR);
                    index = i;
                }
            }
            mView.notifyDownListItem(index);
        }

        @Override
        public void onRetry(DownLoadFilmInfo download) {
            int index = mModel.getIndexFromData(download);
            for(int i=0; i<mModel.getDownLoadList().size(); i++){
                if(mModel.getDownLoadList().get(i).getUrl().equals(download.getUrl())){
                    mModel.getDownLoadList().get(i).setStatus(RETRY);
                    index = i;
                }
            }
            mView.notifyDownListItem(index);
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownFilmDataChangeEvent(DownFilmDataChangeEvent event){
        mModel.updateDownLoadData();
        mView.setDownLoadRecyclerView(mModel.getDownLoadList());
        mView.checkEmptyView(mModel.getDownLoadList());
        mView.notifyAllList();
    }


    @Override
    public void destroy(){
        FilmDownLoadManager.getInstance(AppApplication.getContext()).removeDownloadCallback(mDownCallback);
        EventBus.getDefault().unregister(this);
    }
}
