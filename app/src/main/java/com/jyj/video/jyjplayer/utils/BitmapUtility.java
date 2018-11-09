package com.jyj.video.jyjplayer.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;

import com.zjyang.base.utils.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class BitmapUtility {
    private static final String TAG = "BitmapUtility";

    public BitmapUtility() {
    }

    public static final Bitmap createBitmap(View view, float scale) {
        Bitmap pRet = null;
        if(null == view) {
            LogUtil.i("BitmapUtility", "create bitmap function param view is null");
            return pRet;
        } else {
            int scaleWidth = (int)((float)view.getWidth() * scale);
            int scaleHeight = (int)((float)view.getHeight() * scale);
            if(scaleWidth > 0 && scaleHeight > 0) {
                boolean bViewDrawingCacheEnable = view.isDrawingCacheEnabled();
                if(!bViewDrawingCacheEnable) {
                    view.setDrawingCacheEnabled(true);
                }

                try {
                    Bitmap viewBmp = view.getDrawingCache(true);
                    if(viewBmp == null) {
                        pRet = Bitmap.createBitmap(scaleWidth, scaleHeight, view.isOpaque()?Config.RGB_565:Config.ARGB_8888);
                        Canvas canvas = new Canvas(pRet);
                        canvas.scale(scale, scale);
                        view.draw(canvas);
                        canvas = null;
                    } else {
                        pRet = Bitmap.createScaledBitmap(viewBmp, scaleWidth, scaleHeight, true);
                    }

                    viewBmp = null;
                } catch (OutOfMemoryError var8) {
                    pRet = null;
                    LogUtil.i("BitmapUtility", "create bitmap out of memory");
                } catch (Exception var9) {
                    pRet = null;
                    LogUtil.i("BitmapUtility", "create bitmap exception");
                }

                if(!bViewDrawingCacheEnable) {
                    view.setDrawingCacheEnabled(false);
                }

                return pRet;
            } else {
                LogUtil.i("BitmapUtility", "create bitmap function param view is not layout");
                return pRet;
            }
        }
    }

    public static final Bitmap createBitmap(Bitmap bmp, int desWidth, int desHeight) {
        Bitmap pRet = null;
        if(null == bmp) {
            LogUtil.i("BitmapUtility", "create bitmap function param bmp is null");
            return pRet;
        } else {
            try {
                pRet = Bitmap.createBitmap(desWidth, desHeight, Config.ARGB_8888);
                Canvas canvas = new Canvas(pRet);
                int left = (desWidth - bmp.getWidth()) / 2;
                int top = (desHeight - bmp.getHeight()) / 2;
                canvas.drawBitmap(bmp, (float)left, (float)top, (Paint)null);
                canvas = null;
            } catch (OutOfMemoryError var7) {
                pRet = null;
                LogUtil.i("BitmapUtility", "create bitmap out of memory");
            } catch (Exception var8) {
                pRet = null;
                LogUtil.i("BitmapUtility", "create bitmap exception");
            }

            return pRet;
        }
    }

    public static final Bitmap createScaledBitmap(Bitmap bmp, int scaleWidth, int scaleHeight) {
        Bitmap pRet = null;
        if(null == bmp) {
            LogUtil.i("BitmapUtility", "create scale bitmap function param bmp is null");
            return pRet;
        } else if(scaleWidth == bmp.getWidth() && scaleHeight == bmp.getHeight()) {
            return bmp;
        } else {
            try {
                pRet = Bitmap.createScaledBitmap(bmp, scaleWidth, scaleHeight, true);
            } catch (OutOfMemoryError var5) {
                pRet = null;
                LogUtil.i("BitmapUtility", "create scale bitmap out of memory");
            } catch (Exception var6) {
                pRet = null;
                LogUtil.i("BitmapUtility", "create scale bitmap exception");
            }

            return pRet;
        }
    }

    public static final boolean saveBitmap(Bitmap bmp, String bmpName) {
        return saveBitmap(bmp, bmpName, CompressFormat.PNG);
    }

    public static final boolean saveBitmap(Bitmap bmp, String bmpName, CompressFormat format) {
        if(null == bmp) {
            LogUtil.i("BitmapUtility", "save bitmap to file bmp is null");
            return false;
        } else {
            FileOutputStream stream = null;

            try {
                boolean bCreate;
                try {
                    File file = new File(bmpName);
                    boolean bOk;
                    boolean var7;
                    if(file.exists()) {
                        bCreate = file.delete();
                        if(!bCreate) {
                            LogUtil.i("BitmapUtility", "delete src file fail");
                            bOk = false;
                            return bOk;
                        }
                    } else {
                        File parent = file.getParentFile();
                        if(null == parent) {
                            LogUtil.i("BitmapUtility", "get bmpName parent file fail");
                            bOk = false;
                            return bOk;
                        }

                        if(!parent.exists()) {
                            bOk = parent.mkdirs();
                            if(!bOk) {
                                LogUtil.i("BitmapUtility", "make dir fail");
                                var7 = false;
                                return var7;
                            }
                        }
                    }

                    bCreate = file.createNewFile();
                    if(!bCreate) {
                        LogUtil.i("BitmapUtility", "create file fail");
                        bOk = false;
                        return bOk;
                    } else {
                        stream = new FileOutputStream(file);
                        bOk = bmp.compress(format, 100, stream);
                        if(bOk) {
                            return true;
                        } else {
                            LogUtil.i("BitmapUtility", "bitmap compress file fail");
                            var7 = false;
                            return var7;
                        }
                    }
                } catch (Exception var22) {
                    LogUtil.i("BitmapUtility", var22.toString());
                    bCreate = false;
                    return bCreate;
                }
            } finally {
                if(null != stream) {
                    try {
                        stream.close();
                    } catch (Exception var21) {
                        LogUtil.i("BitmapUtility", "close stream " + var21.toString());
                    }
                }

            }
        }
    }

    public static Bitmap loadBitmap(Context context, Uri uri, int simpleSize) {
        Bitmap pRet = null;
        if(null == context) {
            LogUtil.i("BitmapUtility", "load bitmap context is null");
            return pRet;
        } else if(null == uri) {
            LogUtil.i("BitmapUtility", "load bitmap uri is null");
            return pRet;
        } else {
            InputStream is = null;
            int sampleSize = simpleSize;
            Options opt = new Options();
            boolean bool = true;

            while(bool) {
                try {
                    is = context.getContentResolver().openInputStream(uri);
                    opt.inSampleSize = sampleSize;
                    pRet = null;
                    pRet = BitmapFactory.decodeStream(is, (Rect)null, opt);
                    bool = false;
                } catch (OutOfMemoryError var19) {
                    sampleSize *= 2;
                    if(sampleSize > 1024) {
                        bool = false;
                    }
                } catch (Throwable var20) {
                    bool = false;
                    LogUtil.i("BitmapUtility", var20.getMessage());
                } finally {
                    try {
                        if(is != null) {
                            is.close();
                        }
                    } catch (Exception var18) {
                        LogUtil.i("BitmapUtility", var18.getMessage());
                        LogUtil.i("BitmapUtility", "load bitmap close uri stream exception");
                    }

                }
            }

            return pRet;
        }
    }

    public static BitmapDrawable zoomDrawable(Context context, Drawable drawable, int w, int h) {
        if(drawable != null) {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            Bitmap oldbmp = null;
            if(drawable instanceof BitmapDrawable) {
                oldbmp = ((BitmapDrawable)drawable).getBitmap();
            } else {
                oldbmp = createBitmapFromDrawable(drawable);
            }

            Matrix matrix = new Matrix();
            float scaleWidth = (float)w / (float)width;
            float scaleHeight = (float)h / (float)height;
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);
            matrix = null;
            return new BitmapDrawable(context.getResources(), newbmp);
        } else {
            return null;
        }
    }

    public static BitmapDrawable zoomDrawable(Drawable drawable, float wScale, float hScale, Resources res) {
        if(drawable != null) {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            Bitmap oldbmp = null;
            if(drawable instanceof BitmapDrawable) {
                oldbmp = ((BitmapDrawable)drawable).getBitmap();
            } else {
                oldbmp = createBitmapFromDrawable(drawable);
            }

            Matrix matrix = new Matrix();
            matrix.postScale(wScale, hScale);
            Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);
            matrix = null;
            return new BitmapDrawable(res, newbmp);
        } else {
            return null;
        }
    }

    public static BitmapDrawable clipDrawable(BitmapDrawable drawable, int w, int h, Resources res) {
        if(drawable != null) {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            if(width < w) {
                w = width;
            }

            if(height < h) {
                h = height;
            }

            int x = width - w >> 1;
            int y = height - h >> 1;
            Matrix matrix = new Matrix();
            Bitmap newbmp = Bitmap.createBitmap(drawable.getBitmap(), x, y, w, h, matrix, true);
            matrix = null;
            return new BitmapDrawable(res, newbmp);
        } else {
            return null;
        }
    }

    public static Bitmap toConformBitmap(Bitmap background, Bitmap foreground, Paint paint) {
        if(null == background) {
            return null;
        } else {
            int bgWidth = background.getWidth();
            int bgHeight = background.getHeight();
            Bitmap newbmp = null;

            try {
                newbmp = Bitmap.createBitmap(bgWidth, bgHeight, Config.ARGB_8888);
            } catch (OutOfMemoryError var7) {
                return null;
            }

            Canvas cv = new Canvas(newbmp);
            cv.drawBitmap(background, 0.0F, 0.0F, paint);
            if(null != foreground) {
                cv.drawBitmap(foreground, 0.0F, 0.0F, paint);
            }

            cv.save(Canvas.ALL_SAVE_FLAG);
            cv.restore();
            return newbmp;
        }
    }

    public static Drawable getNeutralDrawable(Drawable srcDrawable) {
        if(srcDrawable != null) {
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(0.0F);
            srcDrawable.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            return srcDrawable;
        } else {
            return null;
        }
    }

    public static Drawable getOriginalDrawable(Drawable neturalDrawable) {
        if(neturalDrawable != null) {
            neturalDrawable.setColorFilter((ColorFilter)null);
            return neturalDrawable;
        } else {
            return null;
        }
    }

    public static Bitmap createBitmapFromDrawable(Drawable drawable) {
        if(drawable == null) {
            return null;
        } else {
            Bitmap bitmap = null;
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();

            Config canvas;
            try {
                canvas = drawable.getOpacity() != -1?Config.ARGB_8888:Config.RGB_565;
                bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, canvas);
            } catch (OutOfMemoryError var5) {
                return null;
            }

            Canvas canvas2 = new Canvas(bitmap);
            drawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
            drawable.draw(canvas2);
            canvas = null;
            return bitmap;
        }
    }
}
