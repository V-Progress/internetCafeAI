package com.yunbiao.internetcafe_ai.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.elvishew.xlog.XLog;
import com.yunbiao.internetcafe_ai.APP;
import com.yunbiao.internetcafe_ai.common.Constants;
import com.yunbiao.internetcafe_ai.utils.ClickUtil;
import com.yunbiao.internetcafe_ai.R;
import com.yunbiao.internetcafe_ai.fragment.BalanceFragment;
import com.yunbiao.internetcafe_ai.fragment.CompareFragment;
import com.yunbiao.internetcafe_ai.fragment.RechargeFragment;
import com.yunbiao.internetcafe_ai.fragment.ScanFragment;
import com.yunbiao.internetcafe_ai.utils.DialogUtil;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private TextView tvWeather;
    private Fragment fragment;
    private DispatchCallback dispatchCallback;

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
        new Thread(new Runnable() {
            @Override
            public void run() {
                final int activeCode = FaceEngine.active(APP.getAppContext(), Constants.Face.APP_ID, Constants.Face.SDK_KEY);
                if(activeCode != ErrorInfo.MOK && activeCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "激活失败", Toast.LENGTH_SHORT).show();
                            DialogUtil.instance().showFailedTips(MainActivity.this,
                                    "激活失败",
                                    "激活失败，请检查网络，或连接其他网络重新激活\n（错误码 "+ activeCode + "）");
                        }
                    });
                }
            }
        }).start();
    }

    public void setDispatchCallback(DispatchCallback dispatchCallback){
        XLog.d("设置事件回调");
        this.dispatchCallback = dispatchCallback;
    }

    /***
     * 预约扫码
     * @param view
     */
    public void scanCode(View view) {
        if (ClickUtil.isFastClick(view)) {
            fragment = new ScanFragment();
            replaceFragment(R.id.fl_content, fragment);
        }
    }

    /***
     * 刷脸上机
     * @param view
     */
    public void checkFace(View view) {
        if (ClickUtil.isFastClick(view)) {
            fragment = new CompareFragment();
            replaceFragment(R.id.fl_content, fragment);
        }
    }


    /***
     * 自助充值
     * @param view
     */
    public void recharge(View view) {
        if (ClickUtil.isFastClick(view)) {
            fragment = new RechargeFragment();
            replaceFragment(R.id.fl_content, fragment);
        }
    }

    /***
     * 余额查询
     * @param view
     */
    public void balanceInquiry(View view) {
        if (ClickUtil.isFastClick(view)) {
            fragment = new BalanceFragment();
            replaceFragment(R.id.fl_content, fragment);
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(dispatchCallback != null){
            return dispatchCallback.dispatchKeyEvent(event);
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public interface DispatchCallback{
        boolean dispatchKeyEvent(KeyEvent event);
    }
}
