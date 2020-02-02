package com.yunbiao.internetcafe_ai;

import android.util.Log;
import android.widget.ImageView;

public class ScanFragment extends BaseFragment {

    @Override
    protected int getLayout() {
        return R.layout.fragment_scan;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onClickRight() {
        Log.e(TAG, "onClickRight: 点击了右边的按钮");
    }
}
