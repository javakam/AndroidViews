package com.work.recycler.group.ours;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.work.R;
import com.work.recycler.group.BaseRecyclerAdapter;
import com.work.recycler.group.bean.ItemParam;

import java.util.List;

public class ArticleAdapter extends BaseRecyclerAdapter<String, Article, String, ArticleViewHolder> {

    public ArticleAdapter(Context c, List<ItemParam> data) {
        super(c, data);
    }

    @Override
    public View getGroupView(ViewGroup parent) {
        return mInflater.inflate(R.layout.item_rv_group, parent, false);
    }

    @Override
    public View getChildView(ViewGroup parent) {
        return mInflater.inflate(R.layout.item_rv_group_expand, parent, false);
    }

    @Override
    public ArticleViewHolder createRealViewHolder(Context c, View view, int viewType) {
        return new ArticleViewHolder(c, view, viewType);
    }

    @Override
    public void onBindItemHolder(ArticleViewHolder holder, int groupPos, int position, Article groupData) {
        holder.tvTitle.setText(groupData.getTitle());
        holder.tvContent.setText(groupData.getContent());
    }

    @Override
    public void onBindExpandHolder(ArticleViewHolder holder, int groupPos, int childPos, int position, String childData) {
        holder.tvName.setText(childData);
    }

    /**
     * true   全部可展开
     * false  同一时间只能展开一个
     */
    @Override
    public boolean canExpandAll() {
        return true;
    }
}