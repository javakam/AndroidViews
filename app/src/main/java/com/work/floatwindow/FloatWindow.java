package com.work.floatwindow;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.work.floatwindow.platform.Miui;
import com.work.utils.UIUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Title:SimpleFloatWindow
 * <p>
 * Description:modify https://github.com/yhaolpz
 * </p>
 *
 * @author Changbao
 * @date 2018/12/25 14:18
 */
public class FloatWindow {

    private static final String TAG = "FloatWindow";

    private static FloatWindow.Builder mBuilder;
    private AbstractFloatView mFloatView;
    private FloatLifecycle mFloatLifecycle;
    private Class[] mActivities;
    private boolean isShow;
    private boolean once = true;
    private ValueAnimator mAnimator;
    private TimeInterpolator mDecelerateInterpolator;
    private float downX;
    private float downY;
    private float upX;
    private float upY;
    private boolean mClick = false;
    private int mSlop;

    private FloatWindow() {
    }

    public FloatWindow(FloatWindow.Builder builder) {
        mBuilder = builder;

        if (mBuilder.mMoveType == MoveType.FIXED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                mFloatView = new FloatPhone(builder.mApplicationContext, mBuilder.mPermissionListener);
            } else {
                mFloatView = new FloatToast(builder.mApplicationContext);
            }
        } else {
            mFloatView = new FloatPhone(builder.mApplicationContext, mBuilder.mPermissionListener);
            initTouchEvent();
        }
        mFloatView.setSize(mBuilder.mWidth, mBuilder.mHeight);
        mFloatView.setGravity(mBuilder.gravity, mBuilder.xOffset, mBuilder.yOffset);
        mFloatView.setView(mBuilder.mView);
        mFloatLifecycle = new FloatLifecycle(mBuilder.mApplicationContext, mBuilder.mShow,
                mActivities = mBuilder.mActivities, new FloatLifecycle.LifecycleListener() {
            @Override
            public void onShow() {
                show();
            }

            @Override
            public void onHide() {
                hide();
            }

            @Override
            public void onBackToDesktop() {
                if (!mBuilder.mDesktopShow) {
                    hide();
                }
                if (mBuilder.mViewStateListener != null) {
                    mBuilder.mViewStateListener.onBackToDesktop();
                }
            }
        });
    }

    @MainThread
    public static FloatWindow.Builder with(@NonNull Context applicationContext) {
        return mBuilder = new FloatWindow.Builder(applicationContext);
    }

    public void show() {
        if (once) {
            mFloatView.init();
            once = false;
            isShow = true;
        } else {
            if (isShow) {
                return;
            }
            getView().setVisibility(View.VISIBLE);
            isShow = true;
        }
        if (mBuilder.mViewStateListener != null) {
            mBuilder.mViewStateListener.onShow();
        }
    }

    public void hide() {
        if (once || !isShow) {
            return;
        }
        getView().setVisibility(View.INVISIBLE);
        isShow = false;
        if (mBuilder.mViewStateListener != null) {
            mBuilder.mViewStateListener.onHide();
        }
    }

    public boolean isShowing() {
        return isShow;
    }

    public boolean isClickable() {
        if (mBuilder != null) {
            return mBuilder.mView.isClickable();
        }
        return true;
    }

    public void dismiss() {
        mFloatView.dismiss();
        isShow = false;
        if (mBuilder.mViewStateListener != null) {
            mBuilder.mViewStateListener.onDismiss();
        }
    }

    public void updateX(int x) {
        checkMoveType();
        mBuilder.xOffset = x;
        mFloatView.updateX(x);
    }

    public void updateY(int y) {
        checkMoveType();
        mBuilder.yOffset = y;
        mFloatView.updateY(y);
    }

    public void updateX(int screenType, float ratio) {
        checkMoveType();
        mBuilder.xOffset = (int) ((screenType == Screen.WIDTH ? mBuilder.mScreenWidth : mBuilder.mScreenHeight) * ratio);
        mFloatView.updateX(mBuilder.xOffset);
    }

    public void updateY(int screenType, float ratio) {
        checkMoveType();
        mBuilder.yOffset = (int) ((screenType == Screen.WIDTH ? mBuilder.mScreenWidth : mBuilder.mScreenHeight) * ratio);
        mFloatView.updateY(mBuilder.yOffset);
    }

    public int getX() {
        return mFloatView.getX();
    }

    public int getY() {
        return mFloatView.getY();
    }

    public View getView() {
        mSlop = ViewConfiguration.get(mBuilder.mApplicationContext).getScaledTouchSlop();
        return mBuilder.mView;
    }

    public Class[] getActivities() {
        return mActivities;
    }

    public void setOnClickListener(View.OnClickListener l) {
        if (mBuilder != null) {
            mBuilder.mView.setOnClickListener(l);
        }
    }

    public void setClickable(boolean clickable) {
        if (mBuilder != null) {
            mBuilder.mView.setClickable(clickable);
        }
    }

    private void checkMoveType() {
        if (mBuilder.mMoveType == MoveType.FIXED) {
            throw new IllegalArgumentException("FloatWindow of this tag is not allowed to move!");
        }
    }

    private void initTouchEvent() {
        switch (mBuilder.mMoveType) {
            case MoveType.INACTIVE:
                break;
            default:
                getView().setOnTouchListener(onTouchListener);
        }
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        float lastX, lastY, deltaX, deltaY;
        int newX, newY;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    getView().setPressed(true);
                    downX = event.getRawX();
                    downY = event.getRawY();
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                    cancelAnimator();
                    break;
                case MotionEvent.ACTION_MOVE:
                    deltaX = event.getRawX() - lastX;
                    deltaY = event.getRawY() - lastY;
                    newX = (int) (mFloatView.getX() + deltaX);
                    newY = (int) (mFloatView.getY() + deltaY);
                    mFloatView.updateXY(newX, newY);
                    if (mBuilder.mViewStateListener != null) {
                        mBuilder.mViewStateListener.onPositionUpdate(newX, newY);
                    }
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    upX = event.getRawX();
                    upY = event.getRawY();
                    mClick = (Math.abs(upX - downX) > mSlop) || (Math.abs(upY - downY) > mSlop);

                    int startX = 0, endX = 0, startY = 0, endY = 0;
                    if (mBuilder.mMoveType == MoveType.SLIDE) {
                        Log.e("123", "屏幕：" + mBuilder.mScreenWidth + "   " + mBuilder.mScreenHeight);
                        Log.w("123", "抬起时： upX: " + upX + "    upY: " + upY
                                + "   getX: " + mFloatView.getX() + "   getY: " + mFloatView.getY()
                                + "   状态栏：" + mBuilder.mStatusBarHeight);

                        //相对屏幕getView().getLocationOnScreen()的 Y值 比 mFloatView.getY() 多一个状态栏高度
                        startY = mFloatView.getY();
                        endY = mFloatView.getY();

                        boolean overScrollY = false;
                        if (startY <= mBuilder.mScopeVertical) {//default 0
                            endY = mBuilder.mSlideTopMargin;
                            overScrollY = true;
                        } else if (startY > mBuilder.mScreenHeight - mBuilder.mScopeVertical - mBuilder.mHeight - mBuilder.mSlideBottomMargin - mBuilder.mStatusBarHeight) {
                            endY = mBuilder.mScreenHeight - mBuilder.mHeight - mBuilder.mSlideBottomMargin - mBuilder.mStatusBarHeight;
                            overScrollY = true;
                        }

                        startX = mFloatView.getX();
                        if (overScrollY) {
                            if (startX > mBuilder.mScreenWidth - mBuilder.mWidth - mBuilder.mSlideRightMargin) {
                                endX = mBuilder.mScreenWidth - mBuilder.mWidth - mBuilder.mSlideRightMargin;
                            } else if (startX < mBuilder.mSlideLeftMargin) {
                                endX = mBuilder.mSlideLeftMargin;
                            } else {
                                endX = mFloatView.getX();
                            }
                        } else {
                            endX = (startX * 2 + mBuilder.mWidth > mBuilder.mScreenWidth) ?
                                    mBuilder.mScreenWidth - mBuilder.mWidth - mBuilder.mSlideRightMargin :
                                    mBuilder.mSlideLeftMargin;
                        }
                    } else if (mBuilder.mMoveType == MoveType.BACK) {
                        startX = mFloatView.getX();
                        endX = mBuilder.xOffset;
                        startY = mFloatView.getY();
                        endY = mBuilder.yOffset;
                    }

                    PropertyValuesHolder pvhX = PropertyValuesHolder.ofInt("x", startX, endX);
                    PropertyValuesHolder pvhY = PropertyValuesHolder.ofInt("y", startY, endY);
                    mAnimator = ObjectAnimator.ofPropertyValuesHolder(pvhX, pvhY);
                    mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int x = (int) animation.getAnimatedValue("x");
                            int y = (int) animation.getAnimatedValue("y");
                            mFloatView.updateXY(x, y);
                            if (mBuilder.mViewStateListener != null) {
                                mBuilder.mViewStateListener.onPositionUpdate(x, y);
                            }
                        }
                    });
                    startAnimator();
                    break;
                default:
                    break;
            }
            return mClick;
        }
    };

    private void startAnimator() {
        if (mBuilder.mInterpolator == null) {
            if (mDecelerateInterpolator == null) {
                mDecelerateInterpolator = new DecelerateInterpolator();
            }
            mBuilder.mInterpolator = mDecelerateInterpolator;
        }
        mAnimator.setInterpolator(mBuilder.mInterpolator);
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimator.removeAllUpdateListeners();
                mAnimator.removeAllListeners();
                mAnimator = null;
                if (mBuilder.mViewStateListener != null) {
                    //解决 selector state_pressed失效问题
                    getView().setPressed(false);
                    mBuilder.mViewStateListener.onMoveAnimEnd();
                }
            }
        });
        mAnimator.setDuration(mBuilder.mDuration).start();
        if (mBuilder.mViewStateListener != null) {
            mBuilder.mViewStateListener.onMoveAnimStart();
        }
    }

    private void cancelAnimator() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
    }

    abstract class AbstractFloatView {

        abstract void setSize(int width, int height);

        abstract void setView(View view);

        abstract void setGravity(int gravity, int xOffset, int yOffset);

        abstract void init();

        abstract void dismiss();

        void updateXY(int x, int y) {
        }

        void updateX(int x) {
        }

        void updateY(int y) {
        }

        int getX() {
            return 0;
        }

        int getY() {
            return 0;
        }
    }

    /**
     * 自定义 toast 方式，无需申请权限
     * 当前版本暂时用 TYPE_TOAST 代替，后续版本可能会再融入此方式
     */
    class FloatToast extends AbstractFloatView {

        private Toast toast;

        private Object mTN;
        private Method show;
        private Method hide;

        private int mWidth;
        private int mHeight;

        FloatToast(Context applicationContext) {
            toast = new Toast(applicationContext);
        }

        @Override
        public void setSize(int width, int height) {
            mWidth = width;
            mHeight = height;
        }

        @Override
        public void setView(View view) {
            toast.setView(view);
            initTN();
        }

        @Override
        public void setGravity(int gravity, int xOffset, int yOffset) {
            toast.setGravity(gravity, xOffset, yOffset);
        }

        @Override
        public void init() {
            try {
                show.invoke(mTN);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void dismiss() {
            try {
                hide.invoke(mTN);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void initTN() {
            try {
                Field tnField = toast.getClass().getDeclaredField("mTN");
                tnField.setAccessible(true);
                mTN = tnField.get(toast);
                show = mTN.getClass().getMethod("show");
                hide = mTN.getClass().getMethod("hide");
                Field tnParamsField = mTN.getClass().getDeclaredField("mParams");
                tnParamsField.setAccessible(true);
                WindowManager.LayoutParams params = (WindowManager.LayoutParams) tnParamsField.get(mTN);
                params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                params.width = mWidth;
                params.height = mHeight;
                params.windowAnimations = 0;
                Field tnNextViewField = mTN.getClass().getDeclaredField("mNextView");
                tnNextViewField.setAccessible(true);
                tnNextViewField.set(mTN, toast.getView());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class FloatPhone extends AbstractFloatView {

        private final Context mContext;
        private final WindowManager mWindowManager;
        private final WindowManager.LayoutParams mLayoutParams;
        private View mView;
        private int mX, mY;
        private boolean isRemove = false;
        private PermissionListener mPermissionListener;

        FloatPhone(Context applicationContext, PermissionListener permissionListener) {
            mContext = applicationContext;
            mPermissionListener = permissionListener;
            mWindowManager = (WindowManager) applicationContext.getSystemService(Context.WINDOW_SERVICE);
            mLayoutParams = new WindowManager.LayoutParams();
            mLayoutParams.format = PixelFormat.RGBA_8888;
            mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
            mLayoutParams.windowAnimations = 0;
        }

        @Override
        public void setSize(int width, int height) {
            mLayoutParams.width = width;
            mLayoutParams.height = height;
        }

        @Override
        public void setView(View view) {
            mView = view;
        }

        @Override
        public void setGravity(int gravity, int xOffset, int yOffset) {
            mLayoutParams.gravity = gravity;
            mLayoutParams.x = mX = xOffset;
            mLayoutParams.y = mY = yOffset;
        }

        @Override
        public void init() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                requestPermissionInner();
            } else if (Miui.rom()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissionInner();
                } else {
                    mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                    Miui.req(mContext, new PermissionListener() {
                        @Override
                        public void onSuccess() {
                            mWindowManager.addView(mView, mLayoutParams);
                            if (mPermissionListener != null) {
                                mPermissionListener.onSuccess();
                            }
                        }

                        @Override
                        public void onFail() {
                            if (mPermissionListener != null) {
                                mPermissionListener.onFail();
                            }
                        }
                    });
                }
            } else {
                try {
                    mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
                    mWindowManager.addView(mView, mLayoutParams);
                } catch (Exception e) {
                    mWindowManager.removeView(mView);
                    Log.e(TAG, "TYPE_TOAST 失败");
                    requestPermissionInner();
                }
            }
        }

        private void requestPermissionInner() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//Android 8.0
                mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            FloatActivity.request(mContext, new PermissionListener() {
                @Override
                public void onSuccess() {
                    mWindowManager.addView(mView, mLayoutParams);
                    if (mPermissionListener != null) {
                        mPermissionListener.onSuccess();
                    }
                }

                @Override
                public void onFail() {
                    if (mPermissionListener != null) {
                        mPermissionListener.onFail();
                    }
                }
            });
        }

        @Override
        public void dismiss() {
            isRemove = true;
            mWindowManager.removeView(mView);
        }

        @Override
        public void updateXY(int x, int y) {
            if (!isRemove) {
                mLayoutParams.x = mX = x;
                mLayoutParams.y = mY = y;
                mWindowManager.updateViewLayout(mView, mLayoutParams);
            }
        }

        @Override
        void updateX(int x) {
            if (!isRemove) {
                mLayoutParams.x = mX = x;
                mWindowManager.updateViewLayout(mView, mLayoutParams);
            }
        }

        @Override
        void updateY(int y) {
            if (!isRemove) {
                mLayoutParams.y = mY = y;
                mWindowManager.updateViewLayout(mView, mLayoutParams);
            }
        }

        @Override
        int getX() {
            return mX;
        }

        @Override
        int getY() {
            return mY;
        }
    }

    public static class Builder {
        private Context mApplicationContext;
        private View mView;
        private int mLayoutId;
        private int mWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
        private int mHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        private int gravity = Gravity.TOP | Gravity.START;
        private int xOffset;
        private int yOffset;
        private boolean mShow = true;
        private Class[] mActivities;
        private int mMoveType = MoveType.SLIDE;
        private int mScopeVertical;
        private int mSlideLeftMargin;
        private int mSlideRightMargin;
        private int mSlideTopMargin;
        private int mSlideBottomMargin;
        private long mDuration = 300;
        private TimeInterpolator mInterpolator;
        private boolean mDesktopShow;
        private PermissionListener mPermissionListener;
        private ViewStateListener mViewStateListener;
        private int mScreenWidth, mScreenHeight, mStatusBarHeight;

        private Builder() {
        }

        Builder(Context applicationContext) {
            mApplicationContext = applicationContext;
            mScreenWidth = UIUtils.getScreenWidth(applicationContext);
            mScreenHeight = UIUtils.getScreenHeight(applicationContext);
            mStatusBarHeight = UIUtils.getStatusBarHeight(applicationContext);
        }

        public FloatWindow.Builder setView(@NonNull View view) {
            mView = view;
            return this;
        }

        public FloatWindow.Builder setView(@LayoutRes int layoutId) {
            mLayoutId = layoutId;
            return this;
        }

        public FloatWindow.Builder setWidth(int width) {
            mWidth = width;
            return this;
        }

        public FloatWindow.Builder setHeight(int height) {
            mHeight = height;
            return this;
        }

        public FloatWindow.Builder setWidth(@Screen.screenType int screenType, float ratio) {
            mWidth = (int) ((screenType == Screen.WIDTH ? mScreenWidth : mScreenHeight) * ratio);
            return this;
        }

        public FloatWindow.Builder setHeight(@Screen.screenType int screenType, float ratio) {
            mHeight = (int) ((screenType == Screen.WIDTH ? mScreenWidth : mScreenHeight) * ratio);
            return this;
        }

        public FloatWindow.Builder setX(int x) {
            xOffset = x;
            return this;
        }

        public FloatWindow.Builder setY(int y) {
            yOffset = y;
            return this;
        }

        public FloatWindow.Builder setX(@Screen.screenType int screenType, float ratio) {
            xOffset = (int) ((screenType == Screen.WIDTH ? mScreenWidth : mScreenHeight) * ratio);
            return this;
        }

        public FloatWindow.Builder setY(@Screen.screenType int screenType, float ratio) {
            yOffset = (int) ((screenType == Screen.WIDTH ? mScreenWidth : mScreenHeight) * ratio);
            return this;
        }

        /**
         * 设置 Activity 过滤器，用于指定在哪些界面显示悬浮窗，默认全部界面都显示
         *
         * @param show       　过滤类型,子类类型也会生效
         * @param activities 　过滤界面
         */
        public FloatWindow.Builder setFilter(boolean show, @NonNull Class... activities) {
            mShow = show;
            mActivities = activities;
            return this;
        }

        /**
         * 设置上下滑动时贴边的判定范围
         *
         * @param scopeVertical
         * @return
         */
        public FloatWindow.Builder setSideScope(@MoveType.MOVE_TYPE int scopeVertical) {
            mScopeVertical = scopeVertical;
            return this;
        }

        public FloatWindow.Builder setMoveType(@MoveType.MOVE_TYPE int moveType) {
            return setMoveType(moveType, 0, 0, 0, 0);
        }

        /**
         * 设置带边距的贴边动画，只有 moveType 为 MoveType.slide，设置边距才有意义，这个方法不标准，后面调整
         *
         * @param moveType         贴边动画 MoveType.slide
         * @param slideLeftMargin  贴边动画左边距，默认为 0
         * @param slideRightMargin 贴边动画右边距，默认为 0
         */
        public FloatWindow.Builder setMoveType(@MoveType.MOVE_TYPE int moveType, int slideLeftMargin, int slideRightMargin,
                                               int slideTopMargin, int slideBottomMargin) {
            mMoveType = moveType;
            mSlideLeftMargin = slideLeftMargin;
            mSlideRightMargin = slideRightMargin;
            mSlideTopMargin = slideTopMargin;
            mSlideBottomMargin = slideBottomMargin;
            return this;
        }

        public FloatWindow.Builder setMoveStyle(long duration, @Nullable TimeInterpolator interpolator) {
            mDuration = duration;
            mInterpolator = interpolator;
            return this;
        }

        public FloatWindow.Builder setDesktopShow(boolean show) {
            mDesktopShow = show;
            return this;
        }

        public FloatWindow.Builder setPermissionListener(PermissionListener listener) {
            mPermissionListener = listener;
            return this;
        }

        public FloatWindow.Builder setViewStateListener(ViewStateListener listener) {
            mViewStateListener = listener;
            return this;
        }

        public FloatWindow build() {
            if (mView == null && mLayoutId == 0) {
                throw new IllegalArgumentException("View has not been set!");
            }
            if (mView == null) {
                mView = UIUtils.inflate(mApplicationContext, mLayoutId);
            }
            return new FloatWindow(this);
        }
    }
}