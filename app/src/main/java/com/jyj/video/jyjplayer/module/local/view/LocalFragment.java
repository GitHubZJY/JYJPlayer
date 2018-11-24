package com.jyj.video.jyjplayer.module.local.view;

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
import com.jyj.video.jyjplayer.filescan.model.bean.FolderInfo;
import com.jyj.video.jyjplayer.module.local.LocalTasksContract;
import com.jyj.video.jyjplayer.module.local.adapter.FolderListAdapter;
import com.jyj.video.jyjplayer.module.local.presenter.LocalPresenter;
import com.jyj.video.jyjplayer.ui.EmptyTipView;
import com.zjyang.base.utils.DrawUtils;
import com.zjyang.base.utils.HandlerUtils;
import com.zjyang.base.utils.ShapeUtils;
import com.zjyang.base.widget.RefreshLoadRecyclerView;
import com.zjyang.base.widget.SpaceItemDecoration;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 74215 on 2018/11/3.
 */

public class LocalFragment extends Fragment implements LocalTasksContract.View, RefreshLoadRecyclerView.RefreshLoadListener, EmptyTipView.ClickEmptyListener {

    private EmptyTipView mEmptyTipView;
    private RefreshLoadRecyclerView mFolderLv;
    private RelativeLayout mSearchEntrance;
    private List<FolderInfo> mFolderList;
    private FolderListAdapter mFolderAdapter;
    LocalPresenter mPresenter;

    public static LocalFragment newInstance() {
        Bundle args = new Bundle();
        LocalFragment fragment = new LocalFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = new LocalPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local, container, false);
        mEmptyTipView = (EmptyTipView) view.findViewById(R.id.empty_view);
        mEmptyTipView.setClickEmptyListener(this);
        mSearchEntrance = (RelativeLayout) view.findViewById(R.id.search_entrance);
        mSearchEntrance.setBackground(ShapeUtils.getRoundRectDrawable(DrawUtils.dp2px(20), Color.WHITE));
        mFolderLv = (RefreshLoadRecyclerView) view.findViewById(R.id.folder_lv);
        mFolderLv.setOnRefreshLoadListener(this);
        mFolderList = new ArrayList<>();
        mFolderAdapter = new FolderListAdapter(getContext(), mFolderList);
        mFolderLv.setAdapter(mFolderAdapter);
        mFolderLv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mFolderLv.addItemDecoration(new SpaceItemDecoration(DrawUtils.dp2px(2), 1, LinearLayoutManager.VERTICAL));
        mPresenter.scanSystemFolderData();
        return view;

    }

    @Override
    public void onRefresh() {
        mPresenter.scanSystemFolderData();
    }

    @Override
    public void onLoadMore(boolean isSilence) {

    }

    @Override
    public void onRelease(float direction) {

    }

    @Override
    public void clickRefresh() {
        if(mPresenter != null){
            mPresenter.scanSystemFolderData();
        }
    }

    @Override
    public void showEmptyView() {
        mEmptyTipView.setVisibility(View.VISIBLE);
        mFolderLv.setVisibility(View.GONE);
    }

    @Override
    public void hideEmptyView() {
        mEmptyTipView.setVisibility(View.GONE);
        mFolderLv.setVisibility(View.VISIBLE);
    }

    @Override
    public void notifyFolderListView(final List<FolderInfo> scanList) {
        HandlerUtils.post(new Runnable() {
            @Override
            public void run() {
                if(mFolderLv != null){
                    mFolderLv.stopRefresh();
                }
                mFolderList.clear();
                mFolderList.addAll(scanList);
                mFolderAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mPresenter != null){
            mPresenter.destroy();
        }
    }
}
