package com.zjyang.base.utils;

import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.postprocessors.IterativeBoxBlurPostProcessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.zjyang.base.utils.DrawUtils;

/**
 * Created by 74215 on 2018/4/10.
 */

public class FrescoUtils {

    public static void showImgByUrl(String url, SimpleDraweeView simpleDraweeView){
        simpleDraweeView.setImageURI(Uri.parse(url));
//        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(AppApplication.getContext().getResources());
//        /**
//         * 设置淡入淡出效果的显示时间
//         * */
//        GenericDraweeHierarchy hierarchy = builder.setFadeDuration(200).build();
//        DraweeController placeHolderDraweeController = Fresco.newDraweeControllerBuilder()
//                .setUri(Uri.parse(url)) //为图片设置url
//                .build();
//        simpleDraweeView.setController(placeHolderDraweeController);
//        simpleDraweeView.setHierarchy(hierarchy);
    }


    /**
     * 以高斯模糊显示。
     *
     * @param draweeView View。
     * @param url        url.
     * @param iterations 迭代次数，越大越魔化。
     * @param blurRadius 模糊图半径，必须大于0，越大越模糊。
     */
    public static void showUrlBlur(SimpleDraweeView simpleDraweeView, String url, int blurRadius) {
        try {
            Uri uri = Uri.parse(url);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setPostprocessor(new IterativeBoxBlurPostProcessor(6, blurRadius))
                    .build();
            AbstractDraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(simpleDraweeView.getController())
                    .setImageRequest(request)
                    .build();
            simpleDraweeView.setController(controller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void setBorderColor(SimpleDraweeView simpleDraweeView, int color){
        RoundingParams roundingParams = RoundingParams.fromCornersRadius(DrawUtils.dp2px(30));
        roundingParams.setBorder(color, DrawUtils.dp2px(1));
        simpleDraweeView.getHierarchy().setRoundingParams(roundingParams);
    }
}
