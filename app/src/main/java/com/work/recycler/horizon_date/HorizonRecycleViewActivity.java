package com.work.recycler.horizon_date;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.work.R;
import com.work.base.BaseActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Title: HorizonRecycleViewActivity
 * <p>
 * Description:横向RecyclerView
 * </p>
 * Author Changbao
 * Date 2018/10/17  9:25
 */
public class HorizonRecycleViewActivity extends BaseActivity {
    private RecyclerView mRvhorizontal;
    private GalleryAdapter mAdapter;
    private List<Integer> mData;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("横向Rv实现日期选择");
        setContentView(R.layout.activity_rv_horizon);
        mData = new ArrayList<>(Arrays.asList(R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher));
        mRvhorizontal = findViewById(R.id.rv_horizontal);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvhorizontal.setLayoutManager(linearLayoutManager);
        //设置适配器
        mAdapter = new GalleryAdapter(this, mData);
        mRvhorizontal.setAdapter(mAdapter);

    }
}
