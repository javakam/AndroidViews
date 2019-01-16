package com.work.recycler.group.bean;

import java.util.List;

public class ItemParam<Sticky, Item, Expand>{

    private ItemBean<Sticky, Item, Expand> itemBean;

    /**
     * @param isExpand   初始化展示数据时，该组数据是否展开
     */
    public ItemParam(Sticky stickyData,Item groupData, List<Expand> childData, boolean isExpand) {
        this.itemBean = new ItemBean(stickyData,groupData,childData,isExpand);
    }

    public ItemParam(Sticky stickyData,Item groupData, List<Expand> childData) {
        this.itemBean = new ItemBean(stickyData,groupData,childData,false);
    }

    public ItemBean getItemBean() {
        return itemBean;
    }

    public void setItemBean(ItemBean itemBean) {
        this.itemBean = itemBean;
    }

    public Item getGroupData(){
       return (Item) itemBean.getGroupData();
    }

    public void removeChild(int position){
        if(null == itemBean || !itemBean.hasChildren()){
            return;
        }
        itemBean.getChildData().remove(position);
    }

    public Expand getChild(int childPosition){
        return (Expand) itemBean.getChildData().get(childPosition);
    }
}