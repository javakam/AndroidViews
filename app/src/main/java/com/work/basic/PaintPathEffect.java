package com.work.basic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.DiscretePathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.SumPathEffect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Title: PaintPathEffect
 * <p>
 * Description:PathEffect 来给图形的轮廓设置效果  https://hencoder.com/ui-1-2/
 * <p>
 * Android 中的 6 种 PathEffect。
 * PathEffect 分为两类，单一效果的 CornerPathEffect DiscretePathEffect DashPathEffect PathDashPathEffect ，
 * 和组合效果的 SumPathEffect ComposePathEffect。
 * </p>
 *
 * @author Changbao
 * @date 2018/12/14  13:58
 */
public class PaintPathEffect extends View {
    public PaintPathEffect(Context context) {
        super(context);
    }

    public PaintPathEffect(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PaintPathEffect(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);//
        paint.setStrokeWidth(5);

        //1 CornerPathEffect 把所有拐角变成圆角
        //CornerPathEffect(float radius) 的参数 radius 是圆角的半径
        PathEffect cornerPathEffect = new CornerPathEffect(20);

        //2 DiscretePathEffect
        //把线条进行随机的偏离，让轮廓变得乱七八糟。乱七八糟的方式和程度由参数决定。
        /*
        DiscretePathEffect 具体的做法是，把绘制改为使用定长的线段来拼接，并且在拼接的时候对路径进行随机偏离。
        它的构造方法 DiscretePathEffect(float segmentLength, float deviation) 的两个参数中，
        segmentLength 是用来拼接的每个线段的长度， deviation 是偏离量。
         */
        PathEffect discretePathEffect = new DiscretePathEffect(20, 5);

        //3 DashPathEffect 使用虚线来绘制线条
        /*
        构造方法 DashPathEffect(float[] intervals, float phase) 中， 第一个参数 intervals 是一个数组，
        它指定了虚线的格式：数组中元素必须为偶数（最少是 2 个），按照「画线长度、空白长度、画线长度、空白长度」……的顺序排列，
        例如上面代码中的 20, 5, 10, 5 就表示虚线是按照「画 20 像素、空 5 像素、画 10 像素、空 5 像素」的模式来绘制；
        第二个参数 phase 是虚线的偏移量。
         */
        PathEffect dashPathEffect = new DashPathEffect(new float[]{20, 10, 5, 8}, 10);

        //4 PathDashPathEffect
        //这个方法比 DashPathEffect 多一个前缀 Path ，所以顾名思义，它是使用一个 Path 来绘制「虚线」。
        /*
        构造方法 PathDashPathEffect(Path shape, float advance, float phase, PathDashPathEffect.Style style) 中，
        shape 参数是用来绘制的 Path ；
        advance 是两个相邻 shape 段之间的间隔，不过注意，这个间隔是两个 shape 段起点的间隔，而不是前一个终点和后一个起点的距离；
        phase 和 DashPathEffect 中一样，是虚线的偏移；
        最后一个参数 style，是用来指定拐弯改变的时候 shape 的转换方式。

        style 的类型为  PathDashPathEffect.Style ，是一个 enum ，具体有三个值：
        TRANSLATE：位移  ROTATE：旋转  MORPH：变体
         */
        Path dashPath = new Path(); // 使用一个三角形来做 dash
        dashPath.moveTo(0, 30);
        dashPath.lineTo(20, 0);
        dashPath.lineTo(40, 30);
        dashPath.close();
        PathEffect pathDashPathEffect = new PathDashPathEffect(dashPath, 40, 0,
                PathDashPathEffect.Style.TRANSLATE);

        //5 SumPathEffect
        //这是一个组合效果类的 PathEffect 。它的行为特别简单，就是分别按照两种 PathEffect 分别对目标进行绘制。
        PathEffect sumPathEffect = new SumPathEffect(dashPathEffect, discretePathEffect);

        //6 ComposePathEffect
        //这也是一个组合效果类的 PathEffect 。不过它是先对目标 Path 使用一个 PathEffect，
        //然后再对这个【改变后】的 Path 使用另一个 PathEffect。
        /*
        构造方法 ComposePathEffect(PathEffect outerpe, PathEffect innerpe) 中的两个 PathEffect 参数，
        innerpe 是先应用的， outerpe 是后应用的。
        所以上面的代码就是「先偏离，再变虚线」。而如果把两个参数调换，就成了「先变虚线，再偏离」。
         */
        //先用后面的 PathEffect ，再用前面的。
        PathEffect composePathEffect = new ComposePathEffect(dashPathEffect, discretePathEffect);




        /*
        【注】：设置不同的 PathEffect 子类 演示效果
         */
        paint.setPathEffect(composePathEffect);
        Path path = new Path();
        path.moveTo(160, 160);
        path.lineTo(200, 0);
        path.lineTo(250, 100);
        path.lineTo(360, 50);
        path.lineTo(400, 200);
        path.lineTo(450, 300);
        path.lineTo(600, 380);
        path.lineTo(700, 100);
        canvas.drawPath(path, paint);




          /*
        注意： PathEffect 在有些情况下不支持硬件加速，需要关闭硬件加速才能正常使用：
        Canvas.drawLine() 和 Canvas.drawLines() 方法画直线时，setPathEffect() 是不支持硬件加速的；
        PathDashPathEffect 对硬件加速的支持也有问题，所以当使用 PathDashPathEffect 的时候，最好也把硬件加速关了。

        剩下的两个效果类方法：setShadowLayer() 和 setMaskFilter() ，它们和前面的效果类方法有点不一样：
        它们设置的是「附加效果」，也就是基于在绘制内容的额外效果。
         */

    }
}
