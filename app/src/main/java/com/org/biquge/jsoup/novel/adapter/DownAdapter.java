package com.org.biquge.jsoup.novel.adapter;

import android.support.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.org.biquge.jsoup.R;
import com.org.biquge.jsoup.novel.entities.DownLoadEntity;

import java.util.HashMap;
import java.util.List;

public class DownAdapter extends BaseQuickAdapter<HashMap, BaseViewHolder> {
    List<HashMap> mData;

    public DownAdapter(int layoutResId, @Nullable List<HashMap> item) {
        super(layoutResId, item);
        this.mData = item;
    }

    @Override
    protected void convert(BaseViewHolder helper, HashMap item) {
        String downLoadInfo = (String) item.get("downLoadInfo");
        DownLoadEntity loadEntity = JSON.parseObject(downLoadInfo,DownLoadEntity.class);
        helper.setText(R.id.tv_book_name, (CharSequence) item.get("title"));
        helper.setText(R.id.tv_chapter_loading,loadEntity.getLoadedPage()+"/"+loadEntity.getAllPage());
        helper.setProgress(R.id.pb_statue,loadEntity.getLoadedPage()/loadEntity.getAllPage());
//        helper.setText()
    }


}
