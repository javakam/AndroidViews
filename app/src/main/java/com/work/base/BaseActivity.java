package com.work.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.work.floatbutton.FloatButton;

/**
 * Title: BaseActivity
 * <p>
 * Description:
 * </p>
 * Author Changbao
 * Date 2018/10/22  13:34
 */
public abstract class BaseActivity extends FragmentActivity {

    /**
     * 悬浮窗
     */
    private FloatButton mFloatButton = FloatButton.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mFloatButton.show(this);
    }
}