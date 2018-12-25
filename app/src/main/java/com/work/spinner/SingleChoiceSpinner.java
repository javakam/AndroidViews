package com.work.spinner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.Shape;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.work.R;
import com.work.utils.UIUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Title:CustomSpinner
 * <p>
 * Description:{@see com.mmednet.library.view.edit.ISpinner}
 * </p>
 *
 * @author Changbao
 * @date 2018/12/18 11:14
 */
@SuppressLint("AppCompatCustomView")
public class SingleChoiceSpinner extends Spinner implements EditView {

    private Context mContext;
    private boolean mEditable;
    private List<String> mHint;
    private List<String> texts;
    private OnItemEditListener mEditListener;

    private SpinnerAdapter mAdapter;
    private Drawable mRDrawable;
    private Drawable mLDrawable;
    private ColorDrawable mDropDrawable; // 下拉的背景

    private static final String DEFAULT = "请选择";
    private String mTextHint;
    private int mPadding;

    private int mTextSize;
    private int mTextColor;
    private int mHintColor;

    private int paddingT;
    private int paddingB;

    private class SpinnerAdapter extends ArrayAdapter<String> {

        private Context context;
        private List<String> list;
        private TextView textView;


        private SpinnerAdapter(Context context, List<String> list) {
            super(context, android.R.layout.simple_spinner_item, list);
            this.context = context;
            this.list = list;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
            }
            textView = (TextView) convertView.findViewById(android.R.id.text1);
            textView.setIncludeFontPadding(false);
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            Drawable right = mEditable ? mRDrawable : null;
            Drawable left = mLDrawable;
            //textView.setCompoundDrawablePadding(mPadding);
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(left, null, right, null);
            textView.setCompoundDrawablePadding(UIUtils.getDimens(context, R.dimen.size_30));
            String text = list.get(position);
            textView.setText((!mEditable && position == 0) ? "- - - - -" : text);
            textView.setTextColor((mEditable && position == 0) ? mHintColor : mTextColor);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            textView.setPadding(mPadding, paddingT, (int) (mPadding * 1.5f), paddingB);
            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
//                convertView = inflater.inflate(com.mmednet.library.R.layout.core_item_spinner_dropdown, parent, false);
//                convertView.setBackground(UIUtils.getDrawable(context, com.mmednet.library.R.drawable.core_selector_select_spinner));
//
//                TextView textView = (TextView) convertView.findViewById(com.mmednet.library.R.id.tv_hint);
                textView.setIncludeFontPadding(false);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
                textView.setTextColor(Color.parseColor("#30395c"));
                textView.setPadding((int) (mPadding * 1.5f), textView.getPaddingTop() + mPadding, (int) (mPadding * 1.5f), textView.getPaddingBottom() + mPadding);
            }

            return super.getDropDownView(position, convertView, parent);
        }
    }

    public SingleChoiceSpinner(Context context) {
        this(context, null);
    }

    public SingleChoiceSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

//        mPadding = UIUtils.getDimens(context, com.mmednet.library.R.dimen.size_title_padding);
//        paddingT = UIUtils.getDimens(context, com.mmednet.library.R.dimen.core_size_10);
//        paddingB = UIUtils.getDimens(context, com.mmednet.library.R.dimen.core_size_7);
//
//        mTextSize = UIUtils.getDimens(context, com.mmednet.library.R.dimen.core_font_24);

        initView();
    }

    private void initView() {
        //编辑框右侧
        Shape shape = new Shape() {
            private Path path = new Path();

            @Override
            public void draw(Canvas canvas, Paint paint) {
                paint.setColor(mHintColor);
                paint.setStrokeWidth(3);
                paint.setStyle(Style.STROKE);
                path.moveTo(0, -5);
                path.lineTo(-10, 5);
                path.lineTo(-20, -5);
                canvas.drawPath(path, paint);
            }
        };
        mRDrawable = new ShapeDrawable(shape);
        //下拉框图形
        mDropDrawable = new ColorDrawable(mTextColor);
        this.setPadding(0, 0, 0, 0);
        this.setEditable(true);
        texts = new ArrayList<>();
        mHint = new ArrayList<>();
        mHint.add(0, TextUtils.isEmpty(mTextHint) ? DEFAULT : mTextHint);
        mAdapter = new SpinnerAdapter(mContext, mHint);
        this.setAdapter(mAdapter);
        //下拉背景为NULL点击空白处Spinner不消失
        this.setPopupBackgroundDrawable(new ColorDrawable(Color.parseColor("#2e365a")));
        this.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        //解决两次点击同一Item不触发的BUG
                        Class<?> clazz = AdapterView.class;
                        Field field = clazz.getDeclaredField("mOldSelectedPosition");
                        field.setAccessible(true);
                        field.setInt(SingleChoiceSpinner.this, AdapterView.INVALID_POSITION);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });
        this.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = position == 0 ? null : mHint.get(position);
                if (mEditListener != null) {
                    mEditListener.onClick(text, true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Spinner关闭
     */
    public void dismiss() {
        try {
            Method method = Spinner.class.getDeclaredMethod("onDetachedFromWindow");
            method.setAccessible(true);
            method.invoke(SingleChoiceSpinner.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Drawable getDrawable() {
        ShapeDrawable normal = new ShapeDrawable(new RectShape());
        Paint paint = normal.getPaint();
        paint.setStyle(Style.STROKE);
        paint.setColor(mHintColor);
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{normal});
        layerDrawable.setLayerInset(0, -2, -2, -2, 1);
        return layerDrawable;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean isEditable() {
        return mEditable;
    }

    @Override
    public void setEditable(boolean editable) {
        this.mEditable = editable;
        this.setEnabled(editable);
        this.setBackground(editable ? getDrawable() : null);
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setTxtSize(int size) {
        this.mTextSize = size;
//        if (mLDrawable instanceof TextDrawable) {
//            ((TextDrawable) mLDrawable).setTextSize(mTextSize);
//        }
//        if (mRDrawable instanceof TextDrawable) {
//            ((TextDrawable) mRDrawable).setTextSize(mTextSize);
//        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setTextHint(String textHint) {
        this.mTextHint = textHint;
        this.mHint.add(0, textHint);
//        if (mRDrawable instanceof TextDrawable) {
//            ((TextDrawable) mRDrawable).setText(textHint);
//        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setHintColor(int color) {
        this.mHintColor = color;
//        if (mLDrawable instanceof TextDrawable) {
//            ((TextDrawable) mLDrawable).setTextColor(mHintColor);
//        }
//        if (mRDrawable instanceof TextDrawable) {
//            ((TextDrawable) mRDrawable).setTextColor(mHintColor);
//        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setTxtColor(int color) {
        this.mTextColor = color;
        this.mDropDrawable.setColor(color);
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void addHint(String... hint) {
        if (hint != null) {
            List<String> list = Arrays.asList(hint);
            mHint.addAll(list);
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setHint(String... hint) {
        mHint.clear();
        //mHint.add(0, TextUtils.isEmpty(mTextHint) ? DEFAULT : mTextHint);
        this.addHint(hint);
    }

    public void setHint(List<String> hint) {
        if (hint == null) {
            hint = new ArrayList();
        }
        String[] array = (String[]) ((List) hint).toArray(new String[((List) hint).size()]);
        this.setHint(array);
    }

    @Override
    public List<String> getHints() {
        ArrayList<String> list = new ArrayList<>();
        list.addAll(mHint);
        list.remove(0);
        return list;
    }

    @Override
    public void setTexts(String... text) {
        if (text != null && text.length > 0) {
            for (String t : text) {
                for (int i = 0; i < mHint.size(); i++) {
                    String h = mHint.get(i);
                    if (TextUtils.equals(t, h)) {
                        this.setSelection(i);
                        return;
                    }
                }
            }
            this.setSelection(getSelectedItemPosition());
        }
        this.setSelection(0);
    }

    @Override
    public List<String> getTexts() {
        texts.clear();
        int position = getSelectedItemPosition();
        String text = mHint.get(position);
        if (!TextUtils.equals(text, DEFAULT)) {
            texts.add(text);
        }
        return texts;
    }

    @Override
    public void setBackgroundView(int resId) {
    }

    @Override
    public void setLeftDrawable(String left) {
        if (left != null) { //左侧背景图形
//            TextDrawable lDrawable = new TextDrawable(mContext);
//            lDrawable.setTextColor(mHintColor);
//            lDrawable.setTextSize(mTextSize);
//            lDrawable.setText(left);
//            mLDrawable = lDrawable;
        }

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setLeftDrawable(Drawable left) {
        mLDrawable = left;
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setRightDrawable(String right) {
        if (right != null) {//右侧背景图形
//            TextDrawable rDrawable = new TextDrawable(mContext);
//            rDrawable.setTextColor(mHintColor);
//            rDrawable.setTextSize(mTextSize);
//            rDrawable.setText(right);
//            mRDrawable = rDrawable;
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setRightDrawable(Drawable right) {
        this.mRDrawable = right;
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setEditInputType(int type) {
    }

    @Override
    public void setOnItemEditListener(OnItemEditListener listener) {
        mEditListener = listener;
    }

}
