package com.yunbiao.internetcafe_ai.cache;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by LiuShao on 2016/2/21.
 */
public class SpUtils {
    private static SharedPreferences sp;
    private static final String SP_NAME = "YB_FACE";

    public SpUtils(Context context) {
        sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public boolean saveStr(String key, String value) {
        if (sp != null) {
            return sp.edit().putString(key, value).commit();
        }
        return false;
    }

    public String getStr(String key, String defaultValue) {
        if (sp != null) {
            return sp.getString(key, defaultValue);
        }
        return defaultValue;
    }

    public void saveInt(String key, int value) {
        if (sp != null) {
            sp.edit().putInt(key, value).commit();
        }
    }

    public int getInt(String key, int def) {
        if (sp != null) {
            return sp.getInt(key, def);
        }
        return def;
    }

    public void saveFloat(String key, float value) {
        if (sp != null) {
            sp.edit().putFloat(key, value).commit();
        }
    }

    public Float getFloat(String key, float defaultValue) {
        if (sp != null) {
            return sp.getFloat(key, defaultValue);
        }
        return 0f;
    }

    public void saveLong(String key, long value) {
        if (sp != null) {
            sp.edit().putLong(key, value).commit();
        }
    }

    public long getLong(String key,long defaultValue){
        if(sp != null){
            return sp.getLong(key,defaultValue);
        }
        return defaultValue;
    }

    public boolean saveBoolean(String key, boolean b) {
        if (sp != null) {
            return sp.edit().putBoolean(key, b).commit();
        }
        return false;
    }

    public boolean getBoolean(String key, boolean defValue) {
        if (sp != null) {
            return sp.getBoolean(key, defValue);
        }
        return defValue;
    }

    public boolean remove(String key){
        if(sp != null){
            return sp.edit().remove(key).commit();
        }
        return false;
    }

    public boolean clear() {
        if (sp != null) {
            return sp.edit().clear().commit();
        }
        return false;
    }

}
