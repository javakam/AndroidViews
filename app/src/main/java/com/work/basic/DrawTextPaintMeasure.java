package com.work.basic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Title: DrawTextPaintMeasure
 * <p>
 * Description:文字尺寸的测量
 * <p>
 * 不论是文字，还是图形或 Bitmap，只有知道了尺寸，才能更好地确定应该摆放的位置。
 * 由于文字的绘制和图形或 Bitmap 的绘制比起来，尺寸的计算复杂得多，所以它有一整套的方法来计算文字尺寸。
 * </p>
 *
 * @author Changbao
 * @date 2018/12/24  9:49
 */
public class DrawTextPaintMeasure extends View {
    public DrawTextPaintMeasure(Context context) {
        super(context);
    }

    public DrawTextPaintMeasure(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawTextPaintMeasure(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //1 float getFontSpacing()
        //获取推荐的行距。即推荐的两行文字的 baseline 的距离。这个值是系统根据文字的字体和字号自动计算的。
        //https://ws3.sinaimg.cn/large/52eb2279ly1fig66d27efj20ev06j401.jpg
        String texts[] = new String[]{"Hello", "你好", "柯南"};
        Paint paint = getPaint();
        int spacing = 100;
        canvas.drawText(texts[0], 50, spacing, paint);
        canvas.drawText(texts[1], 50, spacing + paint.getFontSpacing(), paint);
        canvas.drawText(texts[2], 50, spacing + paint.getFontSpacing() * 2, paint);

        //2 FontMetrics getFontMetrics()  获取 Paint 的 FontMetrics。【重】
        //https://hencoder.com/ui-1-3
        //效果：https://ws3.sinaimg.cn/large/52eb2279ly1fig66iud3gj20ik0bn41l.jpg

        /*
        FontMetrics 是个相对专业的工具类，它提供了几个文字排印方面的数值：ascent, descent, top, bottom,  leading。

        FontMetrics.ascent： float 类型。
        FontMetrics.descent：float 类型。
        FontMetrics.top：    float 类型。
        FontMetrics.bottom： float 类型。
        FontMetrics.leading：float 类型。

        另外，ascent 和 descent 这两个值还可以通过 Paint.ascent() 和 Paint.descent() 来快捷获取。
         */
        Log.e("123", "Paint.ascent() : " + paint.ascent() + "  Paint.descent(): " + paint.descent());
        //paint.ascent() : -41.748047   paint.descent(): 10.986328

        //API介绍：
        /*
        getFontMetrics() 的返回值是 FontMetrics 类型。
        它还有一个重载方法  getFontMetrics(FontMetrics fontMetrics) ，计算结果会直接填进传入的 FontMetrics 对象，
        而不是重新创建一个对象。这种用法在需要频繁获取 FontMetrics 的时候性能会好些。

        另外，这两个方法还有一对同样结构的对应的方法 getFontMetricsInt() 和 getFontMetricsInt(FontMetricsInt fontMetrics) ，
        用于获取 FontMetricsInt 类型的结果。
         */
        Paint.FontMetrics fontMetrics = new Paint.FontMetrics();
        paint.getFontMetrics(fontMetrics);
        Log.w("123", "FontMetrics: " + fontMetrics.top + "   " + fontMetrics.ascent + "    " + fontMetrics.descent + "     " +
                fontMetrics.bottom + "    " + fontMetrics.leading);
        //FontMetrics: -47.526855   -41.748047    10.986328     12.194824    0.0

        //FontMetrics 和 getFontSpacing() 对比
        /*
        从定义可以看出，上图中两行文字的 font spacing (即相邻两行的 baseline 的距离) 可以通过
        bottom - top + leading (top 的值为负，前面刚说过，记得吧？）来计算得出。

        但你真的运行一下会发现， bottom - top + leading 的结果是要大于 getFontSpacing() 的返回值的。

        两个方法计算得出的 font spacing 竟然不一样？
        这并不是 bug，而是因为 getFontSpacing() 的结果并不是通过 FontMetrics 的标准值计算出来的，
        而是另外计算出来的一个值，它能够做到在两行文字不显得拥挤的前提下缩短行距，以此来得到更好的显示效果。
        所以如果你要对文字手动换行绘制，多数时候应该选取 getFontSpacing() 来得到行距，不但使用更简单，显示效果也会更好。
         */

        //3 getTextBounds(String text, int start, int end, Rect bounds) 获取文字的显示范围。
        //参数里，text 是要测量的文字，start 和 end 分别是文字的起始和结束位置，
        //bounds 是存储文字显示范围的对象，方法在测算完成之后会把结果写进 bounds。
        //https://ws3.sinaimg.cn/large/52eb2279ly1fig66pdyg4j20ct02tmxf.jpg
        int offsetX = 50, offsetY = 300;
        String text = "蓝蓝的天上马儿跑";
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText(text, offsetX, offsetY, paint);//写字

        Rect bounds = new Rect();//存储文字显示范围
        paint.getTextBounds(text, 0, text.length(), bounds);
        Log.e("123", bounds.flattenToString() + "  字宽：" + bounds.width() + "  字高: " + bounds.height());
        //1 -37 342 5  字宽：341  字高: 42
        bounds.left += offsetX;
        bounds.top += offsetY;
        bounds.right += offsetX;
        bounds.bottom += offsetY;
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(bounds, paint);//画边界

        //4 float measureText(String text) 测量文字的宽度并返回。
        //https://ws3.sinaimg.cn/large/52eb2279ly1fig671on56j20or04a0te.jpg
        offsetX = 50;
        offsetY = 350;
        text = "noodles蓝天下面马吃草N";
        canvas.drawText(text, offsetX, offsetY, paint);//写字

        float textWidth = paint.measureText(text);
        Log.w("123", "measureText(text) : " + textWidth);
        //paint.measureText(text) : 344.0
        Paint redLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        redLinePaint.setColor(Color.RED);
        canvas.drawLine(offsetX, offsetY, offsetX + textWidth, offsetY, redLinePaint);//画底线

        //getTextBounds 和 measureText 区别：
        /*
        如果你用代码分别使用 getTextBounds() 和 measureText() 来测量文字的宽度，
        你会发现 measureText() 测出来的宽度总是比 getTextBounds() 大一点点。这是因为这两个方法其实测量的是两个不一样的东西。

        getTextBounds: 它测量的是文字的显示范围（关键词：显示）。
        形象点来说，你这段文字外放置一个可变的矩形，然后把矩形尽可能地缩小，一直小到这个矩形恰好紧紧包裹住文字，
        那么这个矩形的范围，就是这段文字的 bounds。

        measureText(): 它测量的是文字绘制时所占用的宽度（关键词：占用）。
        前面已经讲过，一个文字在界面中，往往需要占用比他的实际显示宽度更多一点的宽度，以此来让文字和文字之间保留一些间距，
        不会显得过于拥挤。
        上面的这幅图，我并没有设置 setLetterSpacing() ，这里的 letter spacing 是默认值 0，
        但你可以看到，图中每两个字母之间都是有空隙的。
        【注】另外，下方那条用于表示文字宽度的横线，
        在左边超出了第一个字母 H 一段距离的，在右边也超出了最后一个字母 r（虽然右边这里用肉眼不太容易分辨），
        而就是两边的这两个「超出」，导致了 measureText() 比 getTextBounds() 测量出的宽度要大一些。

        在实际的开发中，测量宽度要用 measureText() 还是 getTextBounds() ，需要根据情况而定。
        不过你只要掌握了上面我所说的它们的本质，在选择的时候就不会为难和疑惑了。
         */

        //5 getTextWidths(String text, float[] widths) 获取字符串中每个字符的宽度，并把结果填入参数 widths。
        /*
        这相当于 measureText() 的一个快捷方法，它的计算等价于对字符串中的每个字符分别调用 measureText() ，
        并把它们的计算结果分别填入 widths 的不同元素。
         */
        text = "noodles你好世界";
        float[] widths = new float[text.length()];
        paint.getTextWidths(text, widths);
        float totalWidth = 0;
        for (int i = 0; i < widths.length; i++) {
            totalWidth += widths[i];
        }
        Log.e("123", "getTextWidths : " + totalWidth + "  measureText(text) : " + paint.measureText(text));
        //totalWidth : 628.0 getTextWidths(text, widths) : 16  measureText(text) : 628.0
        //可见：getTextWidths = measureText(text) ，结果是一样的。
        //【注】getTextWidths 返回值 int ，是 text 文本的长度（字符数量）

        //6 int breakText(String text, boolean measureForwards, float maxWidth, float[] measuredWidth)
        //这个方法也是用来测量文字宽度的。但和 measureText() 的区别是， breakText() 是在给出宽度上限的前提下测量文字的宽度。
        //如果文字的宽度超出了上限，那么在临近超限的位置截断文字。
        text = "Hello如果文字的宽度超出了上限，那么在临近超限的位置截断文字";
        offsetX = 50;
        offsetY = 400;
        paint.setColor(Color.RED);

        int measuredCount = text.length();
        float[] measuredWidth = {0};
        // 宽度上限 20 （不够用，截断）
        int breakText = paint.breakText(text, 0, measuredCount, true, 10, measuredWidth);
        canvas.drawText(text, 0, measuredCount, offsetX, offsetY, paint);

        paint.breakText(text, 0, measuredCount, true, 15, measuredWidth);
        canvas.drawText(text, 0, measuredCount, offsetX, offsetY + 50, paint);
        Log.w("123", "breakText : " + breakText + "  measuredWidth : " + measuredWidth[0]);
        //todo 2018年12月24日 11:37:52
        //breakText : 0  measuredWidth : 31.0
        /*
        breakText() 的返回值是截取的文字个数（如果宽度没有超限，则是文字的总个数）。
        参数:
        text 是要测量的文字；
        measureForwards 表示文字的测量方向，true 表示由左往右测量；
        maxWidth 是给出的宽度上限；
        measuredWidth 是用于接受数据，而不是用于提供数据的：
        方法测量完成后会把截取的文字宽度（如果宽度没有超限，则为文字总宽度）赋值给 measuredWidth[0]。

        这个方法可以用于多行文字的折行计算。
         */

        //7 光标相关
        //对于 EditText 以及类似的场景，会需要绘制光标。
        //光标的计算很麻烦，不过 API 23 引入了两个新的方法，有了这两个方法后，计算光标就方便了很多。
        //https://ws3.sinaimg.cn/large/52eb2279ly1fig67hkga6j20cx0373ys.jpg

        //7.1 getRunAdvance(CharSequence text, int start, int end, int contextStart, int contextEnd, boolean isRtl, int offset)
        //对于一段文字，计算出某个字符处光标的 x 坐标。

        //参数：
        //start end 是文字的起始和结束坐标；
        //contextStart contextEnd 是上下文的起始和结束坐标；
        //isRtl 是文字的方向；
        //offset 是字数的偏移，即计算第几个字符处的光标。

        /*
        其实，说是测量光标位置的，本质上这也是一个测量文字宽度的方法。
        上面这个例子中，start 和  contextStart 都是 0， end contextEnd 和 offset 都等于 text.length()。
        在这种情况下，它是等价于  measureText(text) 的，即完整测量一段文字的宽度。
        而对于更复杂的需求，getRunAdvance() 能做的事就比  measureText() 多了。
         */

        // 包含特殊符号的绘制（如 emoji 表情）
        offsetX = 50;
        offsetY = 500;
        int fontSpacing = 50;
        paint.setColor(Color.BLUE);

        text = "对于一段文字，计算出某个字符处光标的x坐标";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            float advance0 = paint.getRunAdvance(text, 0, 6, 0, 8, false, 2);
            canvas.drawText(text, offsetX, offsetY, paint);
            canvas.drawLine(offsetX + advance0, offsetY - 30, offsetX + advance0, offsetY, paint);
        }

        text = "Hello HenCoder \uD83C\uDDE8\uD83C\uDDF3";// "Hello HenCoder 🇨🇳"
        int length = text.length();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            float advance1 = paint.getRunAdvance(text, 0, length, 0, length, false, length);
            float advance2 = paint.getRunAdvance(text, 0, length, 0, length, false, length - 1);
            float advance3 = paint.getRunAdvance(text, 0, length, 0, length, false, length - 2);
            float advance4 = paint.getRunAdvance(text, 0, length, 0, length, false, length - 3);
            float advance5 = paint.getRunAdvance(text, 0, length, 0, length, false, length - 4);
            float advance6 = paint.getRunAdvance(text, 0, length, 0, length, false, length - 5);
            canvas.drawText(text, offsetX, offsetY + fontSpacing, paint);
            canvas.drawLine(offsetX + advance1, offsetY + fontSpacing - 30, offsetX + advance1, offsetY + fontSpacing, paint);

            canvas.drawText(text, offsetX, offsetY + fontSpacing * 2, paint);
            canvas.drawLine(offsetX + advance2, offsetY + fontSpacing * 2 - 30, offsetX + advance2, offsetY + fontSpacing * 2, paint);

            canvas.drawText(text, offsetX, offsetY + fontSpacing * 3, paint);
            canvas.drawLine(offsetX + advance3, offsetY + fontSpacing * 3 - 30, offsetX + advance3, offsetY + fontSpacing * 3, paint);

            canvas.drawText(text, offsetX, offsetY + fontSpacing * 4, paint);
            canvas.drawLine(offsetX + advance4, offsetY + fontSpacing * 4 - 30, offsetX + advance4, offsetY + fontSpacing * 4, paint);

            canvas.drawText(text, offsetX, offsetY + fontSpacing * 5, paint);
            canvas.drawLine(offsetX + advance5, offsetY + fontSpacing * 5 - 30, offsetX + advance5, offsetY + fontSpacing * 5, paint);

            canvas.drawText(text, offsetX, offsetY + fontSpacing * 6, paint);
            canvas.drawLine(offsetX + advance6, offsetY + fontSpacing * 6 - 30, offsetX + advance6, offsetY + fontSpacing * 6, paint);
        }
        /*
        🇨🇳 虽然占了 4 个字符（\uD83C\uDDE8\uD83C\uDDF3），但当 offset 是表情中间处时，  getRunAdvance() 得出的结果并不会在表情的中间处。
        为什么？因为这是用来计算光标的方法啊，光标当然不能出现在符号中间啦。
         */

        //7.2 getOffsetForAdvance(CharSequence text, int start, int end, int contextStart, int contextEnd, boolean isRtl, float advance)
        //给出一个位置的像素值，计算出文字中最接近这个位置的字符偏移量（即第几个字符最接近这个坐标）。
        /*
        方法的参数很简单：
        text 是要测量的文字；
        start end 是文字的起始和结束坐标；
        contextStart contextEnd 是上下文的起始和结束坐标；
        isRtl 是文字方向；
        advance 是给出的位置的像素值。
        填入参数，对应的字符偏移量将作为返回值返回。

        getOffsetForAdvance() 配合上 getRunAdvance() 一起使用，就可以实现「获取用户点击处的文字坐标」的需求。
         */


        //8  hasGlyph(String string)
        //检查指定的字符串中是否是一个单独的字形 (glyph）。最简单的情况是，string 只有一个字母（比如  a）。
        //https://ws1.sinaimg.cn/large/006tNc79ly1flgaf31rskj31120damyn.jpg



        /*
        以上这些内容，就是文字绘制的相关知识。它们有的常用，有的不常用，有的甚至可以说是在某些情况下没用，
        不过你把它们全部搞懂了，在实际的开发中，就知道哪些事情可以做到，哪些事情做不到，以及应该怎么做了。
         */
    }

    private Paint getPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(45);
        return paint;
    }
}