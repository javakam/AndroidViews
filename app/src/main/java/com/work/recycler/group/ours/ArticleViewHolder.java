package com.work.recycler.group.ours;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.work.R;
import com.work.recycler.group.BaseViewHolder;

public class ArticleViewHolder extends BaseViewHolder {

    //Item
    public TextView tvTitle, tvContent;
    //Expand
    public TextView tvName;

    public ArticleViewHolder(Context ctx, View itemView, int viewType) {
        super(ctx, itemView, viewType);
        tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        tvContent = (TextView) itemView.findViewById(R.id.tv_content);
        tvName = (TextView) itemView.findViewById(R.id.tv_name);
    }

    @Override
    public int getGroupViewResId() {
        return R.id.group;
    }

    @Override
    public int getChildViewResId() {
        return R.id.child;
    }
}