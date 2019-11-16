package com.org.biquge.jsoup.novel.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.org.biquge.jsoup.R;

public class BattaryBroadcast extends BroadcastReceiver {
    TextView battary;
    ImageView iv_battary;

    public BattaryBroadcast(TextView battaryView, ImageView imageView) {
        this.battary = battaryView;
        this.iv_battary = imageView;
    }

    public void setView(TextView battaryView, ImageView imageView){
        this.battary = battaryView;
        this.iv_battary = imageView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int current = intent.getExtras().getInt("level");// 获得当前电量
        int total = intent.getExtras().getInt("scale");// 获得总电量
        int status = intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
        int percent = current * 100 / total;
        if (status==2){
            if (percent == 100) {
                iv_battary.setImageResource(R.drawable.battery_100);
                battary.setText("已充满");
            }else {
                iv_battary.setImageResource(R.drawable.battery_charge);
                battary.setText("充电中...");
            }
        }else {
            battary.setText(percent + "%");
            if (percent == 100) {
                iv_battary.setImageResource(R.drawable.battery_100);
            } else if (percent > 85) {
                iv_battary.setImageResource(R.drawable.battery_85);
            } else if (percent > 71) {
                iv_battary.setImageResource(R.drawable.battery_71);
            } else if (percent > 57) {
                iv_battary.setImageResource(R.drawable.battery_57);
            } else if (percent > 43) {
                iv_battary.setImageResource(R.drawable.battery_43);
            } else if (percent > 28) {
                iv_battary.setImageResource(R.drawable.battery_28);
            } else if (percent > 15) {
                iv_battary.setImageResource(R.drawable.battery_15);
            } else {
                iv_battary.setImageResource(R.drawable.battery_10);
            }
        }
    }
}
