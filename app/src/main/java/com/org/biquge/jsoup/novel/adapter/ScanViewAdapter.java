package com.org.biquge.jsoup.novel.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.org.biquge.jsoup.R;
import com.org.biquge.jsoup.novel.broadcastReceiver.BattaryBroadcast;

public class ScanViewAdapter extends PageAdapter{
    Context context;
    List<String> items;
    AssetManager am;
    ChapterClicker chapterClicker;
    String title="";
    private BattaryBroadcast battaryBroadcast;

    public void setChapterClicker(ChapterClicker clicker){
        this.chapterClicker = clicker;
    }

    public ScanViewAdapter(Context context, List<String> items, String chapterName)
    {
        this.context = context;
        this.items = items;
        this.title = chapterName;
        am = context.getAssets();
    }

    public void addContent(View view, int position)
    {
        TextView content = (TextView) view.findViewById(R.id.content);
        TextView tv = (TextView) view.findViewById(R.id.index);
        if ((position - 1) < 0 || (position - 1) >= getCount())
            return;
        content.setText(items.get(position - 1));
        tv.setText("第"+position+"/"+items.size()+"页");
    }

    public int getCount()
    {
        return items.size();
    }

    public View getView()
    {
        View view = LayoutInflater.from(context).inflate(R.layout.pagelayout,
                null);
        TextView lastchapter = view.findViewById(R.id.tv_last_chapter);
        TextView nextchapter = view.findViewById(R.id.tv_next_chapter);
        TextView tv_title = view.findViewById(R.id.tv_chapter_name);
        TextView tv_now_battary = view.findViewById(R.id.tv_now_battary);
        ImageView iv_battary = view.findViewById(R.id.iv_battary);
        tv_title.setText(title);
        lastchapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chapterClicker!=null){
                    chapterClicker.lastChapterListener();
                }
            }
        });
        nextchapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chapterClicker!=null){
                    chapterClicker.nextChapterListener();
                }
            }
        });

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        battaryBroadcast = new BattaryBroadcast(tv_now_battary,iv_battary);
        context.registerReceiver(battaryBroadcast,filter);

        return view;
    }

    public void unRegister() {
        context.unregisterReceiver(battaryBroadcast);
    }

    public interface ChapterClicker {
        void lastChapterListener();
        void nextChapterListener();
    }

}
