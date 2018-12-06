package com.jyj.video.jyjplayer.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.zjyang.base.base.SkinManager;
import com.zjyang.base.utils.DrawUtils;

/**
 * Created by zhengjiayang on 2018/12/5.
 */

public class VideoLoadingView extends View{

    private Paint mPaint;
    private Paint mDotPaint;
    private Path mPath;
    private ValueAnimator mSweepValueAnimator;
    private ValueAnimator mStartValueAnimator;
    private float mSweepAngle = 0;
    private float mStartAngle = -135;
    private float mNextStartAngle = -135;
    private float mRadius = 30;
    private float mStrokeWidth = 8;
    private int mColor = SkinManager.getInstance().getPrimaryColor();

    public VideoLoadingView(Context context) {
        this(context, null);
    }

    public VideoLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context){
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mDotPaint = new Paint();
        mDotPaint.setColor(mColor);
        mDotPaint.setStyle(Paint.Style.FILL);
        mDotPaint.setAntiAlias(true);
        mDotPaint.setStrokeWidth(5);

        mSweepValueAnimator = ValueAnimator.ofFloat(0, 1);
        mStartValueAnimator = ValueAnimator.ofFloat(0, 1);
        mSweepValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float)animation.getAnimatedValue();
                mSweepAngle = value * 360;
                invalidate();
                if(mSweepAngle == 360){
                    mStartValueAnimator.start();
                }
            }
        });
        mStartValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float)animation.getAnimatedValue();
                mStartAngle = mNextStartAngle + value * 360;
                mSweepAngle = 360 - value * 360;
                invalidate();
                if(mStartAngle == (mNextStartAngle + 360)){
                    mNextStartAngle = mStartAngle;
                    mSweepValueAnimator.start();
                }
            }
        });
        mStartValueAnimator.setDuration(1500);
        mSweepValueAnimator.setDuration(1500);
        mSweepValueAnimator.start();

        mPath = new Path();
        mPath.moveTo(mRadius*3/4, mRadius*3/6);
        mPath.lineTo(mRadius*3/4, mRadius*9/6);
        mPath.lineTo(mRadius*3/2, mRadius);
        mPath.close();


        setLayerType(View.LAYER_TYPE_HARDWARE, null);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int)(mRadius * 2 + mStrokeWidth * 2), (int)(mRadius * 2 + mStrokeWidth * 2));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mStrokeWidth, mStrokeWidth);
        float circleAngle = (float)((mRadius*Math.sqrt(2) - mRadius) / Math.sqrt(2));
        canvas.drawCircle(circleAngle, circleAngle, mStrokeWidth / 2, mDotPaint);
        canvas.drawArc(new RectF(0, 0, mRadius*2, mRadius*2), mStartAngle, mSweepAngle, false, mPaint);


        canvas.drawPath(mPath, mDotPaint);
    }
}
