package com.work.calendarnew;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.work.R;
import com.work.base.BaseActivity;
import com.work.calendarnew.calendar.CalendarRecyclerHelper;
import com.work.calendarnew.calendar.CalendarView;
import com.work.calendarnew.calendar.CustomDate;

/**
 * Created by huang on 2017/11/9.
 */

public class CalendarMonthActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private TextView showTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_new);
        findViewById(R.id.img_cal_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        showTv = (TextView) findViewById(R.id.tv_show_date);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        CalendarRecyclerHelper.init(this, recyclerView, new CustomCalendarAdapter(), new CalendarView.OnCalendarPageChanged() {

            @Override
            public void onPageChanged(CustomDate showDate) {
                showTv.setText(String.format("%d年%d月", showDate.year, showDate.month));
            }
        });
    }
}
