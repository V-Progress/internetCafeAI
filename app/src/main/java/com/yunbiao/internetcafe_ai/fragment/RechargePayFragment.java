package com.yunbiao.internetcafe_ai.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbiao.internetcafe_ai.R;

public class RechargePayFragment extends BaseFragment {

    private TextView tvTime;
    private ImageView ivWechat;
    private ImageView ivAlipay;
    private TextView tvAmount;

    @Override
    protected int getLayout() {
        return R.layout.fragment_recharge_pay;
    }

    @Override
    protected void initView() {
        tvTime = fv(R.id.tv_time_recharge_pay);
        ivWechat = fv(R.id.iv_wechat_recharge_pay);
        ivAlipay = fv(R.id.iv_alipay_recharge_pay);
        tvAmount = fv(R.id.tv_amount_recharge_pay);
    }

    @Override
    protected void initData() {
        Bundle arguments = getArguments();

        String cardNumber = arguments.getString("cardNumber");
        int amount = arguments.getInt("amount");
        tvAmount.setText(amount + ".00");

        Log.e(TAG, "initData: 需要充值卡号：" + cardNumber);
        Log.e(TAG, "initData: 需要充值的金额：" + amount);

        CountDownTimer countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTime.setText("倒计时" + (millisUntilFinished / 1000) + "S");
            }

            @Override
            public void onFinish() {
                exit();
            }
        };
        countDownTimer.start();
    }
}
