package com.work.basic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Title: PaintStyle
 * <p>
 * Description:
 * </p>
 *
 * @author Changbao
 * @date 2018/12/14  11:18
 */
public class PaintStyleBasic extends View {
    public PaintStyleBasic(Context context) {
        super(context);
    }

    public PaintStyleBasic(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PaintStyleBasic(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //1 线条形状
        paint.setStyle(Paint.Style.STROKE);
        /*
        线条宽度 0 和 1 的区别

        默认情况下，线条宽度为 0，但你会发现，这个时候它依然能够画出线，线条的宽度为 1 像素。那么它和线条宽度为 1 有什么区别呢？
        其实这个和后面要讲的一个「几何变换」有关：你可以为 Canvas 设置 Matrix 来实现几何变换（如放大、缩小、平移、旋转），
        在几何变换之后 Canvas 绘制的内容就会发生相应变化，包括线条也会加粗，
        例如 2 像素宽度的线条在 Canvas 放大 2 倍后会被以 4 像素宽度来绘制。而当线条宽度被设置为 0 时，它的宽度就被固定为 1 像素，
        就算 Canvas 通过几何变换被放大，它也依然会被以 1 像素宽度来绘制。
        Google 在文档中把线条宽度为 0 时称作「hairline mode（发际线模式）」。
         */
        paint.setStrokeWidth(1);

        //设置线头的形状。线头形状有三种：BUTT 平头、ROUND 圆头、SQUARE 方头。默认为 BUTT。
        paint.setStrokeCap(Paint.Cap.ROUND);

        //设置拐角的形状。有三个值可以选择：MITER 尖角、 BEVEL 平角和 ROUND 圆角。默认为 MITER。
        paint.setStrokeJoin(Paint.Join.MITER);

        //这个方法是对于 setStrokeJoin() 的一个补充，它用于设置 MITER 型拐角的延长线的最大值。
        //即「 线条在 Join 类型为 MITER 时对于 MITER 的长度限制」
        //默认值是 4，对应的是一个大约 29° 的锐角。默认情况下，大于这个角的尖角会被保留，而小于这个夹角的就会被「削成平角BEVEL」
        paint.setStrokeMiter(4F);//应该叫 setStrokeJoinMiterLimit(limit)


        /*
        线条形状的方法： setStrokeWidth(width) setStrokeCap(cap) setStrokeJoint(join) 和  setStrokeMiter(miter)
         */


        //2 色彩优化
        //Paint 的色彩优化有两个方法： setDither(boolean dither) 和 setFilterBitmap(boolean filter) 。
        //它们的作用都是让画面颜色变得更加「顺眼」，但原理和使用场景是不同的。

        //设置图像的抖动 维基百科(三只猫的图片)： https://en.wikipedia.org/wiki/Dither
        /*
        抖动，是指把图像从较高色彩深度（即可用的颜色数）向较低色彩深度的区域绘制时，在图像中有意地插入噪点，
        通过有规律地扰乱图像来让图像对于肉眼更加真实的做法。

        抖动不只可以用在纯色的绘制。在实际的应用场景中，抖动更多的作用是在图像降低色彩深度绘制时，避免出现大片的色带与色块。

        不过对于现在（2017年）而言， setDither(dither) 已经没有当年那么实用了，因为现在的 Android 版本的绘制，
        默认的色彩深度已经是 32 位的 ARGB_8888 ，效果已经足够清晰了。
        只有当你向自建的 Bitmap 中绘制，并且选择 16 位色的 ARGB_4444 或者 RGB_565 的时候，开启它才会有比较明显的效果。
         */
        paint.setDither(true);

        //设置是否使用双线性过滤来绘制 Bitmap   维基百科（花盆图片） ：tps://zh.wikipedia.org/wiki/双线性过滤
        //图像在放大绘制的时候，默认使用的是最近邻插值过滤，这种算法简单，但会出现马赛克现象；
        //而如果开启了双线性过滤，就可以让结果图像显得更加平滑。
        paint.setFilterBitmap(true);


        canvas.drawRect(100, 100, 400, 300, paint);
        /*
        Paint 的两个色彩优化的方法：
        setDither(dither) ，设置抖动来优化色彩深度降低时的绘制效果；
        setFilterBitmap(filterBitmap) ，设置双线性过滤来优化 Bitmap 放大绘制的效果。
         */


        //next PaintPathEffect
    }
}
