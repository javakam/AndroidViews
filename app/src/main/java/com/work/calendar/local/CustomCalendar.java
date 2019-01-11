package com.work.calendar.local;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.work.R;
import com.work.calendar.local.calendarview.Calendar;
import com.work.calendar.local.calendarview.CalendarLayout;
import com.work.calendar.local.calendarview.CalendarView;

import java.util.HashMap;
import java.util.Map;

/**
 * Title: CustomCalendar
 * <p>
 * Description:组合 CalendarView
 * </p>
 *
 * @author Changbao
 * @date 2019/1/8  9:22
 */
public class CustomCalendar extends LinearLayout implements
        CalendarView.OnCalendarSelectListener {

    private TextView mTvYearMonth;
    private ImageView mIvPrevMonth;
    private ImageView mIvNextMonth;
    private String mDateFormat;
    private CalendarLayout mCalendarLayout;
    private CalendarView mCalendarView;

    public CustomCalendar(Context context) {
        this(context, null, 0);
    }

    public CustomCalendar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomCalendar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundResource(R.color.font_btn_press);
        initView(context);
        initData();
    }

    private void initView(Context context) {

        View v = inflate(context, R.layout.layout_calendar, null);
        mTvYearMonth = v.findViewById(R.id.tv_current_year_month);
        mIvPrevMonth = v.findViewById(R.id.iv_month_sub);
        mIvNextMonth = v.findViewById(R.id.iv_month_add);
        mCalendarLayout = v.findViewById(R.id.calendarLayout);
        mCalendarView = v.findViewById(R.id.calendarView);

        this.setBackgroundColor(getResources().getColor(R.color.color_user_main_sleep_line));
        mCalendarLayout.setBackgroundColor(getResources().getColor(R.color.color_address_phone_item_picked));
        mCalendarView.getMonthViewPager().setBackgroundColor(getResources().getColor(R.color.color_gray));

        mIvPrevMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.scrollToPre(true);
            }
        });
        mIvNextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.scrollToNext(true);
            }
        });

        mCalendarView.setOnCalendarSelectListener(this);
//        mCalendarView.setOnYearChangeListener(this);
        mDateFormat = context.getResources().getString(R.string.calendar_date);
        String showDate = String.format(mDateFormat, mCalendarView.getCurYear(), mCalendarView.getCurMonth());
        mTvYearMonth.setText(showDate);

        addView(v);
    }

    private void initData() {
        int year = mCalendarView.getCurYear();
        int month = mCalendarView.getCurMonth();

        Map<String, Calendar> map = new HashMap<>();
        map.put(getSchemeCalendar(year, month, 3, 0xFF40db25, "假").toString(),
                getSchemeCalendar(year, month, 3, 0xFF40db25, "假"));
        map.put(getSchemeCalendar(year, month, 6, 0xFFe69138, "事").toString(),
                getSchemeCalendar(year, month, 6, 0xFFe69138, "事"));
        map.put(getSchemeCalendar(year, month, 9, 0xFFdf1356, "议").toString(),
                getSchemeCalendar(year, month, 9, 0xFFdf1356, "议"));
        map.put(getSchemeCalendar(year, month, 13, 0xFFedc56d, "记").toString(),
                getSchemeCalendar(year, month, 13, 0xFFedc56d, "记"));
        map.put(getSchemeCalendar(year, month, 14, 0xFFedc56d, "记").toString(),
                getSchemeCalendar(year, month, 14, 0xFFedc56d, "记"));
        map.put(getSchemeCalendar(year, month, 15, 0xFFaacc44, "假").toString(),
                getSchemeCalendar(year, month, 15, 0xFFaacc44, "假"));
        map.put(getSchemeCalendar(year, month, 18, 0xFFbc13f0, "记").toString(),
                getSchemeCalendar(year, month, 18, 0xFFbc13f0, "记"));
        map.put(getSchemeCalendar(year, month, 25, 0xFF13acf0, "假").toString(),
                getSchemeCalendar(year, month, 25, 0xFF13acf0, "假"));
        map.put(getSchemeCalendar(year, month, 27, 0xFF13acf0, "").toString(),
                getSchemeCalendar(year, month, 27, 0xFFFFa800, ""));
        //此方法在巨大的数据量上不影响遍历性能，推荐使用
        mCalendarView.setSchemeDate(map);
    }

    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
//        calendar.setScheme(text);
//        calendar.addScheme(new Calendar.Scheme());
//        calendar.addScheme(0xFF008800, "假");
//        calendar.addScheme(0xFF008800, "节");
        return calendar;
    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        String showDate = String.format(mDateFormat, calendar.getYear(), calendar.getMonth());
        mTvYearMonth.setText(showDate);
    }
}
