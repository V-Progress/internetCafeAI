package com.yunbiao.internetcafe_ai.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yunbiao.internetcafe_ai.R;

import java.util.ArrayList;
import java.util.List;


public class RechargeFragment extends BaseFragment {

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

    }

    @Override
    protected void onClickRight() {
        String cardNumber = edtCardNumber.getText().toString();
        if(TextUtils.isEmpty(cardNumber)){
            Log.e(TAG, "onClickRight: 请输入卡号或刷卡");
            return;
        }

        if (mSelectedAmount == 0) {
            Log.e(TAG, "onClickRight: 请选择金额");
            return;
        }

        Log.e(TAG, "onClickRight: 输入卡号为：" + cardNumber);
        Log.e(TAG, "onClickRight: 选中金额为：" + mSelectedAmount);

        RechargePayFragment rechargePayFragment = new RechargePayFragment();
        Bundle bundle = new Bundle();
        bundle.putString("cardNumber",cardNumber);
        bundle.putInt("amount",mSelectedAmount);
        rechargePayFragment.setArguments(bundle);
        jumpFragment(rechargePayFragment);
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
                                edtCustom.setError("请输入金额");
                                return;
                            }

                            if (edtCustom.isEnabled()) {
                                edtCustom.setEnabled(false);
                                btnCustom.setText("重新输入");
                                mSelectedAmount = Integer.parseInt(number);
                            } else {
                                edtCustom.setEnabled(true);
                                btnCustom.setText("确定");
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
