package com.work.addresspicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.work.R;
import com.work.addresspicker.bean.AddressBean;
import com.work.addresspicker.bean.CityBean;
import com.work.addresspicker.dao.AddressDao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Title: AddressView
 * <p>
 * Description:<p>
 * TabLayout和RecyclerView的地址选择器;<p>
 * 支持Pad和手机端;<p>
 * 地区级别 0:全国 1：省、直辖市 2：市、区 3：县、区 4 ：乡、镇 5：村、居委会<p>
 * </p>
 *
 * @author Changbao
 * @date 2018/11/12  13:27
 */
public class NewAddressPicker extends FrameLayout {
    private static final int MODE_PHONE = 0;                    //手机
    private static final int MODE_PAD = 1;                      //平板
    private static final String TEXT_PLEASE_SELECT = "请选择";  //区域切换时的提示文本
    private PickerAppearanceAttributes mAttributes;
    private Context mContext;                                   //
    private TabLayout mTabLayout;                               //区域切换
    private View mViewLine;                                     //分割线
    private TextView mTvHint;                                   //提示文本
    private RecyclerView mRecycler;                             //地址列表
    private AddressPickerAdapter mAdapter;
    private CallBack mCallBack;                                 //回调给上级窗体

    private List<CityBean> provinceData = new ArrayList<>();
    private List<CityBean> cityData = new ArrayList<>();
    private List<CityBean> countryData = new ArrayList<>();

    public NewAddressPicker(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        this.mContext = context;
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.AddressPicker, R.attr.addressPickerStyle, R.style.AddressPickerPhoneLightStyle);
        readPickerAppearance(a);
        a.recycle();
        initView();
    }

    public NewAddressPicker(Context context, AddressDao addressDao, @StyleRes int resId) {
        super(context, null, 0);
        this.mContext = context;
        setPickerAppearance(resId, false);
        initView();
    }

    private void initView() {
        View v = LayoutInflater.from(mContext).inflate(mAttributes.mPickPlatform == MODE_PAD
                ? R.layout.view_address_picker_pad : R.layout.view_address_picker_phone, null);
        mTabLayout = v.findViewById(R.id.tl_address);
        mViewLine = v.findViewById(R.id.view_address_line);
        mTvHint = v.findViewById(R.id.tv_address_hint);
        mRecycler = v.findViewById(R.id.rv_address);
        applyPickerAppearance();
        mRecycler.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        mRecycler.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecycler.setItemAnimator(null);
        mRecycler.setHasFixedSize(true);
        mAdapter = new AddressPickerAdapter();

        获取省份(v);
    }

    //省份 provinceData = 联网回调返回的省份 -> onSuccess中调用
    private void 获取省份(View v) {
//        {"id":4,"code":"","label":"北京","value":"110000","priority":null,"mark":"","pid":3},
//* {"id":22,"code":"","label":"天津","value":"120000","priority":null,"mark":"","pid":3},{"
        CityBean entity1 = new CityBean("1", "天津市", "110000", 0, 3);
        CityBean entity2 = new CityBean("2", "天津市", "120000", 0, 3);
        CityBean entity3 = new CityBean("3", "天津市", "130000", 0, 3);
        provinceData.add(entity1);
        provinceData.add(entity2);
        provinceData.add(entity3);

        mAdapter.init(mAttributes.mPickPlatform == MODE_PAD ?
                R.layout.item_address_pad : R.layout.item_address_phone);
        mAdapter.setOnAddressPickedListener(new OnAddressPickedListener() {
            @Override
            public void onPick(View v, final CityBean entity, int position) {
                //地区级别 0:全国 1：省、直辖市 2：市、区 3：县、区 4 ：乡、镇 5：村、居委会
                newTab(entity, position);
            }
        });
        mRecycler.setAdapter(mAdapter);
        mAdapter.setItems(provinceData);//第一次加载数据

        mTvHint.setText(HintEnum.PROVINCE.getHintDesc());
        TabLayout.Tab firstTab = mTabLayout.newTab();
        firstTab.setTag(new TabTagParam(0, -1, provinceData));
        mTabLayout.addTab(firstTab.setText(TEXT_PLEASE_SELECT), 0, true);
        mTabLayout.addOnTabSelectedListener(new TabSelected());
        addView(v);
    }

    //下级市县
    private void getAddressBySSUPERID(int newIndex, String sid) {
        if (newIndex == 2) {//只有0，1，2三级
            return;
        }
        //联网获取下一级 传入 sid -> onSuccess
        CityBean entity1 = new CityBean("6", "东城区", "110101", 1, 5);
        CityBean entity2 = new CityBean("7", "西城区", "110102", 1, 5);
        cityData.add(entity1);
        cityData.add(entity2);

        if (cityData.size() > 0) {
            TabLayout.Tab newTab = mTabLayout.getTabAt(newIndex);
            TabTagParam newTabParam = new TabTagParam(newIndex, -1, cityData);
            if (newTab == null) {
                mTabLayout.addTab(newTab = mTabLayout.newTab().setText(TEXT_PLEASE_SELECT), newIndex);
            } else {
                newTab.setText(TEXT_PLEASE_SELECT);
                int count = mTabLayout.getTabCount();
                //只有两个Tab不需要移除
                if (count >= 3) {
                    for (int i = count - 1; i > newIndex; i--) {
                        mTabLayout.removeTabAt(i);
                    }
                }
            }
            newTab.setTag(newTabParam);
            newTab.select();
            mTvHint.setText(HintEnum.findHintDescByIlevel(newIndex));
        } else {
            //两种情形：refresh adapter or 无需点击确定立即销毁弹窗
            if (mCallBack != null) {
                mCallBack.onNoMoreAddress();
            }
            //or
            //mAdapter.setItems(oldTabParam.cacheList);
            //mAdapter.notifyDataSetChanged();
        }
    }

    private class TabSelected extends AbstractTabSelectedListener {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            TabTagParam param = (TabTagParam) tab.getTag();
            mAdapter.setItems(param.cacheList);
            mAdapter.notifyDataSetChanged();
            ((LinearLayoutManager) mRecycler.getLayoutManager()).scrollToPositionWithOffset(param.pickedIndex == -1 ? 0 : param.pickedIndex, 0);
            mTvHint.setText(HintEnum.findHintDescByIlevel(param.tabIndex));
        }
    }

    /**
     * 缓存数据到Tab.Tag，并创建新的Tab，移除尾部Tab
     *
     * @param entity   选中的地址
     * @param position 选中的地址在集合中的索引位置
     */
    private void newTab(final CityBean entity, int position) {
        final int oldIndex = entity.ilevel;//Item单选事件触发时，当前Tab在TabLayout中的索引
        final int newIndex = oldIndex + 1;
        entity.isSelected = true;
        TabLayout.Tab oldTab = mTabLayout.getTabAt(oldIndex);
        oldTab.setText(entity.label);
        TabTagParam oldTabParam = (TabTagParam) oldTab.getTag();
        oldTabParam.pickedIndex = position;
        for (CityBean item : oldTabParam.cacheList) {
            item.isSelected = TextUtils.equals(entity.id, item.id);
        }
        oldTab.setTag(oldTabParam);
        //上一级的 sid 是下一级的 ssuperid
        getAddressBySSUPERID(newIndex, entity.id);
    }

    public List<CityBean> getPickedAddresses() {
        List<CityBean> addressList = new ArrayList<>();
        TabTagParam param;
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            param = (TabTagParam) mTabLayout.getTabAt(i).getTag();
            if (param.pickedIndex != -1) {
                CityBean bean = param.cacheList.get(param.pickedIndex);
                addressList.add(bean);
            }
        }
        return addressList;
    }

    /**
     * 支持外部自定义样式
     *
     * @param resId R.style.AddressPickerPadDarkStyle
     * @param apply 是否立即应用传入的样式
     */
    public void setPickerAppearance(@StyleRes int resId, boolean apply) {
        final TypedArray ta = mContext.obtainStyledAttributes(resId, R.styleable.AddressPicker);
        readPickerAppearance(ta);
        ta.recycle();
        if (apply) {
            applyPickerAppearance();
        }
    }

    private void readPickerAppearance(TypedArray a) {
        if (this.mAttributes == null) {
            this.mAttributes = new PickerAppearanceAttributes();
        }
        final int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.AddressPicker_picker_mode:
                    mAttributes.mPickPlatform = a.getInt(R.styleable.AddressPicker_picker_mode, MODE_PHONE);
                    break;
                case R.styleable.AddressPicker_picker_background:
                    mAttributes.mPickerBackground = a.getColor(R.styleable.AddressPicker_picker_background, getColor(R.color.color_address_phone_bg));
                    break;
                case R.styleable.AddressPicker_picker_tabTextSize:
                    mAttributes.mPickerTabTextSize = a.getDimensionPixelSize(R.styleable.AddressPicker_picker_tabTextSize, getResources().getDimensionPixelOffset(R.dimen.font_26));
                    break;
                case R.styleable.AddressPicker_picker_tabTextColor:
                    mAttributes.mPickerTabTextColor = a.getColor(R.styleable.AddressPicker_picker_tabTextColor, getColor(R.color.color_address_phone_tab));
                    break;
                case R.styleable.AddressPicker_picker_tabSelectedTextColor:
                    mAttributes.mPickerTabSelectedTextColor = a.getColor(R.styleable.AddressPicker_picker_tabSelectedTextColor, getColor(R.color.color_address_phone_tab_picked));
                    break;
                case R.styleable.AddressPicker_picker_tabIndicatorWidth:
                    mAttributes.mPickerTabIndicatorWidth = a.getDimensionPixelSize(R.styleable.AddressPicker_picker_tabIndicatorWidth, getResources().getDimensionPixelOffset(R.dimen.size_74));
                    break;
                case R.styleable.AddressPicker_picker_tabIndicatorHeight:
                    mAttributes.mPickerTabIndicatorHeight = a.getDimensionPixelSize(R.styleable.AddressPicker_picker_tabIndicatorHeight,
                            getResources().getDimensionPixelOffset(R.dimen.size_3));
                    break;
                case R.styleable.AddressPicker_picker_tabIndicatorColor:
                    mAttributes.mPickerTabIndicatorColor = a.getColor(R.styleable.AddressPicker_picker_tabIndicatorColor, getColor(R.color.color_address_phone_tab_picked));
                    break;
                case R.styleable.AddressPicker_picker_tabMode:
                    mAttributes.mPickerTabMode = a.getInt(R.styleable.AddressPicker_picker_tabMode, 0);// 0scrollable  1fixed
                    break;
                case R.styleable.AddressPicker_picker_lineColor:
                    mAttributes.mPickerLineColor = a.getColor(R.styleable.AddressPicker_picker_lineColor, getColor(R.color.color_address_phone_line));
                    break;
                case R.styleable.AddressPicker_picker_hintTextColor:
                    mAttributes.mPickerHintTextColor = a.getColor(R.styleable.AddressPicker_picker_hintTextColor, getColor(R.color.color_address_phone_hint));
                    break;
                case R.styleable.AddressPicker_picker_itemBackground:
                    mAttributes.mPickerItemBackground = a.getColor(R.styleable.AddressPicker_picker_itemBackground, getColor(R.color.color_address_phone_bg));
                    break;
                case R.styleable.AddressPicker_picker_itemPickedBackground:
                    mAttributes.mPickerItemPickedBackground = a.getColor(R.styleable.AddressPicker_picker_itemPickedBackground, getColor(R.color.color_address_phone_item_picked));
                    break;
                case R.styleable.AddressPicker_picker_itemLetterTextColor:
                    mAttributes.mPickerItemLetterTextColor = a.getColor(R.styleable.AddressPicker_picker_itemLetterTextColor, getColor(R.color.color_address_phone_item_letter));
                    break;
                case R.styleable.AddressPicker_picker_itemAreaTextColor:
                    mAttributes.mPickerItemAreaTextColor = a.getColor(R.styleable.AddressPicker_picker_itemAreaTextColor, getColor(R.color.color_address_phone_item_area));
                    break;
                case R.styleable.AddressPicker_picker_itemArrowDrawable:
                    mAttributes.mPickerItemArrowDrawable = a.getDrawable(R.styleable.AddressPicker_picker_itemArrowDrawable);
                    if (mAttributes.mPickerItemArrowDrawable == null) {
                        mAttributes.mPickerItemArrowDrawable = getResources().getDrawable(R.drawable.icon_address_ok_orange);
                    }
                    break;
                default:
            }
        }
    }

    private void applyPickerAppearance() {
        setBackgroundColor(mAttributes.mPickerBackground);
        mTabLayout.setTabTextColors(mAttributes.mPickerTabTextColor, mAttributes.mPickerTabSelectedTextColor);
        mTabLayout.setSelectedTabIndicatorHeight(mAttributes.mPickerTabIndicatorHeight);
        mTabLayout.setSelectedTabIndicatorColor(mAttributes.mPickerTabIndicatorColor);
        mTabLayout.setTabMode(mAttributes.mPickerTabMode);
        mTabLayout.setTabGravity(Gravity.BOTTOM);
        //setSelectedTabIndicator(mAttributes.mPickerTabSelectedTextColor);//todo invalid
        mViewLine.setBackgroundColor(mAttributes.mPickerLineColor);
        mTvHint.setTextColor(mAttributes.mPickerHintTextColor);
        invalidate();
    }

    public void setSelectedTabIndicator(@ColorInt int color) {
        GradientDrawable indicator = new GradientDrawable();
        indicator.setUseLevel(true);
        indicator.setColor(color);
        indicator.setSize(getResources().getDimensionPixelSize(R.dimen.size_74), getResources().getDimensionPixelSize(R.dimen.size_3));
        this.mTabLayout.setSelectedTabIndicator(indicator);
    }

    private static class PickerAppearanceAttributes {
        private int mPickPlatform;                                  //支持平台：0Phone、1Pad
        private int mPickerBackground;                              //背景色
        private int mPickerTabTextSize;                             //TabLayout.Tab 字体大小
        private int mPickerTabTextColor;                            //TabLayout.Tab 文字颜色
        private int mPickerTabSelectedTextColor;                    //TabLayout.Tab 选中状态的文字颜色
        private int mPickerTabIndicatorWidth;                       //TabLayout.Tab Indicator宽度
        private int mPickerTabIndicatorHeight;                      //TabLayout.Tab Indicator高度
        private int mPickerTabIndicatorColor;                       //TabLayout.Tab Indicator颜色
        private int mPickerTabMode;                                 //TabLayout.Tab  0SCROLLABLE  1FIXED
        private int mPickerLineColor;                               //分割线颜色
        private int mPickerHintTextColor;                           //提示文本，如：“选择”+“省份/地区”
        private int mPickerItemBackground;                          //地址列表背景色
        private int mPickerItemPickedBackground;                    //地址列表选中状态的背景色
        private int mPickerItemLetterTextColor;                     //地址列表字母文字颜色
        private int mPickerItemAreaTextColor;                       //地址列表地址文本的文字颜色
        private Drawable mPickerItemArrowDrawable;                  //地址列表选中状态的箭头图标
    }

    private class AddressPickerAdapter
            extends RecyclerView.Adapter<AddressPickerAdapter.AddressSelectHolder> {
        private int layoutRes;
        private List<CityBean> data;
        private OnAddressPickedListener onAddressPickedListener;

        public AddressPickerAdapter() {
        }

        public void init(@LayoutRes int layoutRes) {
            this.layoutRes = layoutRes;
        }

        public void setOnAddressPickedListener(OnAddressPickedListener listener) {
            this.onAddressPickedListener = listener;
        }

        public void setItems(List<CityBean> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        @Override
        public AddressPickerAdapter.AddressSelectHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            return new AddressPickerAdapter.AddressSelectHolder(
                    LayoutInflater.from(mContext).inflate(this.layoutRes, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull AddressPickerAdapter.AddressSelectHolder holder,
                                     @SuppressLint("RecyclerView") final int position) {
            final CityBean bean = data.get(position);
            holder.tvLetter.setText(bean.ilevel);
            holder.tvArea.setText(bean.label);
            holder.ivArrow.setVisibility(bean.isSelected ? View.VISIBLE : View.INVISIBLE);
            //holder.view.setBackgroundColor(evaluateBean.isSelected ? mAttributes.mPickerItemPickedBackground : mAttributes.mPickerItemBackground);
            holder.view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onAddressPickedListener != null) {
                        onAddressPickedListener.onPick(v, bean, position);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }

        class AddressSelectHolder extends RecyclerView.ViewHolder {
            View view;
            TextView tvLetter;
            TextView tvArea;
            ImageView ivArrow;

            AddressSelectHolder(View v) {
                super(v);
                this.view = v;
                tvLetter = v.findViewById(R.id.tv_address_letter);
                tvArea = v.findViewById(R.id.tv_address_area);
                ivArrow = v.findViewById(R.id.iv_address_arrow);
                v.setBackgroundColor(mAttributes.mPickerItemBackground);
                tvLetter.setTextColor(mAttributes.mPickerItemLetterTextColor);
                tvArea.setTextColor(mAttributes.mPickerItemAreaTextColor);
                ivArrow.setImageDrawable(mAttributes.mPickerItemArrowDrawable);
            }
        }
    }

    public interface CallBack {
        /**
         * 没有下级地区，不需点击确定立即销毁弹窗
         */
        void onNoMoreAddress();
    }

    public void setCallBack(CallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    public interface OnAddressPickedListener {
        /**
         * 选中地址
         *
         * @param v
         * @param entity
         * @param position
         */
        void onPick(View v, CityBean entity, int position);
    }

    public abstract class AbstractTabSelectedListener
            implements TabLayout.BaseOnTabSelectedListener {
        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
        }
    }

    private int getColor(int resId) {
        return mContext.getResources().getColor(resId);
    }

    /**
     * TabLayout.Tab.Tag封装的数据
     */
    private static class TabTagParam implements Serializable {
        private static final long serialVersionUID = -1320466334029310860L;
        int tabIndex;
        int pickedIndex;            //1.List.get(pickedIndex)；2.滑动位置
        List<CityBean> cacheList;

        TabTagParam(int tabIndex, int pickedIndex, List<CityBean> cacheList) {
            this.tabIndex = tabIndex;
            this.pickedIndex = pickedIndex;
            this.cacheList = cacheList;
        }
    }

    private enum HintEnum {
        /**
         * 地区级别 0:全国 1：省、直辖市 2：市、区 3：县、区 4 ：乡、镇 5：村、居委会
         */
        NATION(0, "全国"),
        PROVINCE(1, "省份/地区"),
        CITY(2, "城市"),
        COUNTY(3, "区/县"),
        COUNTRYSIDE(4, "乡镇/街道"),
        VILLAGE(5, "村"),
        DEFAULT(100, "地区");
        private static final String TEXT_SELECT = "选择";
        private int ilevel;
        private String hintDesc;

        HintEnum(int ilevel, String hintDesc) {
            this.ilevel = ilevel;
            this.hintDesc = TEXT_SELECT + hintDesc;
        }

        /**
         * 动态改变Hint的文字显示 如：“选择”+“省份/区域”
         */
        public static String findHintDescByIlevel(int ilevel) {
            ilevel++;
            for (HintEnum i : values()) {
                if (i.ilevel == ilevel) {
                    return i.hintDesc;
                }
            }
            return DEFAULT.getHintDesc();
        }

        public String getHintDesc() {
            return hintDesc;
        }
    }

    public Bundle onSaveInstanceState(Bundle state) {
        int tabCount = mTabLayout.getTabCount();
        if (tabCount > 0) {
            TabTagParam param;
            for (int i = 0; i < tabCount; i++) {
                param = (TabTagParam) mTabLayout.getTabAt(i).getTag();
                state.putInt("tabIndex", i);
                state.putSerializable("tabData", param);
            }
            state.putInt("tabSelectIndex", mTabLayout.getSelectedTabPosition());
        }
        return state;
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        int tabCount = mTabLayout.getTabCount();
        if (tabCount > 0) {
            TabTagParam param;
            for (int i = 0; i < tabCount; i++) {
                param = (TabTagParam) savedInstanceState.getSerializable("tabData");
                mTabLayout.getTabAt(savedInstanceState.getInt("tabIndex", i)).setTag(param);
            }
            int selectIndex = savedInstanceState.getInt("tabSelectIndex");
            mTabLayout.getTabAt(selectIndex).select();
        }
    }
}