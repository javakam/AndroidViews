package com.work.addresspicker.bean;

import java.io.Serializable;

/**
 * Title: CityBean
 * <p>
 * Description:服务器返回的地址
 * </p>
 *
 * @author Changbao
 * @date 2018/11/14  11:15
 */
public class CityBean implements Serializable {

    public String id;                   //城市id 要用的下一级需要的字段
    public String code;
    public String label;
    public String value;

    public int ilevel = 0;//级别
    public boolean isSelected = false; //列表单选
    public String priority;
    public String mark;
    public int pid;

    public CityBean(String id, String label, String value, int ilevel, int pid) {
        this.id = id;
        this.label = label;
        this.value = value;
        this.ilevel = ilevel;
        this.pid = pid;
    }
}
