package com.work.basic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.work.R;

/**
 * Title: CanvasTransformMatrix
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
public class CanvasTransformMatrix extends View {
    public CanvasTransformMatrix(Context context) {
        super(context);
    }

    public CanvasTransformMatrix(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CanvasTransformMatrix(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.maps)).getBitmap();
       /*
        二、使用 Matrix 来做常见变换

        Matrix 做常见变换的方式：
        创建 Matrix 对象；
        调用 Matrix 的 pre/postTranslate/Rotate/Scale/Skew() 方法来设置几何变换；
        使用 Canvas.setMatrix(matrix) 或 Canvas.concat(matrix) 来把几何变换应用到 Canvas。
         */
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.postTranslate(200, 30);
//        matrix.postRotate(45, bitmap.getWidth(), bitmap.getHeight());
        //倾斜
        matrix.postSkew(0f, -0.1f, bitmap.getWidth() / 2, bitmap.getHeight() / 2);

        canvas.save();
        canvas.concat(matrix);
        canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.ANTI_ALIAS_FLAG));
        canvas.restore();

        /*
        把 Matrix 应用到 Canvas 有两个方法： Canvas.setMatrix(matrix) 和 Canvas.concat(matrix)。

        Canvas.setMatrix(matrix)：
            用 Matrix 直接替换 Canvas 当前的变换矩阵，即抛弃 Canvas 当前的变换，
            改用 Matrix 的变换（注：根据下面评论里以及我在微信公众号中收到的反馈，不同的系统中
            setMatrix(matrix) 的行为可能不一致，所以还是尽量用 concat(matrix) 吧）；

        Canvas.concat(matrix)：【推荐】
            用 Canvas 当前的变换矩阵和 Matrix 相乘，即基于 Canvas 当前的变换，叠加上  Matrix 中的变换。
         */


        /*
        三、使用 Matrix 来做自定义变换

        Matrix 的自定义变换使用的是 setPolyToPoly() 方法。
         */
        //Matrix.setPolyToPoly(float[] src, int srcIndex, float[] dst, int dstIndex, int pointCount) 用点对点映射的方式设置变换
        int left = 300, top = 300, right = left + bitmap.getWidth(), bottom = top + bitmap.getHeight();
         /*
        "Z"型走位。。。
         */
        float[] srcPoints = {left, top, right, top, left, bottom, right, bottom};
        float[] desPoints = {left - 15, top + 50, right + 15, top - 50, left + 15, bottom, right - 15, bottom};
        matrix.reset();
        matrix.setPolyToPoly(srcPoints, 0, desPoints, 0, 4);

        canvas.save();
        canvas.concat(matrix);
        canvas.drawBitmap(bitmap, left, top, new Paint());
        canvas.restore();

        /*
        参数里，src 和 dst 分别是源点集合、目标点集合；
        srcIndex 和 dstIndex 是第一个点的偏移；
        pointCount 是采集的点的个数（个数不能大于 4，因为大于 4 个点就无法计算变换了）。
         */

        /**
         * {@link CanvasTransformMatrixPractice1 模拟3D翻转卡片}
         */

        /*
         使用 Camera 来做三维变换
         Camera 的三维变换有三类：旋转、平移、移动相机。
         */

    }
}
