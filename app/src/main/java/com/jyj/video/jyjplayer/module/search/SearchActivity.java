package com.jyj.video.jyjplayer.module.search;


import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
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
import com.jyj.video.jyjplayer.module.search.model.LocalSearchController;
import com.zjyang.base.base.BaseActivity;
import com.zjyang.base.base.BasePresenter;
import com.zjyang.base.utils.ScreenUtils;

import static android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM;

/**
 * Created by 74215 on 2018/11/11.
 */

public class SearchActivity extends BaseActivity{

    public static final String SEARCH_IV_SHARE = "SEARCH_IV_SHARE";

    private ActionBar mActionBar;
    private EditText mSearchEd;
    private ImageView mClearIv;
    private ImageView mSearchIv;
    private TextView mCancelTv;

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initActionBar();

        ViewCompat.setTransitionName(mSearchIv, SEARCH_IV_SHARE);
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


        mSearchEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(TextUtils.isEmpty(charSequence)){
                    mClearIv.setVisibility(View.GONE);
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
}
