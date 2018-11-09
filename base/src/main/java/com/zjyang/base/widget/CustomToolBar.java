package com.zjyang.base.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zjyang.base.R;
import com.zjyang.base.base.SkinManager;
import com.zjyang.base.utils.ShapeUtils;
import com.zjyang.base.utils.ScreenUtils;

/**
 * Created by IT_ZJYANG on 2016/9/9.
 * 标题栏
 */
public class CustomToolBar extends LinearLayout implements View.OnClickListener{

    private Boolean isLeftBtnVisible;
    private int leftResId;

    private Boolean isLeftTvVisible;
    private String leftTvText;

    private Boolean isRightBtnVisible;
    private int rightResId;

    private Boolean isRightTvVisible;
    private String rightTvText;

    private Boolean isTitleVisible;
    private String titleText;

    private int backgroundResId;

    private Boolean isShaderVisible;

    public CustomToolBar(Context context) {
        this(context, null);
    }

    public CustomToolBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomToolBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    /**
     * 初始化属性
     * @param attrs
     */
    public void initView(AttributeSet attrs){

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CustomToolBar);
        /**-------------获取左边按钮属性------------*/
        isLeftBtnVisible = typedArray.getBoolean(R.styleable.CustomToolBar_left_btn_visible, false);
        leftResId = typedArray.getResourceId(R.styleable.CustomToolBar_left_btn_src, -1);
        /**-------------获取左边文本属性------------*/
        isLeftTvVisible = typedArray.getBoolean(R.styleable.CustomToolBar_left_tv_visible, false);
        if(typedArray.hasValue(R.styleable.CustomToolBar_left_tv_text)){
            leftTvText = typedArray.getString(R.styleable.CustomToolBar_left_tv_text);
        }
        /**-------------获取右边按钮属性------------*/
        isRightBtnVisible = typedArray.getBoolean(R.styleable.CustomToolBar_right_btn_visible, false);
        rightResId = typedArray.getResourceId(R.styleable.CustomToolBar_right_btn_src, -1);
        /**-------------获取右边文本属性------------*/
        isRightTvVisible = typedArray.getBoolean(R.styleable.CustomToolBar_right_tv_visible, false);
        if(typedArray.hasValue(R.styleable.CustomToolBar_right_tv_text)){
            rightTvText = typedArray.getString(R.styleable.CustomToolBar_right_tv_text);
        }
        /**-------------获取标题属性------------*/
        isTitleVisible = typedArray.getBoolean(R.styleable.CustomToolBar_title_visible, false);
        if(typedArray.hasValue(R.styleable.CustomToolBar_title_text)){
            titleText = typedArray.getString(R.styleable.CustomToolBar_title_text);
        }
        /**-------------背景颜色------------*/
        backgroundResId = typedArray.getResourceId(R.styleable.CustomToolBar_barBackground, -1);
        /**-------------是否显示阴影------------*/
        isShaderVisible = typedArray.getBoolean(R.styleable.CustomToolBar_shader_visible, false);

        typedArray.recycle();

        /**-------------设置内容------------*/
        View barLayoutView = View.inflate(getContext(), R.layout.layout_common_toolbar, null);
        Button leftBtn = (Button)barLayoutView.findViewById(R.id.toolbar_left_btn);
        TextView leftTv = (TextView)barLayoutView.findViewById(R.id.toolbar_left_tv);
        TextView titleTv = (TextView)barLayoutView.findViewById(R.id.toolbar_title_tv);
        Button rightBtn = (Button)barLayoutView.findViewById(R.id.toolbar_right_btn);
        TextView rightTv = (TextView)barLayoutView.findViewById(R.id.toolbar_right_tv);
        RelativeLayout barRlyt = (RelativeLayout)barLayoutView.findViewById(R.id.toolbar_content_rlyt);
        View paddingView = (View)barLayoutView.findViewById(R.id.padding_view);
        View shaderView = (View)barLayoutView.findViewById(R.id.shader);

        leftBtn.setOnClickListener(this);
        leftTv.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
        rightTv.setOnClickListener(this);

        if(SkinManager.getInstance().isDefaultTheme()){
            rightTv.setTextColor(Color.parseColor("#000000"));
        }else{
            rightTv.setTextColor(Color.parseColor("#ffffff"));
        }

        paddingView.getLayoutParams().height = ScreenUtils.getStatusBarHeight();

        if(isLeftBtnVisible){
            leftBtn.setVisibility(VISIBLE);
        }
        if(isLeftTvVisible){
            leftTv.setVisibility(VISIBLE);
        }
        if(isRightBtnVisible){
            rightBtn.setVisibility(VISIBLE);
        }
        if(isRightTvVisible){
            rightTv.setVisibility(VISIBLE);
        }
        if(isTitleVisible){
            titleTv.setVisibility(VISIBLE);
        }
        leftTv.setText(leftTvText);
        rightTv.setText(rightTvText);
        titleTv.setText(titleText);
        titleTv.setTextColor(SkinManager.getInstance().getPrimaryTextColor());

        if(leftResId != -1){
            leftBtn.setBackgroundResource(leftResId);
            if(SkinManager.getInstance().isDefaultTheme()){
                leftBtn.setBackground(ShapeUtils.drawColor(getResources().getDrawable(leftResId), Color.parseColor("#000000")));
            }else{
                leftBtn.setBackground(ShapeUtils.drawColor(getResources().getDrawable(leftResId), Color.parseColor("#ffffff")));
            }
        }
        if(rightResId != -1){
            rightBtn.setBackgroundResource(rightResId);
        }
        if(backgroundResId != -1){
            barRlyt.setBackgroundColor(backgroundResId);
        }else{
            barRlyt.setBackgroundColor(SkinManager.getInstance().getPrimaryColor());
            paddingView.setBackgroundColor(SkinManager.getInstance().getPrimaryColor());
        }
        if(isShaderVisible){
            shaderView.setVisibility(VISIBLE);
        }else{
            shaderView.setVisibility(GONE);
        }
        //将设置完成之后的View添加到此LinearLayout中
        addView(barLayoutView, 0);
    }

    @Override
    public void onClick(View view) {
        if(mClickListener == null){
            return;
        }
        int id = view.getId();
        if(id == R.id.toolbar_left_btn){
            mClickListener.clickLeftBtn();
        }else if(id == R.id.toolbar_left_tv){
            mClickListener.clickLeftBtn();
        }else if(id == R.id.toolbar_right_btn){
            mClickListener.clickRightBtn();
        }else if(id == R.id.toolbar_right_tv){
            mClickListener.clickRightBtn();
        }
    }

    OnClickListener mClickListener;

    public void setClickListener(OnClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public interface OnClickListener {
        void clickRightBtn();
        void clickLeftBtn();
    }
}
