package com.yunbiao.internetcafe_ai.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yunbiao.internetcafe_ai.DialogUtil;
import com.yunbiao.internetcafe_ai.R;
import com.yunbiao.internetcafe_ai.ScanKeyManager;

public class ScanFragment extends BaseFragment {

    private TextView tvTipsScan;
    private ImageView ivTipsScan;
    private final String success_tips = "您预约的座位在<font color='#F6EC1B'>A区089号</font>，请进行身份验证";
    private final String failed_tips = "您的预约码<font color='#F6EC1B'>已过期</font>";
    private String mCode;

    public static ScanFragment newInstance(Bundle bundle) {
        ScanFragment scanFragment = new ScanFragment();
        scanFragment.setArguments(bundle);
        return scanFragment;
    }

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

    private void setSeat(String tips, int imgId) {
        ivTipsScan.setImageResource(imgId);
        tvTipsScan.setText(Html.fromHtml(tips));
        tvTipsScan.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onClickRight() {
        Log.e(TAG, "onClickRight: 点击了右边的按钮");

        if (TextUtils.isEmpty(mCode)) {
            DialogUtil.instance().show1BtnSampleTips(getActivity(), "请扫描二维码~", "请打开小程序，扫描您的二维码");
            return;
        }

        jumpFragment(new CompareFragment());
    }

    /*===测试逻辑=================================================================*/
    private void test() {
        Log.e(TAG, "test: 1111111111111");

        final EditText edtTest = fv(R.id.edt_test);
        fv(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: 222222222222222" );

                DialogUtil.instance().showProgress(getActivity(),"正在查询...");

                String s = edtTest.getText().toString();
                mCode = s;
                if(TextUtils.isEmpty(s)){
                    handler.sendEmptyMessageDelayed(0,2000);
                } else {

                    handler.sendEmptyMessageDelayed(1,2000);
                }
            }
        });
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            DialogUtil.instance().dismissProgress();
            if (msg.what == 0) {
                setSeat(failed_tips, R.mipmap.qrcode_exp);
            } else {
                setSeat(success_tips, R.mipmap.seat_icon);
            }
        }
    };
}
