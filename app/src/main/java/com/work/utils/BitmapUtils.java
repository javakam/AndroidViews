package com.work.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Title: BitmapUtils
 * <p>
 * Description:
 * </p>
 *
 * @author Changbao
 * @date 2018/12/14  10:53
 */
public class BitmapUtils {
    /**
     * 使用Matrix将Bitmap压缩到指定大小
     *
     * @param bitmap
     * @param w
     * @param h
     * @return
     */
    public static Bitmap resizeBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) w) / width;
        float scaleHeight = ((float) h) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }
}
