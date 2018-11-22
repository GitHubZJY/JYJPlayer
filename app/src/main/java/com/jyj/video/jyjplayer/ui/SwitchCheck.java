package com.jyj.video.jyjplayer.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.view.View;

import com.jyj.video.jyjplayer.R;
import com.zjyang.base.utils.DrawUtils;

/**
 * Created by chensuilun on 16-4-20.
 *
 */

public class SwitchCheck extends View {

    private static final int DEFAULT_CHECK_BG_ID = R.drawable.switch_on;
    private static final int DEFAULT_CHECK_DRAWABLE_ID = R.drawable.switch_dot;
    private static final int DEFAULT_UNCHECK_BG_ID = R.drawable.switch_off;
    private static final int DEFAULT_UNCHECK_DRAWABLE_ID = R.drawable.switch_dot;

    private static final int DISABLE_COVER = 0XFFB6B7BA;

    private Drawable mCheckBgDrawable;
    private Drawable mUnCheckBgDrawable;
    private Drawable mCheckDrawable;
    private Drawable mUnCheckDrawable;
    private Context mContext;

    private boolean mIsCheck = true;

    private int mWidth;
    private int mHeight;

    //-----主题----------
    private int mUnCheckedBgResId = DEFAULT_UNCHECK_BG_ID;
    private int mUnCheckedDrawableResId = DEFAULT_UNCHECK_DRAWABLE_ID;
    private int mCheckedBgResId = DEFAULT_CHECK_BG_ID;
    private int mCheckedDrawableResId = DEFAULT_CHECK_DRAWABLE_ID;

    private int mCheckedDrawableWidth = 0;
    private int mUnCheckedDrawableWidth = 0;

    public SwitchCheck(Context context) {
        this(context, null);
    }

    public SwitchCheck(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SwitchCheck(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        mCheckBgDrawable = mContext.getResources().getDrawable(mCheckedBgResId);
        mUnCheckBgDrawable = mContext.getResources().getDrawable(mUnCheckedBgResId);
        mCheckDrawable = mContext.getResources().getDrawable(mCheckedDrawableResId);
        mUnCheckDrawable = mContext.getResources().getDrawable(mUnCheckedDrawableResId);
        mCheckedDrawableWidth = DrawUtils.dp2px(36);
        mUnCheckedDrawableWidth = DrawUtils.dp2px(36);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxWidth = Math.max(mCheckBgDrawable.getIntrinsicWidth(), mCheckDrawable.getIntrinsicWidth());
        int maxHeight = Math.max(mCheckBgDrawable.getIntrinsicHeight(), mCheckDrawable.getIntrinsicHeight());
        int desiredWidth = getPaddingLeft() + getPaddingRight() + maxWidth;
        int desiredHeight = getPaddingBottom() + getPaddingTop() + maxHeight;
        int widthSpec = resolveSizeAndState(desiredWidth, widthMeasureSpec);
        int heightSpec = resolveSizeAndState(desiredHeight, heightMeasureSpec);
        setMeasuredDimension(widthSpec, heightSpec);
        mWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        mHeight = getMeasuredHeight() - getPaddingBottom() - getPaddingTop();
        initBgDrawable(mCheckBgDrawable);
        initBgDrawable(mUnCheckBgDrawable);
        initButtonDrawable(mCheckDrawable, true);
        initButtonDrawable(mUnCheckDrawable, false);
    }

    private void initButtonDrawable(Drawable checkBgDrawable, boolean isRight) {
        if (isRight) {
            final int drawableHeight = checkBgDrawable.getIntrinsicHeight();
            final int drawableWidth = checkBgDrawable.getIntrinsicWidth();
            final int right = mCheckBgDrawable.getBounds().right;
            final int left = right - drawableWidth;
            final int top = (mHeight - drawableHeight) / 2;
            final int bottom = top + drawableHeight;
            checkBgDrawable.setBounds(left, top, right, bottom);
            mCheckedDrawableWidth = right - left;
        } else {
            final int drawableHeight = checkBgDrawable.getIntrinsicHeight();
            final int drawableWidth = checkBgDrawable.getIntrinsicWidth();
            final int left = mCheckBgDrawable.getBounds().left;
            final int right = left + drawableWidth;
            final int top = (mHeight - drawableHeight) / 2;
            final int bottom = top + drawableHeight;
            checkBgDrawable.setBounds(left, top, right, bottom);
            mUnCheckedDrawableWidth = right - left;
        }

    }

    private void initBgDrawable(Drawable checkBgDrawable) {
        //居中
        final int drawableHeight = checkBgDrawable.getIntrinsicHeight();
        final int drawableWidth = checkBgDrawable.getIntrinsicWidth();
        final int left = (mWidth - drawableWidth) / 2;
        final int right = left + drawableWidth;
        final int top = (mHeight - drawableHeight) / 2;
        final int bottom = top + drawableHeight;
        checkBgDrawable.setBounds(left, top, right, bottom);
    }

    public static int resolveSizeAndState(int desireSize, int measureSpec) {
        int result = desireSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = desireSize;
                break;
            case MeasureSpec.AT_MOST:
                if (specSize < desireSize) {
                    result = specSize;
                } else {
                    result = desireSize;
                }
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());
        if (mIsCheck) {
            mCheckBgDrawable.draw(canvas);
            canvas.save();
            canvas.translate(mCheckedDrawableWidth / 4, 0f);
            mCheckDrawable.draw(canvas);
            canvas.restore();
        } else {
            mUnCheckBgDrawable.draw(canvas);
            canvas.save();
            canvas.translate(- mUnCheckedDrawableWidth / 4, 0f);
            mUnCheckDrawable.draw(canvas);
            canvas.restore();
        }
        canvas.restore();
    }

    public void setCheck(boolean check) {
        if (mIsCheck != check) {
            mIsCheck = check;
            invalidate();
        }
    }

    public boolean isCheck() {
        return mIsCheck;
    }

    public void setUnCheckedBgResId(int unCheckedBgResId) {
        if (mUnCheckedBgResId != unCheckedBgResId) {
            mUnCheckedBgResId = unCheckedBgResId;
            mUnCheckBgDrawable = ActivityCompat.getDrawable(getContext(), mUnCheckedBgResId);
            requestLayout();

        }
    }

    public void setUnCheckedDrawableResId(int unCheckedDrawableResId) {
        if (mUnCheckedDrawableResId != unCheckedDrawableResId) {
            mUnCheckedDrawableResId = unCheckedDrawableResId;
            mUnCheckDrawable = ActivityCompat.getDrawable(getContext(), mUnCheckedDrawableResId);
            requestLayout();

        }
    }
}
