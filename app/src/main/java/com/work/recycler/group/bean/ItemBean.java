package com.work.recycler.group.bean;

import java.util.List;

public class ItemBean<Sticky, Item, Expand> {
    /**
     * sticky data
     */
    private Sticky stickyData;

    /**
     * head data
     */
    private Item groupData;

    /**
     * child data
     */
    private List<Expand> childData;

    /**
     * 是否展开,  默认展开
     */
    private boolean isExpand = true;

    /**
     * 返回是否是父节点
     */
    public boolean isParent() {
        return true;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void onExpand() {
        isExpand = !isExpand;
    }

    public ItemBean(Sticky stickyData, Item groupData, List<Expand> childData, boolean isExpand) {
        this.stickyData = stickyData;
        this.groupData = groupData;
        this.childData = childData;
        this.isExpand = isExpand;
    }

    public boolean hasChildren() {
        if (getChildData() == null || getChildData().isEmpty()) {
            return false;
        }
        return true;
    }

    public Sticky getStickyData() {
        return stickyData;
    }

    public void setStickyData(Sticky stickyData) {
        this.stickyData = stickyData;
    }

    public List<Expand> getChildData() {
        return childData;
    }

    public void setChildData(List<Expand> childData) {
        this.childData = childData;
    }

    public void removeChild(int childPosition) {
    }

    public Item getGroupData() {
        return groupData;
    }
}