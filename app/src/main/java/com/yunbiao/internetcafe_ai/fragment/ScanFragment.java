package com.yunbiao.internetcafe_ai.fragment;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbiao.internetcafe_ai.R;
import com.yunbiao.internetcafe_ai.ScanKeyManager;

public class ScanFragment extends BaseFragment{

    private TextView tvTipsScan;
    private ImageView ivTipsScan;
    private final String success_tips = "您预约的座位在<font color='#F6EC1B'>A区089号</font>，请进行身份验证";
    private final String failed_tips = "您的预约码<font color='#F6EC1B'>已过期</font>";

    @Override
    protected int getLayout() {
        return R.layout.fragment_scan;
    }

    @Override
    protected void initView() {
        tvTipsScan = fv(R.id.tv_tips_scan);
        ivTipsScan = fv(R.id.iv_tips_scan);
    }

    @Override
    protected void initData() {
        //1.扫码完成后查询该码
        //2.扫码可用，提示可用，确定按钮可点。
        //3.扫码不可用，提示过期

        test();
    }

    private void test(){
        fv(R.id.btn_test_success).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSeat(success_tips,R.mipmap.seat_icon);
            }
        });
        fv(R.id.btn_test_failed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSeat(failed_tips,R.mipmap.qrcode_exp);
            }
        });
    }

    private void setSeat(String tips,int imgId){
        ivTipsScan.setImageResource(imgId);
        tvTipsScan.setText(Html.fromHtml(tips));
        tvTipsScan.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onClickRight() {
        Log.e(TAG, "onClickRight: 点击了右边的按钮");
        jumpFragment(new CompareFragment());
    }

}
