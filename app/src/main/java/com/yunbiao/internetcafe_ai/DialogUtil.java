package com.yunbiao.internetcafe_ai;

import android.app.Activity;
import android.text.TextUtils;
import android.view.WindowManager;

import com.lipse.sweetalertlib.SweetAlertDialog;

/**
 * Dialog工具类
 * @author 李超
 * @datetime 2017/7/14 17:08
 * @des
 */
public class DialogUtil {

    private final static String TITLE = "提示";
    private final static String BUTTONCANCEL = "关闭";
    private final static String BUTTONACCEPT = "确认";
    private static SweetAlertDialog dialog;

    /**
     * 1个按钮，点击之后关闭对话框
     * @param context
     * @param msg
     * @return
     */
    public static void show1Btn(Activity context, String msg) {
        show1Btn(context, TITLE, msg, null, null, false);
    }

    public static boolean isShowing(){
        return dialog != null && dialog.isShowing();
    }

    /**
     * 1个按钮
     * @param context
     * @param title
     * @param msg
     * @param cancelText
     * @param listener
     * @param isCancelable
     */
    public static void show1Btn(Activity context, String title, String msg, String cancelText,
                                SweetAlertDialog.OnSweetClickListener listener, boolean isCancelable) {
        dissmissDialog();

        dialog = new SweetAlertDialog(context, SweetAlertDialog.ONE_BTN_TYPE);
        dialog.setCancelable(isCancelable);
        dialog.setTitleText(TextUtils.isEmpty(title)?TITLE:title);
        dialog.setContentText(msg);
        if (TextUtils.isEmpty(cancelText)) {
            cancelText = BUTTONCANCEL;
        }

        dialog.setCancelText(cancelText);
        if (listener != null) {
            dialog.setCancelClickListener(listener);
        }

        WindowManager.LayoutParams attributes = dialog.getWindow().getAttributes();
        attributes.width = 500;
        attributes.height = 380;
        dialog.getWindow().setAttributes(attributes);

        dialog.show();
    }

    /**
     * 1个按钮，点击之后关闭对话框
     * @param context
     * @param msg
     * @return
     */
    public static void show2Btn(Activity context, String title,String msg,String confirmText,SweetAlertDialog.OnSweetClickListener listener2) {
        show2Btn(context, title, msg, "取消", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                dissmissDialog();
            }
        }, confirmText, listener2, false);
    }
    /**
     * 2个按钮
     * @param context
     * @param title
     * @param msg
     * @param cancelText
     * @param listener
     * @param isCancelable
     */
    public static void show2Btn(Activity context, String title, String msg,
                                String cancelText, SweetAlertDialog.OnSweetClickListener listener,
                                String confirmText,SweetAlertDialog.OnSweetClickListener listener2,
                                boolean isCancelable) {
        dissmissDialog();

        dialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        dialog.setCancelable(isCancelable);
        dialog.setTitleText(TextUtils.isEmpty(title)?TITLE:title);
        dialog.setContentText(msg);

        if (TextUtils.isEmpty(cancelText)) {
            cancelText = BUTTONCANCEL;
        }
        dialog.setCancelText(cancelText);
        if (listener != null) {
            dialog.setCancelClickListener(listener);
        }

        if (TextUtils.isEmpty(cancelText)) {
            confirmText = BUTTONACCEPT;
        }
        dialog.setConfirmText(confirmText);
        if (listener != null) {
            dialog.setConfirmClickListener(listener2);
        }

        dialog.show();
    }

    public static void dissmissDialog(){
        if( dialog!=null && dialog.isShowing()){
            dialog.dismiss();
            dialog = null;
        }
    }
}
