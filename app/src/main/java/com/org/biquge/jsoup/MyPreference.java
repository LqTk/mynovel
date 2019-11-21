package com.org.biquge.jsoup;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;

import java.util.List;

public class MyPreference {
    public static String saveInfo = "bookInfos";
    private static String fileName = "novelInfo";
    public static String scanViewBgId = "scanViewBgId";
    public static String scanViewOrientation = "scanViewOrientation";
    public static String scanViewScrollPo = "scanViewScrollPo";
    public static String refreshLastTime = "refreshLastTime";

    SharedPreferences preference;
    Context mContext;
    SharedPreferences.Editor editor;
    private static final MyPreference ourInstance = new MyPreference();

    public static MyPreference getInstance() {
        return ourInstance;
    }

    private MyPreference() {
    }

    public void setPreference(Context context){
        this.mContext = context;
        if (preference==null){
            preference = mContext.getSharedPreferences(fileName,Context.MODE_PRIVATE);
        }
        editor = preference.edit();
    }

    public <T> void setObject(String key,T obj){
        String str = JSON.toJSONString(obj);
        setString(key,str);
    }

    public <T> T getObject(String key,Class<T> tClass){
        T obj = JSON.parseObject(getString(key,""),tClass);
        return obj;
    }

    public <T> List<T> getListObject(String key, Class<T> tClass){
        List<T> infos = JSON.parseArray(getString(key, ""), tClass);
        return infos;
    }

    public void setString(String key, String value) {
        // TODO Auto-generated method stub
        editor.putString(key, value);
        editor.commit();
    }

    public void setInt(String key, int value) {
        // TODO Auto-generated method stub
        editor.putInt(key, value);
        editor.commit();
    }

    public void setFloat(String key, float value) {
        // TODO Auto-generated method stub
        editor.putFloat(key, value);
        editor.commit();
    }

    public String getString(String key, String defaultValue) {
        // TODO Auto-generated method stub
        return preference.getString(key, defaultValue);
    }

    public int getInt(String key, int defalutValue){
        return preference.getInt(key,defalutValue);
    }

    public float getFloat(String key, float defalutValue){
        return preference.getFloat(key,defalutValue);
    }

}
