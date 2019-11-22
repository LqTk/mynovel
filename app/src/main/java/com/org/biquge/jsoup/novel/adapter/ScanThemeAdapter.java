package com.org.biquge.jsoup.novel.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.org.biquge.jsoup.R;
import com.org.biquge.jsoup.novel.entities.ScanThemeBgEntity;

import java.util.List;

public class ScanThemeAdapter extends BaseQuickAdapter<ScanThemeBgEntity, BaseViewHolder> {
    public ScanThemeAdapter(int layoutResId, @Nullable List<ScanThemeBgEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ScanThemeBgEntity item) {
        if (item.isChecked()){
            helper.setBackgroundRes(R.id.rl_theme_bg,R.drawable.theme_checked_bg);
        }else {
            helper.setBackgroundRes(R.id.rl_theme_bg,R.drawable.theme_unchecked_bg);
        }
        helper.setImageResource(R.id.iv_theme_item,item.getBgId());
    }
}
