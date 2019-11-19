package com.org.biquge.jsoup.novel.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.org.biquge.jsoup.novel.NovelPublic;
import com.org.biquge.jsoup.novel.adapter.DownAdapter;
import com.org.biquge.jsoup.novel.entities.DownLoadEntity;

public class DownLoadingBroadcast extends BroadcastReceiver {
    DownAdapter mAdapter;

    public DownLoadingBroadcast(DownAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(NovelPublic.downLoadingUpdata)){
            int position = intent.getIntExtra("position",0);
            String loadEntity = intent.getStringExtra("loadEntity");
            DownLoadEntity downLoadEntity = JSON.parseObject(loadEntity,DownLoadEntity.class);
            if (mAdapter!=null)
                mAdapter.updataLoading(position,downLoadEntity);
        }
    }
}
