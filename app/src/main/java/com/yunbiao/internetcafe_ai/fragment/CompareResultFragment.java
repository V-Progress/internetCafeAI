package com.yunbiao.internetcafe_ai.fragment;

import android.os.CountDownTimer;
import android.text.Html;
import android.widget.TextView;

import com.yunbiao.internetcafe_ai.R;

public class CompareResultFragment extends BaseFragment {
    private String resultTips = "您预约的<font color='#F6EC1B'>A区089号</font>机器的设备解锁码已发送到您的手机上，请凭解锁码解锁机器！";
    private TextView tvSeatNumber;
    private TextView tvTime;

    @Override
    protected int getLayout() {
        return R.layout.fragment_compare_result;
    }

    @Override
    protected void initView() {
        tvSeatNumber = fv(R.id.tv_seat_number_compare_result);
        tvTime = fv(R.id.tv_time_compare_result);
    }

    @Override
    protected void initData() {
        tvSeatNumber.setText(Html.fromHtml(resultTips));

        CountDownTimer countDownTimer = new CountDownTimer(20000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTime.setText((millisUntilFinished / 1000) + "S后关闭");
            }

            @Override
            public void onFinish() {
                exit();
            }
        };
        countDownTimer.start();
    }
}
