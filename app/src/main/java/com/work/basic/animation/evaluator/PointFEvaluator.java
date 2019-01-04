package com.work.basic.animation.evaluator;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * Title: PointFEvaluator
 * <p>
 * Description:借助于 TypeEvaluator，属性动画就可以通过 ofObject() 来对不限定类型的属性做动画了
 * </p>
 *
 * @author Changbao
 * @date 2019/1/4  10:32
 */
public class PointFEvaluator implements TypeEvaluator<PointF> {
    private PointF nowPointF = new PointF();

    @Override
    public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
        float curPointX = startValue.x + fraction * (endValue.x - startValue.x);
        float curPointY = startValue.y + fraction * (endValue.y - startValue.y);
        nowPointF.set(curPointX, curPointY);
        return nowPointF;
    }
}