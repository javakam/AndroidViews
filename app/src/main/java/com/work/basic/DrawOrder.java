package com.work.basic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Title: DrawOrder
 * <p>
 * Description:绘制顺序 https://hencoder.com/ui-1-5/
 * </p>
 *
 * @author Changbao
 * @date 2019/1/3  11:54
 */
public class DrawOrder extends View {

    public DrawOrder(Context context) {
        this(context, null);
    }

    public DrawOrder(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawOrder(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(50);
        canvas.drawText("绘制顺序", 40, 40, paint);
    }
}
