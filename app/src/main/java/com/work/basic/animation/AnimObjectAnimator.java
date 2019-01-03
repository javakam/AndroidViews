package com.work.basic.animation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.work.R;

/**
 * Title: AnimObjectAnimator
 * <p>
 * Description:属性动画 ObjectAnimator
 * </p>
 *
 * @author Changbao
 * @date 2019/1/3  14:11
 */
public class AnimObjectAnimator extends View {
    private float progress;

    public AnimObjectAnimator(Context context) {
        super(context);
    }

    public AnimObjectAnimator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimObjectAnimator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(20);

        Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.music)).getBitmap();
        canvas.drawBitmap(bitmap, 30, 50, paint);

        RectF arcRectF = new RectF(230, 30, 500, 260);
        canvas.drawArc(arcRectF, 135, progress * 2.7f, false, paint);

    }
}
