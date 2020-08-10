package com.yunbiao.internetcafe_ai;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DialogUtil {

    private Dialog dialog;
    private static DialogUtil instance;

    private final int TIPS_ICON = R.mipmap.tips_dialog_tip_icon;
    private final int SUCCESS_ICON = R.mipmap.tips_dialog_success_icon;
    private final int FAILED_ICON = R.mipmap.tips_dialog_failed_icon;
    private ProgressDialog progressDialog;

    public static DialogUtil instance() {
        if (instance == null) {
            synchronized (DialogUtil.class) {
                if (instance == null) {
                    instance = new DialogUtil();
                }
            }
        }
        return instance;
    }

    private DialogUtil() {
    }

    public void showProgress(Activity activity, String msg) {
        dismiss();
        dismissProgress();
        progressDialog = new ProgressDialog(activity);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    public void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    /***
     * 显示普通提示框，单按钮
     * @param activity
     * @param title
     * @param msg
     */
    public void show1BtnSampleTips(Activity activity, String title, String msg) {
        showDialog(activity, true, TIPS_ICON, title, msg, false, "确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                },
                "", null,
                null, null);
    }

    /***
     * 显示普通提示框，双按钮
     * @param activity
     * @param title
     * @param msg
     * @param rightOnClick
     */
    public void show2BtnSampleTips(Activity activity, String title, String msg, String rightButton, final View.OnClickListener rightOnClick) {
        showDialog(activity, true, TIPS_ICON, title, msg, true, "取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                },
                rightButton, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (rightOnClick != null) {
                            rightOnClick.onClick(v);
                        }
                        dismiss();
                    }
                },
                null, null);
    }

    public void showSuccessTips(Activity activity, String title, String msg) {
        showDialog(activity, true, SUCCESS_ICON, title, msg, false, "确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                },
                "", null,
                null, null);
    }

    public void showFailedTips(Activity activity, String title, String msg) {
        showDialog(activity, true, FAILED_ICON, title, msg, false, "确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                },
                "", null,
                null, null);
    }

    public void showDialog(Activity activity,
                           boolean showImage, int imageId,
                           String title, String msg,
                           boolean show2Button, String leftButton,
                           View.OnClickListener leftOnClickListener,
                           String rightButton, View.OnClickListener rightOnClickListener,
                           DialogInterface.OnShowListener onShowListener, DialogInterface.OnDismissListener onDismissListener) {
        dismissProgress();
        dismiss();
        dialog = new Dialog(activity, R.style.tipsDialog2);
        dialog.setContentView(R.layout.layout_dialog_tips);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        ImageView iv = dialog.findViewById(R.id.iv_tips_dialog);
        TextView tvTitle = dialog.findViewById(R.id.tv_title_tips_dialog);
        TextView tvMsg = dialog.findViewById(R.id.tv_msg_tips_dialog);
        Button btnLeft = dialog.findViewById(R.id.btn_left_tips_dialog);
        Button btnRight = dialog.findViewById(R.id.btn_right_tips_dialog);

        if (!showImage) {
            iv.setVisibility(View.GONE);
        }

        if (!show2Button) {
            btnRight.setVisibility(View.GONE);
        }

        iv.setImageResource(imageId);
        tvTitle.setText(title);
        tvMsg.setText(msg);
        btnLeft.setText(leftButton);
        btnRight.setText(rightButton);

        if (leftOnClickListener != null) {
            btnLeft.setOnClickListener(leftOnClickListener);
        }
        if (rightOnClickListener != null) {
            btnRight.setOnClickListener(rightOnClickListener);
        }
        if (onShowListener != null) {
            dialog.setOnShowListener(onShowListener);
        }
        if (onDismissListener != null) {
            dialog.setOnDismissListener(onDismissListener);
        }
        dialog.show();
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }
}
