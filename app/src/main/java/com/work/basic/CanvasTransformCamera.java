package com.work.basic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.work.R;

/**
 * Title: CanvasTransformCamera
 * <p>
 * Description:使用 Camera 来做三维变换  https://hencoder.com/ui-1-4/
 * Camera 的三维变换有三类：旋转、平移、移动相机。
 * <p>
 * 使用 Canvas 来做常见的二维变换；<br>
 * 使用 Matrix 来做常见和不常见的二维变换；<br>
 * 使用 Camera 来做三维变换。
 * </p>
 *
 * @author Changbao
 * @date 2018/12/24  14:11
 */
public class CanvasTransformCamera extends View {
    public CanvasTransformCamera(Context context) {
        super(context);
    }

    public CanvasTransformCamera(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CanvasTransformCamera(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.maps)).getBitmap();

        //1  Camera.rotate*() 三维旋转
        Camera camera = new Camera();

        /*
        【注】△ Canvas 的几何变换顺序是反的，所以要把移动到中心的代码写在下面，把从中心移动回来的代码写在上面。
         */
        int centerX = bitmap.getWidth() / 2, centerY = bitmap.getHeight() / 2;
        canvas.save();

        camera.save();                        // 保存 Camera 的状态
        camera.rotateX(60);              // 旋转 Camera 的三维空间
        canvas.translate(centerX, centerY);   // △ 旋转之后把投影移动回来
        camera.applyToCanvas(canvas);         // 把旋转投影到 Canvas
        canvas.translate(-centerX, -centerY); // △ 旋转之前把绘制内容移动到轴心（原点）
        camera.restore();                     // 恢复 Camera 的状态

        canvas.drawBitmap(bitmap, 100, 100, new Paint());
        canvas.restore();

        /*
        此时的图形是歪斜的！
        修复前：https://ws3.sinaimg.cn/large/52eb2279ly1fig5vam5fij20rq0g0agl.jpg

        如果你需要图形左右对称，需要配合上 Canvas.translate()，
        在三维旋转之前把绘制内容的中心点移动到原点，即旋转的轴心，然后在三维旋转后再把投影移动回来：
        修复后：https://ws3.sinaimg.cn/large/52eb2279ly1fig5vucbnrj20ne0cugqq.jpg
         */

        //2 Camera.translate(float x, float y, float z) 移动  它的使用方式和 Camera.rotate*() 相同。

        //3  Camera.setLocation(x, y, z) 设置虚拟相机的位置
        //【注】这个方法有点奇葩，它的参数的单位不是像素，而是 inch，英寸。
        /*
         Android 底层的图像引擎 Skia  https://skia.org/

         这种设计源自 Android 底层的图像引擎 Skia 。在 Skia 中，Camera 的位置单位是英寸，英寸和像素的换算单位在 Skia 中被写死为了 72 像素，
         而 Android 中把这个换算单位照搬了过来。是的，它。写。死。了。

         在 Camera 中，相机的默认位置是 (0, 0, -8)（英寸）。8 x 72 = 576，所以它的默认位置是 (0, 0, -576)（像素）。
         */

        //出现图像投影过大的「糊脸」效果的解决办法
        //使用 setLocation() 方法来把相机往后移动，就可以修复这种问题。camera.setLocation(0, 0, newZ);
        //Camera.setLocation(x, y, z) 的 x 和 y 参数一般不会改变，直接填 0 就好。

        /*
        好了，上面这些就是本期的内容：范围裁切和几何变换。
         */

    }
}
