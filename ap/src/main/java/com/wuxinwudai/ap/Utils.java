package com.wuxinwudai.ap;

import android.content.res.Resources;
import android.graphics.Paint;
import android.util.TypedValue;

/**
 * Utils 通用工具
 * @author 吾心无待 于 2016年06月21日
 */
public class Utils {
    /**
     * dp 转 px
     * @param resources 资源
     * @param dpVal dp 值
     * @return px 值
     */
    public static int dp2px(Resources resources, int dpVal){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dpVal,
                resources.getDisplayMetrics());
    }

    /**
     * sp 转 px
     * @param resources 资源
     * @param spVal px 值
     * @return dp 值
     */
    public static int sp2px(Resources resources,int spVal){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,spVal,
                resources.getDisplayMetrics());
    }

    /**
     * dp 转 px
     * @param resources 资源
     * @param dpVal dp 值
     * @return px 值
     */
    public static float dp2px(Resources resources, float dpVal){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dpVal,
                resources.getDisplayMetrics());
    }

    /**
     * sp 转 px
     * @param resources 资源
     * @param spVal px 值
     * @return dp 值
     */
    public static float sp2px(Resources resources,float spVal){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,spVal,
                resources.getDisplayMetrics());
    }

    public static float getTextHeight(Paint paint){
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (float) Math.ceil(fm.top - fm.bottom);
    }

    /**
     * 获取字体基于
     * @param paint
     * @return
     */
    public static float getTextBaseLineHeight(Paint paint){
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (float) Math.ceil(fm.ascent - fm.descent);
    }
}
