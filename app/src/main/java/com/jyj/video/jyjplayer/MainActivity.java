package com.jyj.video.jyjplayer;

import android.content.Intent;
import android.content.res.Resources;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.jyj.video.jyjplayer.event.ToggleLanguageEvent;
import com.jyj.video.jyjplayer.module.download.DownLoadFragment;
import com.jyj.video.jyjplayer.module.download.view.AddDownLoadActivity;
import com.jyj.video.jyjplayer.module.home.widget.HomeBottomBar;
import com.jyj.video.jyjplayer.module.local.view.LocalFragment;
import com.jyj.video.jyjplayer.module.home.adapter.MainPagerAdapter;
import com.jyj.video.jyjplayer.module.search.SearchActivity;
import com.jyj.video.jyjplayer.module.setting.SettingActivity;
import com.jyj.video.jyjplayer.ui.CustomViewPager;
import com.zjyang.base.base.BaseActivity;
import com.zjyang.base.base.BasePresenter;
import com.zjyang.base.base.SkinManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM;

public class MainActivity extends BaseActivity implements HomeBottomBar.TabClickListener{

    private Unbinder unbinder;

    @BindView(R.id.main_pager)
    CustomViewPager mMainPager;
    @BindView(R.id.bottom_bar)
    HomeBottomBar mBottomBar;
    @BindView(R.id.add_down_btn)
    FloatingActionButton mAddDownBtn;

    private List<Fragment> mFragments;
    private LocalFragment mLocalFragment;
    private DownLoadFragment mDownLoadFragment;
    private MainPagerAdapter mPagerAdapter;
    private FragmentManager fm;

    private ImageView mSearchIv;
    private ImageView mSettingIv;

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        fm = getSupportFragmentManager();
        mFragments = new ArrayList<Fragment>();
        mLocalFragment = LocalFragment.newInstance();
        mDownLoadFragment = DownLoadFragment.newInstance();
        mFragments.add(mLocalFragment);
        mFragments.add(mDownLoadFragment);
        mPagerAdapter = new MainPagerAdapter(fm, mFragments);
        mMainPager.setAdapter(mPagerAdapter);
        mMainPager.setOffscreenPageLimit(3);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayOptions(DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
        mActionBar.setCustomView(R.layout.layout_home_actionbar);
        mSearchIv = (ImageView) mActionBar.getCustomView().findViewById(R.id.action_search);
        mSettingIv = (ImageView) mActionBar.getCustomView().findViewById(R.id.action_settings);

        mSearchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this,
                        new Pair<View, String>(mSearchIv,
                                SearchActivity.SEARCH_IV_SHARE));
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                // ActivityCompat是android支持库中用来适应不同android版本的
                ActivityCompat.startActivity(MainActivity.this, intent, activityOptions.toBundle());
            }
        });

        mSettingIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToSetting = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intentToSetting);
            }
        });

        mBottomBar.setTabClickListener(this);

        mAddDownBtn.setBackgroundColor(SkinManager.getInstance().getPrimaryColor());
    }

    @Override
    public boolean clickTab(int index) {
        mMainPager.setCurrentItem(index-1);
        return false;
    }

    @OnClick(R.id.add_down_btn)
    void clickAddBtn(){
        Intent intent = new Intent(MainActivity.this, AddDownLoadActivity.class);
        startActivity(intent);
    }

    @Subscribe
    public void onToggleLanguageEvent(ToggleLanguageEvent event){
        recreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }
}
