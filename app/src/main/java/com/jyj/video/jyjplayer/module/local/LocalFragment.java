package com.jyj.video.jyjplayer.module.local;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.event.JniScanFoundEvent;
import com.jyj.video.jyjplayer.event.SingleFolderScanFinishEvent;
import com.jyj.video.jyjplayer.event.SystemMediaScanFinishEvent;
import com.jyj.video.jyjplayer.filescan.model.FileVideoModel;
import com.jyj.video.jyjplayer.filescan.model.bean.FolderInfo;
import com.zjyang.base.utils.DrawUtils;
import com.zjyang.base.utils.HandlerUtils;
import com.zjyang.base.utils.LogUtil;
import com.zjyang.base.utils.ShapeUtils;
import com.zjyang.base.widget.SpaceItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 74215 on 2018/11/3.
 */

public class LocalFragment extends Fragment {

    FileVideoModel mFileVideoModel;

    private RecyclerView mFolderLv;
    private RelativeLayout mSearchEntrance;
    private List<FolderInfo> mFolderList;
    private FolderListAdapter mFolderAdapter;

    public static LocalFragment newInstance() {

        Bundle args = new Bundle();

        LocalFragment fragment = new LocalFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local, container, false);
        mSearchEntrance = (RelativeLayout) view.findViewById(R.id.search_entrance);
        mSearchEntrance.setBackground(ShapeUtils.getRoundRectDrawable(DrawUtils.dp2px(20), Color.WHITE));
        mFolderLv = (RecyclerView) view.findViewById(R.id.folder_lv);
        mFolderList = new ArrayList<>();
        mFolderAdapter = new FolderListAdapter(getContext(), mFolderList);
        mFolderLv.setAdapter(mFolderAdapter);
        mFolderLv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mFolderLv.addItemDecoration(new SpaceItemDecoration(DrawUtils.dp2px(2), 1, LinearLayoutManager.VERTICAL));
        mFileVideoModel = new FileVideoModel();
        mFileVideoModel.loadFolderInfosAsync();
        return view;

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSingleFileScanFinishEvent(SingleFolderScanFinishEvent event) {
        List<FolderInfo> scanList = mFileVideoModel.getCachedOnlyFileInfos();
        FileVideoModel.loadLatestVideosDesc();
        LogUtil.d("zjy", "获取扫描结果-单个文件夹扫描: " + (scanList == null ? -1 : scanList.size()));
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onJniScanFoundEvent(JniScanFoundEvent event) {
        final List<FolderInfo> scanList = mFileVideoModel.sortVideos2FolderInfo();
        FileVideoModel.loadLatestVideosDesc();
        FileVideoModel.uploadScantimeStatistic();
        LogUtil.d("zjy", "获取扫描结果-JNI: " + (scanList == null ? -1 : scanList.size()));


        // 存入数据库------------------------------------------------------------------------

    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onSystemMediaScanFoundEvent(SystemMediaScanFinishEvent event) {
        final List<FolderInfo> scanList = mFileVideoModel.sortVideos2FolderInfo();
        FileVideoModel.loadLatestVideosDesc();
        for(FolderInfo folderInfo : scanList){
            LogUtil.d("zjy", folderInfo.getName());
        }
        HandlerUtils.post(new Runnable() {
            @Override
            public void run() {
                mFolderList.addAll(scanList);
                mFolderAdapter.notifyDataSetChanged();
            }
        });
        LogUtil.d("zjy", "获取扫描结果-系统媒体文件: " + (scanList == null ? -1 : scanList.size()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
