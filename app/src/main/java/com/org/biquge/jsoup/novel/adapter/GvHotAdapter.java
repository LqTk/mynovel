package com.org.biquge.jsoup.novel.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.org.biquge.jsoup.R;

import java.util.HashMap;
import java.util.List;

public class GvHotAdapter extends BaseAdapter {
    Context context;
    List<HashMap> datas;

    public GvHotAdapter(Context context, List<HashMap> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.gv_hot,null);
            holder.hotImg = convertView.findViewById(R.id.gv_hot);
            holder.hotName = convertView.findViewById(R.id.tv_hot_name);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        HashMap hashMap = datas.get(position);
        holder.hotName.setText((String) hashMap.get("name"));
        Glide.with(context)
                .load(hashMap.get("url"))
                .into(holder.hotImg);
        return null;
    }

    class ViewHolder{
        ImageView hotImg;
        TextView hotName;
    }
}
