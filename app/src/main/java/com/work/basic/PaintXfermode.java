package com.work.basic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.work.R;
import com.work.utils.BitmapUtils;

/**
 * Title: PaintXfermode
 * <p>
 * Description:
 * </p>
 *
 * @author Changbao
 * @date 2018/12/13  18:15
 */
public class PaintXfermode extends View {
    public PaintXfermode(Context context) {
        super(context);
    }

    public PaintXfermode(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PaintXfermode(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);

        Bitmap rectBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.icon_paint_rect_yellow)).getBitmap();
        Bitmap circleBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.icon_paint_circle_blue)).getBitmap();
        rectBitmap = BitmapUtils.resizeBitmap(rectBitmap, 300, 200);
        circleBitmap = BitmapUtils.resizeBitmap(circleBitmap, 180, 180);


        //Xfermode 只有一个子类 PorterDuffXfermode ！
        //Xfermode 离屏缓存 (Off-screen Buffer) 通过使用离屏缓冲，把要绘制的内容单独绘制在缓冲层
        /*
        1 Canvas.saveLayer() 可以做短时的离屏缓冲。使用方法很简单，在绘制代码的前后各加一行代码，在绘制之前保存，绘制之后恢复 ;
        2 View.setLayerType() 是直接把整个 View 都绘制在离屏缓冲中。 setLayerType(LAYER_TYPE_HARDWARE) 是使用 GPU 来缓冲，
          setLayerType(LAYER_TYPE_SOFTWARE) 是直接直接用一个 Bitmap 来缓冲
        3 硬件加速 : https://developer.android.google.cn/guide/topics/graphics/hardware-accel#java
         */

        int layer = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);
        //或者 -> 推荐方式
        /*
        View.setLayerType() 是直接把整个 View 都绘制在离屏缓冲中。
        setLayerType(LAYER_TYPE_HARDWARE) 是使用 GPU 来缓冲，
        setLayerType(LAYER_TYPE_SOFTWARE) 是直接直接用一个 Bitmap 来缓冲。
         */
        //this.setLayerType(LAYER_TYPE_SOFTWARE, paint);

        //已有图形
        canvas.drawBitmap(rectBitmap, 100, 100, paint); // 画方
        //
        Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);
        paint.setXfermode(xfermode); // 设置 Xfermode
        canvas.drawBitmap(circleBitmap, 300, 180, paint); // 画圆
        paint.setXfermode(null); // 用完及时清除 Xfermode

        canvas.restoreToCount(layer);
        /*
         Paint 的第一类 API——关于颜色的三层设置：
         直接设置颜色的 API 用来给图形和文字设置颜色；
         setColorFilter() 用来基于颜色进行过滤处理；
         setXfermode() 用来处理源图像和 View 已有内容的关系。
         */
    }
}