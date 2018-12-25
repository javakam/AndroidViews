package com.work.basic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.work.R;

/**
 * Title: CanvasSupportClip
 * <p>
 * Description:范围裁切  https://hencoder.com/ui-1-4/
 * 范围裁切有两个方法： clipRect() 和 clipPath()。裁切方法之后的绘制代码，都会被限制在裁切范围内。
 * </p>
 *
 * @author Changbao
 * @date 2018/12/24  14:11
 */
public class CanvasClip extends View {
    public CanvasClip(Context context) {
        super(context);
    }

    public CanvasClip(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CanvasClip(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //1 clipRect()
        Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.maps)).getBitmap();
        canvas.drawBitmap(bitmap, 0, 0, paint);//原图

        //记得要加上 Canvas.save() 和 Canvas.restore() 来及时恢复绘制范围
        canvas.save();
        Point point = new Point(250, 0);
        canvas.clipRect(point.x + 50, point.y + 60, point.x + 90, point.y + 120);
        canvas.drawBitmap(bitmap, point.x, point.y, paint);
        canvas.restore();

        //2 clipPath()
        //其实和 clipRect() 用法完全一样，只是把参数换成了 Path ，所以能裁切的形状更多一些
        point = new Point(550, 90);
        Path path = new Path();
        path.moveTo(point.x, point.y);
        path.addCircle(point.x, point.y, 80, Path.Direction.CW);
        canvas.save();
        canvas.clipPath(path);
        canvas.drawBitmap(bitmap, 450, 0, paint);
        canvas.restore();

    }
}
