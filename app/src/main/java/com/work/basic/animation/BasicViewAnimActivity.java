package com.work.basic.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Path;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.view.animation.PathInterpolator;

import com.work.R;
import com.work.base.BaseActivity;

/**
 * Title: BasicViewAnimation
 * <p>
 * Description:属性动画 https://hencoder.com/ui-1-6/
 * </p>
 *
 * @author Changbao
 * @date 2019/1/3  13:52
 */
public class BasicViewAnimActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_view_anim);
        //1 ViewPropertyAnimator
        //API：https://ws1.sinaimg.cn/large/006tKfTcgy1fj7x3rm1xxj30u50laq6y.jpg
        /*
        ViewPropertyAnimator.withStartAction/EndAction()

        这两个方法是 ViewPropertyAnimator 的独有方法。它们和 set/addListener()
        中回调的 onAnimationStart() /  onAnimationEnd() 相比起来的不同主要有两点：

        withStartAction() / withEndAction() 是一次性的，在动画执行结束后就自动弃掉了，
        就算之后再重用  ViewPropertyAnimator 来做别的动画，用它们设置的回调也不会再被调用。
        而 set/addListener() 所设置的 AnimatorListener 是持续有效的，当动画重复执行时，回调总会被调用。

        withEndAction() 设置的回调只有在动画正常结束时才会被调用，而在动画被取消时不会被执行。
        这点和 AnimatorListener.onAnimationEnd() 的行为是不一致的。
         */
        AnimViewPropertyAnimator viewPropertyAnimatorView = findViewById(R.id.view_anim_prop);

        //2 ObjectAnimator
        final AnimObjectAnimator objectAnimatorView = findViewById(R.id.view_anim_obj);
        final ObjectAnimator animator =
                ObjectAnimator.ofFloat(objectAnimatorView, "progress", 65);

        /*
        监听：Animator.AnimatorListener

        onAnimationRepeat(Animator animation)
        当动画通过 setRepeatMode() / setRepeatCount() 或 repeat() 方法重复执行时，这个方法被调用。

        由于 ViewPropertyAnimator 不支持重复，所以这个方法对 ViewPropertyAnimator 相当于无效。
         */
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                Log.w("123", "onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Log.w("123", "onAnimationEnd");
                /*
                1 AccelerateDecelerateInterpolator
                先加速再减速。默认的 Interpolator 。

                2 【重】核心速度模型：PathInterpolator 自定义动画完成度 / 时间完成度曲线。

                 用这个 Interpolator 你可以定制出任何你想要的速度模型。
                 定制的方式是使用一个 Path 对象来绘制出你要的动画完成度 / 时间完成度曲线。
                 坐标系：https://ws4.sinaimg.cn/large/006tKfTcly1fj8jmom7kaj30cd0ay74f.jpg
                */

                //根据需求，绘制出自己需要的 Path，就能定制出你要的速度模型。
                Path interpolatorPath = new Path();
                // 先以「动画完成度 : 时间完成度 = 1 : 1」的速度匀速运行 25%
                interpolatorPath.lineTo(0.25f, 0.25f);
                // 然后瞬间跳跃到 150% 的动画完成度
                interpolatorPath.moveTo(0.25f, 1.5f);
                // 再匀速倒车，返回到目标点
                interpolatorPath.lineTo(1, 1);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    PathInterpolator pathInterpolator = new PathInterpolator(interpolatorPath);
                    ObjectAnimator objectAnimator =
                            ObjectAnimator.ofFloat(objectAnimatorView, "progress", 90);
                    objectAnimator.setDuration(5000);
                    objectAnimator.setInterpolator(pathInterpolator);
                    objectAnimator.start();
                }
            }
        });
        animator.setDuration(5000);
        animator.setInterpolator(new LinearInterpolator());//AnticipateOvershootInterpolator
        animator.start();


        //3 Evaluator
        AnimEvaluator animEvaluatorView = findViewById(R.id.view_anim_evaluator);
        ObjectAnimator animEvaluator = ObjectAnimator.ofInt(animEvaluatorView,
                "circleColor", 0xffff0000, 0xff00ff00);
        animEvaluator.setDuration(3000);
        animEvaluator.setEvaluator(new ArgbEvaluator());
        animEvaluator.setRepeatMode(ValueAnimator.RESTART);
        animEvaluator.start();
        /*
         在Android 5.0 （API 21） 加入了新的方法 ofArgb()，所以如果你的 minSdk 大于或者等于 21 ，可以直接用下面这种方式：

         ObjectAnimator animator = ObjectAnimator.ofArgb(view, "color", 0xffff0000, 0xff00ff00);
         animator.start();
         */

    }
}