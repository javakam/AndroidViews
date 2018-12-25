package com.work.basic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Title: CanvasDrawText
 * <p>
 * Description:Canvas 的文字绘制方法有三个：drawText() drawTextRun() 和 drawTextOnPath()。
 * </p>
 *
 * @author Changbao
 * @date 2018/12/21  15:46
 */
public class DrawTextCanvas extends View {
    public DrawTextCanvas(Context context) {
        super(context);
    }

    public DrawTextCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawTextCanvas(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(50);
        paint.setStyle(Paint.Style.STROKE);

        String text = "Canvas.drawText 绘制文字";

        //1 drawText
        /*
        参数： text 是文字内容，x 和 y 是文字的坐标。
        但需要注意：这个坐标并不是文字的左上角，而是一个与左下角比较接近的位置。

        效果图：https://ws3.sinaimg.cn/large/52eb2279ly1fig60bobb0j20ek04dwex.jpg
         */
        canvas.drawText(text, 0, 0, paint);//偏离baseline的文本
        canvas.drawText(text, 50, 50, paint);

        /*
        drawText() 参数中的 y ，指的是文字的基线(baseline)的位置。

        https://ws3.sinaimg.cn/large/52eb2279ly1fig6137j5sj20a502rglw.jpg
         */

        //2 drawTextRun()  声明：这个方法对中国人没用。 API 23 新加入的方法
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String textRun = "Canvas.drawTextRun 绘制文字";
            canvas.drawTextRun(
                    textRun, 0, textRun.length(), 0, textRun.length(), 50, 130, true, paint);
        }

        //3 drawTextOnPath() 沿着一条 Path 来绘制文字。这是一个耍杂技的方法。
        //https://ws3.sinaimg.cn/large/52eb2279ly1fig62s8op9j20dp065dgg.jpg
        String textPath = "Canvas.drawTextOnPath 沿着Path绘制文字";
        Path path = new Path();
        path.moveTo(50, 300);
        path.lineTo(150, 190);
        path.lineTo(230, 250);
        path.lineTo(380, 390);
        paint.setColor(Color.RED);
        //paint.setStrokeJoin(Paint.Join.ROUND);
        canvas.drawPath(path, paint);
        canvas.drawTextOnPath(textPath, path, 0, 0, paint);

        /*
        【注】记住一条原则： drawTextOnPath() 使用的 Path ，拐弯处全用圆角，别用尖角。

        drawTextOnPath(String text, Path path, float hOffset, float vOffset, Paint paint)
        参数里，需要解释的只有两个： hOffset 和 vOffset。
        它们是文字相对于 Path 的水平偏移量和竖直偏移量，利用它们可以调整文字的位置。
        例如你设置 hOffset 为 5， vOffset 为 10，文字就会右移 5 像素和下移 10 像素。
         */

        //4 StaticLayout android.text.Layout 的子类
        /*
        缺陷：
        Canvas.drawText() 只能绘制单行的文字，而不能换行
        1. 不能在 View 的边缘自动折行，到了 View 的边缘处，文字继续向后绘制到看不见的地方，而不是自动换行
        2. 不能在换行符 \n 处换行，在换行符 \n 的位置并没有换行，而只是加了个空格

         */
        TextPaint tp = new TextPaint();
        tp.setColor(Color.BLUE);
        tp.setStyle(Paint.Style.FILL);
        tp.setTextSize(40);
        String message = "paint,draw paint指用\n颜色画,如油画颜料、水彩或者水墨画," +
                "而draw 通常指用铅笔、钢笔或者\n粉笔画,后者一般并不涂上颜料。两动词的相应名词分别为p";
        StaticLayout staticLayout = new StaticLayout(message, tp, canvas.getWidth(),
                Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        canvas.translate(50,400);
        staticLayout.draw(canvas);
        /*
        构造方法：
        StaticLayout 的构造方法是 StaticLayout(CharSequence source, TextPaint paint, int width,
        Layout.Alignment align, float spacingmult, float spacingadd, boolean includepad)，

        参数：
        width 是文字区域的宽度，文字到达这个宽度后就会自动换行；
        align 是文字的对齐方向；
        spacingmult 是行间距的倍数，通常情况下填 1 就好；
        spacingadd 是行间距的额外增加值，通常情况下填 0 就好；
        includeadd 是指是否在文字上下添加额外的空间，来避免某些过高的字符的绘制出现越界。

        如果你需要进行多行文字的绘制，并且对文字的排列和样式没有太复杂的花式要求，那么使用  StaticLayout 就好。

        【注】需要指出的是这个layout是默认画在Canvas的(0,0)点的，
         如果需要调整位置只能在draw之前移Canvas的起始坐标canvas.translate(x,y);
         */

    }
}
