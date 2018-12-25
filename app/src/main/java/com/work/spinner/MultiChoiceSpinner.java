package com.work.spinner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

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
@SuppressLint("AppCompatCustomView")
public class MultiChoiceSpinner extends AutoCompleteTextView {
    private static final String TEXT_DEFAULT = "适用对象";
    private static final int TEXT_COLOR = Color.parseColor("#8f9cb5");
    private static final int TEXT_ITEM_COLOR = Color.BLACK;

    private Context mContext;
    private int mTextColor = TEXT_COLOR;
    private Drawable mRDrawable;

    private ListView mLvMultiChoice;
    private PopupWindow mPopupWindow;

    private List<String> mListData = new ArrayList<>();
    private List<String> mListCheckedData = new ArrayList<>();

    public MultiChoiceSpinner(Context context) {
        this(context, null);
    }

    public MultiChoiceSpinner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiChoiceSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initViews();
        initListeners();
    }

    private void initViews() {
        setText(TEXT_DEFAULT);
        setTextColor(TEXT_COLOR);
        setSingleLine(true);
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
        mRDrawable = new ShapeDrawable(shape);
        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, mRDrawable, null);
        setCompoundDrawablePadding(UIUtils.getDimens(mContext, R.dimen.size_36));

        mLvMultiChoice = new ListView(mContext);
//        mLvMultiChoice.setBackgroundColor(getResources().getColor(R.color.core_spinner_normal));
        mLvMultiChoice.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        mListData.add("老年人");
        mListData.add("高血压");
        mListData.add("糖尿病");
        mListData.add("重性精神病");
        mListData.add("儿童");
        mListData.add("孕产妇");
        mListData.add("贫困人口");
        mListData.add("恶性肿瘤");
        mListData.add("其他");

        CustomMultiChoiceAdapterImpl customMultiChoiceAdapter = new CustomMultiChoiceAdapterImpl(mListData);
        mLvMultiChoice.setAdapter(customMultiChoiceAdapter);
//        CheckedTextViewMultiChoiceAdapterImpl checkedTextViewMultiChoiceAdapter=new CheckedTextViewMultiChoiceAdapterImpl(mListData);
//        mLvMultiChoice.setAdapter(checkedTextViewMultiChoiceAdapter);

        mPopupWindow = new PopupWindow(mLvMultiChoice, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setOutsideTouchable(true);
        //如果不设置PopupWindow的背景，有些版本就会出现一个问题：无论是点击外部区域还是Back键都无法dismiss弹框
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                String showText = TEXT_DEFAULT;
                SparseBooleanArray itemPositions = mLvMultiChoice.getCheckedItemPositions();
                Log.w("123", mLvMultiChoice.getCheckedItemPositions().toString()
                        + "     " + mLvMultiChoice.getCheckedItemIds().length);
                if (itemPositions.size() != 0) {
                    StringBuilder skr = new StringBuilder();
                    mListCheckedData.clear();
                    for (int i = 0; i < itemPositions.size(); i++) {
                        if (itemPositions.valueAt(i)) {
                            mListCheckedData.add(mListData.get(itemPositions.keyAt(i)));
                            skr.append(mListData.get(itemPositions.keyAt(i))).append("/");
                        }
                    }
                    showText = skr.toString();
                    showText = showText.substring(0, showText.length() - 1);
                }
                //todo
                for (String item : mListCheckedData) {
                    Log.e("123", item);
                }
                MultiChoiceSpinner.this.setText(showText);
            }
        });
    }

    private void initListeners() {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("123", getLeft() + "  " + getTop() + "   " + getRight() + "  " + getBottom());
                Log.e("123", getHeight() + "  " + getMeasuredHeight() + "   " + getWidth() + "  " + getMeasuredWidth());
                //mPopupWindow.showAtLocation(v, Gravity.BOTTOM, getLeft(), getBottom());
                showPopupWindowAtLocation();
            }
        });
    }

    /**
     * 针对7.0部分机型 PopupWindow 弹出位置不正确的解决方法
     */
    private void showPopupWindowAtLocation() {
        final View view = this;
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        } else {
            int width = view.getWidth();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                int screenHeight = mContext.getResources().getDisplayMetrics().heightPixels;
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
     * 获取选中的文本
     */
    public List<String> getCheckedItems() {
        return mListCheckedData;
    }

    private class CustomMultiChoiceAdapterImpl extends MultiChoiceAdapter<String> {

        public CustomMultiChoiceAdapterImpl(List<String> items) {
            super(items);
        }

        @Override
        protected View initConvertView(int position, View convertView, ViewGroup parent) {
            convertView = new CheckableLinearLayout(mContext);
            convertView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, UIUtils.getDimens(mContext, R.dimen.size_70)));
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

    private class CheckedTextViewMultiChoiceAdapterImpl extends MultiChoiceAdapter<String> {

        public CheckedTextViewMultiChoiceAdapterImpl(List<String> items) {
            super(items);
        }

        @Override
        protected View initConvertView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_nice_spinner, parent, false);
            convertView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, UIUtils.getDimens(mContext, R.dimen.size_70)));
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

    private abstract class MultiChoiceAdapter<T> extends BaseAdapter {

        private List<T> items;

        public MultiChoiceAdapter(List<T> items) {
            this.items = items;
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
}
