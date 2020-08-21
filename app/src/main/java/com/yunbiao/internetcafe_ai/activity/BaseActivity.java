package com.yunbiao.internetcafe_ai.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;

public abstract class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏navigation
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(getLayout());

        initView();

        initData();
    }

    protected void replaceFragment(int layout, Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(layout, fragment).commit();
    }

    protected abstract int getLayout();

    protected abstract void initView();

    protected abstract void initData();

    protected <T extends View> T fv(int id) {
        return findViewById(id);
    }
}
