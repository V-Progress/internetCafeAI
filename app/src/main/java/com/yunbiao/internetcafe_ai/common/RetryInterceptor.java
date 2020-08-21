package com.yunbiao.internetcafe_ai.common;

import com.elvishew.xlog.XLog;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RetryInterceptor implements Interceptor {
    private int maxRetry = 3;
    private int retryNum = 0;

    public RetryInterceptor() {
    }

    public RetryInterceptor(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response proceed = chain.proceed(request);
        if (!proceed.isSuccessful() && retryNum < maxRetry) {
            retryNum ++;
            XLog.d("retry number: " + retryNum);
            proceed = chain.proceed(request);
        }
        return proceed;
    }
}
