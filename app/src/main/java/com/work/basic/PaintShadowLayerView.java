package com.work.basic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Title: LineShadowView
 * <p>
 * Description:阴影效果分割线
 * </p>
 *
 * @author Changbao
 * @date 2018/12/14  17:22
 */
public class PaintShadowLayerView extends View {
    public PaintShadowLayerView(Context context) {
        super(context);
    }

    public PaintShadowLayerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PaintShadowLayerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setLayerType(LAYER_TYPE_SOFTWARE, null);//对单独的View在运行时阶段禁用硬件加速
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);

        /*
        setShadowLayer 在之后的绘制内容下面加一层阴影。
        构造器中， radius 是阴影的模糊范围； dx dy 是阴影的偏移量； shadowColor 是阴影的颜色。
        如果要清除阴影层，使用 clearShadowLayer() 。

        注意：
        在硬件加速开启的情况下， setShadowLayer() 只支持文字的绘制，文字之外的绘制必须关闭硬件加速才能正常绘制阴影。
        如果 shadowColor 是半透明的，阴影的透明度就使用 shadowColor 自己的透明度；
        而如果 shadowColor 是不透明的，阴影的透明度就使用 paint 的透明度。
         */
        paint.setShadowLayer(5F, 25F, 30F, Color.GREEN);
        //paintDash.clearShadowLayer();
        canvas.drawLine(0, 0, getMeasuredWidth() - getPaddingLeft() - getPaddingRight()
                , getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), paint);


        paint.setTextSize(20);
        canvas.drawText("Paint.setShadowLayer", 100, 100, paint);
        canvas.drawCircle(150, 150, 50, paint);
    }
}
