package com.jyj.video.jyjplayer.module.download.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.module.download.AddDownLoadTasksContract;
import com.jyj.video.jyjplayer.module.download.adapter.DownLoadHistoryAdapter;
import com.jyj.video.jyjplayer.module.download.event.StartDownLoadEvent;
import com.jyj.video.jyjplayer.module.download.presenter.AddDownLoadPresenter;
import com.jyj.video.jyjplayer.module.download.widget.DownloadDialog;
import com.jyj.video.jyjplayer.utils.SuffixUtils;
import com.zjyang.base.base.BaseActivity;
import com.zjyang.base.base.SkinManager;
import com.zjyang.base.utils.DrawUtils;
import com.zjyang.base.utils.ShapeUtils;
import com.zjyang.base.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by 74215 on 2018/11/10.
 */

public class AddDownLoadActivity extends AppCompatActivity implements AddDownLoadTasksContract.View{

    public Unbinder unbinder;

    @BindView(R.id.check_down_group)
    CardView mCheckDownBtn;
    @BindView(R.id.check_down_tv)
    TextView mCheckDownTv;
    @BindView(R.id.et_download_url)
    EditText mDownLoadEt;
    @BindView(R.id.history_lv)
    RecyclerView mHistoryLv;

    ActionBar mActionBar;

    DownLoadHistoryAdapter mHistoryAdapter;
    List<String> mHistoryUrlList;

    AddDownLoadPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_download);
        mPresenter = new AddDownLoadPresenter(this);

        unbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        //mCheckDownBtn.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
        //mCheckDownTv.setBackground(ShapeUtils.getRoundRectDrawable(DrawUtils.dp2px(20), SkinManager.getInstance().getPrimaryColor()));
        mCheckDownTv.setTextColor(SkinManager.getInstance().getPrimaryTextColor());
        initActionBar();
        initHistoryLv();
    }

    public void initActionBar() {
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setHomeButtonEnabled(true);
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setTitle(getResources().getString(R.string.add_down_title));
        }
    }

    private void initHistoryLv(){
        mHistoryUrlList = new ArrayList<>();
        mHistoryAdapter = new DownLoadHistoryAdapter(this, mHistoryUrlList);
        mHistoryLv.setAdapter(mHistoryAdapter);
        mHistoryLv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mHistoryAdapter.notifyDataSetChanged();
        mPresenter.initHistory();
    }

    @Override
    public void notifyHistoryListView(List<String> urlList) {
        mHistoryUrlList.clear();
        mHistoryUrlList.addAll(urlList);
        mHistoryAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.check_down_tv)
    void clickDownload(){
        final String url = mDownLoadEt.getText().toString();
        if (TextUtils.isEmpty(url)){
            ToastUtils.showToast(this, getResources().getString(R.string.down_empty_tips));
            return;
        }
        final DownloadDialog dialog = new DownloadDialog(this, R.style.CustomDialog);
        final String newUrl = url.replaceAll("\\\\", "/");
        final String suffix = SuffixUtils.isMatchVideoFile(newUrl);
        if (!Patterns.WEB_URL.matcher(newUrl).matches() || TextUtils.isEmpty(suffix)) {
            ToastUtils.showToast(this, getResources().getString(R.string.unformat_url));
            return;
        }
//        if(!MachineUtils.isNetworkAvailable()){
//            ToastUtil.showShort(getContext(), getResources().getString(R.string.network_error));
//            return;
//        }
//        dialog.showDownloadDialog(DownloadDialog.WAIT_DOWNLOADER, "", "");
        if (newUrl.contains("youtube")) {
            dialog.showDownloadDialog(DownloadDialog.FORBIDDEN_YOUTUBE, "", "");
        }else{
            dialog.showDownloadDialog(DownloadDialog.SAVE_AS, newUrl, "."+suffix);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartDownLoadEvent(StartDownLoadEvent event){
        if(mPresenter == null){
            return;
        }
        mPresenter.addUrlToDownList(event.getUrl());
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
        if(unbinder != null){
            unbinder.unbind();
        }
        EventBus.getDefault().unregister(this);
    }
}
