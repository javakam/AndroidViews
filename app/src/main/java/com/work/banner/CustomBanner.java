package com.work.banner;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.work.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Title: $jltitle$
 * <p>
 * Description:
 * </p>
 *
 * @author Changbao
 * @date 2019/1/9  15:12
 */
public class CustomBanner<T> extends RelativeLayout {

    private Context mContext;
    private List<T> mData;
    private Handler mHandler = new Handler();
    private CustomViewPager mViewPager;
    private InnerPagerAdapter mAdapter;
    private ViewPagerScroller mViewPagerScroller; //控制ViewPager滑动速度的Scroller
    private boolean mIsAutoPlay = true;           //是否自动播放
    private boolean mIsCanLoop = true;            //是否轮播图片
    private int mCurrentItem = 0;                 //当前位置
    private int mDelayedTime = 3000;              //切换时间间隔
    //Indicator
    private int[] mIndicatorRes = new int[]{R.drawable.banner_indicator_normal, R.drawable.banner_indicator_selected};
    private ArrayList<ImageView> mIndicators = new ArrayList<>();
    private LinearLayout mIndicatorContainer;     //indicator容器
    private int mIndicatorPaddingLeft = 0;        //indicator 距离左边的距离
    private int mIndicatorPaddingTop = 0;         //indicator 距离上边的距离
    private int mIndicatorPaddingRight = 0;       //indicator 距离右边的距离
    private int mIndicatorPaddingBottom = 0;      //indicator 距离下边的距离
    private IndicatorAlign mIndicatorAlign = IndicatorAlign.CENTER;

    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private OnPageClickListener mOnPageClickListener;

    public enum IndicatorAlign {
        /**
         *
         */
        LEFT,   //左对齐
        CENTER, //居中对齐
        RIGHT   //右对齐
    }

    public CustomBanner(Context context) {
        this(context, null, 0);
    }

    public CustomBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.layout_banner, this, true);
        mViewPager = (CustomViewPager) view.findViewById(R.id.banner_viewpager);
        mIndicatorContainer = (LinearLayout) view.findViewById(R.id.banner_indicator_container);
        mViewPager.setOffscreenPageLimit(4);

        initViewPagerScroll();// 初始化Scroller
        sureIndicatorPosition();
    }

    /**
     * 设置ViewPager的滑动速度
     */
    private void initViewPagerScroll() {
        try {
            Field mScroller = null;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            mViewPagerScroller = new ViewPagerScroller(
                    mViewPager.getContext());
            mScroller.set(mViewPager, mViewPagerScroller);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * make sure the indicator
     */
    private void sureIndicatorPosition() {
        if (mIndicatorAlign == CustomBanner.IndicatorAlign.LEFT) {
            setIndicatorAlign(CustomBanner.IndicatorAlign.LEFT);
        } else if (mIndicatorAlign == CustomBanner.IndicatorAlign.CENTER) {
            setIndicatorAlign(CustomBanner.IndicatorAlign.CENTER);
        } else {
            setIndicatorAlign(CustomBanner.IndicatorAlign.RIGHT);
        }
    }

    /**
     * 设置 Indicator 对齐方式
     *
     * @param indicatorAlign {@link IndicatorAlign }
     */
    public void setIndicatorAlign(CustomBanner.IndicatorAlign indicatorAlign) {
        mIndicatorAlign = indicatorAlign;
        LayoutParams layoutParams = (LayoutParams) mIndicatorContainer.getLayoutParams();
        if (indicatorAlign == CustomBanner.IndicatorAlign.LEFT) {
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        } else if (indicatorAlign == CustomBanner.IndicatorAlign.RIGHT) {
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        } else {
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        }
        layoutParams.setMargins(0, mIndicatorPaddingTop, 0, mIndicatorPaddingBottom);
        mIndicatorContainer.setLayoutParams(layoutParams);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!mIsCanLoop) {
            return super.dispatchTouchEvent(ev);
        }
        switch (ev.getAction()) {
            // 按住Banner的时候，停止自动轮播
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_DOWN:
                pauseCarousel();
                break;
            case MotionEvent.ACTION_UP:
                startCarousel();
                break;
            default:
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 开始轮播
     *
     * <p>应该确保在调用用了 {@link #setPages(List, ViewHolder)} 之后调用这个方法开始轮播</p>
     */
    public void startCarousel() {
        // 如果Adapter为null, 说明还没有设置数据，这个时候不应该轮播Banner
        if (mAdapter == null) {
            return;
        }
        if (mIsCanLoop) {
            pauseCarousel();
            mIsAutoPlay = true;
            mHandler.postDelayed(mLoopRunnable, mDelayedTime);
        }
    }

    /**
     * 停止轮播
     */
    public void pauseCarousel() {
        mIsAutoPlay = false;
        mHandler.removeCallbacks(mLoopRunnable);
    }

    /**
     * 设置数据，其他的配置应该在这个方法之前调用。
     *
     * @param data   Banner 展示的数据集合
     * @param holder BannerViewHolder
     */
    /**
     * 设置数据，其他的配置应该在这个方法之前调用。
     *
     * @param data   Banner 展示的数据集合
     * @param holder BannerViewHolder
     */
    public void setPages(List<T> data, ViewHolder<T> holder) {
        if (data == null || holder == null) {
            return;
        }
        mData = data;
        //如果在播放，就先让播放停止
        pauseCarousel();

        //初始化Indicator
        initIndicator();

        mAdapter = new InnerPagerAdapter(data, holder, mIsCanLoop);
        mAdapter.setUpViewViewPager(mViewPager);
        mAdapter.setPageClickListener(mOnPageClickListener);

        mViewPager.clearOnPageChangeListeners();
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                int realPosition = position % mIndicators.size();
                if (mOnPageChangeListener != null) {
                    mOnPageChangeListener.onPageScrolled(realPosition, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentItem = position;

                // 切换indicator
                int realSelectPosition = mCurrentItem % mIndicators.size();
                for (int i = 0; i < mData.size(); i++) {
                    if (i == realSelectPosition) {
                        mIndicators.get(i).setImageResource(mIndicatorRes[1]);
                    } else {
                        mIndicators.get(i).setImageResource(mIndicatorRes[0]);
                    }
                }
                // 不能直接将mOnPageChangeListener 设置给ViewPager ,否则拿到的position 是原始的position
                if (mOnPageChangeListener != null) {
                    mOnPageChangeListener.onPageSelected(realSelectPosition);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        mIsAutoPlay = false;
                        break;
                    case ViewPager.SCROLL_STATE_SETTLING:
                        mIsAutoPlay = true;
                        break;
                    default:
                }
                if (mOnPageChangeListener != null) {
                    mOnPageChangeListener.onPageScrollStateChanged(state);
                }
            }
        });
    }

    /**
     * 初始化Indicator
     */
    private void initIndicator() {
        mIndicatorContainer.removeAllViews();
        mIndicators.clear();
        for (int i = 0; i < mData.size(); i++) {
            ImageView imageView = new ImageView(getContext());
            if (mIndicatorAlign == CustomBanner.IndicatorAlign.LEFT) {
                if (i == 0) {
                    imageView.setPadding(mIndicatorPaddingLeft + 6, 0, 6, 0);
                } else {
                    imageView.setPadding(6, 0, 6, 0);
                }
            } else if (mIndicatorAlign == CustomBanner.IndicatorAlign.RIGHT) {
                if (i == mData.size() - 1) {
                    imageView.setPadding(6, 0, 6 + mIndicatorPaddingRight, 0);
                } else {
                    imageView.setPadding(6, 0, 6, 0);
                }
            } else {
                if (mIndicatorPaddingLeft == 0 && mIndicatorPaddingRight == 0) {
                    imageView.setPadding(0, 0, 0, 0);
                } else {
                    imageView.setPadding(6, 0, 6, 0);
                }
            }

            if (i == (mCurrentItem % mData.size())) {
                imageView.setImageResource(mIndicatorRes[1]);
            } else {
                imageView.setImageResource(mIndicatorRes[0]);
            }
            mIndicators.add(imageView);
            mIndicatorContainer.addView(imageView);
        }
    }

    /**
     * 设置是否可以轮播
     *
     * @param canLoop
     */
    public void setCanLoop(boolean canLoop) {
        mIsCanLoop = canLoop;
        if (!canLoop) {
            pauseCarousel();
        }
    }

    /**
     * 设置 切换时间间隔
     */
    public void setDelayedTime(int delayedTime) {
        mDelayedTime = delayedTime;
    }

    public void addOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener;
    }

    /**
     * 添加Page点击事件
     */
    public void setOnPageClickListener(OnPageClickListener bannerPageClickListener) {
        mOnPageClickListener = bannerPageClickListener;
    }

    /**
     * 是否显示Indicator
     *
     * @param visible true 显示
     */
    public void setIndicatorVisible(boolean visible) {
        mIndicatorContainer.setVisibility(visible ? VISIBLE : GONE);
    }

    /**
     * set indicator padding
     *
     * @param paddingLeft
     * @param paddingTop
     * @param paddingRight
     * @param paddingBottom
     */
    public void setIndicatorPadding(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
        mIndicatorPaddingLeft = paddingLeft;
        mIndicatorPaddingTop = paddingTop;
        mIndicatorPaddingRight = paddingRight;
        mIndicatorPaddingBottom = paddingBottom;
        sureIndicatorPosition();
    }

    /**
     * 设置indicator 图片资源
     *
     * @param unSelectRes 未选中状态资源图片
     * @param selectRes   选中状态资源图片
     */
    public void setIndicatorRes(@DrawableRes int unSelectRes, @DrawableRes int selectRes) {
        mIndicatorRes[0] = unSelectRes;
        mIndicatorRes[1] = selectRes;
    }

    /**
     * 设置ViewPager切换的速度
     *
     * @param duration 切换动画时间
     */
    public void setDuration(int duration) {
        mViewPagerScroller.setDuration(duration);
    }

    /**
     * 设置是否使用ViewPager默认是的切换速度
     *
     * @param useDefaultDuration 切换动画时间
     */
    public void setUseDefaultDuration(boolean useDefaultDuration) {
        mViewPagerScroller.setUseDefaultDuration(useDefaultDuration);
    }


    private final Runnable mLoopRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIsAutoPlay) {
                mCurrentItem = mViewPager.getCurrentItem();
                mCurrentItem++;
                if (mCurrentItem == mAdapter.getCount() - 1) {
                    mCurrentItem = 0;
                    mViewPager.setCurrentItem(mCurrentItem, false);
                    mHandler.postDelayed(this, mDelayedTime);
                } else {
                    mViewPager.setCurrentItem(mCurrentItem);
                    mHandler.postDelayed(this, mDelayedTime);
                }
            } else {
                mHandler.postDelayed(this, mDelayedTime);
            }
        }
    };

    private static class ViewPagerScroller extends Scroller {
        private int mDuration = 900;// ViewPager默认的最大Duration 为600,我们默认稍微大一点。值越大越慢。
        private boolean mIsUseDefaultDuration = false;

        public ViewPagerScroller(Context context) {
            super(context);
        }

        public ViewPagerScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public ViewPagerScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, mIsUseDefaultDuration ? duration : mDuration);
        }

        public void setUseDefaultDuration(boolean useDefaultDuration) {
            mIsUseDefaultDuration = useDefaultDuration;
        }

        public boolean isUseDefaultDuration() {
            return mIsUseDefaultDuration;
        }

        public void setDuration(int duration) {
            mDuration = duration;
        }


        public int getScrollDuration() {
            return mDuration;
        }
    }

    public static class InnerPagerAdapter<T> extends PagerAdapter {
        private List<T> mList;
        private ViewHolder<T> mViewHolder;
        private ViewPager mViewPager;
        private boolean mCanLoop;
        private OnPageClickListener mPageClickListener;
        private final int mLooperCountFactor = 500;

        public InnerPagerAdapter(List<T> list, ViewHolder holder, boolean canLoop) {
            if (mList == null) {
                mList = new ArrayList<>();
            }
            //mDatas.add(datas.get(datas.size()-1));// 加入最后一个
//            for (T t : data) {
//                mDatas.add(t);
//            }
            //mDatas.add(datas.get(0));//在最后加入最前面一个
            mList = list;
            mViewHolder = holder;
            mCanLoop = canLoop;
        }

        public void setPageClickListener(OnPageClickListener pageClickListener) {
            mPageClickListener = pageClickListener;
        }

        /**
         * 初始化Adapter和设置当前选中的Item
         */
        public void setUpViewViewPager(ViewPager viewPager) {
            mViewPager = viewPager;
            mViewPager.setAdapter(this);
            mViewPager.getAdapter().notifyDataSetChanged();
            int currentItem = mCanLoop ? getStartSelectItem() : 0;
            mViewPager.setCurrentItem(currentItem);  //设置当前选中的Item
        }

        private int getStartSelectItem() {
            if (getRealCount() == 0) {
                return 0;
            }
            int currentItem = getRealCount() * mLooperCountFactor / 2;
            if (currentItem % getRealCount() == 0) {
                return currentItem;
            }
            while (currentItem % getRealCount() != 0) {
                currentItem++;
            }
            return currentItem;
        }

        public void setData(List<T> data) {
            mList = data;
        }

        @Override
        public int getCount() {
            return mCanLoop ? getRealCount() * mLooperCountFactor : getRealCount();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = getView(position, container);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            if (mCanLoop) { // 轮播模式才执行
                int position = mViewPager.getCurrentItem();
                if (position == getCount() - 1) {
                    position = 0;
                    setCurrentItem(position);
                }
            }
        }

        private void setCurrentItem(int position) {
            try {
                mViewPager.setCurrentItem(position, false);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }

        /**
         * 获取真实的Count
         */
        private int getRealCount() {
            return mList == null ? 0 : mList.size();
        }

        private View getView(int position, ViewGroup container) {

            final int realPosition = position % getRealCount();
            final ViewHolder holder = mViewHolder;

            if (holder == null) {
                throw new RuntimeException("can not return a null holder");
            }
            // create View
            View view = holder.createView(container.getContext());

            if (mList != null && mList.size() > 0) {
                holder.onBind(container.getContext(), realPosition, mList.get(realPosition));
            }

            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPageClickListener != null) {
                        mPageClickListener.onPageClick(v, realPosition);
                    }
                }
            });
            return view;
        }
    }

    public interface OnPageClickListener {
        /**
         * Banner页面选中
         */
        void onPageClick(View view, int position);
    }

    public interface ViewHolder<T> {
        /**
         * 创建View
         */
        View createView(Context context);

        /**
         * 绑定数据
         */
        void onBind(Context context, int position, T data);
    }
}
