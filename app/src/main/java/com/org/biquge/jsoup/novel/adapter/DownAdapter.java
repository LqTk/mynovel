package com.org.biquge.jsoup.novel.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.org.biquge.jsoup.R;
import com.org.biquge.jsoup.novel.entities.DownLoadEntity;
import com.org.biquge.jsoup.novel.events.DownEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;

public class DownAdapter extends RecyclerView.Adapter<DownAdapter.MyHolder>{

    Context context;
    List<HashMap> myBooksLists;

    public DownAdapter(Context context, List<HashMap> myBooksLists) {
        this.context = context;
        this.myBooksLists = myBooksLists;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewHolder = LayoutInflater.from(context).inflate(R.layout.down_item_layout,null);
        return new MyHolder(viewHolder);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        HashMap item = myBooksLists.get(position);
        String downLoadInfo = (String) item.get("downLoadInfo");
        DownLoadEntity loadEntity = JSON.parseObject(downLoadInfo,DownLoadEntity.class);
        holder.tv_name.setText((CharSequence) item.get("title"));
        holder.tv_pro.setText(loadEntity.getLoadedPage()+"/"+loadEntity.getAllPage());
        holder.pb_statue.setMax(loadEntity.getAllPage());
        holder.pb_statue.setProgress(loadEntity.getLoadedPage());
        if (loadEntity.getLoadingStatu()==1){
            holder.tv_show_loading.setText("正在下载...");
            holder.iv.setImageResource(R.drawable.download_stop);
        }else if (loadEntity.getLoadingStatu()==0){
            holder.tv_show_loading.setText("已暂停");
            holder.iv.setImageResource(R.drawable.download_start);
        }else {
            holder.tv_show_loading.setText("下载完成");
            holder.iv.setImageResource(R.drawable.download_start);
        }
        final int pos = position;
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new DownEvent(pos));
            }
        });
    }

    @Override
    public int getItemCount() {
        return myBooksLists.size();
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
            return;
        }
        HashMap item = myBooksLists.get(position);
        String downLoadInfo = (String) item.get("downLoadInfo");
        DownLoadEntity loadEntity = JSON.parseObject(downLoadInfo,DownLoadEntity.class);
        for (Object payload:payloads) {
            switch (String.valueOf(payload)) {
                case "pro":
                    holder.tv_pro.setText(loadEntity.getLoadedPage()+"/"+loadEntity.getAllPage());
                    holder.pb_statue.setProgress(loadEntity.getLoadedPage());
                    break;
                case "iv":
                    if (loadEntity.getLoadingStatu()==1){
                        holder.tv_show_loading.setText("正在下载...");
                        holder.iv.setImageResource(R.drawable.download_stop);
                    }else if (loadEntity.getLoadingStatu()==0){
                        holder.tv_show_loading.setText("已暂停");
                        holder.iv.setImageResource(R.drawable.download_start);
                    }else {
                        holder.tv_show_loading.setText("下载完成");
                        holder.iv.setImageResource(R.drawable.download_start);
                    }
                    break;
            }
        }
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView iv;
        TextView tv_name;
        TextView tv_pro;
        TextView tv_show_loading;
        ProgressBar pb_statue;

        public MyHolder(View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv_st);
            tv_name = itemView.findViewById(R.id.tv_book_name);
            tv_pro = itemView.findViewById(R.id.tv_chapter_loading);
            tv_show_loading = itemView.findViewById(R.id.tv_show_loading);
            pb_statue = itemView.findViewById(R.id.pb_statue);
        }
    }
}
/*
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
        notifyItemChanged(position,"pro");
    }


}

*/