package com.org.biquge.jsoup.novel.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.org.biquge.jsoup.R;

import java.util.HashMap;
import java.util.List;

public class MyBooksAdapter extends BaseQuickAdapter<HashMap, BaseViewHolder> {

    public MyBooksAdapter(int layoutResId, @Nullable List<HashMap> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HashMap item) {
        Glide.with(mContext)
                .load(item.get("img"))
                .into((ImageView) helper.getView(R.id.iv_item));
        ImageView ivNew = helper.getView(R.id.iv_hasnew);
        if ((boolean)item.get("hasNew")) {
            ivNew.setVisibility(View.VISIBLE);
        }else {
            ivNew.setVisibility(View.GONE);
        }
        helper.setText(R.id.tv_name, (String) item.get("title"));
        helper.setText(R.id.tv_author_name, (String) item.get("author"));
        helper.setText(R.id.tv_recent_time, (String) item.get("time"));
        helper.setText(R.id.tv_recent_content, (String) item.get("recentString"));
    }
}
