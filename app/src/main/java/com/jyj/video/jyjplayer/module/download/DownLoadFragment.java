package com.jyj.video.jyjplayer.module.download;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.download.film.bean.DownLoadFilmInfo;
import com.jyj.video.jyjplayer.module.download.adapter.DownLoadListAdapter;
import com.jyj.video.jyjplayer.module.download.presenter.DownLoadPresenter;
import com.zjyang.base.broadcast.NetBroadcastReceiver;
import com.zjyang.base.utils.LogUtil;
import com.zjyang.base.utils.NetworkUtils;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by 74215 on 2018/11/3.
 */

public class DownLoadFragment extends Fragment implements NetBroadcastReceiver.NetEvent, DownloadTasksContract.View{

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

    public DownloadTasksContract.Presenter mPresenter;

    public static DownLoadFragment newInstance() {

        Bundle args = new Bundle();

        DownLoadFragment fragment = new DownLoadFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new DownLoadPresenter(this);
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
        mPresenter.initDownLoadData();
    }

    @Override
    public void setDownLoadRecyclerView(List<DownLoadFilmInfo> downLoadFilmInfos){
        mDownLoadLv.setNestedScrollingEnabled(false);
        mDownLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mDownLoadLv.setLayoutManager(mDownLayoutManager);
        mDownLoadAdapter = new DownLoadListAdapter(getContext(), getActivity(), downLoadFilmInfos);
        mDownLoadLv.setAdapter(mDownLoadAdapter);
    }


    @Override
    public void checkEmptyView(List<DownLoadFilmInfo> downloadList){
        if(downloadList == null || downloadList.size() == 0){
            mEmptyView.setVisibility(View.VISIBLE);
        }else{
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void notifyDownListItem(int index, List<DownLoadFilmInfo> downloadList){
        mDownLoadAdapter.notifyItemChanged(index, downloadList);
    }

    @Override
    public void notifyDownListItem(int index){
        mDownLoadAdapter.notifyItemChanged(index);
    }

    @Override
    public void notifyAllList(){
        mDownLoadAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        //checkEmptyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(unbinder != null){
            unbinder.unbind();
        }
        if(mPresenter != null){
            mPresenter.destroy();
        }
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
