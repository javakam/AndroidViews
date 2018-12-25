package com.work.progressbar;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.work.R;
import com.work.base.BaseActivity;

/**
 * Title: ProgressBarActivity
 * <p>
 * Description:进度条演示页面2
 * </p>
 * Author Changbao
 * Date 2018/10/15  11:35
 */
public class ProgressBarActivity2 extends BaseActivity {
    private RoundView mRoundView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_v_progress_bar2);
        mRoundView = findViewById(R.id.roundView);
        mRoundView.setAngle(80, true);
    }
}
