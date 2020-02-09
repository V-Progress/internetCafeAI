package com.yunbiao.internetcafe_ai;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.yunbiao.internetcafe_ai.fragment.BalanceFragment;
import com.yunbiao.internetcafe_ai.fragment.CompareFragment;
import com.yunbiao.internetcafe_ai.fragment.RechargeFragment;
import com.yunbiao.internetcafe_ai.fragment.ScanFragment;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private TextView tvWeather;
    private ReadCardUtils readCardUtils;

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        tvWeather = fv(R.id.tv_main_weather);
    }

    @Override
    protected void initData() {
        //读卡器声明
        readCardUtils = new ReadCardUtils();
        readCardUtils.setReadSuccessListener(new ReadCardUtils.OnReadSuccessListener() {
            @Override
            public void onScanSuccess(String barcode) {
                Log.e(TAG, "barcode: " + barcode);
            }
        });

    }

    /***
     * 预约扫码
     * @param view
     */
    public void scanCode(View view) {
        if (ClickUtil.isFastClick(view)) {
            replaceFragment(R.id.fl_content, new ScanFragment());
        }
    }

    /***
     * 刷脸上机
     * @param view
     */
    public void checkFace(View view) {
        if (ClickUtil.isFastClick(view)) {
            replaceFragment(R.id.fl_content, new CompareFragment());
        }
    }


    /***
     * 自助充值
     * @param view
     */
    public void recharge(View view) {
        if (ClickUtil.isFastClick(view)) {
            replaceFragment(R.id.fl_content, new RechargeFragment());
        }
    }

    /***
     * 余额查询
     * @param view
     */
    public void balanceInquiry(View view) {
        if (ClickUtil.isFastClick(view)) {
            replaceFragment(R.id.fl_content, new BalanceFragment());
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (ReadCardUtils.isInputFromReader(this, event)) {
            if (readCardUtils != null){
                readCardUtils.resolveKeyEvent(event);
            }
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(readCardUtils != null){
            readCardUtils.removeScanSuccessListener();
            readCardUtils = null;
        }
    }
}
