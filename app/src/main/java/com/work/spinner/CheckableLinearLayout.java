package com.work.spinner;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.LinearLayout;

import com.work.R;
import com.work.utils.UIUtils;

/**
 * Title:CheckableLinearLayout
 * <p>
 * Description:支持多选的LinearLayout，用做Adapter多选ItemView的父布局
 * </p>
 *
 * @author Changbao
 * @date 2018/12/19 10:19
 */
public class CheckableLinearLayout extends LinearLayout implements Checkable {
    private Context mContext;
    private AppCompatCheckBox mCheckBox;
    private AppCompatTextView mTextView;

    private boolean mChecked;

    public CheckableLinearLayout(Context context) {
        this(context, null);
    }

    public CheckableLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckableLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    private void initView() {
        this.setOrientation(HORIZONTAL);
        this.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        this.setBackground(getResources().getDrawable(R.color.color_gray_light));
        this.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        this.setPadding(UIUtils.getDimens(mContext, R.dimen.size_10),0,UIUtils.getDimens(mContext, R.dimen.size_10),0);
        mCheckBox = new AppCompatCheckBox(mContext);
        mTextView = new AppCompatTextView(mContext);
        //
        mCheckBox.setClickable(false);
        mCheckBox.setFocusable(false);
        mCheckBox.setFocusableInTouchMode(false);
        LayoutParams paramsBox = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mCheckBox.setLayoutParams(paramsBox);
        //
        mTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        mTextView.setSingleLine(true);
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, UIUtils.getDimens(mContext, R.dimen.font_24));
        mTextView.setTextColor(Color.parseColor("#5a6589"));
        LayoutParams paramsTv = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsTv.setMarginStart(UIUtils.getDimens(mContext, R.dimen.size_10));
        mTextView.setLayoutParams(paramsTv);
        addView(mCheckBox);
        addView(mTextView);
    }

    @Override
    public void setChecked(boolean checked) {
        this.mChecked = checked;
        this.mCheckBox.setChecked(checked);
//        this.setBackgroundDrawable(checked ? new ColorDrawable(0xff0000a0) : null);//当选中时呈现蓝色
    }

    @Override
    public boolean isChecked() {
        return mChecked;//eq mCheckBox.isChecked()
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
        mCheckBox.toggle();
    }

    public void setText(String text) {
        mTextView.setText(TextUtils.isEmpty(text) ? "" : text);
    }
}
