package com.work.utils;

import android.content.Context;

/**
 * Title: AppUtils
 * <p>
 * Description:
 * </p>
 * Author Changbao
 * Date 2018/10/22  14:01
 */
public class AppUtils {

    private Context mContext;
    private String rootDir;                     //根目录

    private static final AppUtils ourInstance = new AppUtils();

    public static AppUtils getInstance() {
        return ourInstance;
    }

    private AppUtils() {
    }

    /**
     *
     * @param context
     * @param rootDir Sdcard根目录
     */
    public void initialize(Context context,String rootDir) {
        this.mContext = context;
        this.rootDir=rootDir;
    }

    public Context getContext() {
        if (mContext == null) {
            throw new RuntimeException("mContext param is null");
        }
        return mContext;
    }

    public String getRootDir() {
        return rootDir;
    }
}
