package com.work.floatbutton;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
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

    private WindowManager.LayoutParams mLayoutParams = null;
    private static FloatButton floatButton;
    private ImageView mImageView;
    private Activity mActivity;
    private WindowManager mWindowManager;
    private List<Class> mActivities;
    private Class mHomePage;//首页

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
        defaultDisplay.getSize(new Point());

        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.packageName = mActivity.getPackageName();
        mLayoutParams.width = 100;
        mLayoutParams.height = 100;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else  {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;//窗口位置
        mLayoutParams.x = 0;
        mLayoutParams.y = 5;


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
//        mImageView.setOnTouchListener(mTouchListener);
        mImageView.setOnTouchListener(new View.OnTouchListener() {
            private long mDownTime;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mDownTime = event.getDownTime();
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (event.getEventTime() - mDownTime > 200) {
                        mLayoutParams.x = (int) event.getRawX() - 600;
                        mLayoutParams.y = (int) event.getRawY();
                        mWindowManager.updateViewLayout(mImageView, mLayoutParams);
                    }
                }
                return false;
            }
        });

        mWindowManager.addView(mImageView, mLayoutParams);
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
        int startX, startY;  //起始点
        boolean isMove;  //是否在移动
        long startTime;
        int finalMoveX;  //最后通过动画将mView的X轴坐标移动到finalMoveX
        int statusBarHeight;  //解决mViewy坐标不准确的bug，这里需要减去状态栏的高度

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = (int) event.getX();
                    startY = (int) event.getY();
                    startTime = System.currentTimeMillis();
                    int resourceId = mActivity.getResources().getIdentifier("status_bar_height", "dimen", "android");
                    if (resourceId > 0) {
                        statusBarHeight = mActivity.getResources().getDimensionPixelSize(resourceId);
                    }
                    isMove = false;
                    return false;
                case MotionEvent.ACTION_MOVE:
                    mLayoutParams.x = (int) (event.getRawX() - startX);
                    //这里修复了刚开始移动的时候，悬浮窗的y坐标是不正确的，要减去状态栏的高度，可以将这个去掉运行体验一下
                    mLayoutParams.y = (int) (event.getRawY() - startY - statusBarHeight);
                    updateViewLayout();
                    isMove = true;

                    return true;
                case MotionEvent.ACTION_UP:
                    long curTime = System.currentTimeMillis();
                    isMove = curTime - startTime > 100;

                    //判断mView是在Window中的位置，以中间为界
                    if (mLayoutParams.x + mImageView.getMeasuredWidth() / 2 >= mWindowManager.getDefaultDisplay().getWidth() / 2) {
                        finalMoveX = mWindowManager.getDefaultDisplay().getWidth() - mImageView.getMeasuredWidth();
                    } else {
                        finalMoveX = 0;
                    }

                    //使用动画移动mView
                    ValueAnimator animator = ValueAnimator.ofInt(mLayoutParams.x, finalMoveX).setDuration(Math.abs(mLayoutParams.x - finalMoveX));
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            mLayoutParams.x = (int) animation.getAnimatedValue();
                            updateViewLayout();
                        }
                    });
                    animator.start();
                    return isMove;
                default:
                    break;
            }
            return false;
        }
    };

    private void updateViewLayout() {
        if (mWindowManager != null && mImageView != null) {
            mWindowManager.updateViewLayout(mImageView, mLayoutParams);   //更新mView 的位置
        }
    }

    /**
     * 更新设置页面数据
     */
    private void updateSettingTableView() {
    }
}