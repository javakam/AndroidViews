package com.work.banner;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.work.R;
import com.work.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Title: BannerActivity
 * <p>
 * Description:
 * </p>
 *
 * @author Changbao
 * @date 2019/1/9  10:09
 */
public class BannerActivity extends BaseActivity {

    private SimpleBanner simpleBanner;
    private CustomBanner customBanner;
    private List<Integer> list;
    public static final int[] IMAGES = new int[]{R.mipmap.icon_banner_1, R.mipmap.icon_banner_2,
            R.mipmap.icon_banner_3, R.mipmap.icon_banner_4, R.mipmap.icon_banner_5};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        simpleBanner = findViewById(R.id.simple_banner);
        customBanner = findViewById(R.id.custom_banner);

        list = new ArrayList<>();
        for (int i = 0; i < IMAGES.length; i++) {
            list.add(IMAGES[i]);
        }

        showSimpleBanner();
        showCustomBanner();
    }

    private void showSimpleBanner() {
        simpleBanner.setOnPageClickListener(new SimpleBanner.OnPageClickListener() {
            @Override
            public void onPageClick(View view, int position) {
                Toast.makeText(BannerActivity.this, "position : " + position, Toast.LENGTH_SHORT).show();
            }
        });

        simpleBanner.setPages(list, new SimpleBanner.ViewHolder<Integer>() {
            private ImageView mImageView;

            @Override
            public View createView(Context context) {
                // 返回页面布局文件
                View view = LayoutInflater.from(context).inflate(R.layout.item_banner, null);
                mImageView = (ImageView) view.findViewById(R.id.banner_image);
                return view;
            }

            @Override
            public void onBind(Context context, int position, Integer data) {
                mImageView.setImageResource(data); // 数据绑定
            }
        });
    }

    private void showCustomBanner() {
        customBanner.setOnPageClickListener(new CustomBanner.OnPageClickListener() {
            @Override
            public void onPageClick(View view, int position) {
                Toast.makeText(BannerActivity.this, "position : " + position, Toast.LENGTH_SHORT).show();
            }
        });
        customBanner.setIndicatorRes(R.drawable.banner_indicator_rect_normal, R.drawable.banner_indicator_rect_selected);
        customBanner.setIndicatorPadding(0, 0, 0, 0);
        customBanner.setPages(list, new CustomBanner.ViewHolder<Integer>() {
            private ImageView mImageView;

            @Override
            public View createView(Context context) {
                View view = LayoutInflater.from(context).inflate(R.layout.item_banner, null);
                mImageView = (ImageView) view.findViewById(R.id.banner_image);
                return view;
            }

            @Override
            public void onBind(Context context, int position, Integer data) {
                mImageView.setImageResource(data); // 数据绑定
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        simpleBanner.startCarousel();
        customBanner.startCarousel();
    }

    @Override
    protected void onPause() {
        super.onPause();
        simpleBanner.pauseCarousel();
        customBanner.pauseCarousel();
    }
}
