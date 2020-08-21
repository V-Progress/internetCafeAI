package com.yunbiao.internetcafe_ai.cache;

import com.yunbiao.internetcafe_ai.APP;

public class CacheUtil {

    private static CacheUtil cacheUtil;
    private final SpUtils spUtils;

    public static CacheUtil instance(){
        if(cacheUtil == null){
            synchronized (CacheUtil.class){
                if(cacheUtil == null){
                    cacheUtil = new CacheUtil();
                }
            }
        }
        return cacheUtil;
    }

    private CacheUtil(){
        spUtils = new SpUtils(APP.getAppContext());
    }

    public int getInt(String key,int defaultValue){
        return spUtils.getInt(key,defaultValue);
    }

    public boolean getBoolean(String key,boolean defaultValue){
        return spUtils.getBoolean(key,defaultValue);
    }

}
