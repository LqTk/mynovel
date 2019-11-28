package com.org.biquge.jsoup.novel.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.org.biquge.jsoup.R;
import com.org.biquge.jsoup.novel.NovelPublic;

import java.util.HashMap;
import java.util.List;

public class GvHotAdapter extends BaseQuickAdapter<HashMap, BaseViewHolder> {

    public GvHotAdapter(int layoutResId, @Nullable List<HashMap> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HashMap item) {
       helper.setText(R.id.tv_hot_name,(String) item.get("name"));
        Glide.with(mContext)
                .load(item.get("imgUrl"))
                .apply(NovelPublic.errorOptions())
                .into((ImageView) helper.getView(R.id.iv_hot_img));
    }
}
