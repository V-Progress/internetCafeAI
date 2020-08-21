package com.yunbiao.internetcafe_ai.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbiao.internetcafe_ai.R;
import com.yunbiao.internetcafe_ai.common.Constants;
import com.yunbiao.internetcafe_ai.utils.DialogUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;

import okhttp3.OkHttpClient;

public class RechargePayFragment extends BaseNetFragment {
    private static final String TAG = "RechargePayFragment";
    private TextView tvTime;
    private ImageView ivWechat;
    private ImageView ivAlipay;
    private TextView tvAmount;

    public static RechargePayFragment newInstance(Bundle arguments){
        RechargePayFragment rechargePayFragment = new RechargePayFragment();
        rechargePayFragment.setArguments(arguments);
        return rechargePayFragment;
    }

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
        countdownClose();

        Bundle arguments = getArguments();

        String cardNumber = arguments.getString("cardNumber");
        int amount = arguments.getInt("amount");
        tvAmount.setText(amount + ".00");

        Log.e(TAG, "initData: 需要充值卡号：" + cardNumber);
        Log.e(TAG, "initData: 需要充值的金额：" + amount);

        ivWechat.setImageResource(R.mipmap.wechat_test_qrcode);
        ivAlipay.setImageResource(R.mipmap.alipay_test_qrcode);

//        requestTest(Constants.Url.REQUEST_PAY_CODE,new HashMap<String, String>(),true);
        requestTest(Constants.Url.QUERY_PAY_RESULT,new HashMap<String, String>(),false,false,4);
        mQueryResultTime = System.currentTimeMillis();
    }

    @Override
    protected void onError(String mUrl, Exception e) {
        if(TextUtils.equals(Constants.Url.REQUEST_PAY_CODE,mUrl)){
            DialogUtil.instance().showFailedTips(getActivity(),"有点问题","服务器出了些状况，请稍后再试");
        } else if(TextUtils.equals(Constants.Url.QUERY_PAY_RESULT,mUrl)) {
            //如果查询结果超过60秒则提示超时
            if(System.currentTimeMillis() - mQueryResultTime > 60 * 1000){
                DialogUtil.instance().showFailedTips(getActivity(),"请求超时","查询结果超时，请留意信息提醒或联系管理员");
                return;
            }
            requestTest(Constants.Url.QUERY_PAY_RESULT,new HashMap<String, String>(),true,false);
        }
    }
    private long mQueryResultTime = 0;

    @Override
    protected void onResponse(String mUrl, Object o) {
        if(TextUtils.equals(Constants.Url.REQUEST_PAY_CODE,mUrl)){
            ivWechat.setImageResource(R.mipmap.wechat_test_qrcode);
            ivAlipay.setImageResource(R.mipmap.alipay_test_qrcode);
            //获取支付码成功后查询支付结果
            requestTest(Constants.Url.QUERY_PAY_RESULT,new HashMap<String, String>(),false,false);
            mQueryResultTime = System.currentTimeMillis();
        } else if(TextUtils.equals(Constants.Url.QUERY_PAY_RESULT,mUrl)){
            DialogUtil.instance().showSuccessTip(getActivity(),"支付成功","支付成功！",new Runnable(){

                @Override
                public void run() {
                    getFragmentManager().popBackStack();
                }
            });
        }
    }

    private void countdownClose(){
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
