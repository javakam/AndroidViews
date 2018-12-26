package com.work.base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.work.R;
import com.work.common.MainActivity;
import com.work.common.VApplication;
import com.work.floatbutton.FloatButton;
import com.work.floatwindow.FloatWindow;
import com.work.utils.ActivityTaskManager;
import com.work.utils.UIUtils;

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
    protected void onResume() {
        super.onResume();
        mFloatButton.show(this);

        //
        final FloatWindow floatWindow = VApplication.mFloatWindow;
        floatWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (floatWindow.isShowing()) {
                    floatWindow.hide();
                }
                final Dialog dialog = new Dialog(BaseActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);

                View contentView = LayoutInflater.from(BaseActivity.this).inflate(R.layout.item_float_dialog, null, false);
                ImageView ivHome = contentView.findViewById(R.id.iv_float_home);
                ImageView ivVoice = contentView.findViewById(R.id.iv_float_voice);
                ivVoice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(BaseActivity.this, "语音控制", Toast.LENGTH_SHORT).show();
                    }
                });

                ivHome.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String curActivity = ActivityTaskManager.getInstance().getCurrentActivity().getClass().getName();
                        Log.e("123", "当前Activity：" + curActivity + "  首页：" + MainActivity.class.getName());
                        if (!TextUtils.equals(MainActivity.class.getName(), curActivity)) {
                            Intent intent = new Intent(BaseActivity.this, floatWindow.getActivities()[0]);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            dialog.dismiss();
                        }
                    }
                });

                dialog.setContentView(contentView);
                Window window = dialog.getWindow();
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = (int) (UIUtils.getScreenWidth(BaseActivity.this) * 0.8);
                params.height = UIUtils.getScreenHeight(BaseActivity.this) / 3;
                window.setAttributes(params);
                window.setBackgroundDrawableResource(R.drawable.rectangle_float_dialog);

                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface d) {
                        if (!floatWindow.isShowing()) {
                            floatWindow.show();
                        }
                    }
                });
                dialog.show();
            }
        });
    }
}
