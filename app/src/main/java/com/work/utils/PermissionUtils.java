package com.work.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.work.addresspicker.dao.IDictionary;
import com.work.base.BaseActivity;

import io.reactivex.functions.Consumer;


/**
 * Title:PermissionUtils
 * <p>
 * Description:申请动态权限
 * </p>
 * Author Jming.L
 * Date 2018/8/21 16:38
 */
public class PermissionUtils {

    @SuppressLint("CheckResult")
    public static void request(final BaseActivity activity) {
        //申请动态权限
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.setLogging(true);
        rxPermissions.requestEach(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA).subscribe(new Consumer<Permission>() {
            @Override
            public void accept(Permission permission) throws Exception {
                if (permission.granted) {
                    // 用户已经同意该权限
                    IDictionary.initDictionary(activity);
                    // Toast.makeText(activity, permission.name + "  权限申请成功", Toast.LENGTH_SHORT).show();
                } else if (permission.shouldShowRequestPermissionRationale) {
                    // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                    Toast.makeText(activity, permission.name + "  权限申请失败", Toast.LENGTH_SHORT).show();
                } else {
                    // 用户拒绝了该权限，并且选中『不再询问』

                }
            }
        });
    }
}
