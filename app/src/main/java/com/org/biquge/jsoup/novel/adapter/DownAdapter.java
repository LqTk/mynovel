package com.org.biquge.jsoup.novel.adapter;

import android.support.annotation.Nullable;
import android.widget.ProgressBar;

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
        ProgressBar progressBar = helper.getView(R.id.pb_statue);
        progressBar.setMax(loadEntity.getAllPage());
        progressBar.setProgress(loadEntity.getLoadedPage());
        if (loadEntity.getLoadingStatu()==1){
            helper.setText(R.id.tv_show_loading,"正在下载...");
            helper.setImageResource(R.id.iv_st,R.drawable.download_stop);
        }else if (loadEntity.getLoadingStatu()==0){
            helper.setText(R.id.tv_show_loading,"已暂停");
            helper.setImageResource(R.id.iv_st,R.drawable.download_start);
        }else {
            helper.setText(R.id.tv_show_loading,"下载完成");
            helper.setImageResource(R.id.iv_st,R.drawable.download_start);
        }
        helper.addOnClickListener(R.id.iv_st);
//        helper.setProgress(R.id.pb_statue,loadEntity.getLoadedPage()/loadEntity.getAllPage());
//        helper.setText()
    }


    public void updataLoading(int position, DownLoadEntity loadEntity){
        if (position>=mData.size())
            return;
        mData.get(position).put("downLoadInfo",JSON.toJSONString(loadEntity));
        notifyDataSetChanged();
    }
}
