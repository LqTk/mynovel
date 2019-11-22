package com.org.biquge.jsoup.novel.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.org.biquge.jsoup.R;
import com.org.biquge.jsoup.novel.entities.ThemeBgEntity;

import java.util.List;

public class ThemeAdapter extends BaseAdapter {
    List<ThemeBgEntity> data;
    Context context;

    public ThemeAdapter(List<ThemeBgEntity> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.theme_item_layout,null);
            viewHolder.rlThemeBg = convertView.findViewById(R.id.rl_theme_bg);
            viewHolder.tvThemeName = convertView.findViewById(R.id.tv_theme_name);
            viewHolder.ivCheck = convertView.findViewById(R.id.iv_check);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ThemeBgEntity themeBgEntity = data.get(position);

        viewHolder.rlThemeBg.setBackgroundColor(context.getResources().getColor(themeBgEntity.getId()));
        viewHolder.tvThemeName.setText(themeBgEntity.getName());
        if (themeBgEntity.isCheck()){
            viewHolder.ivCheck.setVisibility(View.VISIBLE);
        }else {
            viewHolder.ivCheck.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder{
        RelativeLayout rlThemeBg;
        TextView tvThemeName;
        ImageView ivCheck;
    }
}
