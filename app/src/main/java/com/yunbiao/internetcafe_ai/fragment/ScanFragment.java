package com.yunbiao.internetcafe_ai.fragment;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbiao.internetcafe_ai.activity.MainActivity;
import com.yunbiao.internetcafe_ai.common.Constants;
import com.yunbiao.internetcafe_ai.usb.ReadCardUtils;
import com.yunbiao.internetcafe_ai.utils.DialogUtil;
import com.yunbiao.internetcafe_ai.R;

import java.util.HashMap;
import java.util.Map;

public class ScanFragment extends BaseNetFragment<String> implements MainActivity.DispatchCallback,ReadCardUtils.OnReadSuccessListener {

    private TextView tvTipsScan;
    private ImageView ivTipsScan;
//    private final String success_tips = "您预约的座位在<font color='#F6EC1B'>A区089号</font>，请进行身份验证";
//    private final String failed_tips = "您的预约码<font color='#F6EC1B'>已过期</font>";
    private String mCode;
    private ReadCardUtils readCardUtils;
    public static final String JUMP_PARAM_KEY_QRCODE = "jumpParamQrCode";

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
        ((MainActivity)getActivity()).setDispatchCallback(this);
        //2.扫码可用，提示可用，确定按钮可点。
        //3.扫码不可用，提示过期

        //读卡器声明
        readCardUtils = new ReadCardUtils();
        readCardUtils.setReadSuccessListener(this);
    }

    @Override
    public void onScanSuccess(String barcode) {
        Log.e(TAG, "barcode: " + barcode);
        if (TextUtils.isEmpty(barcode)) {
            DialogUtil.instance().show1BtnSampleTips(getActivity(), getString(R.string.scan_dialog_title), getString(R.string.scan_dialog_message));
            return;
        }
        onCardScan(barcode);
    }

    public void onCardScan(String cardNumber){
        DialogUtil.instance().dismissDialog();

        mCode = cardNumber;
        Map<String,String> params = new HashMap<>();
        params.put("qrCode",mCode);
        requestTest(Constants.Url.QUERY_APPOINTMENT,params,true);
    }

    @Override
    protected void onError(String mUrl, Exception e) {
        DialogUtil.instance().showFailedTips(getActivity(),"请求超时","服务器出了些状况，请您稍后再试呢~");
    }

    @Override
    protected void onResponse(String mUrl, String s) {
        setSeat(String.format(getString(R.string.scan_query_seat_number_tip),"A区83号"), R.mipmap.seat_icon);
//        setSeat(String.format(getString(R.string.scan_query_seat_failed_tip),getString(R.string.scan_code_overdue)), R.mipmap.qrcode_exp);
    }

    @Override
    protected void onClickRight() {
        Log.e(TAG, "onClickRight: 点击了右边的按钮");

        if (TextUtils.isEmpty(mCode)) {
            DialogUtil.instance().show1BtnSampleTips(getActivity(), getString(R.string.scan_dialog_title), getString(R.string.scan_dialog_message));
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString(JUMP_PARAM_KEY_QRCODE,mCode);
        jumpFragment(CompareFragment.newInstance(bundle));
    }

    //设置显示座位
    private void setSeat(String tips, int imgId) {
        ivTipsScan.setImageResource(imgId);
        tvTipsScan.setText(Html.fromHtml(tips));
        tvTipsScan.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (readCardUtils != null && ReadCardUtils.isInputFromReader(getActivity(), event)) {
            readCardUtils.resolveKeyEvent(event);
            return false;
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity)getActivity()).setDispatchCallback(null);
        if(readCardUtils != null){
            readCardUtils.removeScanSuccessListener();
            readCardUtils = null;
        }
    }
}
