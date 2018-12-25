package com.work.basic;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.work.R;
import com.work.base.BaseActivity;

/**
 * Title:BasicViewActivity
 * <p>
 * Description:绘制基础
 * </p>
 *
 * @author Changbao
 * @date 2018/12/11 10:57
 */
public class BasicViewCenterActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_view_center);
    }
}
