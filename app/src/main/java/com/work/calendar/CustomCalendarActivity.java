package com.work.calendar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.work.R;
import com.work.base.BaseActivity;

/**
 * Title: CalendarActivity
 * <p>
 * Description:CalendarView使用案例 https://github.com/huanghaibin-dev/CalendarView
 * </p>
 *
 * @author Changbao
 * @date 2019/1/7  16:32
 */
public class CustomCalendarActivity extends BaseActivity {

    private CustomCalendar mCalendar;

    public static void show(Context context) {
        context.startActivity(new Intent(context, CustomCalendarActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_custom);
        mCalendar = findViewById(R.id.custom_calendar);
    }
}
