package com.work.basic.animation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.work.R;

/**
 * Title: AnimViewPropertyAnimator
 * <p>
 * Description:属性动画  ViewPropertyAnimator
 * </p>
 *
 * @author Changbao
 * @date 2019/1/3  14:11
 */
public class AnimViewPropertyAnimator extends View {
    public AnimViewPropertyAnimator(Context context) {
        super(context);
    }

    public AnimViewPropertyAnimator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimViewPropertyAnimator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.music)).getBitmap();
        canvas.drawBitmap(bitmap, 50, 50, new Paint(Paint.ANTI_ALIAS_FLAG));


        postDelayed(new Runnable() {
            @Override
            public void run() {
                /*
                 1 ViewPropertyAnimator

                 API：https://ws1.sinaimg.cn/large/006tKfTcgy1fj7x3rm1xxj30u50laq6y.jpg

                 CycleInterpolator 0.5F
                 这个也是一个正弦 / 余弦曲线，不过它和 AccelerateDecelerateInterpolator 的区别是，
                 它可以自定义曲线的周期，所以动画可以不到终点就结束，也可以到达终点后回弹，
                 回弹的次数由曲线的周期决定，曲线的周期由 CycleInterpolator() 构造方法的参数决定。
                 */
                animate().setDuration(3000)
                        .setInterpolator(new BounceInterpolator())
                        .translationX(150)
                        .scaleX(1).scaleY(1)
                        .alpha(1);
            }
        }, 1000);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                animate().setDuration(3000)
                        .setInterpolator(new DecelerateInterpolator())
                        .translationXBy(-150)
                        .scaleXBy(-0.5F).scaleYBy(-0.5F)
                        .alpha(0.5F);
            }
        }, 6000);
    }
}