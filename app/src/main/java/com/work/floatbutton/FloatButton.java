package com.work.floatbutton;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.work.R;
import com.work.common.MainActivity;
import com.work.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Title:FloatButton
 * <p>
 * Description:桌面漂浮小球
 * </p>
 * Author Jming.L
 * Date 2018/9/18 16:45
 */
public class FloatButton {

    private WindowManager.LayoutParams mParams = null;
    private static FloatButton floatButton;
    private ImageView mImageView;
    private Activity mActivity;
    private WindowManager mWindowManager;
    private List<Class> mActivities;
    private Class mHomePage;//首页


    private int mTag = 0;
    private int midX;
    private int midY;
    private int mOldOffsetX;
    private int mOldOffsetY;

    private FloatButton() {
        mActivities = new ArrayList<>();
        try {
            mActivities.add(mHomePage = Class.forName(MainActivity.class.getName()));
            mActivities.add(Class.forName(FloatButtonActivity.class.getName()));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public synchronized static FloatButton getInstance() {
        if (floatButton == null) {
            floatButton = new FloatButton();
        }
        return floatButton;
    }

    /**
     * 是否应当显示悬浮窗
     */
    private boolean shouldShow() {
        Class clazz = mActivity.getClass();
        return mActivities.contains(clazz);
    }

    public void show(Activity activity) {
        mActivity = activity;
        //先进行权限设置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(mActivity)) {
                UIUtils.showToast(mActivity, "请设置悬浮框在其他应用上层显示");
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                mActivity.startActivity(intent);
                return;
            }
        }
        if (shouldShow()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(mActivity)) {
                return;
            }
            if (mImageView == null || mImageView.getParent() == null) {
                showWindow();
            }
        } else {
            hide();
        }
    }

    /**
     * 显示全局按钮
     */
    @SuppressLint("ClickableViewAccessibility")
    private void showWindow() {
        mImageView = new ImageView(mActivity);
        mWindowManager = (WindowManager) mActivity.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = mWindowManager.getDefaultDisplay();
        defaultDisplay. getSize(new Point());

        DisplayMetrics dm = new DisplayMetrics();
        defaultDisplay.getMetrics(dm);
        midX = dm.widthPixels / 2;
        midY = dm.heightPixels / 2;

        mParams = new WindowManager.LayoutParams();
        mParams.packageName = mActivity.getPackageName();
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        } else {
            mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        }
        mParams.format = PixelFormat.RGBA_8888;
        mParams.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        mParams.x = 0;
        mParams.y = 5;


        mImageView.setImageResource(R.drawable.selector_float_button);
        mImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mHomePage != mActivity.getClass()) {
                    Intent intent = new Intent(mActivity, mActivities.get(0));
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mActivity.startActivity(intent);
                }
            }
        });
        mImageView.setOnTouchListener(mTouchListener);
//        mImageView.setOnTouchListener(new View.OnTouchListener() {
//            private long mDownTime;
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    mDownTime = event.getDownTime();
//                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
//                    if (event.getEventTime() - mDownTime > 200) {
//                        mParams.x = (int) event.getRawX() - 600;
//                        mParams.y = (int) event.getRawY();
//                        mWindowManager.updateViewLayout(mImageView, mParams);
//                    }
//                }
//                return false;
//            }
//        });

        mWindowManager.addView(mImageView, mParams);
    }

    /**
     * 删除全局按钮
     */
    private void hide() {
        if (mImageView != null && mImageView.getParent() != null) {
            mWindowManager.removeView(mImageView);
        }
    }


    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        float lastX, lastY;
        int paramX, paramY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final int action = event.getAction();

            float x = event.getRawX();
            float y = event.getRawY();

            if (mTag == 0) {
                mOldOffsetX = mParams.x; // 偏移量
                mOldOffsetY = mParams.y; // 偏移量
            }

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    lastX = x;
                    lastY = y;
                    paramX = mParams.x;
                    paramY = mParams.y;
                    break;

                case MotionEvent.ACTION_MOVE:
                    int dx = (int) (x - lastX);
                    int dy = (int) (y - lastY);
                    mParams.x = paramX + dx;
                    mParams.y = paramY + dy;
                    mTag = 1;

                    // 更新悬浮窗位置
                    mWindowManager.updateViewLayout(mImageView, mParams);
                    break;

                case MotionEvent.ACTION_UP:
                    int newOffsetX = mParams.x;
                    int newOffsetY = mParams.y;
                    if (mOldOffsetX == newOffsetX && mOldOffsetY == newOffsetY) {
                        updateSettingTableView();

                        if (Math.abs(mOldOffsetX) > midX) {
                            if (mOldOffsetX > 0) {
                                mOldOffsetX = midX;
                            } else {
                                mOldOffsetX = -midX;
                            }
                        }

                        if (Math.abs(mOldOffsetY) > midY) {
                            if (mOldOffsetY > 0) {
                                mOldOffsetY = midY;
                            } else {
                                mOldOffsetY = -midY;
                            }
                        }
                    } else {
                        mTag = 0;
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    /**
     * 更新设置页面数据
     */
    private void updateSettingTableView() {
    }
}