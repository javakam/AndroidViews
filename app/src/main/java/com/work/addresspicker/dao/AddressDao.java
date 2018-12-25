package com.work.addresspicker.dao;


import android.text.TextUtils;

import com.mmednet.library.database.helper.DatabaseBuilder;
import com.work.addresspicker.bean.AddressBean;
import com.work.database.dao.CommonDaoImpl;

import java.util.List;

/**
 * Title:AddressDao
 * <p>
 * Description:地址Dao
 * </p>
 * Author Jming.L
 * Date 2017/11/25 15:37
 */
public class AddressDao extends CommonDaoImpl<AddressBean> {
    public static final AddressDao DAO = new AddressDao();

    public AddressDao() {
        super(DatabaseBuilder.build(DatabaseBuilder.connect(
                IDictionary.getInstance().getDictionary()), AddressBean.class));
    }

    /**
     * 省、直辖市
     */
    public List<AddressBean> getAllProvince() {
        return query("ssuperid", "000000");
    }

    /**
     * Up
     */
    public List<AddressBean> getAddressBySID(String sid) {
        return query("sid", sid);
    }

    /**
     * Down
     */
    public List<AddressBean> getAddressBySSUPERID(String ssuperid) {
        return query("ssuperid", ssuperid);
    }

    public enum AddressEnum {
        /**
         */
        NATION("-1", "全国"),
        PROVINCE("0", "省份/地区"),
        CITY("1", "城市"),
        COUNTY("2", "区/县"),
        COUNTRYSIDE("3", "乡镇/街道"),
        VILLAGE("4", "村"),
        DEFAULT("100", "地址");

        //地区级别 0:全国 1：省、直辖市 2：市、区 3：县、区 4 ：乡、镇 5：村、居委会
        private String ilevel;
        private String hintDesc;

        AddressEnum(String ilevel, String hintDesc) {
            this.ilevel = ilevel;
            this.hintDesc = hintDesc;
        }

        public static String findHintDescByIlevel(String ilevel) {
            for (AddressEnum i : values()) {
                if (TextUtils.equals(i.ilevel, ilevel)) {
                    return i.hintDesc;
                }
            }
            return DEFAULT.getHintDesc();
        }

        public String getHintDesc() {
            return hintDesc;
        }
    }

/*
{id=3,   ilevel='1',  sid='110000',sname='北京市', ssuperid='000000'}
{id=363, ilevel='1', sid='120000', sname='天津市', ssuperid='000000'}
{id=661, ilevel='1', sid='130000', sname='河北省', ssuperid='000000'}
 */
}
