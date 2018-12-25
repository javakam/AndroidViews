package com.work.basic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Locale;

/**
 * Title: CanvasDrawTextSupport
 * <p>
 * Description: Paint 对文字绘制的辅助，有两类方法：设置显示效果的和测量文字尺寸的。
 * </p>
 *
 * @author Changbao
 * @date 2018/12/21  16:38
 */
public class DrawTextPaintSetting extends View {
    public DrawTextPaintSetting(Context context) {
        super(context);
    }

    public DrawTextPaintSetting(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawTextPaintSetting(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //1 Paint.setTypeface 设置字体
        String textTypeface = "Paint.setTypeface 设置字体";
        Paint paint = getPaint();

        paint.setTypeface(Typeface.DEFAULT);
        canvas.drawText(textTypeface, 20, 50, paint);
        paint.setTypeface(Typeface.SERIF);
        canvas.drawText(textTypeface, 20, 130, paint);
        paint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/Satisfy-Regular.ttf"));
        canvas.drawText(textTypeface, 20, 210, paint);

        /*
        系统设置的字体大小和样式，只会影响 Typeface.DEFAULT 类型的文本

        严格地说，其实 typeface 和 font 意思不完全一样。typeface 指的是某套字体（即 font family ），
        而 font 指的是一个 typeface 具体的某个 weight 和 size 的分支。不过无所谓啦~做人最紧要系开心啦。
         */

        //2 Paint.setFakeBoldText(boolean fakeBoldText)  是否使用伪粗体。
        /*
        之所以叫伪粗体（ fake bold ），因为它并不是通过选用更高 weight 的字体让文字变粗，而是通过程序在运行时把文字给「描粗」了。
         */
        paint = getPaint();
        String textFakeBold = "Paint.setFakeBoldText 设置伪粗体";
        paint.setFakeBoldText(true);
        canvas.drawText(textFakeBold, 20, 290, paint);
        paint.setFakeBoldText(false);

        //3 setStrikeThruText(boolean strikeThruText)  是否加删除线。
        //4 setUnderlineText(boolean underlineText)    是否加下划线。
        String textStrikeThru = "Paint.drawText 删除线&下划线";
        paint.setStrikeThruText(true);
        paint.setUnderlineText(true);
        canvas.drawText(textStrikeThru, 20, 370, paint);

        //5 setTextSkewX(float skewX)  设置文字横向错切角度。其实就是文字倾斜度的啦。
        String textTextSkewX = "Paint.setTextSkewX 文字倾斜度";
        paint = getPaint();
        paint.setTextSkewX(-0.5f);
        canvas.drawText(textTextSkewX, 20, 450, paint);

        //6 setTextScaleX(float scaleX)  设置文字横向放缩。也就是文字变胖变瘦。
        //默认为 1 ,0.8变瘦 1.2变胖
        String textTextScaleX = "Paint.setTextScaleX 设置文字横向放缩 - 变瘦！！！";
        paint = getPaint();
        paint.setTextScaleX(0.5F);
        canvas.drawText(textTextScaleX, 20, 530, paint);

        //7 setLetterSpacing(float letterSpacing) 设置字符间距。默认值是 0。
        String textLetterSpacing = "Paint.setLetterSpacing 设置字符间距";
        paint = getPaint();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            paint.setLetterSpacing(0.1f);
        }
        canvas.drawText(textLetterSpacing, 20, 610, paint);
        /*
        为什么在默认的字符间距为 0 的情况下，字符和字符之间也没有紧紧贴着，
        在前面讲 Canvas.drawText() 的 x 参数的时候已经介绍了
         */

        //8 setFontFeatureSettings(String settings) 用 CSS 的 font-feature-settings 的方式来设置文字。
        //相关文档：https://www.w3.org/TR/css-fonts-3/#font-feature-settings-prop
        String textFontFeatureSettings = "Paint.setFontFeatureSettings 用 CSS 的 font-feature-settings 的方式来设置文字";
        paint = getPaint();
        paint.setTextSize(22);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            paint.setFontFeatureSettings("smcp"); //设置 "small caps"
        }
        canvas.drawText(textFontFeatureSettings, 20, 690, paint);

        //9 setTextAlign(Paint.Align align)
        //设置文字的对齐方式。一共有三个值：LEFT CENTER 和 RIGHT。默认值为 LEFT。
        int centerLine = 290;//中线
        String textTextAlignLeft = "Paint.setTextAlign 左对齐";
        String textTextAlignCenter = "Paint.setTextAlign 居中";
        String textTextAlignRight = "Paint.setTextAlign 右对齐";
        paint = getPaint();
        paint.setTextSize(30);

        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(textTextAlignLeft, centerLine, 750, paint);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(textTextAlignCenter, centerLine, 830, paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(textTextAlignRight, centerLine, 910, paint);
        Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.RED);
        linePaint.setStrokeWidth(3);
        canvas.drawLine(centerLine, 720, centerLine, 910, linePaint);

        //10 setTextLocale(Locale locale) / setTextLocales(LocaleList locales) 设置绘制所使用的 Locale。
        //Locale 直译是「地域」，其实就是系统设置里的「语言」或「语言区域」（具体名称取决于你用的是什么手机）
        /*
        Canvas 绘制的时候，默认使用的是系统设置里的 Locale。
        而通过 Paint.setTextLocale(Locale locale) 就可以在不改变系统设置的情况下，直接修改绘制时的 Locale。

        另外，由于 Android 7.0 ( API v24) 加入了多语言区域的支持，所以在 API v24 以及更高版本上，
        还可以使用 setTextLocales(LocaleList locales) 来为绘制设置多个语言区域。
         */
        String textTextLocale = "setTextLocale雨骨底条今直沿微写";
        paint = getPaint();

        paint.setTextLocale(Locale.CHINA); // 简体中文
        canvas.drawText(textTextLocale, 20, 990, paint);
        paint.setTextLocale(Locale.TAIWAN); // 繁体中文
        canvas.drawText(textTextLocale, 20, 1070, paint);
        paint.setTextLocale(Locale.JAPAN); // 日语
        canvas.drawText(textTextLocale, 20, 1150, paint);
        //todo 2018年12月21日 18:01:04 setTextLocale 无效

        //11 setHinting(int mode) 设置是否启用字体的 hinting （字体微调）。
        /*
         hinting 技术就是为了解决这种问题的：
         通过向字体中加入 hinting 信息，让矢量字体在尺寸过小的时候得到针对性的修正，从而提高显示效果。

         功能很强，效果很赞。不过在现在（ 2018 年），手机屏幕的像素密度已经非常高，
         几乎不会再出现字体尺寸小到需要靠 hinting 来修正的情况，所以这个方法其实……没啥用了。可以忽略。
         */
        //https://ws3.sinaimg.cn/large/52eb2279ly1fig65wwv1yj20ki0bywje.jpg


        //12 setElegantTextHeight(boolean elegant)
        //声明：这个方法对中国人没用，不想看的话可以直接跳过，无毒副作用。
        //设置是否开启文字的 elegant height 。开启之后，文字的高度就变优雅了（误）。

        //13 setSubpixelText(boolean subpixelText) 是否开启次像素级的抗锯齿（ sub-pixel anti-aliasing ）。
        /*
        次像素级抗锯齿这个功能解释起来很麻烦，简单说就是根据程序所运行的设备的屏幕类型，
        来进行针对性的次像素级的抗锯齿计算，从而达到更好的抗锯齿效果。
        更详细的解释可以看这篇文章。 http://alienryderflex.com/sub_pixel/

        不过，和前面讲的字体 hinting 一样，由于现在手机屏幕像素密度已经很高，
        所以默认抗锯齿效果就已经足够好了，一般没必要开启次像素级抗锯齿，所以这个方法基本上没有必要使用。
         */

        //14 setLinearText(boolean linearText)


        /*
        以上就是 Paint 的对文字的显示效果设置类方法。下面介绍它的第二类方法：测量文字尺寸类。
         */
    }

    private Paint getPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(45);
        return paint;
    }
}
