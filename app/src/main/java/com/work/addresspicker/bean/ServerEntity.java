package com.work.addresspicker.bean;

import java.io.Serializable;

/**
 * Title: ServerEntity
 * <p>
 * Description:服务器返回的地址
 * </p>
 *
 * @author Changbao
 * @date 2018/11/14  11:15
 */
public class ServerEntity implements Serializable {
/*
*{"data":[{"id":4,"code":"","label":"北京","value":"110000","priority":null,"mark":"","pid":3},
* {"id":22,"code":"","label":"天津","value":"120000","priority":null,"mark":"","pid":3},{"
*
* {"data":[{"id":6,"code":"","label":"东城区","value":"110101","priority":null,"mark":"","pid":5},
* {"id":7,"code":"","label":"西城区","value":"110102","priority":null,"mark":"","pid":5},{"id":8,"code":"","lab
 */
    public String id;                   //城市id 要用的下一级需要的字段
    public String code;
    public String label;
    public String value;

    public int ilevel = 0;//级别
    public boolean isSelected = false; //列表单选
    public String priority;
    public String mark;
    public int pid;

    public ServerEntity(String id, String label, String value, int ilevel, int pid) {
        this.id = id;
        this.label = label;
        this.value = value;
        this.ilevel = ilevel;
        this.pid = pid;
    }

    //最好是如下数据结构
//    public List<AddressNameBean> addressNames;
//
//    public static class AddressNameBean {
//        public String ilevel;
//        public String sid;
//        public String sname;
//        public String ssuperid;
//    }
}
