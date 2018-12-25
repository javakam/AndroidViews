package com.work.addresspicker;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;

import com.work.R;


/**
 * Title:BottomDialog
 * <p>
 * Description:从底部弹出的Dialog
 * </p>
 * Author Jming.L
 * Date 2018/9/15 10:42
 */
public abstract class BottomDialog extends Dialog {
    public static final int DEFAULT_STYLE = R.style.BottomDialogStyle;
    private static final String TAG = "BottomDialog";
    private final static int ANIMATION_DURATION = 300; // 动画时长

    public Context mContext;
    public boolean mIsAnimating = false;

    public BottomDialog(Context context, int themeResId) {
        super(context, themeResId == 0 ? DEFAULT_STYLE : themeResId);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariables();
        this.setCancelable(true);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(LayoutInflater.from(mContext).inflate(getLayout(), null));
        initWindow();
        initView();
    }

    @SuppressLint("ResourceType")
    public void initWindow() {
        Window window = getWindow();
        if (window != null) {
            hideNavigation(window);
            window.setGravity(Gravity.BOTTOM | Gravity.CENTER);
            WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            if (manager != null) {
                DisplayMetrics dm = new DisplayMetrics();
                manager.getDefaultDisplay().getMetrics(dm);
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.width = dm.widthPixels;
                lp.height = (int) (dm.heightPixels * 0.6);//弹窗高度
                window.setAttributes(lp);
                //window.setWindowAnimations(R.style.AddressDialogAnimation);
            }
        }
    }

    public abstract void initVariables();

    public abstract void initView();

    public abstract int getLayout();

    @Override
    public void show() {
        super.show();
//        animateUp();
    }

    @Override
    public void dismiss() {
        super.dismiss();
//       if (mIsAnimating) {
//           return;
//       }
//       animateDown();
    }

    /**
     * BottomSheet升起动画
     */
    private void animateUp() {
        TranslateAnimation translate = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f
        );
        AlphaAnimation alpha = new AlphaAnimation(0, 1);
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(translate);
        set.addAnimation(alpha);
        set.setInterpolator(new DecelerateInterpolator());
        set.setDuration(ANIMATION_DURATION);
        set.setFillAfter(true);
        //mContentView.startAnimation(set);
        getWindow().getDecorView().startAnimation(set);
    }

    /**
     * BottomSheet降下动画
     */
    private void animateDown() {
        TranslateAnimation translate = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f
        );
        AlphaAnimation alpha = new AlphaAnimation(1, 0);
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(translate);
        set.addAnimation(alpha);
        set.setInterpolator(new DecelerateInterpolator());
        set.setDuration(ANIMATION_DURATION);
        set.setFillAfter(true);
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mIsAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIsAnimating = false;
                /**
                 * Bugfix： Attempting to destroy the window while drawing!
                 */
                getWindow().getDecorView().post(new Runnable() {
                    @Override
                    public void run() {
                        // java.lang.IllegalArgumentException: View=com.android.internal.policy.PhoneWindow$DecorView{22dbf5b V.E...... R......D 0,0-1080,1083} not attached to window manager
                        // 在dismiss的时候可能已经detach了，简单try-catch一下
                        try {
                            BottomDialog.super.dismiss();
                        } catch (Exception e) {
                            Log.w(TAG, "dismiss error\n" + Log.getStackTraceString(e));
                        }
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        getWindow().getDecorView().startAnimation(set);
        //mContentView.startAnimation(set);
    }

    /**
     * 隐藏底部导航栏
     */
    private void hideNavigation(Window window) {
        final View decorView = window.getDecorView();
        final int option = View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(option);
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(option);
                }
            }
        });
    }
}