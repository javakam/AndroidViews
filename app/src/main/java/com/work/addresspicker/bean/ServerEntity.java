package com.work.addresspicker.bean;

import java.util.List;

/**
 * Title: ServerEntity
 * <p>
 * Description:服务器返回的地址
 * </p>
 *
 * @author Changbao
 * @date 2018/11/14  11:15
 */
public class ServerEntity {
    public List<AddressNameBean> addressNames;

    public static class AddressNameBean {
        public String ilevel;
        public String sid;
        public String sname;
        public String ssuperid;
    }
}
