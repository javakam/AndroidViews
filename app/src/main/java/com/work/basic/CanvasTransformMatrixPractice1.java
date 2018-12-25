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
 * Title: CanvasTransformMatrixPractice
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
public class CanvasTransformMatrixPractice1 extends View {
    public CanvasTransformMatrixPractice1(Context context) {
        super(context);
    }

    public CanvasTransformMatrixPractice1(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CanvasTransformMatrixPractice1(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.maps)).getBitmap();
        /*
        Matrix 的自定义变换使用的是 setPolyToPoly() 方法。
        Matrix.setPolyToPoly(float[] src, int srcIndex, float[] dst, int dstIndex, int pointCount)
        用点对点映射的方式设置变换
         */
        Paint textPaint = new Paint();
        textPaint.setTextSize(30);
        textPaint.setFakeBoldText(true);
        canvas.drawText("Matrix自定义变换-Matrix.setPolyToPoly 模拟3D翻转卡片", 0, 30, textPaint);

        // "Z"型走位。。。
        int left = 150, top = 150, right = left + bitmap.getWidth(), bottom = top + bitmap.getHeight();
        int widthChange = 35, heightChange = 40;

        float[] srcPoints = {left, top, right, top, left, bottom, right, bottom};
        float[] desPoints = {left + widthChange, top + heightChange, right - widthChange, top + heightChange,
                left - widthChange, bottom - heightChange, right + widthChange, bottom - heightChange};


        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.setPolyToPoly(srcPoints, 0, desPoints, 0, 4);
        canvas.save();
        canvas.concat(matrix);
        canvas.drawBitmap(bitmap, left, top, new Paint(Paint.ANTI_ALIAS_FLAG));
        canvas.restore();

        /*
        参数里，src 和 dst 分别是源点集合、目标点集合；
        srcIndex 和 dstIndex 是第一个点的偏移；
        pointCount 是采集的点的个数（个数不能大于 4，因为大于 4 个点就无法计算变换了）。
         */
    }
}
