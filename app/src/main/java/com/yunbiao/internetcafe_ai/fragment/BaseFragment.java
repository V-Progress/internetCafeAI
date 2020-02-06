package com.yunbiao.internetcafe_ai.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.yunbiao.internetcafe_ai.ClickUtil;
import com.yunbiao.internetcafe_ai.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

public abstract class BaseFragment extends Fragment implements View.OnTouchListener {
    protected final String TAG = this.getClass().getSimpleName();
    private Button btnLeft;
    private Button btnRight;
    private ProgressDialog progressDialog;
    private FragmentManager fragmentManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(getLayout(), container, false);
        inflate.setOnTouchListener(this);
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        initView();

        fragmentManager = getFragmentManager();

        setFragmentButton();

        initData();
    }

    private void setFragmentButton() {
        btnLeft = fv(R.id.btn_fragment_left);
        btnRight = fv(R.id.btn_fragment_right);
        if (btnLeft != null) {
            String left = setLeftString();
            if (!TextUtils.isEmpty(left)) {
                btnLeft.setText(left);
            }
            btnLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ClickUtil.isFastClick(v)) {
                        onClickLeft();
                    } else {
                        Toast.makeText(getActivity(), "点击太快", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if (btnRight != null) {
            String right = setRightString();
            if (!TextUtils.isEmpty(right)) {
                btnRight.setText(right);
            }
            btnRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ClickUtil.isFastClick(v)) {
                        onClickRight();
                    } else {
                        Toast.makeText(getActivity(), "点击太快", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    protected abstract int getLayout();

    protected abstract void initView();

    protected abstract void initData();

    protected <T extends View> T fv(int id) {
        return getView().findViewById(id);
    }

    protected void showProgress(String msg){
        dismissProgress();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    protected void dismissProgress(){
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /***
     * 退出这个fragment
     */
    protected void exit() {
        fragmentManager.beginTransaction().remove(this).commit();
    }

    protected void jumpFragment(BaseFragment fragment) {
        fragmentManager.beginTransaction().replace(R.id.fl_content, fragment).commit();
    }

    protected void request(String url, StringCallback callback) {
        request(url, null, callback);
    }

    protected void request(String url, Map<String, String> params, StringCallback callback) {
        OkHttpUtils.post().url(url).params(params).build().execute(callback);
    }

    /**
     * 为左边的按钮设置文字
     *
     * @return
     */
    protected String setLeftString() {
        return "取消";
    }

    /***
     * 为右边的按钮设置文字
     * @return
     */
    protected String setRightString() {
        return "确定";
    }

    /***
     * 点击左边按钮
     */
    protected void onClickLeft() {
        exit();
    }

    /***
     * 点击右边按钮
     */
    protected void onClickRight() {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }
}
