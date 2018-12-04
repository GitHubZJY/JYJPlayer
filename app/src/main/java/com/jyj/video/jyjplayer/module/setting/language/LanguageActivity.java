package com.jyj.video.jyjplayer.module.setting.language;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.event.ToggleLanguageEvent;
import com.jyj.video.jyjplayer.manager.SpManager;
import com.jyj.video.jyjplayer.module.setting.language.model.LanguageBean;
import com.jyj.video.jyjplayer.ui.StatusLoadingDialog;
import com.jyj.video.jyjplayer.utils.LanguageUtils;
import com.jyj.video.jyjplayer.utils.MailService;
import com.zjyang.base.base.BaseActivity;
import com.zjyang.base.base.BasePresenter;
import com.zjyang.base.utils.HandlerUtils;
import com.zjyang.base.utils.ToastUtils;

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

public class LanguageActivity extends BaseActivity {

    private Unbinder unbinder;

    private ActionBar mActionBar;

    @BindView(R.id.language_list)
    RecyclerView mLanguageLv;
    @BindView(R.id.auto_select_iv)
    ImageView mAutoSelectIv;
    @BindView(R.id.auto_select_group)
    RelativeLayout mAutoSelectItem;

    LanguageAdapter mAdapter;

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        unbinder = ButterKnife.bind(this);
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        initActionBar();

        List<LanguageBean> languageBeanList = new ArrayList<>();
        languageBeanList.add(new LanguageBean(Locale.CHINA));
        languageBeanList.add(new LanguageBean(Locale.ENGLISH));
        mAdapter = new LanguageAdapter(this, languageBeanList);
        mLanguageLv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mLanguageLv.setAdapter(mAdapter);

        LanguageBean curLanguage = SpManager.getInstance().getCurLanguage();
        if(curLanguage == null){
            mAutoSelectIv.setVisibility(View.VISIBLE);
        }else{
            mAutoSelectIv.setVisibility(View.GONE);
        }
    }

    public void initActionBar() {
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setHomeButtonEnabled(true);
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setTitle(getResources().getString(R.string.switch_language));
        }
    }

    @OnClick(R.id.auto_select_group)
    void clickAutoSelect(){
        SpManager.getInstance().setCurLanguage(new LanguageBean(Locale.getDefault()));
        LanguageUtils.updateLanguage(Locale.getDefault());
        mAdapter.notifyDataSetChanged();
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

    @Subscribe
    public void onToggleLanguageEvent(ToggleLanguageEvent event){
        recreate();
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
