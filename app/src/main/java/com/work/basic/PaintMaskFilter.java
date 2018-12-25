package com.work.basic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.work.R;
import com.work.utils.BitmapUtils;

/**
 * Title: PaintMaskFilter
 * <p>
 * Description:PaintMaskFilter
 * </p>
 *
 * @author Changbao
 * @date 2018/12/14  17:47
 */
public class PaintMaskFilter extends View {
    public PaintMaskFilter(Context context) {
        super(context);
    }

    public PaintMaskFilter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PaintMaskFilter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setPadding(10, 10, 10, 10);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.icon_paint_man)).getBitmap();
        bitmap = BitmapUtils.resizeBitmap(bitmap, 150, 150);
        //【注】解决设置 MaskFilter 无效的问题
        this.setLayerType(LAYER_TYPE_SOFTWARE, paint);

        // setMaskFilter(MaskFilter maskfilter) 为之后的绘制设置 MaskFilter。

        //上一个方法 setShadowLayer() 是设置的在绘制层下方的附加效果；
        //而这个 MaskFilter 和它相反，设置的是在绘制层上方的附加效果。

        //到现在已经有两个 setXxxFilter(filter) 了。前面有一个 setColorFilter(filter) ，是对每个像素的颜色进行过滤；
        //而这里的 setMaskFilter(filter) 则是基于整个画面来进行过滤。

        /*MaskFilter 有两种： BlurMaskFilter 和 EmbossMaskFilter。*/

        //BlurMaskFilter  模糊效果的 MaskFilter。
        /*
        它的构造方法 BlurMaskFilter(float radius, BlurMaskFilter.Blur style) 中，
        radius 参数是模糊的范围，
        style 是模糊的类型。一共有四种：
            NORMAL: 内外都模糊绘制
            SOLID: 内部正常绘制，外部模糊
            INNER: 内部模糊，外部不绘制
            OUTER: 内部不绘制，外部模糊（什么鬼？）

            网站：https://hencoder.com/ui-1-2/
            效果图：https://ws3.sinaimg.cn/large/006tNc79ly1fig7fr4dwgj30lk0mbgne.jpg
         */
        BlurMaskFilter blurMaskFilter1 = new BlurMaskFilter(50, BlurMaskFilter.Blur.NORMAL);
        BlurMaskFilter blurMaskFilter2 = new BlurMaskFilter(50, BlurMaskFilter.Blur.INNER);
        BlurMaskFilter blurMaskFilter3 = new BlurMaskFilter(50, BlurMaskFilter.Blur.OUTER);
        BlurMaskFilter blurMaskFilter4 = new BlurMaskFilter(50, BlurMaskFilter.Blur.SOLID);

        //EmbossMaskFilter 浮雕效果的 MaskFilter。
        /*
        构造方法 EmbossMaskFilter(float[] direction, float ambient, float specular, float blurRadius) 的参数里，
        direction 是一个 3 个元素的数组，指定了光源的方向；
        ambient 是环境光的强度，数值范围是 0 到 1；
        specular 是炫光的系数；
        blurRadius 是应用光线的范围。
         */
        EmbossMaskFilter embossMaskFilter = new EmbossMaskFilter(
                new float[]{50, 100, 60}, 0.8F, 8F, 30F);

        paint.setMaskFilter(blurMaskFilter1);
        canvas.drawBitmap(bitmap, 50, 50, paint);
        paint.setMaskFilter(blurMaskFilter2);
        canvas.drawBitmap(bitmap, 300, 50, paint);
        paint.setMaskFilter(blurMaskFilter3);
        canvas.drawBitmap(bitmap, 50, 300, paint);
        paint.setMaskFilter(blurMaskFilter4);
        canvas.drawBitmap(bitmap, 300, 300, paint);
        paint.setMaskFilter(embossMaskFilter);
        canvas.drawBitmap(bitmap, 500, 150, paint);

        /*
        setColorFilter(filter) ，是对每个像素的颜色进行过滤；而这里的 setMaskFilter(filter) 则是基于整个画面来进行过滤。
         */
    }
}
