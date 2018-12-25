package com.work.floatbutton;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.work.R;
import com.work.base.BaseActivity;
import com.work.common.MainActivity;
import com.work.common.VApplication;
import com.work.floatwindow.FloatWindow;

public class FloatButtonActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_float_button);
        FloatingActionButton floatingActionButton = findViewById(R.id.floating_button);
        //必须有监听事件
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
