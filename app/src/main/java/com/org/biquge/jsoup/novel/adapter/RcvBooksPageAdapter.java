package com.org.biquge.jsoup.novel.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.org.biquge.jsoup.R;
import com.org.biquge.jsoup.novel.entities.BooksPageEntity;

import java.util.List;

public class RcvBooksPageAdapter extends BaseQuickAdapter<BooksPageEntity, BaseViewHolder> {
    public RcvBooksPageAdapter(int layoutResId, @Nullable List<BooksPageEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BooksPageEntity item) {
        helper.setText(R.id.tv_name,item.getName());
        View view = helper.getView(R.id.v_bottom);
        if (item.isIscheck()){
            view.setVisibility(View.VISIBLE);
        }else {
            view.setVisibility(View.GONE);
        }
    }
}
