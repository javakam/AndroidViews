package com.work.addresspicker.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Title:AddressBean
 * <p>
 * Description:地址字典
 * </p>
 *
 * @author Changbao
 * @date 2018/11/7 11:31
 */
@DatabaseTable(tableName = "sys_address")
public class AddressBean implements Serializable {
    private static final long serialVersionUID = -6326147211131237266L;
    /**
     * ilevel : 5
     * sid :
     * sname : 小营街道
     * ssuperid : 330102001
     */
    @DatabaseField(generatedId = true, canBeNull = false)
    public int id;              //PK
    @DatabaseField
    public String ilevel;       //地区级别 0:全国 1：省、直辖市 2：市、区 3：县、区 4 ：乡、镇 5：村、居委会
    @DatabaseField(index = true)
    public String sid;          //当前ID
    @DatabaseField
    public String sname;        //地址名
    @DatabaseField(index = true)
    public String ssuperid;     //父级ID，添加索引防止访问过慢

    public boolean isSelected = false; //列表单选
    public AddressBean() {
    }

    public AddressBean(int id, String ilevel, String sid, String sname, String ssuperid) {
        this.id = id;
        this.ilevel = ilevel;
        this.sid = sid;
        this.sname = sname;
        this.ssuperid = ssuperid;
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", ilevel='" + ilevel + '\'' +
                ", sid='" + sid + '\'' +
                ", sname='" + sname + '\'' +
                ", ssuperid='" + ssuperid;
    }
}
