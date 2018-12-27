package com.work.spinner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.work.R;
import com.work.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Title:NiceSpinner
 * <p>
 * Description:单选、多选下拉框
 * </p>
 *
 * @author Changbao
 * @date 2018/12/19 9:52
 */
public class CommonSpinner extends android.support.v7.widget.AppCompatAutoCompleteTextView {

    private static final String TEXT_DEFAULT = "请选择";         //Spinner 默认文本
    private static final String TEXT_DEFAULT_SEPARATOR = "/";   //Spinner 多文本分割线
    public static final int CHOICE_SINGLE = 1;
    public static final int CHOICE_MULTIPLE = 2;
    public static final int DROP_FITTEXT = 1;
    public static final int DROP_FULLSCREEN = 2;

    private Context mContext;
    private ListView mLvMultiChoice;                   //ListView
    private AbstractMultiChoiceAdapter mAdapter;       //ListView适配器
    private PopupWindow mPopupWindow;                  //ListView所属的PopupWindow

    //默认值
    private Drawable mDefaultTextBackground;           //默认文本背景
    private Drawable mDefaultDropBackground;           //默认ItemView 文本背景
    private int mDefaultTextSize;                      //默认文本大小
    private int mDefaultTextColor;                     //默认文本颜色
    private int mDefaultTextPadding;                   //默认文本内间距
    private int mDefaultDropItemHeight;                //默认ItemView 高度
    private int mDefaultDropItemTextSize;              //默认ItemView 文本大小
    private int mDefaultDropItemTextColor;             //默认ItemView 文本颜色

    private boolean mIsMultiChoice;                    //单选 MODE_SINGLE；多选 MODE_MULTIPLE
    private boolean mIsLiveUpdate;                     //多选 实时更新数据的开关，Activity需设置OnItemClickListener
    private Drawable mTextBackground;                  //文本背景
    private String mText;                              //文本内容
    private int mTextSize;                             //文本大小
    private int mTextColor;                            //文本颜色
    private int mTextPadding;                          //文本内间距
    private Drawable mDropBackground;                  //列表背景
    private int mDropWidthMode;                        //列表宽度设置，默认为Spinner宽度false
    private int mDropItemHeight;                       //列表 ItemView 高度
    private int mDropItemTextSize;                     //列表 ItemView文本大小
    private int mDropItemTextColor;                    //列表 ItemView文本颜色

    private Drawable mTextRightDrawable;               //文本右侧箭头
    private List<String> mListData;                    //列表所有数据
    private List<String> mListCheckedItems;            //列表选中的数据
    private OnItemClickListener mOnItemClickListener;  //列表ItemView点击事件

    public CommonSpinner(Context context) {
        this(context, null);
    }

    public CommonSpinner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initDefaultStyle();
        initViews(attrs);
        initListeners();
    }

    private void initDefaultStyle() {
        mListData = new ArrayList<>();
        mListCheckedItems = new ArrayList<>();

        setSingleLine(true);
        mDefaultTextBackground = getResources().getDrawable(R.drawable.shape_service_package_title);
        mText = TEXT_DEFAULT;
        mDefaultTextSize = getResources().getDimensionPixelSize(R.dimen.font_24);
        mDefaultTextColor = getResources().getColor(R.color.font_white);
        mDefaultTextPadding = getResources().getDimensionPixelSize(R.dimen.size_10);
        mDefaultDropBackground = getResources().getDrawable(R.drawable.core_selector_select_spinner);
        mDefaultDropItemHeight = getResources().getDimensionPixelSize(R.dimen.size_50);
        mDefaultDropItemTextSize = getResources().getDimensionPixelSize(R.dimen.font_24);
        mDefaultDropItemTextColor = getResources().getColor(R.color.font_black);
    }

    private void initViews(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.CommonSpinner);
        int choiceMode = a.getInt(R.styleable.CommonSpinner_spinnerChoiceMode, CHOICE_SINGLE);
        mIsMultiChoice = (choiceMode == CHOICE_MULTIPLE);
        mIsLiveUpdate = a.getBoolean(R.styleable.CommonSpinner_spinnerLiveUpdate, !mIsMultiChoice);
        mTextBackground = a.getDrawable(R.styleable.CommonSpinner_spinnerTextBackground);
        mTextBackground = mTextBackground == null ? mDefaultTextBackground : mTextBackground;
        mText = a.getString(R.styleable.CommonSpinner_spinnerText);
        mTextSize = a.getDimensionPixelSize(R.styleable.CommonSpinner_spinnerTextSize, mDefaultTextSize);
        mTextColor = a.getColor(R.styleable.CommonSpinner_spinnerTextColor, mDefaultTextColor);
        mTextPadding = a.getDimensionPixelSize(R.styleable.CommonSpinner_spinnerTextPadding, mDefaultTextPadding);
        mDropBackground = a.getDrawable(R.styleable.CommonSpinner_spinnerDropBackground);
        mDropBackground = mDropBackground == null ? mDefaultDropBackground : mDropBackground;
        mDropWidthMode = a.getInt(R.styleable.CommonSpinner_spinnerDropWidthMode, DROP_FITTEXT);
        mDropItemHeight = a.getDimensionPixelSize(R.styleable.CommonSpinner_spinnerDropItemHeight, mDefaultDropItemHeight);
        mDropItemTextSize = a.getDimensionPixelSize(R.styleable.CommonSpinner_spinnerDropItemTextSize, mDefaultDropItemTextSize);
        mDropItemTextColor = a.getColor(R.styleable.CommonSpinner_spinnerDropItemTextColor, mDefaultDropItemTextColor);

        setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        setText(getDefaultText());
        setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        setTextColor(mTextColor);
        a.recycle();

        //编辑框右侧
        Shape shape = new Shape() {
            private Path path = new Path();

            @Override
            public void draw(Canvas canvas, Paint paint) {
                paint.setColor(mTextColor);
                paint.setStrokeWidth(3);
                paint.setStyle(Paint.Style.STROKE);
                path.moveTo(0, -5);
                path.lineTo(-10, 5);
                path.lineTo(-20, -5);
                canvas.drawPath(path, paint);
            }
        };

        mTextRightDrawable = new ShapeDrawable(shape);
        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, mTextRightDrawable, null);
        setCompoundDrawablePadding(UIUtils.getDimens(mContext, R.dimen.size_34));
        setPadding(mTextPadding, mTextPadding, mTextPadding, mTextPadding);
        setBackground(mTextBackground);

        mLvMultiChoice = new ListView(mContext);
        mLvMultiChoice.setBackground(mDropBackground);
        mLvMultiChoice.setChoiceMode(mIsMultiChoice ? ListView.CHOICE_MODE_MULTIPLE : ListView.CHOICE_MODE_SINGLE);

        mPopupWindow = new PopupWindow(mLvMultiChoice, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setOutsideTouchable(true);
        //如果不设置PopupWindow的背景，有些版本就会出现一个问题：无论是点击外部区域还是Back键都无法dismiss弹框
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());//mDropBackground
        //mPopupWindow.setAnimationStyle(R.style.user_sign_service_spinner);
    }

    private void initListeners() {
        if (mIsMultiChoice && !mIsLiveUpdate) {
            //多选非实时更新数据，在 PopupWindow 销毁时触发筛选
            mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    updateMultiChoices();
                }
            });
        }

        //点击展开列表
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindowAtLocation();
            }
        });

        //列表点击监听
        mLvMultiChoice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //多选 + LiveUpdate
                if (mIsMultiChoice) {
                    updateMultiChoices();
                } else {
                    String showText = getDefaultText();
                    Object item = mAdapter.getItem(position);
                    if (item instanceof String) {
                        showText = (String) item;
                        mListCheckedItems.clear();
                        mListCheckedItems.add(showText);
                        if (mPopupWindow != null && mPopupWindow.isShowing()) {
                            mPopupWindow.dismiss();
                        }
                    }
                    setText(showText);

                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onClick();
                    }
                }
            }
        });
    }

    /**
     * 多选 - 设置显示文本、添加选中项到集合 ，同时区分销毁更新和实时更新
     */
    private void updateMultiChoices() {
        if (mIsMultiChoice) {
            String showText = getDefaultText();

            SparseBooleanArray itemPositions = mLvMultiChoice.getCheckedItemPositions();
            if (itemPositions.size() != 0) {
                StringBuilder skr = new StringBuilder();
                mListCheckedItems.clear();
                for (int i = 0; i < itemPositions.size(); i++) {
                    //SparseBooleanArray.valueAt 返回值为所在位置的check状态boolean
                    if (itemPositions.valueAt(i)) {
                        mListCheckedItems.add(mListData.get(itemPositions.keyAt(i)));
                        skr.append(mListData.get(itemPositions.keyAt(i))).append(TEXT_DEFAULT_SEPARATOR);
                    }
                }
                if (skr.toString().length() != 0) {
                    showText = skr.toString();
                    showText = showText.substring(0, showText.length() - 1);
                }
            }
            setText(showText);

            if (mIsLiveUpdate) {//LiveUpdate
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onClick();
                }
            }
        }
    }

    /**
     * 针对7.0部分机型 PopupWindow 弹出位置不正确的解决方法
     */
    private void showPopupWindowAtLocation() {
        final View view = this;
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        } else {
            DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
            int width = view.getWidth();
            if (mDropWidthMode == DROP_FULLSCREEN) {
                width = displayMetrics.widthPixels;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                int screenHeight = displayMetrics.heightPixels;
                int[] location = new int[2];
                //view.getLocationInWindow(location);
                view.getLocationOnScreen(location);
                int tempHeight = mPopupWindow.getHeight();
                if (tempHeight == WindowManager.LayoutParams.MATCH_PARENT || screenHeight <= tempHeight) {
                    mPopupWindow.setHeight(screenHeight - location[1] - view.getHeight());
                }
                mPopupWindow.setWidth(width);
                mPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0], location[1] + view.getHeight());
                mPopupWindow.update();
            } else {
                if (mPopupWindow != null) {
                    mPopupWindow.setWidth(width);
                    mPopupWindow.showAsDropDown(view, 0, 0);
                    mPopupWindow.update();
                }
            }
        }
    }

    /**
     * 通用 - 获取显示的文本
     *
     * @return 如果调用过 setDefaultText或配置过 spinner_text 返回 mDefaultText，否则返回 TEXT_DEFAULT= "请选择"
     */
    public String getDefaultText() {
        return TextUtils.isEmpty(mText) ? TEXT_DEFAULT : mText;
    }

    /**
     * 通用 - 设置 Spinner 默认显示文本
     *
     * @param text
     */
    public void setDefaultText(String text) {
        this.mText = text;
        setText(text);
    }

    /**
     * 通用 - 设置下拉视图宽度 1.铺满屏幕宽度 2.默认和Spinner一般宽
     *
     * @param mode {@link #DROP_FITTEXT}(default) or {@link #DROP_FULLSCREEN}
     */
    public void setDropWidthMode(int mode) {
        this.mDropWidthMode = mode;
    }

    /**
     * 通用 - 设置列表数据
     *
     * @param dropList
     */
    public void setDropItems(List<String> dropList) {
        if (dropList != null && dropList.size() > 0) {
            this.mListData = dropList;
            //单选模式下要给出一个默认选项
            if (!mIsMultiChoice) {
                mListCheckedItems.clear();
                mListCheckedItems.add(dropList.get(0));
            }

            if (mLvMultiChoice != null) {
                if (mLvMultiChoice.getAdapter() == null) {
                    mAdapter = new CustomMultiChoiceAdapterImpl(dropList, mIsMultiChoice);
                    // CheckedTextView 实现方式
                    // mAdapter = new CheckedTextViewAbstractMultiChoiceAdapterImpl(mListData);
                    mLvMultiChoice.setAdapter(mAdapter);
                } else {
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    /**
     * 通用 - 重置Spinner原始状态
     * <p>
     * 单选模式下要给出一个默认选项
     * <p>
     * 多选要清空所有选项
     */
    public void reset() {
        if (mIsMultiChoice) {
            mListCheckedItems.clear();
            clearChoices();
        } else {
            mListCheckedItems.clear();
            mListCheckedItems.add(this.mListData.get(0));
        }
        setText(getDefaultText());
    }

    /**
     * 单选 - 获取选中的文本
     *
     * @return 单选模式下只有一个元素
     */
    public String getCheckedItem() {
        return mListCheckedItems.get(0);
    }

    /**
     * 多选 - 获取选中的文本
     */
    public List<String> getCheckedItems() {
        return mListCheckedItems;
    }

    /**
     * 多选 - 设置选中文本
     *
     * @param items 下拉列表的子选项
     */
    public void checkItems(List<String> items) {
        if (items != null && items.size() > 0 && mIsMultiChoice) {
            StringBuilder skr = new StringBuilder();
            mListCheckedItems.clear();
            for (int i = 0; i < items.size(); i++) {
                final String item = items.get(i);
                if (mListData.contains(item)) {//子选项
                    skr.append(item);
                    if (i != items.size() - 1) {
                        skr.append(TEXT_DEFAULT_SEPARATOR);
                    }

                    //设置选项
                    int index = mListData.indexOf(item);
                    mListCheckedItems.add(mListData.get(index));
                    mLvMultiChoice.setItemChecked(index, true);
                }
            }
            //设置文本
            setText(TextUtils.isEmpty(skr.toString()) ? getDefaultText() : skr.toString());
        }
    }

    /**
     * 多选 - 重置功能 - 清除选项
     */
    public void clearChoices() {
        if (mLvMultiChoice != null && mIsMultiChoice) {
            mLvMultiChoice.clearChoices();
        }
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        this.mOnItemClickListener = l;
    }

    public interface OnItemClickListener {
        /**
         * 点击事件
         */
        void onClick();
    }

    private class CustomMultiChoiceAdapterImpl extends AbstractMultiChoiceAdapter<String> {

        public CustomMultiChoiceAdapterImpl(List<String> items, boolean isMultiChoice) {
            super(items, isMultiChoice);
        }

        @Override
        protected View initConvertView(int position, View convertView, ViewGroup parent) {
            convertView = new CheckableLinearLayout(mContext, mIsMultiChoice);
            convertView.setLayoutParams(new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT, mDropItemHeight));
            convertView.setTag(new CustomViewHolder(convertView));
            return convertView;
        }

        @Override
        public void initializeViews(int position, String object, BaseViewHolder baseHolder) {
            if (baseHolder instanceof CustomViewHolder) {
                CustomViewHolder holder = (CustomViewHolder) baseHolder;
                holder.checkView.setText(object);
                holder.checkView.toggle();
            }
        }

        private class CustomViewHolder extends BaseViewHolder {
            private CheckableLinearLayout checkView;

            private CustomViewHolder(View view) {
                checkView = (CheckableLinearLayout) view;
            }
        }
    }

    private class CheckedTextViewMultiChoiceAdapterImpl extends AbstractMultiChoiceAdapter<String> {

        public CheckedTextViewMultiChoiceAdapterImpl(List<String> items) {
            super(items);
        }

        @Override
        protected View initConvertView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_nice_spinner, parent, false);
            convertView.setLayoutParams(new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT, mDropItemHeight));
            convertView.setTag(new SimpleViewHolder(convertView));
            return convertView;
        }

        @Override
        public void initializeViews(int position, String text, BaseViewHolder baseHolder) {
            if (baseHolder instanceof SimpleViewHolder) {
                SimpleViewHolder holder = (SimpleViewHolder) baseHolder;
                holder.checkedTextView.setText(text);
                holder.checkedTextView.toggle();
            }
        }

        private class SimpleViewHolder extends BaseViewHolder {
            private CheckedTextView checkedTextView;

            private SimpleViewHolder(View view) {
                checkedTextView = view.findViewById(R.id.checkedTextViewSpinner);
            }
        }
    }

    public abstract class AbstractMultiChoiceAdapter<T> extends BaseAdapter {

        private List<T> items;
        protected boolean mIsMultiChoice;

        public AbstractMultiChoiceAdapter(List<T> items) {
            this.items = items;
        }

        public AbstractMultiChoiceAdapter(List<T> items, boolean isMultiChoice) {
            this.items = items;
            this.mIsMultiChoice = isMultiChoice;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = initConvertView(position, convertView, parent);
            }
            initializeViews(position, (T) getItem(position), (BaseViewHolder) convertView.getTag());
            return convertView;
        }

        protected abstract View initConvertView(int position, View convertView, ViewGroup parent);

        public abstract void initializeViews(int position, T text, BaseViewHolder baseHolder);

        @Override
        public int getCount() {
            return items == null ? 0 : items.size();
        }

        @Override
        public T getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class BaseViewHolder {
        }
    }

    /**
     * 支持多选的LinearLayout，用做Adapter多选ItemView的父布局
     */
    private class CheckableLinearLayout extends LinearLayout implements Checkable {
        private Context mContext;
        private CheckBox mCheckBox;
        private TextView mTextView;

        private boolean isMultiChoice;
        private boolean mChecked;

        public CheckableLinearLayout(Context context, boolean isMultiChoice) {
            super(context, null, 0);
            this.mContext = context;
            this.isMultiChoice = isMultiChoice;
            initView();
        }

        private void initView() {
            this.setOrientation(HORIZONTAL);
            this.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            this.setBackgroundColor(Color.WHITE);
            this.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            this.setPadding(UIUtils.getDimens(mContext, R.dimen.size_10), 0,
                    UIUtils.getDimens(mContext, R.dimen.size_10), 0);
            if (isMultiChoice) {
                mCheckBox = new CheckBox(mContext);
                mCheckBox.setClickable(false);
                mCheckBox.setFocusable(false);
                mCheckBox.setFocusableInTouchMode(false);
                LayoutParams paramsBox = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                mCheckBox.setLayoutParams(paramsBox);
                addView(mCheckBox);
            }
            mTextView = new TextView(mContext);
            mTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            mTextView.setSingleLine(true);
            mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDropItemTextSize);
            mTextView.setTextColor(mDropItemTextColor);
            LayoutParams paramsTv = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (isMultiChoice) {
                paramsTv.setMarginStart(UIUtils.getDimens(mContext, R.dimen.size_8));
            }
            mTextView.setLayoutParams(paramsTv);

            addView(mTextView);
        }

        @Override
        public void setChecked(boolean checked) {
            this.mChecked = checked;
            if (isMultiChoice) {
                this.mCheckBox.setChecked(checked);
            }
            //this.setBackgroundDrawable(checked ? new ColorDrawable(0xff0000a0) : null);//当选中时呈现蓝色
        }

        @Override
        public boolean isChecked() {
            return mChecked;//eq mCheckBox.isChecked()
        }

        @Override
        public void toggle() {
            setChecked(!mChecked);
            if (isMultiChoice) {
                mCheckBox.toggle();
            }
        }

        public void setText(String text) {
            mTextView.setText(TextUtils.isEmpty(text) ? "" : text);
        }

        public String getText() {
            return mTextView.getText().toString();
        }
    }
}