package com.work.basic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.work.R;

/**
 * Title: CanvasTransformBasic
 * <p>
 * Description:几何变换  https://hencoder.com/ui-1-4/
 * 几何变换的使用大概分为三类：
 * <p>
 * 使用 Canvas 来做常见的二维变换；<br>
 * 使用 Matrix 来做常见和不常见的二维变换；<br>
 * 使用 Camera 来做三维变换。
 * </p>
 *
 * @author Changbao
 * @date 2018/12/24  14:11
 */
public class CanvasTransformBasic extends View {
    public CanvasTransformBasic(Context context) {
        super(context);
    }

    public CanvasTransformBasic(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CanvasTransformBasic(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.maps)).getBitmap();
        /*
        一、使用 Canvas 来做常见的二维变换：
         */
        //1 Canvas.translate(float dx, float dy) 平移  参数里的 dx 和 dy 表示横向和纵向的位移。
        canvas.save();
        canvas.translate(50, 20);
        canvas.drawBitmap(bitmap, 0, 0, new Paint());
        canvas.restore();

        //2 Canvas.rotate(float degrees, float px, float py) 旋转
        //参数里的 degrees 是旋转角度，单位是度（也就是一周有 360° 的那个单位），方向是顺时针为正向； px 和 py 是轴心的位置。
        canvas.save();
        //【注】不要用  canvas.rotate(45);
        canvas.rotate(45, 360 + bitmap.getWidth() / 2, 20 + bitmap.getHeight() / 2);
        canvas.drawBitmap(bitmap, 360, 20, new Paint());
        canvas.restore();

        //3 Canvas.scale(float sx, float sy, float px, float py) 缩放
        //参数里的 sx sy 是横向和纵向的放缩倍数； px py 是放缩的轴心。
        canvas.save();
        canvas.scale(1.2f, 0.8f, 10 + bitmap.getWidth() / 2, 260 + bitmap.getHeight() / 2);
        canvas.drawBitmap(bitmap, 10, 260, new Paint());
        canvas.restore();

        //4  skew(float sx, float sy) 错切  参数里的 sx 和 sy 是 x 方向和 y 方向的错切系数。
        canvas.save();
        canvas.skew(0.3f, 0.2f);
        canvas.drawBitmap(bitmap, 360, 300, new Paint());
        canvas.restore();


        /**
         {@link CanvasTransformMatrix 使用 Matrix 来做变换}
         */
    }
}
