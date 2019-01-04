package com.work.basic.animation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Title: AnimPvhKeyframeProgress
 * <p>
 * Description: PropertyValuesHolder.ofKeyframe() 把同一个属性拆分
 * <p>
 * 效果：https://ws4.sinaimg.cn/large/006tNc79ly1fjfig8edhmg30ck07046i.gif
 * </p>
 *
 * @author Changbao
 * @date 2019/1/4  14:38
 */
public class AnimPvhKeyframeProgress extends View {

    private float progress;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);

    public AnimPvhKeyframeProgress(Context context) {
        this(context, null, 0);
    }

    public AnimPvhKeyframeProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimPvhKeyframeProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(15);
        paint.setColor(Color.CYAN);

        paintText.setTextSize(25);
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //圆心
        PointF center = new PointF(getHeight() / 2, getHeight() / 2);
        //半径
        float radius = 150F;

        RectF rectF = new RectF(center.x - radius, center.y - radius, center.x + radius, center.y + radius);
        //Log.w("123", "进度：" + (int) Math.ceil(progress) + "     " + getSweepAngle());

        canvas.drawArc(rectF, 0, getSweepAngle(), false, paint);

        //文本居中！
//        Paint.FontMetrics fontMetrics = new Paint.FontMetrics();
//        paint.getFontMetrics(fontMetrics);
//        fontMetrics.ascent + fontMetrics.descent

        //【注】不能直接使用 (int)progress ，因为达不到100，即100%进度！
        canvas.drawText("当前进度" + Math.round(progress) + "%",
                center.x, center.y - (paintText.ascent() + paintText.descent()) / 2, paintText);
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    private float getSweepAngle() {
        return progress * 3.6F;
    }
}