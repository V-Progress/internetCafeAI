package com.yunbiao.internetcafe_ai.fragment;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbiao.internetcafe_ai.R;

public class BalanceFragment extends BaseFragment {

    private EditText edtCardNumber;
    private Button btnQuery;
    private ImageView ivPig;
    private ImageView ivVip;
    private View llInfo;
    private TextView tvCardNumber;
    private TextView tvBalance;
    private View pbLoading;
    private TextView tvName;

    @Override
    protected int getLayout() {
        return R.layout.fragment_balance;
    }

    @Override
    protected void initView() {
        edtCardNumber = fv(R.id.edt_card_number_balance);
        btnQuery = fv(R.id.btn_query_balance);

        ivPig = fv(R.id.iv_pig_balance);
        ivVip = fv(R.id.iv_vip_balance);

        pbLoading = fv(R.id.pb_loading_balance);

        llInfo = fv(R.id.ll_info_balance);
        tvCardNumber = fv(R.id.tv_card_number_balance);
        tvBalance = fv(R.id.tv_balance_balance);
        tvName = fv(R.id.tv_name_balance);

        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = edtCardNumber.getText().toString();
                if(TextUtils.isEmpty(s)){
                    Log.e(TAG, "onClick: 请输入卡号");
                    return;
                }

                pbLoading.setVisibility(View.VISIBLE);
                handler.sendEmptyMessageDelayed(0,3000);
            }
        });
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                pbLoading.setVisibility(View.GONE);
                ivPig.setVisibility(View.GONE);
                llInfo.setVisibility(View.VISIBLE);

                tvName.setText("张哈哈");
                tvBalance.setText("50.00");
                tvCardNumber.setText(edtCardNumber.getText().toString());
            }
        }
    };


    @Override
    protected void initData() {

    }
}
