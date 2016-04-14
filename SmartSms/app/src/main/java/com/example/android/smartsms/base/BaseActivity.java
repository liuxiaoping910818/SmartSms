package com.example.android.smartsms.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

/**
 * Created by Administrator on 2016/4/9.
 */
public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initListener();
        initData();
    }

    public abstract void initView();
    public abstract void initListener();
    public abstract void initData();
    public abstract void processClick(View v);

    @Override
    public void onClick(View v) {
        processClick(v);

    }
}

