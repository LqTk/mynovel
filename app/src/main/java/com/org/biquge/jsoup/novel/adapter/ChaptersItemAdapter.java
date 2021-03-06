package com.org.biquge.jsoup.novel.adapter;

import android.graphics.Color;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.org.biquge.jsoup.R;
import com.org.biquge.jsoup.novel.NovelPublic;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import static com.org.biquge.jsoup.novel.NovelPublic.novelSaveDirName;

public class ChaptersItemAdapter extends BaseQuickAdapter<HashMap, BaseViewHolder> {
    private String nowChapter;
    List<HashMap> datas;

    public ChaptersItemAdapter(int layoutResId, @Nullable List<HashMap> data) {
        super(layoutResId, data);
        this.datas = data;
    }

    @Override
    protected void convert(BaseViewHolder helper, HashMap item) {
//        Glide.with(mContext).load(item.get("img")).into((ImageView) helper.getView(R.id.iv_novel));
        Log.d("item index=",datas.indexOf(item)+"当前position");
        TextView name = helper.getView(R.id.tv_name);
        if (nowChapter.equals((String) item.get("name"))){
            name.setTextColor(mContext.getResources().getColor(R.color.blue_main));
        }else {
            name.setTextColor(Color.GRAY);
        }
        String nowChapter = (String)item.get("href");
        String[] split = nowChapter.split(NovelPublic.getHomeUrl(3))[1].split(".html");
        nowChapter = split[0]+".txt";
        String savePath = Environment.getExternalStorageDirectory()+novelSaveDirName+nowChapter;
        File saveFile = new File(savePath);
        if (saveFile.exists()){
            helper.setText(R.id.tv_saved,"已下载");
        }else {
            helper.setText(R.id.tv_saved,"");
        }
        name.setText((CharSequence)item.get("name"));
    }

    public void setNowChapter(String chapter){
        this.nowChapter = chapter;
    }

}
