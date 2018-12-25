package com.work.recycler.horizon_date;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.work.R;

import java.util.List;

/**
 * Title:GalleryAdapter
 * <p>
 * Description:
 * </p>
 * Author Changbao
 * Date 2018/10/17  9:35
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private List<Integer> mDatas;

    public GalleryAdapter(Context context, List<Integer> datats) {
        mInflater = LayoutInflater.from(context);
        mDatas = datats;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivItemRvHorizon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItemRvHorizon = itemView.findViewById(R.id.iv_item_rv_horizon);
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        View view = mInflater.inflate(R.layout.item_rv_horizontal, viewGroup, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        viewHolder.ivItemRvHorizon.setImageResource(mDatas.get(i));
    }
}
