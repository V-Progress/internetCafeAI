package com.yunbiao.internetcafe_ai.fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbiao.internetcafe_ai.R;
import com.yunbiao.internetcafe_ai.common.Constants;
import com.yunbiao.internetcafe_ai.usb.IDCardReader;
import com.yunbiao.internetcafe_ai.usb.IdCardMsg;
import com.yunbiao.internetcafe_ai.utils.DialogUtil;
import com.yunbiao.internetcafe_ai.utils.SpeechUtil;

import java.util.HashMap;

public class BalanceFragment extends BaseNetFragment {

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

                requestTest(Constants.Url.QUERY_BALANCE,new HashMap<String, String>(),false);
            }
        });
    }

    @Override
    protected void initData() {
        IDCardReader.getInstance().startReaderThread(getActivity(),readListener);
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
            requestTest(Constants.Url.QUERY_BALANCE,new HashMap<String, String>(),true);
        }
    };

    private void setBalanceInfo(){
        ivPig.setVisibility(View.GONE);
        llInfo.setVisibility(View.VISIBLE);

        tvName.setText("张哈哈");
        tvBalance.setText("50.00");
        tvCardNumber.setText(edtCardNumber.getText().toString());
    }

    @Override
    protected void onError(String mUrl, Exception e) {
        DialogUtil.instance().showFailedTips(getActivity(),"查询失败","查询失败了，请重试一下吧~");
    }

    @Override
    protected void onResponse(String mUrl, Object o) {
        setBalanceInfo();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        IDCardReader.getInstance().closeReaderThread();
    }
}
