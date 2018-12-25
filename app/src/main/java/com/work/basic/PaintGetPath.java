package com.work.basic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Title: PaintGetPath
 * <p>
 * Description:获取绘制的 Path
 * </p>
 *
 * @author Changbao
 * @date 2018/12/21  14:31
 */
public class PaintGetPath extends View {
    public PaintGetPath(Context context) {
        super(context);
    }

    public PaintGetPath(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PaintGetPath(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(30);
        paint.setColor(Color.BLACK);

        Path path = new Path();
        path.moveTo(100, 100);
        path.lineTo(150, 150);
        path.lineTo(220, 80);
        path.lineTo(300, 100);
        //给Path加上效果
        DashPathEffect dashPathEffect = new DashPathEffect(new float[]{10, 20, 30, 15}, 10);
        paint.setPathEffect(dashPathEffect);
        //正常画路径
        //canvas.drawPath(path, paint);

        //1 getFillPath(Path src, Path dst)
        //效果图：https://ws3.sinaimg.cn/large/006tNc79ly1fig7ggbut0j30rw0me76k.jpg
        /*
        原理：
        实际 Path」。所谓实际 Path ，指的就是 drawPath() 的绘制内容的轮廓，要算上线条宽度和设置的 PathEffect。
        默认情况下（线条宽度为 0、没有 PathEffect），原 Path 和实际 Path 是一样的；
        而在线条宽度不为 0 （并且模式为 STROKE 模式或 FLL_AND_STROKE ），
        或者设置了 PathEffect 的时候，实际 Path 就和原 Path 不一样了
        API：
        通过 getFillPath(src, dst) 方法就能获取这个实际 Path。
        方法的参数里，src 是原 Path ，而 dst 就是实际 Path 的保存位置。
        getFillPath(src, dst) 会计算出实际 Path，然后把结果保存在 dst 里。
         */
        Path realPath = new Path();
        paint.getFillPath(path, realPath);
        paint.setColor(Color.parseColor("#80FF0000"));
        canvas.drawPath(realPath, paint);

        //2 getTextPath
        //效果图：https://ws2.sinaimg.cn/large/006tNc79ly1fig7gs0dc1j30i005mq3f.jpg
        /*
        原理：
        文字的绘制，虽然是使用 Canvas.drawText() 方法，但其实在下层，文字信息全是被转化成图形，对图形进行绘制的。
        getTextPath() 方法，获取的就是目标文字所对应的  Path 。这个就是所谓「文字的 Path」。
         */
        String text = "Hello HenCoder 文字的绘制效果";

        paint.reset();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(0);
        paint.setTextSize(50);
        paint.setColor(Color.BLACK);
        //canvas.drawText(text, 100, 200, textPaint);

        Path textPath = new Path();
        paint.getTextPath(text, 0, text.length() - 1, 100, 250, textPath);
        canvas.drawPath(textPath, paint);

        /*
        这两个方法， getFillPath() 和 getTextPath() ，就是获取绘制的 Path 的方法。
        之所以把它们归类到「效果」类方法，是因为它们主要是用于图形和文字的装饰效果的位置计算，
        比如自定义的下划线效果。

        效果图：https://ws3.sinaimg.cn/large/006tNc79ly1fig7h4hk1kj30d506q3yw.jpg
         */



        /*
        Paint 初始化类
         */
        /*Paint.reset*/
        //【注】 重置 Paint 的所有属性为默认值。相当于重新 new 一个，不过性能当然高一些啦。
        // paint.reset();

        /*Paint.set*/
        //【这两种方式效果相同】 setClassVariablesFrom
        //Paint newPaint=new Paint(paint);
        //newPaint.set(paint);

        /*Paint.setFlags*/
        //paint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        //这行代码，和下面这两行是等价的：
        //paint.setAntiAlias(true);
        //paint.setDither(true);

        /*
        这些就是 Paint 的四类方法：颜色类、效果类、文字绘制相关以及初始化类。
        其中颜色类、效果类和初始化类都已经在这节里面讲过了，剩下的一类——文字绘制类，下一节单独讲。
         */
    }
}
