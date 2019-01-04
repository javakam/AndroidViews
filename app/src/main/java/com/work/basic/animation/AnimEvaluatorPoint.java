package com.work.basic.animation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * Title: AnimEvaluatorPoint
 * <p>
 * Description:  https://hencoder.com/ui-1-7/
 * </p>
 *
 * @author Changbao
 * @date 2019/1/3  17:51
 */
public class AnimEvaluatorPoint extends View {
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

    private PointF position = new PointF();
    private String content;

    public AnimEvaluatorPoint(Context context) {
        super(context);
    }

    public AnimEvaluatorPoint(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimEvaluatorPoint(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //1 画圆
        paint.setColor(Color.CYAN);
        float radius = 30;
        float innerPaddingLeft = 50;
        float innerPaddingTop = 50;
        float deltaX = (getWidth() - innerPaddingLeft - radius * 2) * position.x;
        float deltaY = (getHeight() - innerPaddingTop - radius * 2) * position.y;
        canvas.drawCircle(innerPaddingLeft + radius + deltaX
                , innerPaddingTop + radius + deltaY, radius, paint);
        //Log.w("123", "AnimEvaluatorPoint 画圆：" + position.toString());
        /*
        Log 数据：

        PointF(0.00625, 0.00625)
        PointF(0.027125, 0.027125)
        PointF(0.0295, 0.0295)
        PointF(0.033625, 0.033625)
        PointF(0.03575, 0.03575)
        PointF(0.037875, 0.037875)
        PointF(0.04, 0.04)
        PointF(0.042125, 0.042125)
         */
        //canvas.drawCircle(deltaX, deltaY, radius, paint);

        //2 写字
        if (!TextUtils.isEmpty(content)) {
            // canvas.drawText(content, 0, 30, textPaint);//不支持换行
            canvas.save();
            textPaint.setStyle(Paint.Style.STROKE);
            textPaint.setTextSize(25);
            // java.lang.NullPointerException:
            // Attempt to invoke interface method 'int java.lang.CharSequence.length()' on a null object reference
            StaticLayout staticLayout = new StaticLayout(content, textPaint, getMeasuredWidth(),
                    Layout.Alignment.ALIGN_LEFT, 1F, 0F, false);
            staticLayout.draw(canvas);
            canvas.restore();

        }

    }

    public PointF getPosition() {
        return position;
    }

    public void setPosition(PointF position) {
        if (position != null) {
            this.position.set(position);
            invalidate();
        }
    }

    public void setDescription(String content) {
        this.content = content;
        invalidate();
    }
}
