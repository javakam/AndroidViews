package com.work.basic.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.PathInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.work.R;
import com.work.base.BaseActivity;
import com.work.basic.animation.evaluator.HsvEvaluator;
import com.work.basic.animation.evaluator.PointFEvaluator;

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
                            ObjectAnimator.ofFloat(objectAnimatorView, "progress", 100);
                    objectAnimator.setDuration(6000);
                    objectAnimator.setInterpolator(pathInterpolator);
                    objectAnimator.start();
                }
            }
        });
        animator.setDuration(6000);
        animator.setInterpolator(new LinearInterpolator());//AnticipateOvershootInterpolator
        animator.start();


        //3 Evaluator Color
        LinearLayout animEvaluatorContent = findViewById(R.id.ll_anim_evaluator_content);

        final AnimEvaluatorColor animEvaluatorColorView = new AnimEvaluatorColor(this);
        AnimEvaluatorColor animEvaluatorColorView0 = new AnimEvaluatorColor(this);
        AnimEvaluatorColor animEvaluatorColorView1 = new AnimEvaluatorColor(this);
        animEvaluatorColorView.setTitle("IntEvaluator");
        animEvaluatorColorView0.setTitle("ArgbEvaluator");
        animEvaluatorColorView1.setTitle("自定义HsvEvaluator颜色过渡效果更适合人类的感官");
        animEvaluatorColorView.setContent("点我");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                260, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams params0 = new LinearLayout.LayoutParams(
                260, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                260, ViewGroup.LayoutParams.MATCH_PARENT);
        params0.leftMargin = 5;
        params1.leftMargin = 5;
        animEvaluatorColorView.setLayoutParams(params);
        animEvaluatorColorView0.setLayoutParams(params0);
        animEvaluatorColorView1.setLayoutParams(params1);

        animEvaluatorContent.addView(animEvaluatorColorView);
        animEvaluatorContent.addView(animEvaluatorColorView0);
        animEvaluatorContent.addView(animEvaluatorColorView1);

        //3.1 IntEvaluator 默认
        animEvaluatorColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //晃眼睛。。。
                ObjectAnimator animEvaluator = ObjectAnimator.ofInt(animEvaluatorColorView,
                        "circleColor", 0xffff0000, 0xff00ff00);
                startAnimation(animEvaluator, null);//默认为 IntEvaluator()
            }
        });

        //3.2 ArgbEuator
        ObjectAnimator animEvaluator0 = ObjectAnimator.ofInt(animEvaluatorColorView0,
                "circleColor", 0xffff0000, 0xff00ff00);
        startAnimation(animEvaluator0, new ArgbEvaluator());


        //3.3 自定义 HsvEvaluator 颜色过渡效果更适合人类的感官
        ObjectAnimator animEvaluator1 = ObjectAnimator.ofInt(animEvaluatorColorView1,
                "circleColor", 0xffff0000, 0xff00ff00);
        startAnimation(animEvaluator1, new HsvEvaluator());
        /*
         在Android 5.0 （API 21） 加入了新的方法 ofArgb()，所以如果你的 minSdk 大于或者等于 21 ，可以直接用下面这种方式：

         ObjectAnimator animator = ObjectAnimator.ofArgb(view, "color", 0xffff0000, 0xff00ff00);
         animator.start();
         */

        //4 Evaluator PointF
        AnimEvaluatorPoint animEvaluatorPointView = findViewById(R.id.view_anim_evaluator_point);
        animEvaluatorPointView.setDescription("PointFEvaluator");
        ObjectAnimator animEvaluatorPoint = ObjectAnimator.ofObject(animEvaluatorPointView, "position",
                new PointFEvaluator(), new PointF(0, 0), new PointF(1, 1));
        startAnimation(animEvaluatorPoint, null);
        /*
        在 API 21 中，已经自带了 PointFEvaluator 这个类，
        所以如果你的 minSdk 大于或者等于 21，上面这个类你就不用写了，直接用就行了。
         */
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            android.animation.PointFEvaluator pointFEvaluator = new android.animation.PointFEvaluator(null);
        }

        //5 ofMultiInt() ofMultiFloat() 兴趣拓展

        //6 PropertyValuesHolder 适用于【同一个】动画中改变多个属性
        /*
        很多时候，你在同一个动画中会需要改变多个属性，例如在改变透明度的同时改变尺寸。
        如果使用  ViewPropertyAnimator，你可以直接用连写的方式来在一个动画中同时改变多个属性：
        view.animate().scaleX(1).scaleY(1).alpha(1);
        效果：https://ws2.sinaimg.cn/large/006tNc79ly1fjfirzt0kog30bc0a0wja.gif

        而对于 ObjectAnimator，是不能这么用的。
        不过你可以使用 PropertyValuesHolder 来同时在一个动画中改变多个属性。
         */
        ImageView ivPvh = findViewById(R.id.iv_anim_pvh);
        PropertyValuesHolder holder1 = PropertyValuesHolder.ofFloat("scaleX", 0, 1);
        PropertyValuesHolder holder2 = PropertyValuesHolder.ofFloat("scaleY", 0, 1);
        PropertyValuesHolder holder3 = PropertyValuesHolder.ofFloat("alpha", 0, 1);
        ObjectAnimator pvhAnimator = ObjectAnimator.ofPropertyValuesHolder(ivPvh, holder1, holder2, holder3);
        startAnimation(pvhAnimator, null);

        //7 AnimatorSet 多个动画配合执行
        /*
        有的时候，你不止需要在一个动画中改变多个属性，还会需要多个动画配合工作，
        比如，在内容的大小从 0 放大到 100% 大小后开始移动。

        这种情况使用 PropertyValuesHolder 是不行的，因为这些属性如果放在同一个动画中
        ，需要共享动画的开始时间、结束时间、Interpolator 等等一系列的设定，这样就不能有先后次序地执行动画了。
         */
        ImageView ivAnimSet = findViewById(R.id.iv_anim_set);
        ObjectAnimator animatorInSet0 = ObjectAnimator.ofFloat(ivAnimSet, "scaleX", 0, 1);
        ObjectAnimator animatorInSet1 = ObjectAnimator.ofFloat(ivAnimSet, "scaleY", 0, 1);
        ObjectAnimator animatorInSet2 = ObjectAnimator.ofFloat(ivAnimSet, "alpha", 0, 1);
        animatorInSet0.setRepeatCount(2);
        animatorInSet0.setRepeatMode(ValueAnimator.REVERSE);
        animatorInSet1.setRepeatCount(100);
        animatorInSet1.setRepeatMode(ValueAnimator.REVERSE);
        animatorInSet2.setRepeatCount(100);
        animatorInSet2.setRepeatMode(ValueAnimator.REVERSE);

        AnimatorSet animatorSet = new AnimatorSet();
        //1 依次执行动画
        //animatorSet.playSequentially(animatorInSet0, animatorInSet1, animatorInSet2);

        //2 同时执行动画
        //animatorSet.playTogether(animatorInSet0, animatorInSet1, animatorInSet2);

        /*
        3 AnimatorSet 还可以这样 set.play(anim2).with(anim3).after(anim1)
         */
        //【注】无法创建
        //AnimatorSet.Builder builder=AnimatorSet.Builder.

        //animatorSet.play(animatorInSet2).before(animatorInSet0).with(animatorInSet1);
        //执行顺序为：2、1 -> 0

        animatorSet.play(animatorInSet2).with(animatorInSet0).with(animatorInSet1);
        //执行顺序为：一起执行！

        animatorSet.setDuration(6000);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.start();

        //8 PropertyValuesHolders.ofKeyframe() 把同一个属性拆分
        /*
        @param fraction
        The time, expressed as a value between 0 and 1, representing the fraction
        of time elapsed of the overall animation duration.

        @param value
        The value that the object will animate to as the animation time approaches
        the time in this keyframe, and the the value animated from as the time passes the time in
        this keyframe.
         */
        AnimPvhKeyframeProgress animPvhKeyframeProgress = findViewById(R.id.view_anim_pvh_keyframe);
        // 在 0% 处开始
        Keyframe keyframe0 = Keyframe.ofFloat(0, 0);
        // 时间经过 30% 的时候，动画完成度 100%
        Keyframe keyframe1 = Keyframe.ofFloat(0.3F, 100);
        // 时间经过 80% 的时候，动画完成度倒退到 60%，即反弹 40%
        Keyframe keyframe2 = Keyframe.ofFloat(0.8F, 60);
        // 时间经过 100% 的时候，动画完成度从 50% 到 100%
        Keyframe keyframe3 = Keyframe.ofFloat(1, 100);

        PropertyValuesHolder pvhKeyframeHolder =
                PropertyValuesHolder.ofKeyframe("progress", keyframe0, keyframe1, keyframe2, keyframe3);
        ObjectAnimator animatorPvhKeyframe =
                ObjectAnimator.ofPropertyValuesHolder(animPvhKeyframeProgress, pvhKeyframeHolder);
        animatorPvhKeyframe.setDuration(10000);
        animatorPvhKeyframe.setInterpolator(new LinearInterpolator());
//        animatorPvhKeyframe.setRepeatCount(100);
//        animatorPvhKeyframe.setRepeatMode(ValueAnimator.RESTART);
        animatorPvhKeyframe.start();

        /*
        第二部分，「关于复杂的属性关系来做动画」，就这么三种：

        1.使用 PropertyValuesHolder 来对多个属性同时做动画；
        2.使用 AnimatorSet 来同时管理调配多个动画；
        3.PropertyValuesHolder 的进阶使用：
        使用 PropertyValuesHolder.ofKeyframe() 来把一个属性拆分成多段，执行更加精细的属性动画。
         */


        //另外 ValueAnimator 最基本的轮子
        /*
        额外简单说一下 ValuesAnimator。很多时候，你用不到它，
        只是在你使用一些第三方库的控件，而你想要做动画的属性却没有 setter / getter 方法的时候，会需要用到它。

        特征：
        功能最少、最不方便，但有时也是束缚最少、最灵活。
        比如有的时候，你要给一个第三方控件做动画，你需要更新的那个属性没有 setter 方法，
        只能直接修改，这样的话 ObjectAnimator 就不灵了啊。
        怎么办？这个时候你就可以用 ValueAnimator，在它的 onUpdate() 里面更新这个属性的值，并且手动调用 invalidate()。


        ViewPropertyAnimator、ObjectAnimator、ValueAnimator 这三种 Animator，
        它们其实是一种递进的关系：从左到右依次变得更加难用，也更加灵活。

        但我要说明一下，它们的性能是一样的，因为 ViewPropertyAnimator 和 ObjectAnimator
        的内部实现其实都是 ValueAnimator，ObjectAnimator 更是本来就是 ValueAnimator 的子类，它们三个的性能并没有差别。
        它们的差别只是使用的便捷性以及功能的灵活性。
        所以在实际使用时候的选择，只要遵循一个原则就行：尽量用简单的。
        能用 View.animate() 实现就不用 ObjectAnimator，能用 ObjectAnimator 就不用 ValueAnimator。
         */
        /**
         * ValueAnimator
         *
         * {@link PlanProgressBar.startAnimation}
         */
    }

    private void startAnimation(ValueAnimator animator, TypeEvaluator evaluator) {
        animator.setDuration(6000);
        animator.setInterpolator(new LinearInterpolator());
        if (evaluator != null) {
            animator.setEvaluator(evaluator);
        }
        animator.setRepeatCount(100);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.start();
    }
}