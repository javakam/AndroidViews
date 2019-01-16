package com.work.recycler.group;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.work.recycler.group.bean.ItemBean;
import com.work.recycler.group.bean.ItemParam;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static com.work.recycler.group.BaseViewHolder.VIEW_TYPE_CHILD;
import static com.work.recycler.group.BaseViewHolder.VIEW_TYPE_PARENT;

public abstract class BaseRecyclerAdapter<Sticky, Item, Expand, VH extends BaseViewHolder> extends RecyclerView.Adapter<VH> {

    public static final String TAG = "123";

    protected Context mContext;
    protected LayoutInflater mInflater;
    /**
     * all data
     */
    protected List<ItemParam> allData;

    /**
     * sticky data
     */
    protected List<Sticky> stickyData;

    protected LinkedHashMap<Sticky, List<Item>> stickyGroup;

    /**
     * showing data
     */
    protected List showingData = new ArrayList<>();

    /**
     * child data
     */
    protected List<List<Expand>> childData;

    private OnRecyclerViewListener.OnItemClickListener itemClickListener;
    private OnRecyclerViewListener.OnItemLongClickListener itemLongClickListener;

    public void setOnItemClickListener(OnRecyclerViewListener.OnItemClickListener l) {
        this.itemClickListener = l;
    }

    public void setOnItemLongClickListener(OnRecyclerViewListener.OnItemLongClickListener l) {
        this.itemLongClickListener = l;
    }

    public BaseRecyclerAdapter(Context c, List<ItemParam> data) {
        this.mContext = c;
        this.mInflater = LayoutInflater.from(c);
        this.allData = data;
        setShowingData();
        this.notifyDataSetChanged();

        this.stickyData = new ArrayList<>();
        for (ItemParam param : allData) {
            this.stickyData.add((Sticky) param.getItemBean().getStickyData());
        }
        stickyGroup = new LinkedHashMap<>();
        for (int i = 0; i < showingData.size(); i++) {
            stickyGroup.put(stickyData.get(i),showingData.get(i));
        }
    }

    public void setAllData(List<ItemParam> allData) {
        this.allData = allData;
        setShowingData();
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return null == showingData ? 0 : showingData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (showingData.get(position) instanceof ItemBean) {
            return VIEW_TYPE_PARENT;
        } else {
            return VIEW_TYPE_CHILD;
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == VIEW_TYPE_PARENT) {
            view = getGroupView(parent);
        } else if (viewType == VIEW_TYPE_CHILD) {
            view = getChildView(parent);
        }
        return createRealViewHolder(mContext, view, viewType);
    }


    @Override
    public void onBindViewHolder(final VH holder, final int position) {
        final Object item = showingData.get(position);
        final int gp = getGroupPosition(position);
        final int cp = getChildPosition(gp, position);
        if (item instanceof ItemBean) {
            onBindItemHolder(holder, gp, position, (Item) ((ItemBean) item).getGroupData());
            holder.groupView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != itemClickListener) {
                        itemClickListener.onGroupItemClick(position, gp, holder.groupView);
                    }
                    if (((ItemBean) item).isExpand()) {
                        collapseGroup(position);
                    } else {
                        expandGroup(position);
                    }
                }
            });
            holder.groupView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (null != itemLongClickListener) {
                        itemLongClickListener.onGroupItemLongClick(position, gp, holder.groupView);
                    }
                    return true;
                }
            });
        } else {
            onBindExpandHolder(holder, gp, cp, position, (Expand) item);
            holder.childView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != itemClickListener) {
                        itemClickListener.onChildItemClick(position, gp, cp, holder.childView);
                    }
                }
            });
            holder.childView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (null != itemLongClickListener) {
                        int gp = getGroupPosition(position);
                        itemLongClickListener.onChildItemLongClick(position, gp, cp, holder.childView);
                    }
                    return true;
                }
            });
        }
    }

    /**
     * 返回特定的标题
     */
    Sticky getGroup(int groupPosition) {
        return stickyData.get(groupPosition);
    }

    /**
     * 获得分组的数量
     *
     * @return 组的数量
     */
    int getGroupCount() {
        return stickyData.size();
    }

    /**
     * 获取某一组的数量
     *
     * @param groupPosition groupPosition
     * @return 某一组的数量
     */
    int getChildCount(int groupPosition) {
        if (stickyData == null || stickyGroup.size() == 0) {
            return 0;
        }
        if (stickyGroup.get(stickyData.get(groupPosition)) == null) {
            return 0;
        }
        return stickyGroup.get(stickyData.get(groupPosition)).size();
    }

    /**
     * 重置分组数据
     *
     * @param groups groups
     * @param titles titles
     */
    public void resetGroups(LinkedHashMap<Sticky, List<Item>> groups, List<Sticky> titles) {
        if (groups == null || titles == null) {
            return;
        }
        stickyGroup.clear();
        stickyData.clear();
        stickyGroup.putAll(groups);
        stickyData.addAll(titles);
        getShowingData().clear();
        for (Sticky key : stickyGroup.keySet()) {
            getShowingData().addAll(stickyGroup.get(key));
        }
        notifyDataSetChanged();
    }

    /**
     * 清除分组数据
     */
    public final void clearGroup() {
        stickyData.clear();
        stickyGroup.clear();
        clear();
    }

    /**
     * 从分组移除数据
     *
     * @param position 下标
     * @return 分组是否为空，要移除分组
     */
    public boolean removeGroupItem(int position) {
        int group = getGroupIndex(position);
        removeGroupChildren(group);
        int count = getChildCount(group);
        removeItem(position);
        if (count <= 0) {
            stickyData.remove(group);
            return true;
        }
        return false;
    }

    /**
     * 获取所在分组
     *
     * @param position 下标
     * @return 获取所在分组
     */
    private int getGroupIndex(int position) {
        int count = 0;
        if (position <= count) {
            return 0;
        }
        int i = 0;
        for (Sticky parent : stickyGroup.keySet()) {
            count += stickyGroup.get(parent).size();
            if (position < count) {
                return i;
            }
            i++;
        }
        return 0;
    }

    private void removeGroupChildren(int groupPosition) {
        if (groupPosition >= stickyData.size()) {
            return;
        }
        List<Item> childList = stickyGroup.get(stickyData.get(groupPosition));
        if (childList != null && childList.size() != 0) {
            childList.remove(childList.size() - 1);
        }
    }

    /**
     * setup showing data
     */
    private void setShowingData() {
        if (null != showingData) {
            showingData.clear();
        }
        if (this.childData == null) {
            this.childData = new ArrayList<>();
        }
        childData.clear();
        ItemBean itemBean;
        for (int i = 0; i < allData.size(); i++) {
            if (allData.get(i).getItemBean() != null) {
                itemBean = allData.get(i).getItemBean();
            } else {
                break;
            }
            childData.add(i, itemBean.getChildData());
            showingData.add(itemBean);
            if (itemBean.hasChildren() && itemBean.isExpand()) {
                showingData.addAll(itemBean.getChildData());
            }
        }
    }

    /**
     * expandGroup
     *
     * @param position showingData position
     */
    private void expandGroup(int position) {
        Object item = showingData.get(position);
        if (null == item) {
            return;
        }
        if (!(item instanceof ItemBean)) {
            return;
        }
        if (((ItemBean) item).isExpand()) {
            return;
        }
        if (!canExpandAll()) {
            for (int i = 0; i < showingData.size(); i++) {
                if (i != position) {
                    int tempPositino = collapseGroup(i);
                    if (tempPositino != -1) {
                        position = tempPositino;
                    }
                }
            }
        }

        List<ItemBean> tempChilds;
        if (((ItemBean) item).hasChildren()) {
            tempChilds = ((ItemBean) item).getChildData();
            ((ItemBean) item).onExpand();
            if (canExpandAll()) {
                showingData.addAll(position + 1, tempChilds);
                notifyItemRangeInserted(position + 1, tempChilds.size());
                notifyItemRangeChanged(position + 1, showingData.size() - (position + 1));
            } else {
                int tempPsi = showingData.indexOf(item);
                showingData.addAll(tempPsi + 1, tempChilds);
                notifyItemRangeInserted(tempPsi + 1, tempChilds.size());
                notifyItemRangeChanged(tempPsi + 1, showingData.size() - (tempPsi + 1));
            }
        }
    }

    /**
     * collapseGroup
     *
     * @param position showingData position
     */
    private int collapseGroup(int position) {
        Object item = showingData.get(position);
        if (null == item) {
            return -1;
        }
        if (!(item instanceof ItemBean)) {
            return -1;
        }
        if (!((ItemBean) item).isExpand()) {
            return -1;
        }
        int tempSize = showingData.size();
        List<ItemBean> tempChilds;
        if (((ItemBean) item).hasChildren()) {
            tempChilds = ((ItemBean) item).getChildData();
            ((ItemBean) item).onExpand();
            showingData.removeAll(tempChilds);
            notifyItemRangeRemoved(position + 1, tempChilds.size());
            notifyItemRangeChanged(position + 1, tempSize - (position + 1));
            return position;
        }
        return -1;
    }

    /**
     * @param position showingData position
     * @return GroupPosition
     */
    private int getGroupPosition(int position) {
        Object item = showingData.get(position);
        if (item instanceof ItemBean) {
            for (int j = 0; j < allData.size(); j++) {
                if (allData.get(j).getItemBean().equals(item)) {
                    return j;
                }
            }
        }
        for (int i = 0; i < childData.size(); i++) {
            if (childData.get(i).contains(item)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @param groupPosition
     * @param showDataPosition
     * @return ChildPosition
     */
    private int getChildPosition(int groupPosition, int showDataPosition) {
        Object item = showingData.get(showDataPosition);
        try {
            return childData.get(groupPosition).indexOf(item);
        } catch (IndexOutOfBoundsException ex) {
            Log.e(TAG, ex.getMessage());
        }
        return 0;
    }

    public List getShowingData() {
        return showingData;
    }

    /**
     * return groupView
     */
    public abstract View getGroupView(ViewGroup parent);

    /**
     * return childView
     */
    public abstract View getChildView(ViewGroup parent);

    /**
     * return <VH extends BaseViewHolder> instance
     */
    public abstract VH createRealViewHolder(Context c, View view, int viewType);

    /**
     * onBind groupData to groupView
     *
     * @param holder
     * @param position
     */
    public abstract void onBindItemHolder(VH holder, int groupPos, int position, Item groupData);

    /**
     * onBind childData to childView
     *
     * @param holder
     * @param position
     */
    public abstract void onBindExpandHolder(VH holder, int groupPos, int childPos, int position, Expand childData);

    /**
     * if return true Allow all expand otherwise Only one can be expand at the same time
     */
    public boolean canExpandAll() {
        return true;
    }

    /**
     * 对原数据进行增加删除，调用此方法进行notify
     */
    public void notifyRecyclerViewData() {
        notifyDataSetChanged();
        setShowingData();
    }

    void addAll(List<Item> items) {
        if (items != null && items.size() > 0) {
            showingData.addAll(items);
            notifyItemRangeInserted(showingData.size(), items.size());
        }
    }

    final void addItem(Item item) {
        if (item != null) {
            this.showingData.add(item);
            notifyItemChanged(showingData.size());
        }
    }

    final List<Item> getItems() {
        return showingData;
    }


    final Item getItem(int position) {
        if (position < 0 || position >= showingData.size()) {
            return null;
        }
        return (Item) showingData.get(position);
    }

    public final void removeItem(Item item) {
        if (this.showingData.contains(item)) {
            int position = showingData.indexOf(item);
            this.showingData.remove(item);
            notifyItemRemoved(position);
        }
    }

    protected final void removeItem(int position) {
        if (this.getItemCount() > position) {
            this.showingData.remove(position);
            notifyItemRemoved(position);
        }
    }

    protected final void clear() {
        showingData.clear();
        notifyDataSetChanged();
    }
}
