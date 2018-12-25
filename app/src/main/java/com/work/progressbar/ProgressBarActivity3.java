package com.work.progressbar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.work.R;
import com.work.base.BaseActivity;

import java.lang.ref.WeakReference;

/**
 * Title: ProgressBarActivity
 * <p>
 * Description:进度条演示页面2
 * </p>
 * Author Changbao
 * Date 2018/10/15  11:35
 */
public class ProgressBarActivity3 extends BaseActivity {
    private PlanProgressBar mCircleProgressBar;
    private ProgressHandler myHandler = new ProgressHandler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_v_progress_bar2);
        mCircleProgressBar = findViewById(R.id.circleProgressBar);
        mCircleProgressBar.setStrokeRoundCap(true);
        //模拟业务情形，已完成 5/8 的目标
        final int hasFinished = 5;
        final int total = 8;

        mCircleProgressBar.setPlanProgressBarTextGenerator(new PlanProgressBar.PlanProgressBarTextGenerator() {
            @Override
            public String generateText(PlanProgressBar progressBar, int value, int maxValue) {
//                return 100 * value / maxValue + "%";
                return String.valueOf(hasFinished);
            }
        });

        myHandler.setProgressBar(mCircleProgressBar);
    }

    private class ProgressHandler extends Handler {
        private WeakReference<PlanProgressBar> weakCircleProgressBar;

        void setProgressBar(PlanProgressBar circleProgressBar) {
            this.weakCircleProgressBar = new WeakReference<>(circleProgressBar);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!Thread.currentThread().isInterrupted()) {
                if (weakCircleProgressBar.get() != null) {
                    Toast.makeText(ProgressBarActivity3.this, "" + msg.arg1, Toast.LENGTH_SHORT).show();
                    weakCircleProgressBar.get().setProgress(msg.arg1);
                }
            }
        }
    }

}
