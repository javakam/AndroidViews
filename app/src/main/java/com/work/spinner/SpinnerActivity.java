package com.work.spinner;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.work.R;
import com.work.base.BaseActivity;
import com.work.utils.DateUtils;
import com.work.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Title:SpinnerActivity
 * <p>
 * Description:下拉框
 * </p>
 *
 * @author Changbao
 * @date 2018/12/18 11:15
 */
public class SpinnerActivity extends BaseActivity {
    private static final String TEXT_ALL = "全部";
    private static final String TEXT_OTHER = "其他";
    private static final String TEXT_OTHER_PKG = "其他服务包";

    private SingleChoiceSpinner spinnerYear;
    private SingleChoiceSpinner spinnerObject;
    private MultiChoiceSpinner multiChoiceSpinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinner);

        spinnerYear = findViewById(R.id.spinner_year);
        spinnerObject = findViewById(R.id.spinner_object);
        multiChoiceSpinner =findViewById(R.id.niceSpinner);

        List<String> years = new ArrayList<>();
        List<String> objects = new ArrayList<>();

        //年份选择
        int curYearNum = DateUtils.getYearForSystem();
        int length = 5;
        years.add(TEXT_ALL);
        for (int i = 1; i < length; i++) {
            years.add(i, String.valueOf(curYearNum + 2 - i));
        }
        spinnerYear.setHint(years);
        //适用对象
        objects.add("老年人");
        objects.add("高血压");
        objects.add("糖尿病");
        objects.add("重性精神病");
        objects.add("儿童");
        objects.add("孕产妇");
        objects.add("贫困人口");
        objects.add("恶性肿瘤");
        objects.add("其他");
        spinnerObject.setHint(objects);

        //
        spinnerYear.setEditable(true);
        spinnerYear.setTextHint("年份选择");
        spinnerYear.setTxtColor(Color.parseColor("#8f9cb5"));
        spinnerYear.setHintColor(Color.parseColor("#8f9cb5"));
        spinnerYear.setBackgroundResource(R.drawable.shape_service_package_title);
        spinnerYear.setOnItemEditListener(new OnItemEditListener() {
            @Override
            public void onClick(String s, boolean b) {
                spinnerYear.setTexts(s);

                UIUtils.showToast(SpinnerActivity.this, spinnerObject.getTexts().get(0));
            }
        });


        spinnerObject.setEditable(true);
        spinnerObject.setTextHint("适用对象");
        spinnerObject.setTxtColor(Color.parseColor("#8f9cb5"));
        spinnerObject.setHintColor(Color.parseColor("#8f9cb5"));
        spinnerObject.setBackgroundResource(R.drawable.shape_service_package_title);
        spinnerObject.setOnItemEditListener(new OnItemEditListener() {
            @Override
            public void onClick(String s, boolean b) {
                spinnerObject.setTexts(s);

                UIUtils.showToast(SpinnerActivity.this, spinnerObject.getTexts().get(0));
            }
        });

        //多选
        multiChoiceSpinner.setBackgroundResource(R.drawable.shape_service_package_title);
        multiChoiceSpinner.getCheckedItems();
    }
}
