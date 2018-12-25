package com.work.addresspicker.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.work.utils.ACache;
import com.work.utils.FileUtils;

import java.io.File;
import java.io.InputStream;

/**
 * Title:IDictionary
 * <p>
 * Description:根据字典类型查询字典（现在业务字典，业务字典查不到，查询前置字典）
 * </p>
 * Author Jming.L
 * Date 2017/10/18 9:44
 */
public class IDictionary {

    private static IDictionary mDictionary;
    private static final String TAG = "IDictionary";
    private static final String KEY_DATABASE = "database";
    private static final String DIR_FILE = "dictionary.db";
    private static final String DIR_DIR = "data";

    private IDictionary() {
    }

    public static IDictionary getInstance() {
        if (mDictionary == null) {
            synchronized (IDictionary.class) {
                if (mDictionary == null) {
                    mDictionary = new IDictionary();
                }
            }
        }
        return mDictionary;
    }

    //初始化字典
    public static void initDictionary(final Context context) {
        ACache aCache = ACache.build();
        int aCacheVersion = aCache.getAsInt(KEY_DATABASE);
        int sdcardVersion = 0;
        String name = DIR_FILE;
        String path = FileUtils.getDirectoryPath(DIR_DIR) + name;
        File file = new File(path);
        if (file.exists()) {
            try {
                SQLiteDatabase current = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
                sdcardVersion = current.getVersion();
                current.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG, "数据库版本：SdcardVersion is " + sdcardVersion + " and ACacheVersion is " + aCacheVersion);
        if (aCacheVersion == sdcardVersion) {
            return;
        }
        try {
            InputStream inputStream = context.getAssets().open(name);
            FileUtils.write2File(inputStream, path);
            SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(path, null);
            int version = database.getVersion();
            database.close();
            //数据库进行缓存
            aCache.put(KEY_DATABASE, version);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public  String getDictionary() {
        return FileUtils.getDirectoryPath(DIR_DIR) + DIR_FILE;
    }
    /**
     * 文本转编码
     * @param type	字典类型
     * @param text	字典文本
     * @return		文本编码
     */
    public String  textToCode(String type, String text) {
        return null;
    }
    /**
     * 编码转文本
     * @param type	字典类型
     * @param code	文本编码
     * @return		字典文本
     */
    public String codeToText(String type, String code) {
        return null;
    }
}
