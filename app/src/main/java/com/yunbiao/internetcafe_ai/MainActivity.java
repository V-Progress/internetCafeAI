package com.yunbiao.internetcafe_ai;

import android.view.View;
import android.widget.TextView;

import com.yunbiao.internetcafe_ai.fragment.BalanceFragment;
import com.yunbiao.internetcafe_ai.fragment.FaceFragment;
import com.yunbiao.internetcafe_ai.fragment.RechargeFragment;
import com.yunbiao.internetcafe_ai.fragment.ScanFragment;

public class MainActivity extends BaseActivity {

    private TextView tvWeather;

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

    }

    /***
     * 预约扫码
     * @param view
     */
    public void scanCode(View view) {
        if(ClickUtil.isFastClick(view)){
            replaceFragment(R.id.fl_content,new ScanFragment());
        }
    }

    /***
     * 刷脸上机
     * @param view
     */
    public void checkFace(View view) {
        if(ClickUtil.isFastClick(view)){
            replaceFragment(R.id.fl_content,new FaceFragment());
        }
    }


    /***
     * 自助充值
     * @param view
     */
    public void recharge(View view) {
        if(ClickUtil.isFastClick(view)){
            replaceFragment(R.id.fl_content,new RechargeFragment());
        }
    }

    /***
     * 余额查询
     * @param view
     */
    public void balanceInquiry(View view) {
        if(ClickUtil.isFastClick(view)){
            replaceFragment(R.id.fl_content,new BalanceFragment());
        }
    }
}
