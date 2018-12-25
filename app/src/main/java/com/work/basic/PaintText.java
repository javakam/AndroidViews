package com.work.basic;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Title: PaintText
 * <p>
 * Description:
 * </p>
 *
 * @author Changbao
 * @date 2018/12/16  18:41
 */
public class PaintText extends View {
    public PaintText(Context context) {
        super(context);
    }

    public PaintText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PaintText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
