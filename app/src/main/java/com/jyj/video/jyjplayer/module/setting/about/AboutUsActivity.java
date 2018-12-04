package com.jyj.video.jyjplayer.module.setting.about;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jyj.video.jyjplayer.AppApplication;
import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.event.ToggleLanguageEvent;
import com.jyj.video.jyjplayer.manager.SpManager;
import com.jyj.video.jyjplayer.module.setting.language.LanguageAdapter;
import com.jyj.video.jyjplayer.module.setting.language.model.LanguageBean;
import com.jyj.video.jyjplayer.utils.LanguageUtils;
import com.jyj.video.jyjplayer.utils.TypefaceUtil;
import com.zjyang.base.base.BaseActivity;
import com.zjyang.base.base.BasePresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by zhengjiayang on 2018/11/26.
 */

public class AboutUsActivity extends BaseActivity {

    private Unbinder unbinder;

    private ActionBar mActionBar;

    @BindView(R.id.version_code_tv)
    TextView mVcTv;
    @BindView(R.id.share_tv)
    TextView mShareTv;

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        unbinder = ButterKnife.bind(this);
        initActionBar();
        mVcTv.setTypeface(TypefaceUtil.getDefaultTypeface(this));
        mVcTv.setText(getResources().getString(R.string.version_code) + " v" + AppApplication.getVersionName());
        mShareTv.setText(getResources().getString(R.string.share) + getResources().getString(R.string.app_name));
    }

    public void initActionBar() {
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setHomeButtonEnabled(true);
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setTitle(getResources().getString(R.string.about_me));
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
    protected void onDestroy() {
        super.onDestroy();
        if(unbinder != null){
            unbinder.unbind();
        }
    }

}
