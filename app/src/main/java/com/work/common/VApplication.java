package com.work.common;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.work.BuildConfig;
import com.work.R;
import com.work.addresspicker.bean.AddressBean;
import com.work.floatbutton.FloatButtonActivity;
import com.work.floatwindow.FloatWindow;
import com.work.floatwindow.MoveType;
import com.work.floatwindow.PermissionListener;
import com.work.floatwindow.Screen;
import com.work.floatwindow.ViewStateListener;
import com.work.utils.ActivityTaskManager;
import com.work.utils.AppUtils;
import com.work.utils.DaoUtils;
import com.work.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Title: VApplication
 * <p>
 * Description:
 * </p>
 * Author Changbao
 * Date 2018/10/22  14:01
 */
public class VApplication extends Application {
    public static FloatWindow mFloatWindow;

    @Override
    public void onCreate() {
        super.onCreate();
        //1
        AppUtils.getInstance().initialize(this, BuildConfig.ROOT_DIR);
        //2
        List<Class<?>> list = new ArrayList<>();
        list.add(AddressBean.class);
        DaoUtils.initDatabase(this, 1, list);
//        Library.init(this, BuildConfig.ROOT_DIR , true);//+ "/" + BuildConfig.ROOT_PATH
//        IDictionary.initDictionary(this);

        //3 悬浮窗
        initFloatWindow();

        //4 监听Activity生命周期
        initActivitiesCallbacks();
    }

    private static final String TAG = "FloatWindow";

    private void initFloatWindow() {
        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setImageResource(R.drawable.selector_float_button);
//        imageView.setBackgroundResource(R.drawable.selector_float_button);

        mFloatWindow = FloatWindow.with(getApplicationContext())
                .setView(imageView)
                .setWidth(Screen.WIDTH, 0.15f) //设置悬浮控件宽高 - 按照屏幕比例
                .setHeight(Screen.WIDTH, 0.15f)
                .setX(Screen.WIDTH, 0.82f)//设置悬浮控件屏幕偏移
                .setY(Screen.HEIGHT, 0.5f)
                .setSideScope(UIUtils.getDimens(getApplicationContext(), R.dimen.size_60))//设置悬浮控件上下贴边判定范围
                .setMoveType(MoveType.SLIDE, 20, 20, 20, 20)
                //DecelerateInterpolator BounceInterpolator  AccelerateDecelerateInterpolator
                .setMoveStyle(300, new DecelerateInterpolator())
                .setFilter(true, MainActivity.class, FloatButtonActivity.class)
                .setViewStateListener(mViewStateListener)
                .setPermissionListener(mPermissionListener)
                .setDesktopShow(false) // 设置Home键回到桌面时，是否仍然显示悬浮控件
                .build();
    }

    private PermissionListener mPermissionListener = new PermissionListener() {
        @Override
        public void onSuccess() {
            Log.d(TAG, "onSuccess");
        }

        @Override
        public void onFail() {
            Log.d(TAG, "onFail");
        }
    };

    private ViewStateListener mViewStateListener = new ViewStateListener() {
        @Override
        public void onPositionUpdate(int x, int y) {
            Log.d(TAG, "onPositionUpdate: x=" + x + " y=" + y);
        }

        @Override
        public void onShow() {
            Log.d(TAG, "onShow");
        }

        @Override
        public void onHide() {
            Log.d(TAG, "onHide");
        }

        @Override
        public void onDismiss() {
            Log.d(TAG, "onDismiss");
        }

        @Override
        public void onMoveAnimStart() {
            Log.d(TAG, "onMoveAnimStart");
        }

        @Override
        public void onMoveAnimEnd() {
            Log.d(TAG, "onMoveAnimEnd");
        }

        @Override
        public void onBackToDesktop() {
            Log.d(TAG, "onBackToDesktop");
        }
    };

    private void initActivitiesCallbacks() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                ActivityTaskManager.getInstance().setCurrentActivity(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });

    }
}