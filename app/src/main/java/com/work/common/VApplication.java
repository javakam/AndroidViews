package com.work.common;

import android.app.Application;

import com.work.BuildConfig;
import com.work.addresspicker.bean.AddressBean;
import com.work.utils.AppUtils;
import com.work.utils.DaoUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Title: VApplication
 * <p>
 * Description:
 * </p>
 * Author Changbao
 * Date 2018/10/22  14:01
 */
public class VApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //1
        AppUtils.getInstance().initialize(this,BuildConfig.ROOT_DIR);
        //2
        List<Class<?>> list = new ArrayList<>();
        list.add(AddressBean.class);
        DaoUtils.initDatabase(this,1,list);
//        Library.init(this, BuildConfig.ROOT_DIR , true);//+ "/" + BuildConfig.ROOT_PATH
//        IDictionary.initDictionary(this);
    }
}
