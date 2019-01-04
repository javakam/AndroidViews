package com.work.basic.animation.evaluator;

import android.animation.TypeEvaluator;
import android.graphics.Color;

/**
 * Title:HsvEvaluator
 * <p>
 * Description: 自定义 HsvEvaluator
 * <p>
 * HSL 和 HSV（也叫做 HSB）是对RGB 色彩空间中点的两种有关系的表示，<br>
 * 它们尝试描述比 RGB 更准确的感知颜色联系，并仍保持在计算上简单。<br>
 * HSL 表示 hue（色相）、saturation（饱和度）、lightness（亮度），<br>
 * HSV 表示 hue（色相）、saturation（饱和度）、value(色调) <br>
 * HSB 表示 hue（色相）、saturation（饱和度）、brightness（明度）。
 * <p>
 * https://github.com/hencoder/PracticeDraw7/blob/10efecfc004d974ddd392d68141eed0b916ab643/app/src/main/java/com/hencoder/hencoderpracticedraw7/sample/sample02/Sample02HsvEvaluatorLayout.java
 * <p>
 * <code>
 * ObjectAnimator animator = ObjectAnimator.ofInt(view, "color", 0xff00ff00);
 * animator.setEvaluator(new HsvEvaluator());// 使用自定义的 HslEvaluator
 * animator.start();
 * </code>
 *
 * @author Changbao
 * @date 2019/1/4 9:28
 */
public class HsvEvaluator implements TypeEvaluator<Integer> {
    private float[] startHsv = new float[3];
    private float[] endHsv = new float[3];
    private float[] outHsv = new float[3];

    @Override
    public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
        // 把 ARGB 转换成 HSV
        Color.colorToHSV(startValue, startHsv);
        Color.colorToHSV(endValue, endHsv);

        // 计算当前动画完成度（fraction）所对应的颜色值
        if (endHsv[0] - startHsv[0] > 180) {
            endHsv[0] -= 360;
        } else if (endHsv[0] - startHsv[0] < -180) {
            endHsv[0] += 360;
        }
        outHsv[0] = startHsv[0] + (endHsv[0] - startHsv[0]) * fraction;
        if (outHsv[0] > 360) {
            outHsv[0] -= 360;
        } else if (outHsv[0] < 0) {
            outHsv[0] += 360;
        }
        outHsv[1] = startHsv[1] + (endHsv[1] - startHsv[1]) * fraction;
        outHsv[2] = startHsv[2] + (endHsv[2] - startHsv[2]) * fraction;

        // 计算当前动画完成度（fraction）所对应的透明度
        int alpha = startValue >> 24 + (int) ((endValue >> 24 - startValue >> 24) * fraction);

        // 把 HSV 转换回 ARGB 返回
        return Color.HSVToColor(alpha, outHsv);
    }
}