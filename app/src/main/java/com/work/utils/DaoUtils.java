package com.work.utils;

import android.content.Context;

import java.util.List;

/**
 * Created by changbao on 2018/11/11 0011
 */
public class DaoUtils {

    private int databaseVersion;                //数据库版本
    private List<Class<?>> databaseBeans;       //数据库Bean


    private static final DaoUtils ourInstance = new DaoUtils();

    public static DaoUtils getInstance() {
        return ourInstance;
    }

    private DaoUtils() {
    }
    /**
     * 初始化数据库
     *
     * @param databaseVersion 数据库版本
     * @param databaseBeans   映射字节码
     */
    public static void initDatabase(Context context, int databaseVersion, List<Class<?>> databaseBeans) {
        DaoUtils instance = DaoUtils.getInstance();
        if (databaseBeans != null && databaseBeans.size() > 0) {
            instance.databaseVersion = databaseVersion;
            instance.databaseBeans = databaseBeans;
            com.mmednet.library.database.helper.DatabaseHelper.initDatabaseHelper(context);
        }
    }

    public int getDatabaseVersion() {
        return databaseVersion;
    }
    public List<Class<?>> getDatabaseBeans() {
        return databaseBeans;
    }
}
