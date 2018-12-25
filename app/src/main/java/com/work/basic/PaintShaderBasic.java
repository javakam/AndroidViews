package com.work.basic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Title: LinearShaderView
 * <p>
 * Description:
 * </p>
 *
 * @author Changbao
 * @date 2018/12/13  13:50
 */
public class PaintShaderBasic extends View {
    public PaintShaderBasic(Context context) {
        super(context);
    }

    public PaintShaderBasic(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PaintShaderBasic(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();

        //LinearGradient
        Shader shaderLinear = new LinearGradient(0, 0, 300, 300,
                Color.parseColor("#E91E63"),
                Color.parseColor("#2196F3"), Shader.TileMode.CLAMP);
        paint.setShader(shaderLinear);
        canvas.drawCircle(150, 150, 100, paint);

        //RadialGradient
        Shader shaderRadial = new RadialGradient(280, 280, 100,
                Color.parseColor("#E91E63"),
                Color.parseColor("#2196F3"), Shader.TileMode.CLAMP);
        paint.setShader(shaderRadial);
        canvas.drawCircle(280, 280, 100, paint);
    }
}