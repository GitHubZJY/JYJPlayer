package com.jyj.video.jyjplayer.module.search;


import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.filescan.model.bean.VideoInfo;
import com.jyj.video.jyjplayer.module.folderdetail.VideoListAdapter;
import com.jyj.video.jyjplayer.module.search.model.LocalSearchController;
import com.jyj.video.jyjplayer.module.search.model.SearchObserver;
import com.jyj.video.jyjplayer.ui.EmptyTipView;
import com.jyj.video.jyjplayer.utils.AndroidDevice;
import com.zjyang.base.base.BaseActivity;
import com.zjyang.base.base.BasePresenter;
import com.zjyang.base.base.SkinManager;
import com.zjyang.base.utils.ScreenUtils;
import com.zjyang.base.utils.ShapeUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM;

/**
 * Created by 74215 on 2018/11/11.
 */

public class SearchActivity extends BaseActivity implements SearchObserver{

    public static final String SEARCH_IV_SHARE = "SEARCH_IV_SHARE";

    private Unbinder unbinder;

    @BindView(R.id.search_result_lv)
    RecyclerView mSearchResultLv;
    @BindView(R.id.empty_view)
    EmptyTipView mSearchEmptyView;

    ImageView mEmptyIv;

    private ActionBar mActionBar;
    private EditText mSearchEd;
    private ImageView mClearIv;
    private ImageView mSearchIv;
    private TextView mCancelTv;


    private VideoListAdapter mSearchListAdapter;
    private LinearLayoutManager mDownLayoutManager;
    private List<VideoInfo> mSearchList;

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        unbinder = ButterKnife.bind(this);
        initActionBar();

        ViewCompat.setTransitionName(mSearchIv, SEARCH_IV_SHARE);
        LocalSearchController.register(this);
    }

    public void initActionBar(){

        mActionBar = getSupportActionBar();
        mActionBar.setTitle("");
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayOptions(DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
        mActionBar.setCustomView(R.layout.layout_search_actionbar);
        mSearchEd = (EditText) mActionBar.getCustomView().findViewById(R.id.search_edit);
        mClearIv = (ImageView) mActionBar.getCustomView().findViewById(R.id.search_clear);
        mSearchIv = (ImageView) mActionBar.getCustomView().findViewById(R.id.search_iv);
        mCancelTv = (TextView) mActionBar.getCustomView().findViewById(R.id.cancel_search_tv);

        mSearchList = new ArrayList<>();
        mSearchResultLv.setNestedScrollingEnabled(false);
        mDownLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mSearchResultLv.setLayoutManager(mDownLayoutManager);
        mSearchListAdapter = new VideoListAdapter(this, mSearchList);
        mSearchResultLv.setAdapter(mSearchListAdapter);

        mSearchEmptyView.setReloadEnable(false);
        mSearchEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(TextUtils.isEmpty(charSequence)){
                    mClearIv.setVisibility(View.GONE);
                    mSearchEmptyView.setVisibility(View.GONE);
                }else{
                    mClearIv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mSearchEd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    AndroidDevice.hideSoftInput(SearchActivity.this);
                    LocalSearchController.getInstance().searchVideos(mSearchEd.getText().toString());
                    return true;
                }
                return false;
            }
        });

        mClearIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchEd.setText("");
            }
        });

        mCancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onSearchFinish(String searchKey, List<?> list, int resultCount) {
        if(list == null || list.size() == 0){
            mSearchResultLv.setVisibility(View.GONE);
            mSearchEmptyView.setVisibility(View.VISIBLE);
            return;
        }
        mSearchResultLv.setVisibility(View.VISIBLE);
        mSearchEmptyView.setVisibility(View.GONE);
        mSearchList.clear();
        mSearchList.addAll((List<VideoInfo>)list);
        mSearchListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSearchFailed(String searchKey, int errorCode) {
        mSearchResultLv.setVisibility(View.GONE);
        mSearchEmptyView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalSearchController.unregister(this);
        if(unbinder != null){
            unbinder.unbind();
        }
    }
}
