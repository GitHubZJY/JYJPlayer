package com.jyj.video.jyjplayer.subtitle;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.constant.SpConstant;
import com.jyj.video.jyjplayer.event.PlaySettingCloseEvent;
import com.jyj.video.jyjplayer.event.SubtitleSwitchEvent;
import com.jyj.video.jyjplayer.filescan.model.bean.VideoInfo;
import com.jyj.video.jyjplayer.manager.VideoPlayDataManager;
import com.jyj.video.jyjplayer.subtitle.bean.SubTitleFileInfo;
import com.jyj.video.jyjplayer.ui.SwitchCheck;
import com.jyj.video.jyjplayer.utils.TypefaceUtil;
import com.zjyang.base.utils.DrawUtils;
import com.zjyang.base.utils.HandlerUtils;
import com.zjyang.base.utils.SpUtils;
import com.zjyang.base.utils.ToastUtils;


import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by zhengjiayang on 2017/12/5.
 */

public class SelectSubTitlePanel extends RelativeLayout {

    private TextView mLocalTitleTv;
    private TextView mOnlineTitleTv;
    private int mLocalTitleWidth = 0;
    private int mOnlineTitleWidth = 0;
    private View mCursorIv;
    private SwitchCheck mSubtitleSwitch;
    private ImageView mCloseIv;

    private UnScrollViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    //已有字幕列表页面
    private LocalSubListPage mLocalSubListPage;
    //添加本地字幕页面
    private AddLocalSubPage mLocalSubAddPage;
    //字幕页面集合
    private List<View> mPageList;

    //在线字幕搜索页面
    private OnLineSearchPager mOnLineSearch;
    //在线字幕搜索中页面
    private SearchingPage mSearchingPage;
    //在线字幕搜索结果页面
    private SearchResultPage mSearchResultPage;
    //在线字幕下载中页面
    private DownLoadingPage mDownLoadingPage;
    //在线字幕下载结果页面
    private DownFinishPage mDownFinishPage;

    public SelectSubTitlePanel(Context context) {
        this(context, null);
    }

    public SelectSubTitlePanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectSubTitlePanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    private void initView(final Context context){
        View rootView = LayoutInflater.from(context).inflate(R.layout.layout_select_subtitle, null);
        mLocalTitleTv = rootView.findViewById(R.id.local_title_tv);
        mOnlineTitleTv = rootView.findViewById(R.id.online_title_tv);
        mCursorIv = rootView.findViewById(R.id.title_cursor);
        mSubtitleSwitch = rootView.findViewById(R.id.subtitle_switch);
        mCloseIv = rootView.findViewById(R.id.close_dialog_btn);
        mViewPager = rootView.findViewById(R.id.local_viewpager);
        initLocalView(context);
        initOnLineView(context);
        initCursor();

        mLocalTitleTv.setTypeface(TypefaceUtil.getDefaultTypeface(context));
        mOnlineTitleTv.setTypeface(TypefaceUtil.getDefaultTypeface(context));
        mLocalTitleTv.setTextColor(Color.parseColor("#43cfff"));
        mOnlineTitleTv.setTextColor(Color.parseColor("#ffffff"));

        mLocalTitleTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(0);
                setCursorPosition(0);
                mLocalTitleTv.setTextColor(Color.parseColor("#43cfff"));
                mOnlineTitleTv.setTextColor(Color.parseColor("#ffffff"));
                mPageList.clear();
                mViewPager.removeAllViews();
                mPageList.add(mLocalSubListPage);
                mPageList.add(mLocalSubAddPage);
                mPagerAdapter.notifyDataSetChanged();
            }
        });

        mOnlineTitleTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(0);
                setCursorPosition(1);
                mOnlineTitleTv.setTextColor(Color.parseColor("#43cfff"));
                mLocalTitleTv.setTextColor(Color.parseColor("#ffffff"));
                mPageList.clear();
                mViewPager.removeAllViews();
                mPageList.add(mOnLineSearch);
                mPageList.add(mSearchingPage);
                mPageList.add(mSearchResultPage);
                mPageList.add(mDownLoadingPage);
                mPageList.add(mDownFinishPage);
                mPagerAdapter.notifyDataSetChanged();
            }
        });


        boolean isOpen = SpUtils.obtain(SpConstant.DEFAULT_SP_FILE).getBoolean(SpConstant.IS_OPEN_SUBTITLE, true);
        mSubtitleSwitch.setCheck(isOpen);

        mSubtitleSwitch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mSubtitleSwitch.setCheck(!mSubtitleSwitch.isCheck());
                SpUtils.obtain(SpConstant.DEFAULT_SP_FILE).save(SpConstant.IS_OPEN_SUBTITLE, mSubtitleSwitch.isCheck());
                EventBus.getDefault().post(new SubtitleSwitchEvent());
            }
        });

        mCloseIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new PlaySettingCloseEvent());
            }
        });

        addView(rootView);
    }

    public void initLocalData(String videoUrl){
        if(mLocalSubAddPage != null){
            mLocalSubAddPage.initFolderLv(videoUrl);
        }
        if(mLocalSubListPage != null){
            mLocalSubListPage.initSubData(videoUrl);
        }
    }

    public void initCursor(){
        mLocalTitleTv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
            @Override
            public void onGlobalLayout() {
                mLocalTitleTv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                float tvWidth = mLocalTitleTv.getWidth() - mLocalTitleTv.getPaddingLeft() - mLocalTitleTv.getPaddingRight(); //控件可用宽度
                mLocalTitleWidth = (int)tvWidth;
                LayoutParams params = (LayoutParams)mCursorIv.getLayoutParams();
                params.setMargins(mLocalTitleWidth/2 + DrawUtils.dp2px(8), 0, 0, 0);
                mCursorIv.setLayoutParams(params);
            }
        });
        mOnlineTitleTv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
            @Override
            public void onGlobalLayout() {
                mOnlineTitleTv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                float tvWidth = mOnlineTitleTv.getWidth() - mOnlineTitleTv.getPaddingLeft() - mOnlineTitleTv.getPaddingRight(); //控件可用宽度
                mOnlineTitleWidth = (int)tvWidth;
            }
        });
    }

    public void setCursorPosition(int position){
        switch (position){
            case 0:
                LayoutParams params1 = (LayoutParams)mCursorIv.getLayoutParams();
                params1.setMargins(mLocalTitleWidth/2 + DrawUtils.dp2px(8), 0, 0, 0);
                mCursorIv.setLayoutParams(params1);
                break;
            case 1:
                LayoutParams params2 = (LayoutParams)mCursorIv.getLayoutParams();
                params2.setMargins(mLocalTitleWidth + mOnlineTitleWidth/2 + DrawUtils.dp2px(48), 0, 0, 0);
                mCursorIv.setLayoutParams(params2);
                break;
        }
    }


    public void initLocalView(Context context){
        initViewPager(mViewPager);
        mLocalSubListPage = new LocalSubListPage(context);
        mLocalSubAddPage = new AddLocalSubPage(context);
        mPageList = new ArrayList<>();
        mPageList.add(mLocalSubListPage);
        mPageList.add(mLocalSubAddPage);
        mLocalSubListPage.setPageClickListener(new LocalSubListPage.PageClickListener() {
            @Override
            public void clickAddSubTitle() {
                mViewPager.setCurrentItem(1);
            }
        });
        mLocalSubAddPage.setPageClickListener(new AddLocalSubPage.PageClickListener() {
            @Override
            public void cancelAdd() {
                mViewPager.setCurrentItem(0);
            }
        });

        mPagerAdapter = new PagerAdapter() {

            private int mChildCount = 0;
            @Override
            public int getCount() {
                return mPageList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(mPageList.get(position));
                return mPageList.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mPageList.get(position));
            }

            @Override public void notifyDataSetChanged() {
                mChildCount = getCount();
                super.notifyDataSetChanged();
            }

            @Override public int getItemPosition(Object object) {
                if (mChildCount > 0) {
                    mChildCount--;
                    return POSITION_NONE;
                }
                return super.getItemPosition(object);
            }
        };
        mViewPager.setAdapter(mPagerAdapter);
    }

    public void initOnLineView(final Context context){
        mOnLineSearch = new OnLineSearchPager(context);
        mSearchingPage = new SearchingPage(context);
        mSearchResultPage = new SearchResultPage(context);
        mDownLoadingPage = new DownLoadingPage(context);
        mDownFinishPage = new DownFinishPage(context);

        VideoInfo videoInfo = VideoPlayDataManager.getInstance().getCurPlayVideoInfo();
        if(videoInfo != null){
            mOnLineSearch.setDefaultName(videoInfo.getName());
        }

        mOnLineSearch.setPageClickListener(new OnLineSearchPager.PageClickListener() {
            @Override
            public void clickSearch() {
                mSearchingPage.setVisibility(VISIBLE);
                mViewPager.setCurrentItem(1);
            }

            @Override
            public void searchFinish(boolean isSuccess, final List<SubTitleFileInfo> result) {
                if(isSuccess){
                    HandlerUtils.post(new Runnable() {
                        @Override
                        public void run() {
                            mViewPager.setCurrentItem(2);
                            mSearchResultPage.setResultData(result);
                        }
                    });
                }else{
                    HandlerUtils.post(new Runnable() {
                        @Override
                        public void run() {
                            mViewPager.setCurrentItem(0);
                            ToastUtils.showToast(context, getResources().getString(R.string.network_connection_error));
                        }
                    });
                }
            }
        });


        mSearchResultPage.setPageClickListener(new SearchResultPage.PageClickListener() {
            @Override
            public void clickCancel() {
                mSearchResultPage.scrollToTop();
                mSearchingPage.setVisibility(GONE);
                mViewPager.setCurrentItem(0);
            }

            @Override
            public void clickDownload() {
                mViewPager.setCurrentItem(3);
            }

            @Override
            public void downFinish(final boolean isSuccess, final String filePath) {
                HandlerUtils.post(new Runnable() {
                    @Override
                    public void run() {
                        mDownFinishPage.setPageStatus(isSuccess);
                        mDownFinishPage.setDownLoadFile(filePath);
                        mViewPager.setCurrentItem(4);
                    }
                });

            }
        });

        mDownLoadingPage.setPageClickListener(new DownLoadingPage.PageClickListener() {
            @Override
            public void cancelDownLoad() {
                mViewPager.setCurrentItem(2);
            }
        });

        mDownFinishPage.setPageClickListener(new DownFinishPage.PageClickListener() {
            @Override
            public void clickBack() {
                mViewPager.setCurrentItem(2);
            }

            @Override
            public void clickConfirm() {
            }
        });
    }

    public void initViewPager(UnScrollViewPager viewPager){
        viewPager.setPagingEnabled(false);
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(viewPager.getContext(),
                    new LinearInterpolator());
            field.set(viewPager, scroller);
            scroller.setmDuration(500);
        } catch (Exception e) {
        }
    }

}