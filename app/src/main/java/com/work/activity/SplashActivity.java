package com.work.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.work.R;
import com.work.base.BaseActivity;
import com.work.utils.PermissionUtils;

/**
 * Title: SplashActivity
 * <p>
 * Description:
 * </p>
 * Author Changbao
 * Date 2018/11/7  15:16
 */
public class SplashActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.mipmap.ic_launcher_round);
        setContentView(imageView);

        PermissionUtils.request(this);
    }
}
