package com.work.floatwindow.platform;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Title: Rom
 * <p>
 * Description:平台适配
 * </p>
 *
 * @author Changbao
 * @date 2018/12/25  15:27
 */
public class Rom {

    public static boolean isIntentAvailable(Intent intent, Context context) {
        return intent != null && context.getPackageManager().queryIntentActivities(
                intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }

    public static String getRomProp(String name) {
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + name);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            String line = input.readLine();
            input.close();
            return line;
        } catch (IOException ex) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
