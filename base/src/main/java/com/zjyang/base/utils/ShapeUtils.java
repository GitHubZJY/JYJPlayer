package com.zjyang.base.utils;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.v4.graphics.drawable.DrawableCompat;

import com.zjyang.base.BaseApp;
import com.zjyang.base.R;

/**
 * Created by zhengjiayang on 2018/3/1.
 */

public class ShapeUtils {

    public static ShapeDrawable getRoundRectDrawable(){
        float[] outerRadii = {60, 60, 60, 60, 60, 60, 60, 60};//外矩形 左上、右上、右下、左下的圆角半径
        RectF inset = new RectF(2, 2, 2, 2);//内矩形距外矩形，左上角x,y距离， 右下角x,y距离
        float[] innerRadii = {20, 20, 20, 20, 20, 20, 20, 20};//内矩形 圆角半径

        RoundRectShape roundRectShape = new RoundRectShape(outerRadii, null, null);

        ShapeDrawable drawable = new ShapeDrawable(roundRectShape);
        drawable.getPaint().setColor(BaseApp.sContext.getResources().getColor(R.color.colorPrimary));
        drawable.getPaint().setAntiAlias(true);
        drawable.getPaint().setStyle(Paint.Style.FILL);//描边
        return drawable;
    }

    public static ShapeDrawable getRoundRectDrawable(int radius){
        float[] outerRadii = {radius, radius, radius, radius, radius, radius, radius, radius};//外矩形 左上、右上、右下、左下的圆角半径
        RectF inset = new RectF(2, 2, 2, 2);//内矩形距外矩形，左上角x,y距离， 右下角x,y距离
        float[] innerRadii = {20, 20, 20, 20, 20, 20, 20, 20};//内矩形 圆角半径

        RoundRectShape roundRectShape = new RoundRectShape(outerRadii, null, null);

        ShapeDrawable drawable = new ShapeDrawable(roundRectShape);
        drawable.getPaint().setColor(BaseApp.sContext.getResources().getColor(R.color.colorPrimary));
        drawable.getPaint().setAntiAlias(true);
        drawable.getPaint().setStyle(Paint.Style.FILL);//描边
        return drawable;
    }


    public static ShapeDrawable getRoundRectDrawable(int radius, int color){
        float[] outerRadii = {radius, radius, radius, radius, radius, radius, radius, radius};//外矩形 左上、右上、右下、左下的圆角半径
        RectF inset = new RectF(2, 2, 2, 2);//内矩形距外矩形，左上角x,y距离， 右下角x,y距离
        float[] innerRadii = {20, 20, 20, 20, 20, 20, 20, 20};//内矩形 圆角半径

        RoundRectShape roundRectShape = new RoundRectShape(outerRadii, null, null);

        ShapeDrawable drawable = new ShapeDrawable(roundRectShape);
        drawable.getPaint().setColor(color);
        drawable.getPaint().setAntiAlias(true);
        drawable.getPaint().setStyle(Paint.Style.FILL);//描边
        return drawable;
    }

    /**

     * 重绘Drawable，将drawable颜色着色为color

     * @param drawable

     * @param color

     * @return 重绘后的Drawable

     */

    public static Drawable drawColor(Drawable drawable, int color) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        wrappedDrawable.mutate();
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }
}
