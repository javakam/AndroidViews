package com.work.floatwindow;


import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.transition.ArcMotion;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import com.work.R;
import com.work.base.BaseActivity;
import com.work.floatwindow.transition.MorphTransition;
import com.work.utils.UIUtils;

/**
 * Title: DialogActivity
 * <p>
 * Description:
 * </p>
 *
 * @author Changbao
 * @date 2018/12/26  17:24
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class DialogActivity extends BaseActivity {
    private LinearLayout container;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_float_dialog);
        container = findViewById(R.id.container);

        View.OnClickListener dismissListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        };
        container.setOnClickListener(dismissListener);


        setupSharedElementTransitions();
    }

    public void setupSharedElementTransitions() {
        ArcMotion arcMotion = new ArcMotion();
        arcMotion.setMinimumHorizontalAngle(50f);
        arcMotion.setMinimumVerticalAngle(50f);

        Interpolator easeInOut = AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in);

        //100是随意给的一个数字，可以修改，需要注意的是这里调用container.getHeight()结果为0
        MorphTransition sharedEnter = new MorphTransition(ContextCompat.getColor(this, R.color.fab_background_color),
                ContextCompat.getColor(this, R.color.dialog_background_color), 100,
                getResources().getDimensionPixelSize(R.dimen.size_50), true);
        sharedEnter.setPathMotion(arcMotion);
        sharedEnter.setInterpolator(easeInOut);

        MorphTransition sharedReturn = new MorphTransition(ContextCompat.getColor(this, R.color.dialog_background_color),
                ContextCompat.getColor(this, R.color.fab_background_color),
                getResources().getDimensionPixelSize(R.dimen.size_50), 100, false);
        sharedReturn.setPathMotion(arcMotion);
        sharedReturn.setInterpolator(easeInOut);

        if (container != null) {
            sharedEnter.addTarget(container);
            sharedReturn.addTarget(container);
        }
        getWindow().setSharedElementEnterTransition(sharedEnter);
        getWindow().setSharedElementReturnTransition(sharedReturn);

        //todo 2018年12月26日 18:18:41
        Window window = getWindow();
//        window.setBackgroundDrawableResource(R.drawable.rectangle_float_dialog);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = (int) (UIUtils.getScreenWidth(this) * 0.8);
        params.height = UIUtils.getScreenHeight(this) / 3;
        window.setAttributes(params);

    }

    @Override
    public void onBackPressed() {
        dismiss();
    }

    public void dismiss() {
        setResult(Activity.RESULT_CANCELED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        }
    }
}
