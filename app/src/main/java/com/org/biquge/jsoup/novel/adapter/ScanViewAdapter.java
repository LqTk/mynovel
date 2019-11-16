package com.org.biquge.jsoup.novel.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.org.biquge.jsoup.R;
import com.org.biquge.jsoup.novel.broadcastReceiver.BattaryBroadcast;

public class ScanViewAdapter extends PageAdapter{
    Context context;
    List<String> items;
    AssetManager am;
    ChapterClicker chapterClicker;
    private View scanView;
    private RelativeLayout rl_scanView;
    private int scanViewBg;

    public void setChapterClicker(ChapterClicker clicker){
        this.chapterClicker = clicker;
    }

    public ScanViewAdapter(Context context, List<String> items, int bgId)
    {
        this.scanViewBg = bgId;
        this.context = context;
        this.items = items;
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
            scanView = LayoutInflater.from(context).inflate(R.layout.pagelayout,
                    null);
            rl_scanView = scanView.findViewById(R.id.rl_scanView);
            rl_scanView.setBackground(context.getResources().getDrawable(scanViewBg));
            TextView lastchapter = scanView.findViewById(R.id.tv_last_chapter);
            TextView nextchapter = scanView.findViewById(R.id.tv_next_chapter);
            lastchapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (chapterClicker != null) {
                        chapterClicker.lastChapterListener();
                    }
                }
            });
            nextchapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (chapterClicker != null) {
                        chapterClicker.nextChapterListener();
                    }
                }
            });

        return scanView;
    }

    public void setScanViewBg(int bgId){
        this.scanViewBg = bgId;
    }

    public interface ChapterClicker {
        void lastChapterListener();
        void nextChapterListener();
    }

}
