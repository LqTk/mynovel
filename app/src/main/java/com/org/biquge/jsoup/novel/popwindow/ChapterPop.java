package com.org.biquge.jsoup.novel.popwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChapterPop extends PopupWindow {
    List<HashMap> chaptersLists=new ArrayList<>();
    NovelItemAdapter novelItemAdapter;
    Context mContext;
    LayoutInflater inflater;
    View popView;
    ChaptersClick chaptersClick;
    RecyclerView rcv_chapter;
    TextView tv_name;
//    private NovelItemAdapter novelItemAdapter;

    public ChapterPop(Context context) {
        super(context);
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        novelItemAdapter = new NovelItemAdapter(R.layout.novel_item, chaptersLists);
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

        initView();
    }

    public void initView(){
        LinearLayout ll_chapters = popView.findViewById(R.id.ll_chapters);
        tv_name = popView.findViewById(R.id.tv_name);
        rcv_chapter = popView.findViewById(R.id.rcv_chapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcv_chapter.setLayoutManager(layoutManager);

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

    public void setData(List<HashMap> chapters,String name,String nowChapter){
        this.chaptersLists = chapters;
        tv_name.setText(name);
        novelItemAdapter.setNowChapter(nowChapter);
        novelItemAdapter.setNewData(chaptersLists);
        novelItemAdapter.notifyDataSetChanged();
        RecyclerView.LayoutManager layoutManager = rcv_chapter.getLayoutManager();
        layoutManager.scrollToPosition(getStartPos(nowChapter,chapters));
    }

    private int getStartPos(String nowChapter,List<HashMap> chaptersLists){
        int pos=0;
        for (int i=0;i<chaptersLists.size();i++){
            HashMap hashMap = chaptersLists.get(i);
            if(nowChapter.contains((CharSequence) hashMap.get("name"))){
                pos=i;
                break;
            }
        }
        return pos>7?pos-7:pos;
    }

    public interface ChaptersClick{
        void chapterItemClick(int position);
    }
}
