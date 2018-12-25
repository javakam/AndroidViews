package com.work.addresspicker;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.work.R;
import com.work.addresspicker.bean.AddressBean;
import com.work.addresspicker.dao.AddressDao;

import java.util.List;

/**
 * Title: AddressPickerDialog
 * <p>
 * Description:<p>
 * 支持平板和手机两种平台的地址选择器，默认是手机平台<p>
 * </p>
 *
 * @author Changbao
 * @date 2018/11/12  16:34
 */
public class AddressPickerDialog extends BottomDialog {
    private static final int MODE_PHONE = 0, MODE_PAD = 1;
    private static final int THEME_LIGHT = 0, THEME_DARK = 1;
    private TextView mTvTitle;
    private TextView mTvCancel;
    private TextView mTvConfirm;
    private LinearLayout mDecorView;
    private AddressPicker mAddressPicker;
    private AddressDao mAddressDao;
    private OnConfirmClickListener mOnConfirmClickListener;
    private Config mConfig;

    public AddressPickerDialog(Context context) {
        this(context, 0);
    }

    public AddressPickerDialog(Context context, Builder builder) {
        this(context, builder.themeResId);
        this.mConfig = builder.config;
        this.mOnConfirmClickListener = builder.listener;
    }

    public AddressPickerDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public void initVariables() {
        this.mAddressDao = AddressDao.DAO;
        if (mConfig == null) {
            mConfig = new Config(MODE_PHONE, THEME_LIGHT);
        }
    }

    @Override
    public void initView() {
        mTvTitle = findViewById(R.id.tv_address_title);
        mTvCancel = findViewById(R.id.tv_address_cancel);
        mTvConfirm = findViewById(R.id.tv_address_confirm);
        if (mConfig.mode == MODE_PAD && mConfig.theme == THEME_DARK) {
            mTvTitle.setTextColor(mContext.getResources().getColor(mConfig.theme == THEME_LIGHT ?
                    R.color.color_address_pad_tab : R.color.color_address_pad_tab_dark));
            mDecorView = findViewById(R.id.ll_address);
            mAddressPicker = new AddressPicker(mContext, mAddressDao,
                    R.style.AddressPickerPadDarkStyle);
            ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            marginLayoutParams.topMargin=mContext.getResources().getDimensionPixelSize(R.dimen.size_30);
            mAddressPicker.setLayoutParams(marginLayoutParams);
            mDecorView.addView(mAddressPicker);
        } else {//XML
            mAddressPicker = findViewById(R.id.view_address_picker);
        }
        mAddressPicker.setCallBack(new AddressPicker.CallBack() {
            @Override
            public void onNoMoreAddress() {
                if (mTvConfirm != null) {
                    mTvConfirm.performClick();
                }
            }
        });
        mTvCancel.setOnClickListener(new View.OnClickListener() {//取消
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mTvConfirm.setOnClickListener(new View.OnClickListener() {//确定
            @Override
            public void onClick(View v) {
                if (mOnConfirmClickListener != null && mAddressPicker != null) {
                    mOnConfirmClickListener.confirm(mAddressPicker.getPickedAddresses());
                }
                dismiss();
            }
        });
    }

    @Override
    public int getLayout() {
        int layoutRes = R.layout.layout_address_dialog_phone;
        if (mConfig.mode == MODE_PAD) {
            layoutRes = mConfig.theme == THEME_DARK ?
                    R.layout.layout_address_dialog_pad : R.layout.layout_address_dialog_pad_xml;
        }
        return layoutRes;
    }

    public static Config getConfigOfPhoneLight() {
        return new Config(MODE_PHONE, THEME_LIGHT);
    }

    public static Config getConfigOfPadLight() {
        return new Config(MODE_PAD, THEME_LIGHT);
    }

    public static Config getConfigOfPadDark() {
        return new Config(MODE_PAD, THEME_DARK);
    }

    public void setOnConfirmClickListener(OnConfirmClickListener listener) {
        this.mOnConfirmClickListener = listener;
    }

    public void setConfig(Config mConfig) {
        this.mConfig = mConfig;
    }

    @NonNull
    @Override
    public Bundle onSaveInstanceState() {
        final Bundle state = super.onSaveInstanceState();
        if (mAddressPicker != null) {
            mAddressPicker.onSaveInstanceState(state);
        }
        return state;
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (mAddressPicker != null) {
            mAddressPicker.onRestoreInstanceState(savedInstanceState);
        }
    }

    interface OnConfirmClickListener {
        /**
         * 确定
         *
         * @param pickedAddresses
         */
        void confirm(List<AddressBean> pickedAddresses);
    }

    private static class Config {
        public int mode;
        public int theme;

        public Config(int mode, int theme) {
            this.mode = mode;
            this.theme = theme;
        }
    }

    public static class Builder {
        Context context;
        OnConfirmClickListener listener;
        Config config;
        int themeResId;

        Builder(Context context) {
            this.context = context;
        }

        Builder setConfig(Config config) {
            this.config = config;
            return this;
        }

        Builder setThemeResId(int themeResId) {
            this.themeResId = themeResId;
            return this;
        }

        Builder setOnConfirmClickListener(OnConfirmClickListener listener) {
            this.listener = listener;
            return this;
        }

        AddressPickerDialog build() {
            return new AddressPickerDialog(context, this);
        }
    }
}
