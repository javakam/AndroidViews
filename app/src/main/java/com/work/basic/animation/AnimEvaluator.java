package com.work.basic.animation;

import android.animation.TypeEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Title: AnimEvaluator
 * <p>
 * Description:  https://hencoder.com/ui-1-7/
 * </p>
 *
 * @author Changbao
 * @date 2019/1/3  17:51
 */
public class AnimEvaluator extends View {
    private int circleColor = Color.LTGRAY;

    public AnimEvaluator(Context context) {
        super(context);
    }

    public AnimEvaluator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimEvaluator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(circleColor);
        canvas.drawCircle(200,200,100,paint);
    }

    public int getCircleColor() {
        return circleColor;
    }

    public void setCircleColor(int circleColor) {
        this.circleColor = circleColor;
    }

    private class CustomEvaluate implements TypeEvaluator<Integer> {

        @Override
        public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
            return null;
        }
    }
}
