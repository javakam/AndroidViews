package com.work.basic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.work.R;

/**
 * Title: ShaderView
 * <p>
 * Description:ShaderView
 * </p>
 *
 * @author Changbao
 * @date 2018/12/13  11:19
 */
public class PaintShaderCompose extends View {
    private Point mCircleCenter;
    private int mCircleRadius;
    //优化处理：直接均匀渐变，会让渐变盖住一部分脸，让脸显得朦胧，需要创建颜色和位置数组把中心脸的部分露出来
    private static final int[] mColors = new int[]{
            Color.TRANSPARENT, Color.TRANSPARENT, Color.WHITE
    };
    private static final float[] mPositions = new float[]{
            0, 0.5f, 1f
    };

    public PaintShaderCompose(Context context) {
        super(context);
    }

    public PaintShaderCompose(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PaintShaderCompose(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.setBackgroundColor(Color.parseColor("#FFEFD5"));
        Paint mBitmapPaint = new Paint();
        //圆心
        int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        mCircleRadius = width / 2;
        mCircleCenter = new Point(mCircleRadius, mCircleRadius);

        //创建位图
        Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.icon_paint_man)).getBitmap();
        //将图scale成我们想要的大小
        bitmap = Bitmap.createScaledBitmap(bitmap, width, width, false);

        //1创建位图渲染
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        //2.1创建环形渐变
//        RadialGradient radialGradient = new RadialGradient(mCircleCenter.x, mCircleCenter.y, mCircleRadius
//                , Color.TRANSPARENT, Color.WHITE, Shader.TileMode.MIRROR);
        //2.2创建环形渐变
        //优化处理：直接均匀渐变，会让渐变盖住一部分脸，让脸显得朦胧，需要创建颜色和位置数组把中心脸的部分露出来
        RadialGradient radialGradient = new RadialGradient(mCircleCenter.x, mCircleCenter.y, mCircleRadius,
                mColors, mPositions, Shader.TileMode.MIRROR);

        //3创建组合渐变，由于直接按原样绘制就好，所以选择Mode.SRC_OVER
        /*
        注意：如果使用两个 BitmapShader 来作为 ComposeShader() 的参数，
        而 ComposeShader() 在硬件加速下是不支持两个相同类型的 Shader 的， 所以这里也需要关闭硬件加速才能看到效果。
         */
        ComposeShader composeShader = new ComposeShader(bitmapShader, radialGradient, PorterDuff.Mode.SRC_OVER);

        //将组合渐变设置给paint
        mBitmapPaint.setShader(composeShader);
        //onDraw中绘制圆形
        canvas.drawCircle(mCircleCenter.x, mCircleCenter.y, mCircleRadius, mBitmapPaint);

    }
}
