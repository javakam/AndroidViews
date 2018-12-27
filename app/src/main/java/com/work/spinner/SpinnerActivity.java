package com.work.spinner;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

    private SingleChoiceSpinner spinnerYear;
    private SingleChoiceSpinner spinnerObject;
    private MultiChoiceSpinner multiChoiceSpinner;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinner);

        spinnerYear = findViewById(R.id.spinner_year);
        spinnerObject = findViewById(R.id.spinner_object);
        multiChoiceSpinner = findViewById(R.id.niceSpinner);


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

        /*CommonSpinner*/
        demoCommonSpinner();
    }

    /*CommonSpinner*/
    private CommonSpinner mSpYear;                      //年份选择
    private CommonSpinner mSpObject;                    //适用对象
    private CommonSpinner mSpPkgType;                   //服务包类别
    private ViewGroup mFlServiceReset;                  //重置功能

    private List<String> mListYears;                    //集合-》年份选择
    private List<String> mListObjects;                  //集合-》适用对象
    private List<String> mListPkgTypes;                 //集合-》服务包类别

    private void demoCommonSpinner() {
        mSpYear = findViewById(R.id.spinner_year2);
        mSpObject = findViewById(R.id.spinner_object2);
        mSpPkgType = findViewById(R.id.spinner_package_type);
        mFlServiceReset = findViewById(R.id.fl_service_reset);
        //数据筛选
        mListYears = new ArrayList<>();
        mListObjects = new ArrayList<>();
        mListPkgTypes = new ArrayList<>();
        //年份选择
        int curYearNum = DateUtils.getYearForSystem();
        int length = 5;
        mListYears.add(TEXT_ALL);
        for (int i = 1; i < length; i++) {
            mListYears.add(i, String.valueOf(curYearNum + 2 - i));
        }
        //适用对象
        mListObjects.add("老年人");
        mListObjects.add("高血压");
        mListObjects.add("糖尿病");
        mListObjects.add("重性精神病");
        mListObjects.add("儿童");
        mListObjects.add("孕产妇");
        mListObjects.add("贫困人口");
        mListObjects.add("恶性肿瘤");
        mListObjects.add("肺结核");
        mListObjects.add("其他");
        //服务包类别
        mListPkgTypes.add("基础服务包");
        mListPkgTypes.add("初级服务包");
        mListPkgTypes.add("中级服务包");
        mListPkgTypes.add("高级服务包");
        mListPkgTypes.add("其他服务包");

        mSpYear.setDropItems(mListYears);
        mSpObject.setDropItems(mListObjects);
        mSpPkgType.setDropItems(mListPkgTypes);


        //年份选择Item Click监听
        mSpYear.setOnItemClickListener(new CommonSpinner.OnItemClickListener() {
            @Override
            public void onClick() {
                search(mSpYear.getCheckedItem());
            }
        });

        //适用对象Item Click监听
        mSpObject.setOnItemClickListener(new CommonSpinner.OnItemClickListener() {
            @Override
            public void onClick() {
                StringBuilder skr = new StringBuilder();
                for (int i = 0; i < mSpObject.getCheckedItems().size(); i++) {
                    skr.append(mSpObject.getCheckedItems().get(i));
                    if (i != mSpObject.getCheckedItems().size() - 1) {
                        skr.append("  ");
                    }
                }
                search(skr.toString());
            }
        });

        //服务包类别Item Click监听
        mSpPkgType.setOnItemClickListener(new CommonSpinner.OnItemClickListener() {
            @Override
            public void onClick() {
                StringBuilder skr = new StringBuilder();
                for (int i = 0; i < mSpPkgType.getCheckedItems().size(); i++) {
                    skr.append(mSpPkgType.getCheckedItems().get(i));
                    if (i != mSpPkgType.getCheckedItems().size() - 1) {
                        skr.append("  ");
                    }
                }
                search(skr.toString());
            }
        });

        mFlServiceReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpYear.reset();
                mSpObject.reset();
                mSpPkgType.reset();
//                mElSelectPkgTypeInput.setText("");
            }
        });
    }

    private Toast toast;

    private void search(String text) {
        String showText = "Search : " + text;
        if (toast == null) {
            toast = Toast.makeText(SpinnerActivity.this, showText, Toast.LENGTH_SHORT);
        } else {
            toast.setText(showText);
        }
        toast.show();
    }
}
