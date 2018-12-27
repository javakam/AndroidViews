package com.work.floatbutton;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.transition.AutoTransition;
import android.support.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.work.R;
import com.work.base.BaseActivity;

public class FloatButtonActivity extends BaseActivity {
    private int INTENT_REQ = 100;
    private ViewGroup container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_float_button);
        container=findViewById(R.id.container);
        FloatingActionButton floatingActionButton = findViewById(R.id.floating_button);
        //必须有监听事件
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        final TextView mTextView = (TextView) findViewById(R.id.tv_content);
        final Button mBtnTransition = (Button) findViewById(R.id.btn_transition);
        mBtnTransition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(container, new AutoTransition());
                if (mTextView.getVisibility() != View.VISIBLE) {
                    mTextView.setVisibility(View.VISIBLE);
                } else {
                    mTextView.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

//        getFloatWindow().setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(FloatButtonActivity.this, DialogActivity.class);
//                ActivityOptions options =
//                        ActivityOptions.makeSceneTransitionAnimation(FloatButtonActivity.this, container,
//                                getString(R.string.transition_dialog));//floatWindow.getView()
//                startActivityForResult(intent, INTENT_REQ, options.toBundle());
//            }
//        });
    }
}
