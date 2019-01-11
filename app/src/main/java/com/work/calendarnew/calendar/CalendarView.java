package com.work.calendarnew.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.work.R;
import com.work.calendar.local.calendarview.Calendar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class CalendarView extends View {

    public static final int TOTAL_COL = 7;
    protected int TOTAL_ROW;
    private Paint mPaint;
    protected Paint mSchemePaint;           //标记的日期背景颜色画笔
    private int mCellSpace;
    private int mCellSpaceY;
    protected Cell[][] mCells;
    private int touchSlop;
    private int defaultTextColor;
    private int defaultTextSize;
    private CalendarDraw calendarDraw;      //绘制全部交给calendarDraw
    public CustomDate mShowDate;
    private OnClickListener onClickListener;

    /**
     * 计划，可以用来标记当天是否有任务,这里是默认的，如果使用多标记，请使用下面API
     * using addScheme(int schemeColor,String scheme); multi scheme
     */
    private String scheme;

    /**
     * 各种自定义标记颜色、没有则选择默认颜色，如果使用多标记，请使用下面API
     * using addScheme(int schemeColor,String scheme); multi scheme
     */
    private int schemeColor;

    /**
     * 多标记
     * multi scheme,using addScheme();
     */
    private List<Calendar.Scheme> schemes;

    public CalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarView(Context context) {
        this(context, null);
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TOTAL_ROW = getTotalRow();
        mCells = new Cell[TOTAL_ROW][TOTAL_COL];
        init(context, attrs);

    }

    protected abstract void measureClickCell(int col, int row);


    @Override
    protected void onDraw(Canvas canvas) {
        calendarDraw.onDraw(this);
        Rect rect;
        Cell cell;
        for (int i = 0; i < TOTAL_ROW; i++) {
            if (mCells[i] != null) {
                for (int j = 0; j < TOTAL_COL; j++) {
                    cell = mCells[i][j];
                    if (cell != null && cell.getDate() != null) {
                        rect = cell.getRect(mCellSpace, mCellSpaceY);
                        geCalendarDraw().onDraw(this, canvas, cell, rect, mPaint);
                    }
                }
            }
        }
    }

    private void init(Context context, AttributeSet attrs) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSchemePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CalendarViewNew);
            defaultTextSize = (int) a.getDimension(R.styleable.CalendarViewNew_baseTextSize, 25);
            defaultTextColor = a.getColor(R.styleable.CalendarViewNew_baseTextColor, Color.parseColor("#333333"));
            mCellSpaceY = (int) a.getDimension(R.styleable.CalendarViewNew_rowHeight, getResources().getDimensionPixelOffset(R.dimen.calendar_view_height));
            a.recycle();
        } else {
            defaultTextColor = Color.parseColor("#333333");
            defaultTextSize = 25;
        }
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        calendarDraw = new CalendarDraw();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = mCellSpaceY * TOTAL_ROW;
        setMeasuredDimension(width, height);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int mViewWidth = w;
        mCellSpace = mViewWidth / TOTAL_COL;
        mPaint.setTextSize(mCellSpace / 3);
    }

    private float mDownX;
    private float mDownY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float disX = event.getX() - mDownX;
                float disY = event.getY() - mDownY;
                if (Math.abs(disX) < touchSlop && Math.abs(disY) < touchSlop) {
                    int col = (int) (mDownX / mCellSpace);
                    int row = (int) (mDownY / mCellSpaceY);
                    measureClickCell(col, row);
                }
                break;
            default:
        }
        return true;
    }

    public Cell getCell(int row, int col) {

        return mCells[row][col];
    }

    public static class Cell {
        private CustomDate date;
        public int row; //行
        public int col; //列
        private Rect rect;

        Cell(CustomDate date, int row, int col) {
            this.date = date;
            this.row = row;
            this.col = col;
            rect = new Rect();
        }

        void update(CustomDate date, int row, int col) {
            this.date = date;
            this.col = col;
            this.row = row;
        }

        public CustomDate getDate() {
            return date;
        }

        public Rect getRect(int perW, int perH) {
            int left = perW * col;
            int top = row * perH;
            int right = left + perW;
            int bottom = top + perH;
            rect.set(left, top, right, bottom);
            return rect;
        }

    }

    public CustomDate getShowDate() {
        return mShowDate;
    }

    protected CalendarDraw geCalendarDraw() {
        return calendarDraw;
    }

    public void addDrawFormat(IDrawFormat drawFormat) {
        calendarDraw.getDrawFormats().add(drawFormat);
    }

    public List<IDrawFormat> getDrawFormats() {

        return calendarDraw.getDrawFormats();
    }


    public int getDefaultTextColor() {
        return defaultTextColor;
    }

    public void setDefaultTextColor(int defaultTextColor) {
        this.defaultTextColor = defaultTextColor;
    }

    public int getDefaultTextSize() {
        return defaultTextSize;
    }

    public void setDefaultTextSize(int defaultTextSize) {
        this.defaultTextSize = defaultTextSize;
    }

    public static class CalendarDraw implements IDrawFormat {

        private List<IDrawFormat> smallDrawFormats = new ArrayList<>();


        @Override
        public void onDraw(CalendarView calendarView) {
            int size = smallDrawFormats.size();
            for (int i = 0; i < size; i++) {
                smallDrawFormats.get(i).onDraw(calendarView);
            }
        }

        @Override
        public int getDateType(CalendarView calendarView, Cell cell) {
            return 0;
        }

        @Override
        public void onDraw(CalendarView calendarView, Canvas canvas, Cell cell, Rect rect, Paint paint) {
            int size = smallDrawFormats.size();
            for (int i = 0; i < size; i++) {
                smallDrawFormats.get(i).onDraw(calendarView, canvas, cell, rect, paint);
            }
        }


        @Override
        public void onClick(CalendarView calendarView, Cell cell) {
            int size = smallDrawFormats.size();
            for (int i = 0; i < size; i++) {
                smallDrawFormats.get(i).onClick(calendarView, cell);
            }
        }

        public List<IDrawFormat> getDrawFormats() {
            return smallDrawFormats;
        }
    }

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(CalendarView calendarView, Cell cell);
    }

    public interface OnCalendarPageChanged {

        void onPageChanged(CustomDate showDate);
    }

    public abstract int getTotalRow();

    public interface IDrawFormat {

        void onDraw(CalendarView calendarView);

        int getDateType(CalendarView calendarView, Cell cell);

        void onDraw(CalendarView calendarView, Canvas canvas, Cell cell, Rect rect, Paint paint);

        void onClick(CalendarView calendarView, Cell cell);

    }

    public static int getTextHeight(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return (int) (fontMetrics.descent - fontMetrics.ascent);
    }

    public static float getTextCenterY(int centerY, Paint paint) {
        return centerY - ((paint.descent() + paint.ascent()) / 2);
    }


    public String getScheme() {
        return scheme;
    }


    public void setScheme(String scheme) {
        this.scheme = scheme;
    }


    public int getSchemeColor() {
        return schemeColor;
    }

    public void setSchemeColor(int schemeColor) {
        this.schemeColor = schemeColor;
    }


    public List<Calendar.Scheme> getSchemes() {
        return schemes;
    }

    public void setSchemes(List<Calendar.Scheme> schemes) {
        this.schemes = schemes;
    }


    public void addScheme(Calendar.Scheme scheme) {
        if (schemes == null) {
            schemes = new ArrayList<>();
        }
        schemes.add(scheme);
    }

    public void addScheme(int schemeColor, String scheme) {
        if (schemes == null) {
            schemes = new ArrayList<>();
        }
        schemes.add(new Calendar.Scheme(schemeColor, scheme));
    }

    public void addScheme(int type, int schemeColor, String scheme) {
        if (schemes == null) {
            schemes = new ArrayList<>();
        }
        schemes.add(new Calendar.Scheme(type, schemeColor, scheme));
    }

    public void addScheme(int type, int schemeColor, String scheme, String other) {
        if (schemes == null) {
            schemes = new ArrayList<>();
        }
        schemes.add(new Calendar.Scheme(type, schemeColor, scheme, other));
    }

    public void addScheme(int schemeColor, String scheme, String other) {
        if (schemes == null) {
            schemes = new ArrayList<>();
        }
        schemes.add(new Calendar.Scheme(schemeColor, scheme, other));
    }

    final void clearScheme() {
        setScheme("");
        setSchemeColor(0);
        setSchemes(null);
    }

    /**
     * 事件标记服务，现在多类型的事务标记建议使用这个
     */
    public final static class Scheme implements Serializable {
        private int type;
        private int shcemeColor;
        private String scheme;
        private String other;

        public Scheme() {
        }

        public Scheme(int type, int shcemeColor, String scheme, String other) {
            this.type = type;
            this.shcemeColor = shcemeColor;
            this.scheme = scheme;
            this.other = other;
        }

        public Scheme(int type, int shcemeColor, String scheme) {
            this.type = type;
            this.shcemeColor = shcemeColor;
            this.scheme = scheme;
        }

        public Scheme(int shcemeColor, String scheme) {
            this.shcemeColor = shcemeColor;
            this.scheme = scheme;
        }

        public Scheme(int shcemeColor, String scheme, String other) {
            this.shcemeColor = shcemeColor;
            this.scheme = scheme;
            this.other = other;
        }

        public int getShcemeColor() {
            return shcemeColor;
        }

        public void setShcemeColor(int shcemeColor) {
            this.shcemeColor = shcemeColor;
        }

        public String getScheme() {
            return scheme;
        }

        public void setScheme(String scheme) {
            this.scheme = scheme;
        }

        public String getOther() {
            return other;
        }

        public void setOther(String other) {
            this.other = other;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

    }
}
