package com.jyj.video.jyjplayer.module.download;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.download.film.DownFilmListener;
import com.jyj.video.jyjplayer.download.film.FilmDownLoadManager;
import com.jyj.video.jyjplayer.download.film.bean.DownLoadFilmInfo;
import com.jyj.video.jyjplayer.event.DownFilmDataChangeEvent;
import com.jyj.video.jyjplayer.filescan.model.FileVideoModel;
import com.jyj.video.jyjplayer.module.download.adapter.DownLoadListAdapter;
import com.zjyang.base.broadcast.NetBroadcastReceiver;
import com.zjyang.base.utils.DrawUtils;
import com.zjyang.base.utils.LogUtil;
import com.zjyang.base.utils.NetworkUtils;
import com.zjyang.base.utils.ScreenUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.jyj.video.jyjplayer.download.Constant.Status.ERROR;
import static com.jyj.video.jyjplayer.download.Constant.Status.PAUSE;
import static com.jyj.video.jyjplayer.download.Constant.Status.RETRY;

/**
 * Created by 74215 on 2018/11/3.
 */

public class DownLoadFragment extends Fragment implements NetBroadcastReceiver.NetEvent{

    private static final String TAG = "DownFilmListFragment";
    private Unbinder unbinder;

    @BindView(R.id.down_load_film_lv)
    RecyclerView mDownLoadLv;

    @BindView(R.id.empty_view)
    LinearLayout mEmptyView;

    @BindView(R.id.network_connection_error)
    TextView mNetConnectionError;
    /**
     * 监控网络的广播
     */
    private NetBroadcastReceiver netBroadcastReceiver;

    private DownLoadListAdapter mDownLoadAdapter;
    private LinearLayoutManager mDownLayoutManager;
    private List<DownLoadFilmInfo> mDownFilmList;

    private static final int BACK_FROM_DOWN_LOAD_DIALOG_VIEW = 6;
    private boolean mIsFromLocalDownLoadView;
    int preY = 0;

    public static DownLoadFragment newInstance() {

        Bundle args = new Bundle();

        DownLoadFragment fragment = new DownLoadFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_download, container, false);
        initView(view, savedInstanceState);
        return view;

    }

    protected void initView(View root, Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, root);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        FileVideoModel.loadDownFilmInfoFromDB();
        setDownLoadRecyclerView();
        setScrollListener();

        if (NetworkUtils.isNetworkOK(getContext())) {
            mNetConnectionError.setVisibility(View.GONE);
        } else {
            mNetConnectionError.setVisibility(View.VISIBLE);
        }

        //注册广播
        netBroadcastReceiver = new NetBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        getContext().registerReceiver(netBroadcastReceiver, filter);
        /**
         * 设置监听
         */
        netBroadcastReceiver.setNetEvent(this);
        initData(savedInstanceState);
    }

    protected void initData(Bundle savedInstanceState) {
        FilmDownLoadManager.getInstance(getContext()).addDownloadCallback(mDownCallback);

        checkEmptyView();
    }

    private void setDownLoadRecyclerView(){
        mDownLoadLv.setNestedScrollingEnabled(false);
        mDownLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mDownLoadLv.setLayoutManager(mDownLayoutManager);
        mDownFilmList = new ArrayList<>();
        mDownLoadAdapter = new DownLoadListAdapter(getContext(), getActivity(), mDownFilmList);
        mDownLoadLv.setAdapter(mDownLoadAdapter);
        notifyDownLoadData();
    }

    public void notifyDownLoadData(){
        mDownFilmList.clear();
        List<DownLoadFilmInfo> cachelist = FileVideoModel.getDownFilmInfoCached();
        LogUtil.d(TAG, "cache size: "+cachelist.size());
        for(DownLoadFilmInfo downLoadFilmInfo : cachelist){
            LogUtil.d(TAG, "film name: "+downLoadFilmInfo.getPath());
            //mDownFilmList.add(downLoadFilmInfo);
        }
        mDownFilmList.addAll(cachelist);
        mDownLoadAdapter.notifyDataSetChanged();
    }

    public void setScrollListener(){

    }

    private void checkEmptyView(){
        if(mDownFilmList == null || mDownFilmList.size() == 0){
            mEmptyView.setVisibility(View.VISIBLE);
        }else{
            mEmptyView.setVisibility(View.GONE);
        }
    }

    /**
     * 避免继续下载时有多个相同item
     * @param download
     */
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


    public DownFilmListener mDownCallback = new DownFilmListener() {
        @Override
        public void onStart(DownLoadFilmInfo download) {
            int index = mDownFilmList.indexOf(download);
            LogUtil.d(TAG, "onStart: " + index);
            //LogUtil.d("zjy", "onStart: " + download.getPath() + ", status: " + download.getStatus() + ", index: " + index);
            if(index != -1){
                //假如不等于-1，说明是失败重试触发的onStart
                addOrReplaceDownload(download);
                mDownLoadAdapter.notifyItemChanged(index, mDownFilmList);
            }else{
                addOrReplaceDownload(download);
                mDownLoadAdapter.notifyDataSetChanged();
            }
            checkEmptyView();
        }

        @Override
        public void onProgress(DownLoadFilmInfo download) {
            int index = -1;
            for(int i=0; i<mDownFilmList.size(); i++){
                if(mDownFilmList.get(i).getUrl().equals(download.getUrl())){
                    index = i;
                }
            }
            LogUtil.d(TAG, "onProgress: " + index);
            if(index == -1){
                LogUtil.e("DOWNLOAD_INS_HOME", "onProgress INDEX == -1 ERROR");
                return;
            }
            DownLoadFilmInfo d = mDownFilmList.get(index);
            d.setDownLoadSpeed(download.getDownLoadSpeed());
            d.setRemainSecond(download.getRemainSecond());
            d.setPercentage(download.getPercentage());
            d.setCurrentLength(download.getCurrentLength());
            d.setTotalLength(download.getTotalLength());

            mDownLoadAdapter.notifyItemChanged(index, mDownFilmList);
        }

        @Override
        public void onPause(DownLoadFilmInfo download) {
            LogUtil.d("FileVideoModel", "页面收到onPause");
            if(FilmDownLoadManager.getInstance(getContext()).isExistInDownloadMap(download)){
                LogUtil.d("FileVideoModel", "在下载队列中");
                int index = mDownFilmList.indexOf(download);
                for(int i=0; i<mDownFilmList.size(); i++){
                    if(mDownFilmList.get(i).getUrl().equals(download.getUrl())){
                        mDownFilmList.get(i).setStatus(PAUSE);
                        index = i;
                    }
                }
                mDownLoadAdapter.notifyItemChanged(index);
            }
        }

        @Override
        public void onCancel(DownLoadFilmInfo download) {

        }

        @Override
        public void onFinish(DownLoadFilmInfo download) {
            int index = mDownFilmList.indexOf(download);
            addOrReplaceDownload(download);
            mDownLoadAdapter.notifyItemChanged(index);
            EventBus.getDefault().post(new DownFilmDataChangeEvent());
        }

        @Override
        public void onWait(DownLoadFilmInfo download) {

        }

        @Override
        public void onError(int error, DownLoadFilmInfo download) {
            int index = mDownFilmList.indexOf(download);
            for(int i=0; i<mDownFilmList.size(); i++){
                if(mDownFilmList.get(i).getUrl().equals(download.getUrl())){
                    mDownFilmList.get(i).setStatus(ERROR);
                    index = i;
                }
            }
            mDownLoadAdapter.notifyItemChanged(index);
        }

        @Override
        public void onRetry(DownLoadFilmInfo download) {
            int index = mDownFilmList.indexOf(download);
            for(int i=0; i<mDownFilmList.size(); i++){
                if(mDownFilmList.get(i).getUrl().equals(download.getUrl())){
                    mDownFilmList.get(i).setStatus(RETRY);
                    index = i;
                }
            }
            mDownLoadAdapter.notifyItemChanged(index);
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownFilmDataChangeEvent(DownFilmDataChangeEvent event){
        checkEmptyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BACK_FROM_DOWN_LOAD_DIALOG_VIEW){
            //说明是从本地黏贴页跳转过来的，不进行刷新数据操作
            mIsFromLocalDownLoadView = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!mIsFromLocalDownLoadView){
            //FileVideoModel.loadDownFilmInfoFromDB();
            //notifyDownLoadData();
        }
        mIsFromLocalDownLoadView = false;
        checkEmptyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(unbinder != null){
            unbinder.unbind();
        }
        EventBus.getDefault().unregister(this);
        FilmDownLoadManager.getInstance(getContext()).removeDownloadCallback(mDownCallback);
        if (netBroadcastReceiver != null) {
            //注销广播
            getContext().unregisterReceiver(netBroadcastReceiver);
        }
    }

    @Override
    public void onNetChange(int netMobile) {
//        switch (netMobile) {
//            case NetUtil.NETWORK_WIFI:
//                mNetConnectionError.setVisibility(View.GONE);
//                break;
//            case NetUtil.NETWORK_MOBILE:
//                mNetConnectionError.setVisibility(View.GONE);
//                break;
//            case NetUtil.NETWORK_NONE:
//                mNetConnectionError.setVisibility(View.VISIBLE);
//                break;
//        }
    }
}
