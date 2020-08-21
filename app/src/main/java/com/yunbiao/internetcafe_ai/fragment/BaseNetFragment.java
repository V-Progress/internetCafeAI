package com.yunbiao.internetcafe_ai.fragment;

import com.elvishew.xlog.XLog;
import com.yunbiao.internetcafe_ai.R;
import com.yunbiao.internetcafe_ai.utils.DialogUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import okhttp3.Call;
import okhttp3.Request;

public abstract class BaseNetFragment<T> extends BaseFragment{
    protected void requestTest(String url,Map<String,String> params,final boolean isSuccess){
        requestTest(url, params, isSuccess,true);
    }
    protected void requestTest(String url, Map<String,String> params, final boolean isSuccess,boolean showProgress){
        requestTest(url, params, isSuccess, showProgress,2);
    }
    protected void requestTest(String url, Map<String,String> params, final boolean isSuccess,boolean showProgress,int delay){
        final StringCallback stringCallback = new MyStringCallback(url, getString(R.string.scan_query_ing),showProgress);
        stringCallback.onBefore(null,-1);
        Observable.timer(delay,TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Long value) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                if(isSuccess){
                    stringCallback.onResponse("",-1);
                } else {
                    stringCallback.onError(null,new Exception("123"),-1);
                }
                stringCallback.onAfter(-1);
            }
        });
    }

    protected void request(String url, Map<String,String> params){
        request(url,params,getString(R.string.scan_query_ing));
    }

    protected void request(String url, Map<String,String> params, String message){
        request(url, params, message,true);
    }

    protected void request(final String url, final Map<String,String> params, final String message, final boolean showProgress){
        StringBuffer paramsLog = new StringBuffer();
        paramsLog.append("{");
        for (Map.Entry<String, String> stringStringEntry : params.entrySet()) {
            paramsLog.append(stringStringEntry.getKey()).append(":").append(stringStringEntry.getValue()).append(",");
        }
        paramsLog.append("}");

        XLog.d("地址：" + url + "\n" + paramsLog.toString());
        OkHttpUtils.post().url(url).params(params).build().execute(new MyStringCallback(url,message,showProgress));
    }

    private class MyStringCallback extends StringCallback{
        private String message;
        private String mUrl;
        private boolean showProgress;

        public MyStringCallback(String url, String message,boolean showProgress) {
            this.mUrl = url;
            this.message = message;
            this.showProgress = showProgress;
        }

        @Override
        public void onBefore(Request request, int id) {
            if(showProgress){
                DialogUtil.instance().showProgress(getActivity(),message);
            }
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            BaseNetFragment.this.onError(mUrl,e);
        }

        @Override
        public void onResponse(String response, int id) {
            BaseNetFragment.this.onResponse(mUrl,(T) response);
        }

        @Override
        public void onAfter(int id) {
            DialogUtil.instance().dismissProgress();
        }
    }

    /***
     * 该方法必然在主线程执行
     * @param mUrl
     * @param e
     */
    protected abstract void onError(String mUrl, Exception e);

    /***
     * 该方法必然在主线程执行
     * @param mUrl
     * @param t
     */
    protected abstract void onResponse(String mUrl, T t);
}
