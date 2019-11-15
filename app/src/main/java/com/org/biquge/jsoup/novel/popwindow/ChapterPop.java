package com.org.biquge.jsoup.novel.popwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.org.biquge.jsoup.R;
import com.org.biquge.jsoup.novel.adapter.NovelItemAdapter;

import java.util.HashMap;
import java.util.List;

public class ChapterPop extends PopupWindow {
    Context mContext;
    LayoutInflater inflater;
    View popView;
    ChaptersClick chaptersClick;
    private NovelItemAdapter novelItemAdapter;

    public ChapterPop(Context context) {
        super(context);
        this.mContext = mContext;
        this.inflater = LayoutInflater.from(context);

        init();
    }

    public void setChaptersClick(ChaptersClick click){
        this.chaptersClick = click;
    }

    private void init() {
        popView = inflater.inflate(R.layout.chapter_pop_layout,null);
        setContentView(popView);
        setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        setHeight(RelativeLayout.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0x30000000);
        setBackgroundDrawable(dw);

    }

    public void setData(List<HashMap> itemsList,String name){
        LinearLayout ll_chapters = popView.findViewById(R.id.ll_chapters);
        TextView tv_name = popView.findViewById(R.id.tv_name);
        RecyclerView rcv_chapter = popView.findViewById(R.id.rcv_chapter);

        tv_name.setText(name);
        novelItemAdapter = new NovelItemAdapter(R.layout.novel_item, itemsList);
        novelItemAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                chaptersClick.chapterItemClick(position);
                dismiss();
            }
        });
        rcv_chapter.setAdapter(novelItemAdapter);

        ll_chapters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public interface ChaptersClick{
        void chapterItemClick(int position);
    }
}
