package com.jyj.video.jyjplayer.module.folderdetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;

import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.filescan.model.FileVideoModel;
import com.jyj.video.jyjplayer.filescan.model.bean.FolderInfo;
import com.jyj.video.jyjplayer.filescan.model.bean.VideoInfo;
import com.zjyang.base.base.BaseActivity;
import com.zjyang.base.base.BasePresenter;
import com.zjyang.base.utils.DrawUtils;
import com.zjyang.base.widget.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by 74215 on 2018/11/4.
 */

public class FolderDetailActivity extends BaseActivity {

    private Unbinder unbinder;
    public static final String FILE_BEAN_KEY = "FILE_BEAN_KEY";

    @BindView(R.id.video_lv)
    RecyclerView mVideoLv;

    private ActionBar mActionBar;

    private List<VideoInfo> mVideoList;
    private VideoListAdapter mVideoListAdapter;

    private FolderInfo mFolderInfo;

    public static void start(Context context, String folderKey) {
        Intent intent = new Intent(context, FolderDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(FILE_BEAN_KEY, folderKey);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_detail);
        unbinder = ButterKnife.bind(this);

        initActionBar();
        initListView();
        getFileInfo();
    }

    public void initActionBar() {
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setHomeButtonEnabled(true);
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initListView() {
        mVideoList = new ArrayList<>();
        mVideoListAdapter = new VideoListAdapter(this, mVideoList);
        mVideoLv.setAdapter(mVideoListAdapter);
        mVideoLv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mVideoLv.addItemDecoration(new SpaceItemDecoration(DrawUtils.dp2px(2), 1, LinearLayoutManager.VERTICAL));
    }

    private void getFileInfo() {
        String path = getIntent().getStringExtra(FILE_BEAN_KEY);

        mFolderInfo = FileVideoModel.getFileInfo(path);
        mVideoList.clear();
        if (mFolderInfo != null) {
            List<VideoInfo> videoInfos = mFolderInfo.getVideoList();
            if (videoInfos != null && videoInfos.size() > 0) {
                mVideoList.addAll(videoInfos);
            }
            String folderName = mFolderInfo.getName();
            mActionBar.setTitle(TextUtils.isEmpty(folderName) ? "" : folderName);
        }
        mVideoListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}