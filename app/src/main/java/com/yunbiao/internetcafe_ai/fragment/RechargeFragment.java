package com.yunbiao.internetcafe_ai.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yunbiao.internetcafe_ai.R;
import com.yunbiao.internetcafe_ai.common.Constants;
import com.yunbiao.internetcafe_ai.usb.IDCardReader;
import com.yunbiao.internetcafe_ai.usb.IdCardMsg;
import com.yunbiao.internetcafe_ai.utils.DialogUtil;
import com.yunbiao.internetcafe_ai.utils.SpeechUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RechargeFragment extends BaseNetFragment {

    private List<Integer> amtIdList = new ArrayList<>();
    private List<Integer> amtNumberList = new ArrayList<>();
    private List<Integer> amtYuanList = new ArrayList<>();

    private int mSelectedAmount;
    private EditText edtCardNumber;

    @Override
    protected int getLayout() {
        return R.layout.fragment_recharge;
    }

    @Override
    protected void initView() {
        edtCardNumber = fv(R.id.edt_card_number_recharge);
        initAmt();
    }

    @Override
    protected void initData() {
        IDCardReader.getInstance().startReaderThread(getActivity(), readListener);
    }

    private IDCardReader.ReadListener readListener = new IDCardReader.ReadListener() {
        @Override
        public void getCardInfo(IdCardMsg msg) {
            SpeechUtil.getInstance().playOkRing();
            if(msg == null || TextUtils.isEmpty(msg.id_num)){
                edtCardNumber.setText("");
                DialogUtil.instance().showFailedTips(getActivity(),"读取失败","证件读取失败，请重新刷卡");
                return;
            }
            edtCardNumber.setText(msg.id_num);
        }
    };

    @Override
    protected void onClickRight() {
        String cardNumber = edtCardNumber.getText().toString();
        if(TextUtils.isEmpty(cardNumber)){
            Log.e(TAG, "onClickRight: 请输入卡号或刷卡");
            DialogUtil.instance().show1BtnSampleTips(getActivity(),getString(R.string.recharge_card_empty_title),getString(R.string.recharge_card_empty_tip));
            return;
        }

        if (mSelectedAmount == 0) {
            Log.e(TAG, "onClickRight: 请选择金额");
            DialogUtil.instance().show1BtnSampleTips(getActivity(),getString(R.string.recharge_amt_empty_title),getString(R.string.recharge_amt_empty_tip));
            return;
        }

        Log.e(TAG, "onClickRight: 输入卡号为：" + cardNumber);
        Log.e(TAG, "onClickRight: 选中金额为：" + mSelectedAmount);

        requestTest(Constants.Url.REQUEST_ORDER,new HashMap<String, String>(),true);
    }

    @Override
    protected void onError(String mUrl, Exception e) {
        DialogUtil.instance().showFailedTips(getActivity(),"请求超时","服务器出了些状况，请您再试一次呢~");
    }

    @Override
    protected void onResponse(String mUrl, Object o) {
        String cardNumber = edtCardNumber.getText().toString();
        Bundle bundle = new Bundle();
        bundle.putString("cardNumber",cardNumber);
        bundle.putInt("amount",mSelectedAmount);
        jumpFragment(RechargePayFragment.newInstance(bundle));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        IDCardReader.getInstance().closeReaderThread();
    }
    /***
     * =====初始化金额输入========================================================================================
     */
    private void initAmt() {
        amtIdList.add(R.id.ll_20_recharge);
        amtIdList.add(R.id.ll_30_recharge);
        amtIdList.add(R.id.ll_50_recharge);
        amtIdList.add(R.id.ll_100_recharge);
        amtIdList.add(R.id.ll_200_recharge);
        amtIdList.add(R.id.ll_custom_recharge);

        amtNumberList.add(R.id.tv_20_recharge);
        amtNumberList.add(R.id.tv_30_recharge);
        amtNumberList.add(R.id.tv_50_recharge);
        amtNumberList.add(R.id.tv_100_recharge);
        amtNumberList.add(R.id.tv_200_recharge);
        amtNumberList.add(R.id.tv_custom_recharge);

        amtYuanList.add(R.id.tv_yuan_20_recharge);
        amtYuanList.add(R.id.tv_yuan_30_recharge);
        amtYuanList.add(R.id.tv_yuan_50_recharge);
        amtYuanList.add(R.id.tv_yuan_100_recharge);
        amtYuanList.add(R.id.tv_yuan_200_recharge);
        amtYuanList.add(R.id.ll_custom_input_recharge);

        for (int i = 0; i < amtIdList.size(); i++) {
            fv(amtIdList.get(i)).setOnClickListener(onClickListener);
        }

        selectedAmount(R.id.ll_30_recharge);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectedAmount(v.getId());
        }
    };

    //选择金额（如果选的是自定义，则显示输入框，如果取消选择自定义则不显示输入框）
    private void selectedAmount(int id) {
        for (int i = 0; i < amtIdList.size(); i++) {
            Integer thisId = amtIdList.get(i);//取出金额选择框的ID
            if (thisId == id) {//找出选中的ID
                //设置金额
                mSelectedAmount = getAmount(thisId);
                //设置背景色
                fv(thisId).setBackgroundResource(R.mipmap.recharge_amt_selected);
                //设置金额颜色
                ((TextView) fv(amtNumberList.get(i))).setTextColor(getResources().getColor(R.color.fragment_recharge_amt_text_color_checked));
                //设置元字颜色
                if (i < amtYuanList.size() - 1) {
                    ((TextView) fv(amtYuanList.get(i))).setTextColor(getResources().getColor(R.color.fragment_recharge_amt_text_color_checked));
                } else {//如果是最后一个，则显示输入框
                    fv(amtYuanList.get(i)).setVisibility(View.VISIBLE);
                    final EditText edtCustom = fv(R.id.edt_custom_recharge);
                    final Button btnCustom = fv(R.id.btn_custom_confirm_recharge);

                    String number = edtCustom.getText().toString();
                    if(!TextUtils.isEmpty(number)){
                        mSelectedAmount = Integer.parseInt(number);
                    }

                    btnCustom.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e(TAG, "onClick: " + edtCustom.getText().toString());
                            String number = edtCustom.getText().toString();
                            if (TextUtils.isEmpty(number)) {
                                edtCustom.setError(getString(R.string.recharge_amt_input_please));
                                return;
                            }

                            if (edtCustom.isEnabled()) {
                                edtCustom.setEnabled(false);
                                btnCustom.setText(getString(R.string.recharge_amt_input_btn_again));
                                mSelectedAmount = Integer.parseInt(number);
                            } else {
                                edtCustom.setEnabled(true);
                                btnCustom.setText(getString(R.string.recharge_amt_input_btn_confirm));
                            }
                        }
                    });
                }
            } else {//找出未选中的ID
                //设置背景色
                fv(thisId).setBackgroundResource(R.drawable.shape_recharge_amt_radiobutton_bg);
                //设置数字颜色
                ((TextView) fv(amtNumberList.get(i))).setTextColor(getResources().getColor(R.color.fragment_recharge_amt_text_color_normal));
                if (i < amtYuanList.size() - 1) {
                    //设置元字颜色
                    ((TextView) fv(amtYuanList.get(i))).setTextColor(getResources().getColor(R.color.fragment_recharge_amt_text_color_normal));
                } else {
                    //如果是最后一个则隐藏输入框
                    fv(amtYuanList.get(i)).setVisibility(View.GONE);
                }
            }
        }
    }

    //根据id获取金额（如果选择自定义，则返回0）
    private int getAmount(int id) {
        switch (id) {
            case R.id.ll_20_recharge:
                return 20;
            case R.id.ll_30_recharge:
                return 30;
            case R.id.ll_50_recharge:
                return 50;
            case R.id.ll_100_recharge:
                return 100;
            case R.id.ll_200_recharge:
                return 200;
            default:
                return 0;
        }
    }

}
