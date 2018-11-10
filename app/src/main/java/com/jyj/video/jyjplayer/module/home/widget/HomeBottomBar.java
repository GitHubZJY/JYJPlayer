package com.jyj.video.jyjplayer.module.home.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.module.home.model.HomeModel;
import com.zjyang.base.base.SkinManager;
import com.zjyang.base.utils.DrawUtils;
import com.zjyang.base.utils.ShapeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by 74215 on 2018/5/13.
 * 封裝首页底部tab,以便于动态更改顺序位置（更改位置在HomeModel类中进行设置）
 */

public class HomeBottomBar extends LinearLayout{

    private int mScreenWidth;
    private List<LinearLayout> mTabItemList = new ArrayList<>();
    private LinearLayout mLocalTab;
    private LinearLayout mDownLoadTab;
    private ImageView mLocalIv;
    private ImageView mDownLoadIv;
    private TextView mLocalTv;
    private TextView mDownloadTv;

    private Drawable mDiscoverNormal = getResources().getDrawable(R.drawable.bottom_bar_local_normal);
    private Drawable mFocusNormal = getResources().getDrawable(R.drawable.bottom_bar_download_normal);


    private Drawable mDiscoverActive = ShapeUtils.drawColor(getResources().getDrawable(R.drawable.bottom_bar_local_normal), SkinManager.getInstance().getPrimaryColor());
    private Drawable mFocusActive = ShapeUtils.drawColor(getResources().getDrawable(R.drawable.bottom_bar_download_normal), SkinManager.getInstance().getPrimaryColor());

    public HomeBottomBar(Context context) {
        this(context, null);
    }

    public HomeBottomBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HomeBottomBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScreenWidth = getScreenWidth(context);
        initView(context);
    }

    private void initView(Context context){
        setOrientation(HORIZONTAL);
        setBackgroundColor(getResources().getColor(R.color.white));

        mLocalTab = new LinearLayout(context);
        mDownLoadTab = new LinearLayout(context);
        mLocalIv = new ImageView(context);
        mDownLoadIv = new ImageView(context);
        mLocalTv = new TextView(context);
        mDownloadTv = new TextView(context);

        mTabItemList.add(mLocalTab);
        mTabItemList.add(mDownLoadTab);


        LayoutParams mItemIvParams = new LayoutParams(DrawUtils.dp2px(24), DrawUtils.dp2px(24));
        mItemIvParams.setMargins(0,0,0, DrawUtils.dp2px(3));

        mLocalIv.setImageDrawable(mDiscoverNormal);
        //mLocalIv.setBackgroundResource(R.drawable.bottom_bar_local_normal);
        mLocalIv.setLayoutParams(mItemIvParams);
        mDownLoadIv.setImageDrawable(mFocusNormal);
        //mDownLoadIv.setBackgroundResource(R.drawable.bottom_bar_download_normal);
        mDownLoadIv.setLayoutParams(mItemIvParams);
        //mMessageIv.setBackgroundResource(R.drawable.bottom_bar_circle_normal);
        //mMeIv.setBackgroundResource(R.drawable.bottom_bar_me_normal);
        mLocalTab.addView(mLocalIv);
        mDownLoadTab.addView(mDownLoadIv);

        initTabItemTv(mLocalTv);
        initTabItemTv(mDownloadTv);
        mLocalTv.setText(getResources().getString(R.string.local_tab_name));
        mDownloadTv.setText(getResources().getString(R.string.download_tab_name));
        mLocalTab.addView(mLocalTv);
        mDownLoadTab.addView(mDownloadTv);

        initTabItem(mLocalTab);
        initTabItem(mDownLoadTab);

        sortTab();

        initOnClick();
    }

    private void initTabItemTv(TextView itemTv){
        itemTv.setTextColor(Color.parseColor("#8a8a8a"));
        itemTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        LayoutParams mItemTvParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemTv.setLayoutParams(mItemTvParams);
    }


    private void initTabItem(LinearLayout tabItem){
        tabItem.setOrientation(VERTICAL);
        tabItem.setGravity(Gravity.CENTER);
        LayoutParams mDiscoverParams = new LayoutParams(mScreenWidth / 2, ViewGroup.LayoutParams.MATCH_PARENT);
        tabItem.setLayoutParams(mDiscoverParams);
    }

    public void sortTab(){
        removeAllViews();
        mLocalTab.setTag(HomeModel.DISCOVER_TAB_SORT);
        mDownLoadTab.setTag(HomeModel.FOCUS_TAB_SORT);
        Collections.sort(mTabItemList, new TabItemComparator());
        for(int i=0; i < mTabItemList.size(); i++){
            if(i == 0){
                Drawable mFirstActiveDrawable;
                if(mTabItemList.get(i) == mLocalTab){
                    mFirstActiveDrawable = mDiscoverActive;
                }else{
                    mFirstActiveDrawable = mFocusActive;
                }
                ((TextView)mTabItemList.get(i).getChildAt(1)).setTextColor(SkinManager.getInstance().getPrimaryColor());
                ((ImageView)mTabItemList.get(i).getChildAt(0)).setImageDrawable(mFirstActiveDrawable);
            }
            addView(mTabItemList.get(i));
        }
    }

    private void initOnClick(){
        mLocalTab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mLocalIv.setImageDrawable(mDiscoverActive);
                mDownLoadIv.setImageDrawable(mFocusNormal);
                mLocalTv.setTextColor(SkinManager.getInstance().getPrimaryColor());
                mDownloadTv.setTextColor(Color.parseColor("#8a8a8a"));
                if(mTabClickListener != null){
                    mTabClickListener.clickTab((Integer) mLocalTab.getTag());
                }
            }
        });
        mDownLoadTab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mLocalIv.setImageDrawable(mDiscoverNormal);
                mDownLoadIv.setImageDrawable(mFocusActive);
                mLocalTv.setTextColor(Color.parseColor("#8a8a8a"));
                mDownloadTv.setTextColor(SkinManager.getInstance().getPrimaryColor());
                if(mTabClickListener != null){
                    mTabClickListener.clickTab((Integer) mDownLoadTab.getTag());
                }
            }
        });
    }

    class TabItemComparator implements Comparator<LinearLayout> {


        @Override
        public int compare(LinearLayout tab1, LinearLayout tab2) {
            if((Integer)tab1.getTag() > (Integer)tab2.getTag()){
                return 1;
            }else if((Integer)tab1.getTag() < (Integer)tab2.getTag()){
                return -1;
            }
            return 0;
        }


    }

    private TabClickListener mTabClickListener;

    public void setTabClickListener(TabClickListener mTabClickListener) {
        this.mTabClickListener = mTabClickListener;
    }

    public interface TabClickListener {
        boolean clickTab(int index);
    }


    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return width of the screen.
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }
}
