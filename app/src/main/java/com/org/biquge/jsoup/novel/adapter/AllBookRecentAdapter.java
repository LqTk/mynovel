package com.org.biquge.jsoup.novel.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.org.biquge.jsoup.R;

import java.util.HashMap;
import java.util.List;

public class AllBookRecentAdapter extends BaseQuickAdapter<HashMap, BaseViewHolder> {
    public AllBookRecentAdapter(int layoutResId, @Nullable List<HashMap> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HashMap item) {
        helper.setText(R.id.tv_book_name, (String) item.get("name"));
        helper.setText(R.id.tv_recent_chapter, (String) item.get("chapterName"));
        helper.setText(R.id.tv_author, (String) item.get("author"));
        helper.setText(R.id.tv_recent_time, (String) item.get("time"));
        helper.addOnClickListener(R.id.tv_book_name);
        helper.addOnClickListener(R.id.tv_recent_chapter);
    }
}
