package com.work.progressbar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.work.R;
import com.work.base.BaseActivity;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Title: ProgressBarActivity
 * <p>
 * Description:进度条演示页面
 * </p>
 * Author Changbao
 * Date 2018/10/15  11:35
 */
public class ProgressBarActivity extends BaseActivity {

    private static final int STOP = 0x10000;
    private static final int NEXT = 0x10001;
    private ProgressHandler myHandler = new ProgressHandler();
    private PlanProgressBar mRectProgressBar;
    private PlanProgressBar mCircleProgressBar;
    int count;
    private ExecutorService executorService = Executors.newFixedThreadPool(1);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_v_progress_bar);
        initView();
    }


    private void initView() {
        mRectProgressBar = findViewById(R.id.rectProgressBar);
        mCircleProgressBar = findViewById(R.id.circleProgressBar);
        mCircleProgressBar.setStrokeRoundCap(true);
        //模拟业务情形，已完成 5/8 的目标
        final int hasFinished = 5;
        final int total = 8;

        mRectProgressBar.setPlanProgressBarTextGenerator(new PlanProgressBar.PlanProgressBarTextGenerator() {
            @Override
            public String generateText(PlanProgressBar progressBar, int value, int maxValue) {
                return value + "/" + maxValue;
            }
        });

        mCircleProgressBar.setPlanProgressBarTextGenerator(new PlanProgressBar.PlanProgressBarTextGenerator() {
            @Override
            public String generateText(PlanProgressBar progressBar, int value, int maxValue) {
//                return 100 * value / maxValue + "%";
                return String.valueOf(hasFinished);
            }
        });

        myHandler.setProgressBar(mRectProgressBar, mCircleProgressBar);

        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                count = 0;
//                executorService.execute(runnable);
                count = Math.round((hasFinished / (float) total) * 100);
                Log.e("123", "count : " + count);
                Message msg = new Message();
                msg.what = NEXT;
                msg.arg1 = count;
                myHandler.sendMessage(msg);
            }
        });
        findViewById(R.id.btn_rollback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRectProgressBar.setProgress(0);
                mCircleProgressBar.setProgress(0);
            }
        });

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

//            for (int i = 0; i <= 20; i++) {
//                try {
//                    count = (i + 1) * 5;
//                    if (i == 20) {
//                        Message msg = new Message();
//                        msg.what = STOP;
//                        myHandler.sendMessage(msg);
//                    } else {
//                        Message msg = new Message();
//                        msg.what = NEXT;
//                        msg.arg1 = count;
//                        myHandler.sendMessage(msg);
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
        }
    };

    private class ProgressHandler extends Handler {
        private WeakReference<PlanProgressBar> weakRectProgressBar;
        private WeakReference<PlanProgressBar> weakCircleProgressBar;

        void setProgressBar(PlanProgressBar rectProgressBar, PlanProgressBar circleProgressBar) {
            weakRectProgressBar = new WeakReference<>(rectProgressBar);
            weakCircleProgressBar = new WeakReference<>(circleProgressBar);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case STOP:
                    break;
                case NEXT:
                    if (!Thread.currentThread().isInterrupted()) {
                        if (weakRectProgressBar.get() != null && weakCircleProgressBar.get() != null) {
                            Toast.makeText(ProgressBarActivity.this, "" + msg.arg1, Toast.LENGTH_SHORT).show();
                            weakRectProgressBar.get().setProgress(msg.arg1);
                            weakCircleProgressBar.get().setProgress(msg.arg1);
                        }
                    }
                default:
                    break;
            }
        }
    }

}
