package com.work.common;

import com.work.R;
import com.work.addresspicker.AddressPickerActivity;
import com.work.basic.BasicViewActivity;
import com.work.basic.BasicViewCenterActivity;
import com.work.floatbutton.FloatButtonActivity;
import com.work.progressbar.ProgressBarActivity;
import com.work.progressbar.ProgressBarActivity2;
import com.work.progressbar.ProgressBarActivity3;
import com.work.recycler.horizon_date.HorizonRecycleViewActivity;
import com.work.shadowline.ShadowLineActivity;
import com.work.spinner.SpinnerActivity;

/**
 * Title: UIRouter
 * <p>
 * Description:
 * </p>
 * Author Changbao
 * Date 2018/10/22  13:27
 */
public enum UIRouter {
    /**
     *
     */
    PROGRESSBAR1(0, "进度圆环1", R.drawable.ic_launcher_background, ProgressBarActivity.class),
    PROGRESSBAR2(1, "进度圆环2", R.drawable.ic_launcher_background, ProgressBarActivity2.class),
    PROGRESSBAR3(2, "进度圆环3", R.drawable.ic_launcher_background, ProgressBarActivity3.class),
    DATE_SELECT(3, "日期选择", R.drawable.ic_launcher_background, HorizonRecycleViewActivity.class),
    ADDRESS_SELECT(4, "地址选择", R.drawable.ic_launcher_background, AddressPickerActivity.class),
    SHADOW_LINE(5, "阴影效果", R.drawable.ic_launcher_background, ShadowLineActivity.class),
    BASIC_VIEW(6, "绘制基础1-3", R.drawable.ic_launcher_background, BasicViewActivity.class),
    BASIC_VIEW_CENTER(7, "绘制基础4", R.drawable.ic_launcher_background, BasicViewCenterActivity.class),
    //    STRING_BUILDER(8, "多层SpannableStringBuilder", R.drawable.ic_launcher_background, StringBuilderActivity.class),
    SPINNER(8, "下拉框", R.drawable.ic_launcher_background, SpinnerActivity.class),
    FLOAT_BUTTON(9, "全局悬浮窗", R.drawable.ic_launcher_background, FloatButtonActivity.class);

    public static Class<?> findClassById(int id) {
        for (UIRouter r : UIRouter.values()) {
            if (r.getId() == id) {
                return r.clz;
            }
        }
        return null;
    }

    private int id;
    private String description;
    private int icon;
    private Class<?> clz;

    UIRouter(int id, String name, int icon, Class<?> clz) {
        this.id = id;
        this.description = name;
        this.icon = icon;
        this.clz = clz;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public int getIcon() {
        return icon;
    }

    public Class<?> getClz() {
        return clz;
    }
}
