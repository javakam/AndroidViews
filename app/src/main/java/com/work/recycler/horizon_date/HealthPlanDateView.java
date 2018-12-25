package com.work.recycler.horizon_date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.work.R;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Title: HealthPlanDateView
 * <p>
 * Description:日期选择控件
 * </p>
 * Author Changbao
 * Date 2018/11/2  16:21
 */
public class HealthPlanDateView extends FrameLayout {
    private static final int DATE_BEGIN = -15;          //时间起始值
    private static final int DATE_END = 15;             //时间结束值
    private static final int DATE_CENTER = 15;          //中间值索引(今天所在位置)
    //
    private Context mContext;
    private RecyclerView mRvDate;
    private LinearLayoutManager mDateLinearManager;
    private DateAdapter mDateAdapter;
    private List<DateBean> mDateList;

    //数据
    private String mToday;
    private boolean isToday = true; //默认今天
    private int mScreenWidth; //屏幕宽度

    public HealthPlanDateView(@NonNull Context context) {
        this(context, null);
    }

    public HealthPlanDateView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HealthPlanDateView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        this.mDateLinearManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        this.mToday = DateUtils.getDate(System.currentTimeMillis(), "yyyy-MM-dd");
        setBackground(new ColorDrawable(Color.WHITE));
        initViews();
    }

    @SuppressLint("InflateParams")
    private void initViews() {
        mRvDate = new RecyclerView(mContext);
//        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_rv_date, null);
//        mRvDate = v.findViewById(R.id.rv_date);
        mRvDate.setLayoutManager(mDateLinearManager);
        mRvDate.setItemAnimator(null);
        mRvDate.setHasFixedSize(true);
        //
        mScreenWidth = getScreenWidth(mContext);

        //日期
        mDateList = new ArrayList<>();
        setDataDate();//装载日期到集合中
        mDateAdapter = new DateAdapter(mContext, mDateList);
        mRvDate.setAdapter(mDateAdapter);
        mDateAdapter.setOnClickListener(dateListener);
        //移动“今”至屏幕中心
        mDateLinearManager.scrollToPositionWithOffset(DATE_CENTER,
                mScreenWidth / 2 - 67);
        addView(mRvDate);
    }

    /**
     * 健康计划-日期选择监听
     */
    private DateAdapter.OnClickListener dateListener = new DateAdapter.OnClickListener() {
        @Override
        public void onClick(View v, DateAdapter.DateAdapterHolder holder, Object obj, int i) {
            DateBean planDateBean = (DateBean) obj;
            //1.是否选中今天 yyyy-MM-dd
            String planDate = DateUtils.getPlanDate(planDateBean.date);
            isToday = planDate.equals(mToday);
            //2.联网获取当前日期的计划
            initPlan(planDate);
            //3.移动至屏幕中心
            smoothToCenter(i);
        }
    };

    private void initPlan(String planDate) {
        // Net for Data
    }

    /**
     * 选中的条目滑动的屏幕中间
     */
    private void smoothToCenter(final int position) {
        final int width = mScreenWidth / 2;//半屏宽度
        mRvDate.post(new Runnable() {
            @Override
            public void run() {
                final View childAt = mRvDate.getChildAt(position
                        - mDateLinearManager.findFirstVisibleItemPosition());
                int changeX = (int) childAt.getX();
                int childWidth = childAt.getWidth() / 2;
                mRvDate.smoothScrollBy(changeX - width + childWidth, 0);

                //滑动过程中的参数
//                Log.e("123", width + "  " + changeX + "  " + childWidth + "  " + (changeX - width + childWidth));
            }
        });
    }

    public static class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateAdapterHolder> {
        private Context mContext;
        private LayoutInflater mInflater;
        private List<DateBean> mDateBeans;
        //四种选取状态
        private Drawable mDrawableGray;
        private Drawable mDrawableRed;
        private Drawable mDrawableBlue;
        private Drawable mDrawableEmpty;
        //只能单选
        private int mSelectedPosition;
        private OnClickListener mOnClickListener;

        public interface OnClickListener {
            void onClick(View v, DateAdapterHolder holder, Object obj, int i);
        }

        public DateAdapter(Context context, List<DateBean> data) {
            this.mContext = context;
            this.mInflater = LayoutInflater.from(mContext);
            this.mDateBeans = data;
            //初始化四种选取状态
            mDrawableGray = mContext.getResources().getDrawable(R.drawable.health_plan_date_old);
            mDrawableRed = mContext.getResources().getDrawable(R.drawable.health_plan_date_old_selected);
            mDrawableBlue = mContext.getResources().getDrawable(R.drawable.health_plan_date_today);
            mDrawableEmpty = mContext.getResources().getDrawable(R.color.color_health_plan_date_future);
            //默认选中今天
            this.mSelectedPosition = HealthPlanDateView.DATE_CENTER;
        }

        @Override
        public void onBindViewHolder(final DateAdapterHolder holder, final int i) {
            final DateBean planDate = mDateBeans.get(i);

            final int type = planDate.type;//-1过去时 0今天 1未来
            //设置背景、日期
            if (type == -1) {
                if (planDate.isSelected) {
                    holder.rbPlanDate.setBackground(mDrawableRed);
                } else {
                    holder.rbPlanDate.setBackground(mDrawableGray);
                }
                holder.rbPlanDate.setText(String.valueOf(planDate.day));
                holder.viewPoint.setVisibility(View.INVISIBLE);
            } else if (type == 0) {
                if (planDate.isSelected) {
                    holder.rbPlanDate.setBackground(mDrawableBlue);
                } else {
                    holder.rbPlanDate.setBackground(mDrawableEmpty);
                }
                //0今天 显示“今”
                holder.rbPlanDate.setText("今");
                holder.viewPoint.setVisibility(View.VISIBLE);
            } else if (type == 1) {
                if (planDate.isSelected) {
                    holder.rbPlanDate.setBackground(mDrawableBlue);
                } else {
                    holder.rbPlanDate.setBackground(mDrawableEmpty);
                }
                holder.rbPlanDate.setText(String.valueOf(planDate.day));
                holder.viewPoint.setVisibility(View.INVISIBLE);
            }
            holder.rbPlanDate.setSelected(planDate.isSelected);

            //事件监听
            holder.rbPlanDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!planDate.isSelected) {
                        holder.rbPlanDate.setSelected(true);
                        //1.更新按钮状态 - 去旧迎新
                        mDateBeans.get(mSelectedPosition).isSelected = false;
                        planDate.isSelected = true;
                        notifyItemChanged(mSelectedPosition);//剔除上次选择状态
                        notifyItemChanged(i);
                        mSelectedPosition = i;

                        //2.业务处理交给Fragment处理
                        if (mOnClickListener != null) {
                            mOnClickListener.onClick(v, holder, planDate, i);
                        }
                    }
                }
            });
        }

        @Override
        public DateAdapterHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
            return new DateAdapterHolder(mInflater.inflate(R.layout.item_rv_date, viewGroup, false));
        }

        @Override
        public int getItemCount() {
            return mDateBeans == null ? 0 : mDateBeans.size();
        }

        public class DateAdapterHolder extends RecyclerView.ViewHolder {
            public TextView rbPlanDate;
            public View viewPoint;

            public DateAdapterHolder(@NonNull View itemView) {
                super(itemView);
                rbPlanDate = itemView.findViewById(R.id.tv_item_health_plan_date);
                viewPoint = itemView.findViewById(R.id.view_health_plan_point);
            }
        }

        public int getSelectedPosition() {
            return mSelectedPosition;
        }

        public void setOnClickListener(OnClickListener listener) {
            this.mOnClickListener = listener;
        }
    }

    /**
     * 顶部日期的数据
     */
    private void setDataDate() {
        DateBean bean;
        for (int i = DATE_BEGIN; i <= DATE_END; i++) {
            final Date date = DateUtils.getDayOffset(i);
            final String day = DateUtils.getDay(date);
            //-1过去时 0今天 1未来
            if (i < 0) {
                bean = new DateBean(date, day, -1, false);
            } else if (i == 0) {
                bean = new DateBean(date, day, 0, true);//默认选取今天的计划
                Log.e("123", "今日 : " + new SimpleDateFormat(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS).format(date));
            } else {
                bean = new DateBean(date, day, 1, false);
            }
            mDateList.add(bean);
        }
    }

    /**
     * 获取屏幕宽度
     */
    public int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;// 屏幕宽度（像素）
        return width;
    }

    public static class DateUtils {
        public static final String TYPE_YYYY_MM_DD = "yyyy-MM-dd";
        public static final String TYPE_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";//2018-10-16 20:17:22

        public static String getDate(Long time, String format) {
            Date date = new Date(time);
            return new SimpleDateFormat(format).format(date);
        }

        /**
         * 根据日期获取健康计划所需日期格式 yyyy-MM-dd
         */
        @SuppressLint("SimpleDateFormat")
        public static String getPlanDate(Date date) {
            return new SimpleDateFormat(TYPE_YYYY_MM_DD).format(date);
        }

        /**
         * 根据日期获取日
         */
        public static String getDay(Date date) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal.get(Calendar.DAY_OF_MONTH) + "";
        }

        /**
         * 根据日期获取星期
         */
        public static String getWeek(Date date) {
            String[] weeks = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int weekIndex = cal.get(Calendar.DAY_OF_WEEK) - 1;
            if (weekIndex < 0) {
                weekIndex = 0;
            }
            return weeks[weekIndex];
        }

        /**
         * 根据时间进行偏移
         */
        public static Date getDayOffset(int offset) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.DAY_OF_MONTH, offset);
            return calendar.getTime();
        }
    }

    public static class DateBean implements Serializable {
        private static final long serialVersionUID = 7341272474286469263L;
        public Date date;
        public String day;
        /**
         * -1过去时 0今天 1未来
         */
        public Integer type = -1;
        public Boolean isSelected = false;

        public DateBean(Date date, String day, Integer type, Boolean isSelected) {
            this.date = date;
            this.day = day;
            this.type = type;
            this.isSelected = isSelected;
        }
    }

}
