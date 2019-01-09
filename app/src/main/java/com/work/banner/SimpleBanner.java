package com.work.banner;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.work.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Title: CustomBanner
 * <p>
 * Description:Simple Banner
 * </p>
 *
 * @author Changbao
 * @date 2019/1/9  15:12
 */
public class SimpleBanner<T> extends RelativeLayout {

    private Context mContext;
    private CustomViewPager mViewPager;
    private InnerPagerAdapter mAdapter;
    private List<T> mData;
    private boolean mIsAutoPlay = true;// 是否自动播放
    private boolean mIsCanLoop = true; // 是否轮播图片

    private Handler mHandler = new Handler();
    private int mCurrentItem = 0;//当前位置
    private int mDelayedTime = 3000;// Banner 切换时间间隔
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private OnPageClickListener mOnPageClickListener;

    public SimpleBanner(Context context) {
        this(context, null, 0);
    }

    public SimpleBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_banner, this, true);
        mViewPager = v.findViewById(R.id.banner_viewpager);
        mViewPager.setOffscreenPageLimit(4);
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


    public void setOnPageClickListener(OnPageClickListener pageClickListener) {
        this.mOnPageClickListener = pageClickListener;
    }

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

        mAdapter = new InnerPagerAdapter(data, holder, mIsCanLoop);
        mAdapter.setUpViewViewPager(mViewPager);
        mAdapter.setPageClickListener(mOnPageClickListener);

        mViewPager.clearOnPageChangeListeners();
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                int realPosition = position % mData.size();
//                int realPosition = position % mIndicators.size();
                if (mOnPageChangeListener != null) {
                    mOnPageChangeListener.onPageScrolled(realPosition, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentItem = position;
                // 切换indicator
                int realSelectPosition = mCurrentItem % mData.size();

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
            this.mPageClickListener = pageClickListener;
        }

        /**
         * 初始化Adapter和设置当前选中的Item
         */
        public void setUpViewViewPager(ViewPager viewPager) {
            mViewPager = viewPager;
            mViewPager.setAdapter(this);
            notifyDataSetChanged();//todo
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
