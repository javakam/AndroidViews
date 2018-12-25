package com.work.progressbar;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.work.R;
import com.work.utils.UIUtils;


/**
 * 一个进度条控件，通过颜色变化显示进度，支持环形和矩形两种形式，主要特性如下：
 * <ol>
 * <li>支持在进度条中以文字形式显示进度，支持修改文字的颜色和大小。</li>
 * <li>可以通过 xml 属性修改进度背景色，当前进度颜色，进度条尺寸。</li>
 * <li>支持限制进度的最大值。</li>
 * </ol>
 *
 * @author cginechen
 * @date 2015-07-29
 */
public class PlanProgressBar extends View {

    public static final int TYPE_RECT = 0;
    public static final int TYPE_CIRCLE = 1;
    public static int TOTAL_DURATION = 1000;
    public static int DEFAULT_PROGRESS_COLOR = Color.BLUE;
    public static int DEFAULT_BACKGROUND_COLOR = Color.GRAY;
    public static int DEFAULT_TEXT_SIZE = 20;
    public static int DEFAULT_TEXT_COLOR = Color.BLACK;
    /*circle_progress member*/
    public static int DEFAULT_STROKE_WIDTH_INNER = UIUtils.dpToPx(1);
    public static int DEFAULT_STROKE_WIDTH = UIUtils.dpToPx(15);
    PlanProgressBarTextGenerator mPlanProgressBarTextGenerator;
    /*rect_progress member*/
    RectF mBgRect;
    RectF mProgressRect;
    /*common member*/
    private int mWidth;
    private int mHeight;
    private int mType;
    private int mProgressColor;
    private int mBackgroundColor;
    private boolean isAnimating = false;
    private int mMaxValue;
    private int mValue;
    private SweepGradient mSweepShader;//渐变
    private int[] mArcColors = {0xff4cd1ff, 0xff4d94ec};//渐变开始颜色、结束颜色
    private ValueAnimator mAnimator;
    private Paint mInnerBackgroundPaint = new Paint();
    private Paint mBackgroundPaint = new Paint();
    private Paint mPaint = new Paint();
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTextPaintSmall = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF mArcOval = new RectF();
    private String mText = "";
    private int mInnerStrokeWidth;
    //外环内边到内环外边的距离
    private int mInnerCircleDistance;
    private int mStrokeWidth;
    private int mCircleRadius;
    private Point mCenterPoint;


    public PlanProgressBar(Context context) {
        this(context, null);
    }

    public PlanProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlanProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context, attrs);
    }

    public void setup(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PlanProgressBar);
        mType = array.getInt(R.styleable.PlanProgressBar_plan_type, TYPE_RECT);
        mProgressColor = array.getColor(R.styleable.PlanProgressBar_plan_progress_color, DEFAULT_PROGRESS_COLOR);
        mBackgroundColor = array.getColor(R.styleable.PlanProgressBar_plan_background_color, DEFAULT_BACKGROUND_COLOR);

        mMaxValue = array.getInt(R.styleable.PlanProgressBar_plan_max_value, 100);
        mValue = array.getInt(R.styleable.PlanProgressBar_plan_value, 0);

        boolean isRoundCap = array.getBoolean(R.styleable.PlanProgressBar_plan_stroke_round_cap, false);

        int textSize = DEFAULT_TEXT_SIZE;
        if (array.hasValue(R.styleable.PlanProgressBar_android_textSize)) {
            textSize = array.getDimensionPixelSize(R.styleable.PlanProgressBar_android_textSize, DEFAULT_TEXT_SIZE);
        }
        int textColor = DEFAULT_TEXT_COLOR;
        if (array.hasValue(R.styleable.PlanProgressBar_android_textColor)) {
            textColor = array.getColor(R.styleable.PlanProgressBar_android_textColor, DEFAULT_TEXT_COLOR);
        }

        if (mType == TYPE_CIRCLE) {
            mInnerStrokeWidth = array.getDimensionPixelSize(R.styleable.PlanProgressBar_plan_stroke_width_inner, DEFAULT_STROKE_WIDTH_INNER);
            mStrokeWidth = array.getDimensionPixelSize(R.styleable.PlanProgressBar_plan_stroke_width, DEFAULT_STROKE_WIDTH);
            mInnerCircleDistance = array.getDimensionPixelSize(R.styleable.PlanProgressBar_plan_stroke_inner_circle_distance, UIUtils.dpToPx(30));
        }
        array.recycle();
        configPaint(textColor, textSize, isRoundCap);

        setProgress(mValue);
    }

    private void configShape() {
        if (mType == TYPE_RECT) {
            mBgRect = new RectF(getPaddingLeft(), getPaddingTop(), mWidth + getPaddingLeft(), mHeight + getPaddingTop());
            mProgressRect = new RectF();
        } else {
            mCircleRadius = (Math.min(mWidth, mHeight) - mStrokeWidth) / 2;
            mCenterPoint = new Point(mWidth / 2, mHeight / 2);
        }
    }

    /**
     * 配置画笔
     */
    private void configPaint(int textColor, int textSize, boolean isRoundCap) {
        mPaint.setColor(mProgressColor);
        // 设定阴影 (柔边, X轴位移, Y轴位移, 阴影颜色)
        mPaint.setShadowLayer(2, 3, 3, 0x8e1b1a);
        mBackgroundPaint.setColor(mBackgroundColor);
        mInnerBackgroundPaint.setColor(mBackgroundColor);
        if (mType == TYPE_RECT) {
            mPaint.setStyle(Paint.Style.FILL);
            mBackgroundPaint.setStyle(Paint.Style.FILL);
            mInnerBackgroundPaint.setStyle(Paint.Style.FILL);
        } else {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(mStrokeWidth);
            mPaint.setAntiAlias(true);
            if (isRoundCap) {
                mPaint.setStrokeCap(Paint.Cap.ROUND);
            }
            mBackgroundPaint.setStyle(Paint.Style.STROKE);
            mBackgroundPaint.setStrokeWidth(mStrokeWidth);
            mBackgroundPaint.setAntiAlias(true);

            mInnerBackgroundPaint.setStyle(Paint.Style.STROKE);
            mInnerBackgroundPaint.setStrokeWidth(mInnerStrokeWidth);
            mInnerBackgroundPaint.setAntiAlias(true);
        }

        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);

        mTextPaintSmall.setColor(getResources().getColor(R.color.color_health_plan_progress_text_above));//todo 硬编码
        mTextPaintSmall.setTextSize(36);//todo 硬编码
        mTextPaintSmall.setTextAlign(Paint.Align.CENTER);
    }


    /**
     * 设置外环背景色
     */
    public void setProgressColor(int color) {
        this.mProgressColor = color;
        mPaint.setColor(mProgressColor);
        invalidate();
        requestLayout();
    }

    /**
     * 设置进度文案的文字大小
     *
     * @see #setTextColor(int)
     * @see #setPlanProgressBarTextGenerator(PlanProgressBarTextGenerator)
     */
    public void setTextSize(int textSize) {
        mTextPaint.setTextSize(textSize);
        invalidate();
    }

    /**
     * 设置进度文案的文字颜色
     *
     * @see #setTextSize(int)
     * @see #setPlanProgressBarTextGenerator(PlanProgressBarTextGenerator)
     */
    public void setTextColor(int textColor) {
        mTextPaint.setColor(textColor);
        invalidate();
    }

    /**
     * 设置环形进度条的两端是否有圆形的线帽，类型为{@link #TYPE_CIRCLE}时生效
     */
    public void setStrokeRoundCap(boolean isRoundCap) {
        mPaint.setStrokeCap(isRoundCap ? Paint.Cap.ROUND : Paint.Cap.BUTT);
        invalidate();
    }

    /**
     * 通过 {@link PlanProgressBarTextGenerator} 设置进度文案
     */
    public void setPlanProgressBarTextGenerator(PlanProgressBarTextGenerator PlanProgressBarTextGenerator) {
        mPlanProgressBarTextGenerator = PlanProgressBarTextGenerator;
    }

    public PlanProgressBarTextGenerator getPlanProgressBarTextGenerator() {
        return mPlanProgressBarTextGenerator;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mPlanProgressBarTextGenerator != null) {
            mText = mPlanProgressBarTextGenerator.generateText(this, mValue, mMaxValue);
        }

        if (mType == TYPE_RECT) {
            drawRect(canvas);
        } else {
            drawCircle(canvas);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        mHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();

        configShape();
        setMeasuredDimension(mWidth, mHeight);
    }

    /**
     * 水平进度条
     */
    private void drawRect(Canvas canvas) {
        canvas.drawRect(mBgRect, mBackgroundPaint);
        mProgressRect.set(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + parseValueToWidth(), getPaddingTop() + mHeight);
        canvas.drawRect(mProgressRect, mPaint);
        if (mText != null && mText.length() > 0) {
            Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
            float baseline = mBgRect.top + (mBgRect.height() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
            canvas.drawText(mText, mBgRect.centerX(), baseline, mTextPaint);
        }
    }

    /**
     * 圆形进度条
     */
    private void drawCircle(Canvas canvas) {
        //内环
        canvas.drawCircle(mCenterPoint.x, mCenterPoint.y,
                mCircleRadius - mInnerCircleDistance, mInnerBackgroundPaint);

        //外环背景
        canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mCircleRadius, mBackgroundPaint);
        mArcOval.left = mCenterPoint.x - mCircleRadius;
        mArcOval.right = mCenterPoint.x + mCircleRadius;
        mArcOval.top = mCenterPoint.y - mCircleRadius;
        mArcOval.bottom = mCenterPoint.y + mCircleRadius;

        //外环
        float textpadding = 50;
        Paint.FontMetricsInt fontMetricsSmall = mTextPaintSmall.getFontMetricsInt();
        float baselineSmall =
                mArcOval.top + (mArcOval.height() - fontMetricsSmall.bottom + fontMetricsSmall.top) / 2
                        - fontMetricsSmall.top - textpadding;
        canvas.drawText("已完成(项)", mCenterPoint.x, baselineSmall, mTextPaintSmall);//todo 硬编码

        if (mText != null && mText.length() > 0) {
            Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
            float baseline = mArcOval.top + (mArcOval.height() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
            canvas.drawText(mText, mCenterPoint.x, baseline + textpadding, mTextPaint);
        }

        mSweepShader = new SweepGradient(mCenterPoint.x, mCenterPoint.y , mArcColors, null);
        mPaint.setShader(mSweepShader);
        //逆时针旋转90度
        canvas.save();

        // 旋转画布90度+笔头半径转过的角度
//        double radian = radianToAngle(getMeasuredWidth() / 2);
        // 90度+
//        double degrees = Math.toDegrees(-2 * Math.PI / 360 * (90 + radian));
        canvas.rotate((float) (-90), mCenterPoint.x, mCenterPoint.y);

        canvas.drawArc(mArcOval, 0, 360 * mValue / mMaxValue, false, mPaint);
    }


    /**
     * 已知圆半径和切线长求弧长公式
     */
    private double radianToAngle(float radios) {
        double aa = mStrokeWidth / 2 / radios;
        double asin = Math.asin(aa);
        double radian = Math.toDegrees(asin);
        return radian;
    }

    private int parseValueToWidth() {
        return mWidth * mValue / mMaxValue;
    }

    public int getProgress() {
        return mValue;
    }

    public void setProgress(int progress) {
        setProgress(progress, true);
    }


    public void setProgress(int progress, boolean animated) {
        if (progress > mMaxValue && progress < 0) {
            return;
        }
        if (isAnimating) {
            isAnimating = false;
            mAnimator.cancel();
        }
        int oldValue = mValue;
        mValue = progress;
        if (animated) {
            startAnimation(oldValue, progress);
        } else {
            invalidate();
        }
    }

    public int getMaxValue() {
        return mMaxValue;
    }

    public void setMaxValue(int maxValue) {
        mMaxValue = maxValue;
    }

    private void startAnimation(int start, int end) {
        mAnimator = ValueAnimator.ofInt(start, end);
        int duration = Math.abs(TOTAL_DURATION * (end - start) / mMaxValue);
        mAnimator.setDuration(duration);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mValue = (int) animation.getAnimatedValue();
                invalidate();
            }
        });

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        mAnimator.start();
    }

    public interface PlanProgressBarTextGenerator {
        /**
         * 设置进度文案, {@link PlanProgressBar} 会在进度更新时调用该方法获取要显示的文案
         *
         * @param value    当前进度值
         * @param maxValue 最大进度值
         * @return 进度文案
         */
        String generateText(PlanProgressBar progressBar, int value, int maxValue);
    }

}
