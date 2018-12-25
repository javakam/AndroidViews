package com.work.progressbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

import com.work.R;

/**
 * 自定义进度圆环
 */
public class RoundView extends View implements Animation.AnimationListener {

    /**
     * 圆环转过的角度
     */
    private float mSweepAngle = 1;
    /**
     * 之前的角度
     */
    private float mOldAngle = 0;
    /**
     * 新的角度
     */
    private float mNewAngle = 0;
    /**
     * 圆环宽度,默认半径的1／5
     */
    private float mRingWidth = 0;
    /**
     * 圆环颜色,默认 #CBCBCB
     */
    private int mRingColor = 0;
    /**
     * 进度条圆环宽度
     */
    private float mProgressRingWidth = 0;
    /**
     * 进度条圆环开始颜色，进度条圆环是渐变的,默认
     */
    private int mProgressRingStartColor = 0;
    /**
     * 进度条圆环结束颜色，进度条圆环是渐变的,默认
     */
    private int mProgressRingEndColor = 0;
    /**
     * 圆环半径,默认：Math.min(getHeight()/2,getWidth()/2)
     */
    private float mRadius = 0;
    /**
     * 进度条圆环Paint
     */
    private Paint mProgressRingPaint;
    /**
     * 进度条圆环渐变shader
     */
    private Shader mProgressRingShader;

    /**
     * 底环画笔
     */
    private Paint mRingPaint;
    /**
     * 箭头画笔
     */
    private Paint mArrowPaint;
    /**
     * 箭头大小
     */
    private int arrowSize = 0;
    /**
     * 文字画笔
     */
    private Paint mTextPaint;
    /**
     * 文字颜色
     */
    private int mTextColor;
    /**
     * 文字大小
     */
    private float mTextSize;

    private int[] arcColors = {};// 渐变色
    /**
     * 进度动画
     */
    private BarAnimation barAnimation;
    /**
     * 抖动（缩放）动画
     */
    private ScaleAnimation scaleAnimation;
    /**
     * 是否正在改变
     */
    private boolean isAnimation = false;

    public RoundView(Context context) {
        this(context, null);
    }

    public RoundView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public RoundView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RoundView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundView);
        mRingWidth = typedArray.getDimension(R.styleable.RoundView_ring_width, 0);
        mRingColor = typedArray.getColor(R.styleable.RoundView_ring_color, Color.parseColor("#CBCBCB"));
        mProgressRingWidth = typedArray.getDimension(R.styleable.RoundView_progress_ring_width, 0);
        mProgressRingStartColor = typedArray.getColor(R.styleable.RoundView_progress_ring_start_color, Color.parseColor("#f90aa9"));
        mProgressRingEndColor = typedArray.getColor(R.styleable.RoundView_progress_ring_end_color, Color.parseColor("#931c80"));
        mRadius = typedArray.getDimension(R.styleable.RoundView_android_radius, 0);
        mTextColor = typedArray.getColor(R.styleable.RoundView_text_color, Color.parseColor("#f90aa9"));
        mTextSize = typedArray.getDimension(R.styleable.RoundView_text_size, dp2px(context, 40));
        arrowSize = dp2px(context, 7);
        typedArray.recycle();
        init();
    }

    /**
     * 初始化
     */
    private void init() {

        mRingPaint = new Paint();
        mRingPaint.setAntiAlias(true);// 抗锯齿效果
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setColor(mRingColor);// 背景灰

        // 圆环紫色渐变色
        arcColors = new int[]{mProgressRingStartColor, mProgressRingEndColor};
        mProgressRingPaint = new Paint();
        mProgressRingPaint.setAntiAlias(true);// 抗锯齿效果
        mProgressRingPaint.setStyle(Paint.Style.STROKE);
        mProgressRingPaint.setStrokeCap(Paint.Cap.ROUND);// 圆形笔头
        mProgressRingPaint.setStrokeWidth(mProgressRingWidth);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);// 抗锯齿效果
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(mTextColor);// 字体颜色
        mTextPaint.setTextSize(mTextSize);

        mArrowPaint = new Paint();
        mArrowPaint.setAntiAlias(true);// 抗锯齿效果
        mArrowPaint.setStyle(Paint.Style.FILL);
        mArrowPaint.setColor(Color.parseColor("#000000"));
        mArrowPaint.setStrokeCap(Paint.Cap.ROUND);
        mArrowPaint.setStrokeWidth(5);


        barAnimation = new BarAnimation();
        barAnimation.setAnimationListener(this);

        scaleAnimation = new ScaleAnimation();
        scaleAnimation.setDuration(100);
    }

    /**
     * 设置新的角度
     * newAngle:角度
     */
    public void setAngle(final float newAngle) {
        setAngle(newAngle, true);
    }

    /**
     * 设置新的角度
     * newAngle:角度
     * isScale：是否抖动
     */
    public void setAngle(final float newAngle, boolean isScale) {
        if (!isAnimation) {
            if (isScale) {
                scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        changeAngle(newAngle);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                startAnimation(scaleAnimation);
            } else {
                changeAngle(newAngle);
            }
        }
    }


    private void changeAngle(float newAngle) {
        mOldAngle = mNewAngle;
        mNewAngle = newAngle;
        //计算动画时间
        int duration = (int) (Math.abs(mNewAngle - mOldAngle) * 15);
        barAnimation.setDuration(duration);
        //动画差值器
        barAnimation.setInterpolator(new DecelerateInterpolator());
        startAnimation(barAnimation);
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {

        // 圆心坐标,当前View的中心
        float x = getWidth() / 2;
        float y = getHeight() / 2;

        //如果未设置半径，则半径的值为view的宽、高一半的较小值
        float radius = mRadius == 0 ? Math.min(getWidth() / 2, getHeight() / 2) : mRadius;
        //圆环的宽度默认为半径的1／5
        float ringWidth = mRingWidth == 0 ? radius / 5 : mRingWidth;
        //由于圆环本身有宽度，所以半径要减去圆环宽度的一半，不然一部分圆会在view外面
        radius = radius - ringWidth / 2;
        mRingPaint.setStrokeWidth(ringWidth);

        // 底环
        canvas.drawCircle(x, y, radius, mRingPaint);
        //----绘制圆环（最底部）结束------

        //----绘制进度圆环------
        // 设置渐变色
        if (mProgressRingShader == null) {
            mProgressRingShader = new SweepGradient(x, y, arcColors, null);
            mProgressRingPaint.setShader(mProgressRingShader);
        }
        //计算进度圆环宽度,默认和底部圆滑一样大
        float progressRingWidth = mProgressRingWidth == 0 ? ringWidth : mProgressRingWidth;
        mProgressRingPaint.setStrokeWidth(progressRingWidth);
        float left = x - radius;
        float top = y - radius;
        float right = x + radius;
        float bottom = y + radius;
        // 旋转画布90度+笔头半径转过的角度
        double radian = radianToAngle(radius);
        double degrees = Math.toDegrees(-2 * Math.PI / 360 * (90 + radian));// 90度+
        canvas.save();
        canvas.rotate((float) degrees, x, y);
        canvas.drawArc(new RectF(left, top, right, bottom), (float) radian, mOldAngle + mSweepAngle, false, mProgressRingPaint);
        canvas.restore();
        //----绘制进度圆环结束------

        //----绘制箭头开始------
        //画布恢复到上一次save到状态，也就是旋转的画布

        //将我们要画箭头的位置旋转到顶部，方便我们计算箭头的坐标，绘制完箭头在将画布恢复
        canvas.save();
        double hudu = 2 * Math.PI / 360 * (mOldAngle + mSweepAngle);
        double radians = Math.toDegrees(hudu);
        // 旋转画布
        canvas.rotate((float) radians, x, y);
        //绘制箭头
        canvas.drawLine(x - arrowSize, y - radius, x + arrowSize, y - radius, mArrowPaint);
        canvas.drawLine(x + arrowSize, y - radius, x, y - radius - arrowSize, mArrowPaint);
        canvas.drawLine(x + arrowSize, y - radius, x, y - radius + arrowSize, mArrowPaint);
        canvas.restore();
        //----绘制箭头结束------

        //----绘制百分比字体开始------

        int percentage = (int) ((mOldAngle + mSweepAngle) / 360 * 100);
        float sizeHeight = getFontHeight();
        canvas.drawText(percentage + "%", x, y + sizeHeight / 2, mTextPaint);

        //----绘制百分比字体结束------
        super.onDraw(canvas);
    }

    public float getFontHeight() {
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

    /**
     * 已知圆半径和切线长求弧长公式
     *
     * @param radios
     * @return
     */
    private double radianToAngle(float radios) {
        double aa = mProgressRingWidth / 2 / radios;
        double asin = Math.asin(aa);
        double radian = Math.toDegrees(asin);
        return radian;
    }


    /**
     * 抖动（缩放动画）
     */
    public class ScaleAnimation extends Animation {
        private int centerX;
        private int centerY;

        public ScaleAnimation() {
        }

        @Override
        public void initialize(int width, int height, int parentWidth,
                               int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            centerX = width / 2;
            centerY = height / 2;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);

            if (interpolatedTime < 0.25f) {
                // 1-1.1
                float ss = interpolatedTime * 0.4f + 1f;
                Matrix matrix = t.getMatrix();
                matrix.setScale(ss, ss, centerX, centerY);
            } else if (interpolatedTime >= 0.25f && interpolatedTime < 0.5f) {
                // 1.1-1
                float ss = (0.5f - interpolatedTime) * 0.4f + 1f;
                Matrix matrix = t.getMatrix();
                matrix.setScale(ss, ss, centerX, centerY);
            } else if (interpolatedTime >= 0.5f && interpolatedTime < 0.75f) {
                // 1-0.9
                float ss = (0.75f - interpolatedTime) * 0.4f + 0.9f;
                Matrix matrix = t.getMatrix();
                matrix.setScale(ss, ss, centerX, centerY);
            } else if (interpolatedTime >= 0.75f && interpolatedTime <= 1f) {
                // 0.9-1
                float ss = interpolatedTime * 0.4f + 0.6f;
                Matrix matrix = t.getMatrix();
                matrix.setScale(ss, ss, centerX, centerY);
            }
        }
    }

    /**
     * 进度条动画
     */
    public class BarAnimation extends Animation {


        public BarAnimation() {
        }

        /**
         * 然后调用postInvalidate()不停的绘制view。
         */
        @Override
        protected void applyTransformation(float interpolatedTime,
                                           Transformation t) {

            super.applyTransformation(interpolatedTime, t);
            if (mNewAngle - mOldAngle >= 0) {
                // 正向
                mSweepAngle = interpolatedTime * (mNewAngle - mOldAngle);
            } else {
                // 逆向
                mSweepAngle = interpolatedTime * (mNewAngle - mOldAngle);
            }
            postInvalidate();
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {
        isAnimation = true;
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        isAnimation = false;

    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub

    }

    private int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }


}