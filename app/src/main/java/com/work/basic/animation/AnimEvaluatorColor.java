package com.work.basic.animation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * Title: AnimEvaluatorColor
 * <p>
 * Description:  https://hencoder.com/ui-1-7/
 * </p>
 *
 * @author Changbao
 * @date 2019/1/3  17:51
 */
public class AnimEvaluatorColor extends View {
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private int circleColor = Color.LTGRAY;
    private String title;
    private String content;

    public AnimEvaluatorColor(Context context) {
        super(context);
    }

    public AnimEvaluatorColor(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimEvaluatorColor(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //1 画圆
        paint.setColor(circleColor);
        canvas.drawCircle(90, 130, 90, paint);

        //2 写标题
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setTextSize(25);
        if (!TextUtils.isEmpty(title)) {
            // canvas.drawText(content, 0, 30, textPaint);//不支持换行
            canvas.save();
            StaticLayout staticLayout = new StaticLayout(title, textPaint, getMeasuredWidth() - getPaddingRight(),
                    Layout.Alignment.ALIGN_LEFT, 1F, 0F, false);
            staticLayout.draw(canvas);
            canvas.restore();
        }

        //3 写圆内文案
        if (!TextUtils.isEmpty(content)) {
            canvas.drawText(content, 70, 140, textPaint);
        }
    }

    public int getCircleColor() {
        return circleColor;
    }

    public void setCircleColor(int circleColor) {
        this.circleColor = circleColor;
        invalidate();
    }

    public void setTitle(String title) {
        this.title = title;
        invalidate();
    }
    public void setContent(String content) {
        this.content = content;
        invalidate();
    }
}
