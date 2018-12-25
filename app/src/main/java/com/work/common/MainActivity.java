package com.work.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.work.R;
import com.work.base.BaseActivity;
import com.work.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Title:MainActivity
 * <p>
 * Description:
 * </p>
 *
 * @author Changbao
 * @date 2018/10/22 14:50
 */
public class MainActivity extends BaseActivity {
    private RecyclerView mRvMain;
    private MainAdapter mMainAdapter;
    private List<UIRouter> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionUtils.request(this);
        setContentView(R.layout.activity_main);
        mRvMain = findViewById(R.id.rv_main);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3
                , GridLayoutManager.VERTICAL, false);
        mRvMain.setLayoutManager(layoutManager);
        mRvMain.setItemAnimator(new DefaultItemAnimator());
        // mRvMain.addItemDecoration(new MyItemDecoration(this));
        mRvMain.setHasFixedSize(false);
        mMainAdapter = new MainAdapter();
        //data
        mList = new ArrayList<>();
        mList.addAll(Arrays.asList(UIRouter.values()));//去掉默认项
        mMainAdapter.setData(mList);
        //
        mRvMain.setAdapter(mMainAdapter);
    }


    public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

        private List<UIRouter> mList;

        public void setData(List<UIRouter> mList) {
            this.mList = mList;
        }

        @NonNull
        @Override
        public MainViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = View.inflate(MainActivity.this, R.layout.item_main, null);
            final int pixel = MainActivity.this.getResources().getDisplayMetrics().widthPixels / 3;
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(pixel, pixel);
            v.setLayoutParams(params);

//            L.v("item : " + v.getMeasuredWidth() + "  " + widthPixels);
            return new MainViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MainViewHolder holder, final int i) {

            final String name = mList.get(i).getDescription();
            holder.tvMain.setText(name);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, UIRouter.findClassById(i)));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }

        class MainViewHolder extends RecyclerView.ViewHolder {
            View itemView;
            TextView tvMain;

            MainViewHolder(@NonNull View itemView) {
                super(itemView);
                this.itemView = itemView;
                tvMain = itemView.findViewById(R.id.tv_main);
            }
        }
    }
}
