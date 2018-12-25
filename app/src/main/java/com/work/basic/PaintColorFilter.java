package com.work.basic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.work.R;

/**
 * Title: PaintColorFilterView
 * <p>
 * Description:
 * </p>
 *
 * @author Changbao
 * @date 2018/12/13  17:09
 */
public class PaintColorFilter extends View {
    Paint mPaintText = new Paint();

    public PaintColorFilter(Context context) {
        super(context);
    }

    public PaintColorFilter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PaintColorFilter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int padding = 5;//间距
        int width = getMeasuredWidth();
        int height = getMeasuredHeight() - padding * 3;//padding
        int childHeight = height / 4;

        Paint paint = new Paint();
        //绘制原图
        Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.icon_paint_red)).getBitmap();
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        paint.setShader(bitmapShader);
        mPaintText.setTextSize(50);
        mPaintText.setColor(Color.RED);
        canvas.drawRect(0, 0, width, childHeight, paint);
        canvas.drawText("原图", 0, 60, mPaintText);

        //ColorFilter 三个子类：LightingColorFilter PorterDuffColorFilter 和  ColorMatrixColorFilter
        //1 LightingColorFilter 是用来模拟简单的光照效果的
        LightingColorFilter lightingColorFilter = new LightingColorFilter(0x00ffff, 0x000000);//去除红色
        paint.setColorFilter(lightingColorFilter);

        canvas.drawRect(0, childHeight + padding, width, childHeight * 2, paint);
        mPaintText.setColor(Color.RED);
        canvas.drawText("过滤掉红色", 0, childHeight + 60, mPaintText);

        LightingColorFilter lightingColorFilter2 = new LightingColorFilter(0x00ffff, 0x005000);
        paint.setColorFilter(lightingColorFilter2);
        canvas.drawRect(0, childHeight * 2 + padding, width, childHeight * 3, paint);
        mPaintText.setColor(Color.RED);
        canvas.drawText("过滤掉红色同时加点绿色", 0, childHeight * 2 + 60, mPaintText);

        //2 PorterDuffColorFilter
        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(0x00ffff, PorterDuff.Mode.DARKEN);
        paint.setColorFilter(porterDuffColorFilter);
        canvas.drawRect(0, childHeight * 3 + padding, width, childHeight * 4, paint);
        mPaintText.setColor(Color.RED);
        canvas.drawText("PorterDuffColorFilter", 0, childHeight * 3 + 60, mPaintText);

        //3 ColorMatrixColorFilter
        /*
        ColorMatrixColorFilter 使用一个 ColorMatrix 来对颜色进行处理。 ColorMatrix 这个类，内部是一个 4x5 的矩阵
        https://github.com/chengdazhi/StyleImageView
         */
        ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(new ColorMatrix());

        /*
        ===========================================================================================
        除了基本颜色的设置（ setColor/ARGB(), setShader() ）以及基于原始颜色的过滤（ setColorFilter() ）之外，
        Paint 最后一层处理颜色的方法是 setXfermode(Xfermode xfermode) ，它处理的是「当颜色遇上 View」的问题。
        ===========================================================================================
         */
    }
}
