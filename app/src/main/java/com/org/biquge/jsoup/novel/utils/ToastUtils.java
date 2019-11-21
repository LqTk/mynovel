package com.org.biquge.jsoup.novel.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    public static void showShortMsg(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
}
