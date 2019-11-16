package com.org.biquge.jsoup.novel.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.org.biquge.jsoup.R;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

public class NovelItemAdapter extends BaseQuickAdapter<HashMap, BaseViewHolder> {
    private String nowChapter="";

    public NovelItemAdapter(int layoutResId, @Nullable List<HashMap> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HashMap item) {
//        Glide.with(mContext).load(item.get("img")).into((ImageView) helper.getView(R.id.iv_novel));
        TextView name = helper.getView(R.id.tv_name);
        if (isNowChapter((String) item.get("name"))){
            name.setTextColor(mContext.getResources().getColor(R.color.blue_main));
        }else {
            name.setTextColor(Color.GRAY);
        }
        name.setText((CharSequence)item.get("name"));
    }

    public void setNowChapter(String chapter){
        this.nowChapter = chapter;
    }

    private boolean isNowChapter(String chapter){
        return nowChapter.contains(chapter);
    }

}
