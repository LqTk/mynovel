package com.org.biquge.jsoup.novel.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.org.biquge.jsoup.R;

import java.util.HashMap;
import java.util.List;

public class NovelItemAdapter extends BaseQuickAdapter<HashMap, BaseViewHolder> {
    public NovelItemAdapter(int layoutResId, @Nullable List<HashMap> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HashMap item) {
//        Glide.with(mContext).load(item.get("img")).into((ImageView) helper.getView(R.id.iv_novel));
        helper.setText(R.id.tv_name, (CharSequence) item.get("name"));
    }
}
