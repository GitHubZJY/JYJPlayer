package com.jyj.video.jyjplayer.module.setting.about;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.TextView;

import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.utils.TypefaceUtil;
import com.zjyang.base.base.BaseActivity;
import com.zjyang.base.base.BasePresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by 74215 on 2018/12/9.
 */

public class VersionDescribeActivity extends BaseActivity{


    private ActionBar mActionBar;

    private Unbinder unbinder;

    @BindView(R.id.title_tv)
    TextView mTitleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version_descirbe);
        unbinder = ButterKnife.bind(this);
        initActionBar();
        mTitleTv.setTypeface(TypefaceUtil.getDefaultTypeface(this));
    }

    public void initActionBar() {
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setHomeButtonEnabled(true);
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setTitle(getResources().getString(R.string.version_describe));
        }
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
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
