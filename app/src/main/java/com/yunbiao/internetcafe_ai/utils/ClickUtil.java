package com.yunbiao.internetcafe_ai.utils;

import android.view.View;

public class ClickUtil {
    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 500;
    private static long lastClickTime;
    private static int viewId = -1;

    public static boolean isFastClick(View view) {
        if(view.getId() != viewId){
            viewId = view.getId();
            return true;
        }

        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }
}