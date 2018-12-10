package com.jyj.video.jyjplayer.module.fullscreen.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jyj.video.jyjplayer.R;
import com.jyj.video.jyjplayer.event.PlaySettingCloseEvent;
import com.jyj.video.jyjplayer.subtitle.SelectSubTitlePanel;
import com.zjyang.base.utils.DrawUtils;
import com.zjyang.base.utils.ScreenUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by zhengjiayang on 2017/12/5.
 */

public class SpeedPanel extends RelativeLayout{

    private boolean isExit;
    private boolean mIsExitAnimEnd = true;
    private Context mContext;
    private View mPanel;
    private ImageView mCloseIv;

    //竖屏时菜单面板的高度
    private int MENU_PANEL_HEIGHT = 416;


    public SpeedPanel(Context context) {
        this(context, null);
    }

    public SpeedPanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpeedPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void initSpeedPanel(Context context, int orientation) {
        mPanel = LayoutInflater.from(context).inflate(R.layout.layout_speed_panel, null);
        mCloseIv = (ImageView) mPanel.findViewById(R.id.close_dialog_btn);
        //mMenuPanel.setMenuItemClickListener(this);
        addView(mPanel);
        LayoutParams menuParams = (LayoutParams)mPanel.getLayoutParams();
        menuParams.width = ScreenUtils.getsScreenWidth() / 2;
        menuParams.height = ScreenUtils.getsScreenWidth();
        menuParams.addRule(ALIGN_PARENT_RIGHT);
        mPanel.setLayoutParams(menuParams);
        mPanel.setBackgroundColor(Color.parseColor("#e610101c"));
        mPanel.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //拦截视频页面触摸，点击设置项的区域都不可触发视频页面触摸逻辑
                return true;
            }
        });
        mCloseIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaySettingCloseEvent event = new PlaySettingCloseEvent();
                event.setPanelType(PlaySettingCloseEvent.SPEED_PANEL);
                EventBus.getDefault().post(event);
            }
        });
        resetAllChildWidth(orientation);
    }

    public void resetAllChildWidth(int orientation){

        for(int i=0; i < getChildCount(); i++){
            View view = getChildAt(i);
            LayoutParams childParams = (LayoutParams) view.getLayoutParams();
            childParams.width = ScreenUtils.getsScreenWidth() / 2;
            childParams.height = ScreenUtils.getsScreenWidth();
            view.setLayoutParams(childParams);
        }


    }

    public void changeOrientation(int orientation){
        if(mPanel == null){
            return;
        }
        LayoutParams menuParams = (LayoutParams)mPanel.getLayoutParams();
        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            menuParams.width = ScreenUtils.getsScreenWidth();
            menuParams.height = ScreenUtils.getsScreenWidth();
            menuParams.addRule(ALIGN_PARENT_RIGHT);
        }else{
            menuParams.width = ScreenUtils.getsScreenWidth();
            menuParams.height = DrawUtils.dp2px(MENU_PANEL_HEIGHT);
            menuParams.addRule(ALIGN_PARENT_BOTTOM);
        }
        mPanel.setLayoutParams(menuParams);

        resetAllChildWidth(orientation);
    }


    public void startEnterAnimation() {
        clearAnimation();
        isExit = false;
        Animation animation;
        animation = AnimationUtils.loadAnimation(mContext, R.anim.playing_menu_open_landscape);   //得到一个LayoutAnimationController对象；
        LayoutAnimationController controller = new LayoutAnimationController(animation);   //设置控件显示的顺序；
        controller.setOrder(LayoutAnimationController.ORDER_REVERSE);   //设置控件显示间隔时间；
        controller.setDelay(0.1f);   //为ListView设置LayoutAnimationController属性；
        setLayoutAnimation(controller);
        startLayoutAnimation();
    }

    public void startExitAnimation() {
        if(!mIsExitAnimEnd){
            return;
        }
        clearAnimation();
        isExit = true;
        mIsExitAnimEnd = false;
        Animation animation;   //得到一个LayoutAnimationController对象；
        animation= AnimationUtils.loadAnimation(mContext, R.anim.playing_menu_close_landscape);   //得到一个LayoutAnimationController对象；
        LayoutAnimationController controller = new LayoutAnimationController(animation);   //设置控件显示的顺序；
        controller.setOrder(LayoutAnimationController.ORDER_REVERSE);   //设置控件显示间隔时间；
        controller.setDelay(0.1f);   //为ListView设置LayoutAnimationController属性；
        setLayoutAnimation(controller);
        startLayoutAnimation();
        setLayoutAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIsExitAnimEnd = true;
                if(isExit){
                    setVisibility(GONE);
                    clearAllViews();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void clearAllViews(){
        removeAllViews();
    }
}
