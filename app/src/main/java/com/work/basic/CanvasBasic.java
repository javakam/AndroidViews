package com.work.basic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.work.R;
import com.work.utils.UIUtils;

/**
 * Title: LineView
 * <p>
 * Description:
 * </p>
 *
 * @author Changbao
 * @date 2018/12/11  10:52
 */
public class CanvasBasic extends View {
    private Paint mLinePaint;
    private Paint mTextPaint;
    private Paint mPathPaint;

    public CanvasBasic(Context context) {
        super(context);
        initView();
    }

    public CanvasBasic(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CanvasBasic(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        this.setBackgroundColor(Color.parseColor("#E1FFFF"));
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(Color.RED);
        mLinePaint.setStrokeWidth(5);
        mLinePaint.setDither(true);
        mLinePaint.setSubpixelText(true);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.MAGENTA);
        mTextPaint.setStrokeWidth(5);
        mTextPaint.setDither(true);
        mTextPaint.setTextSize(UIUtils.getDimens(R.dimen.font_24));
        mTextPaint.setSubpixelText(true);

        mPathPaint = new Paint();
        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setColor(Color.BLUE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0, 0, 300, 60, mLinePaint);
        canvas.drawText("早上好GoodMorning!", 100, 200, mTextPaint);

        //Path
        Path path = new Path();
        path.lineTo(100, 100);
        path.rLineTo(50, 0);
        path.arcTo(150, 100, 230, 230, -90, 90, false);
        //前提-》paint.setStyle(Style.STROKE); FILL 或 FILL_AND_STROKE，Path 会自动封闭子图形
        //lineTo() arcTo() 等方法的时候，则是每一次断线（即每一次「抬笔」），都标志着一个子图形的结束，以及一个新的子图形的开始
        path.close();
        canvas.drawPath(path, mPathPaint);

    }
}