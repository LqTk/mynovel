package com.org.biquge.jsoup.novel.popwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.org.biquge.jsoup.R;
import com.org.biquge.jsoup.fastscroller.RecyclerFastScroller;
import com.org.biquge.jsoup.novel.adapter.ChaptersItemAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ChapterPop extends PopupWindow {
    List<HashMap> newChaptersLists=new ArrayList<>();
    ChaptersItemAdapter novelItemAdapter;
    Context mContext;
    LayoutInflater inflater;
    View popView;
    ChaptersClick chaptersClick;
    RecyclerView rcv_chapter;
    TextView tv_name;
    RecyclerFastScroller fastScroller;
    private boolean isDown = true;
    private String nowChapter;

    public ChapterPop(Context context) {
        super(context);
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        novelItemAdapter = new ChaptersItemAdapter(R.layout.chapter_item, newChaptersLists);
        init();
    }

    public void setChaptersClick(ChaptersClick click){
        this.chaptersClick = click;
    }

    private void init() {
        isDown = true;
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
        final ImageView iv_order = popView.findViewById(R.id.iv_order);
        fastScroller = popView.findViewById(R.id.fast_scroll);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcv_chapter.setLayoutManager(layoutManager);
        novelItemAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!isDown)
                    position = newChaptersLists.size()-position-1;
                chaptersClick.chapterItemClick(position);
                dismiss();
            }
        });
        rcv_chapter.setAdapter(novelItemAdapter);
        fastScroller.attachRecyclerView(rcv_chapter);

        fastScroller.setTouchTargetWidth(56);
        fastScroller.setBarColor(mContext.getResources().getColor(R.color.transparent));
        fastScroller.setHidingEnabled(false);
        fastScroller.touchIsDrawable(true);

        iv_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDown){
                    iv_order.setImageResource(R.drawable.order_up);
                }else {
                    iv_order.setImageResource(R.drawable.order_down);
                }
                isDown = !isDown;
                Collections.reverse(newChaptersLists);
                setData(newChaptersLists,tv_name.getText().toString(),nowChapter);
            }
        });
        ll_chapters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setData(List<HashMap> chapters,String name,String nowChapter){
        List<HashMap> tempList = new ArrayList<>();
        tempList.addAll(chapters);
        this.newChaptersLists.clear();
        this.newChaptersLists.addAll(tempList);
        tv_name.setText(name);
        this.nowChapter = nowChapter;
        int position = getStartPos(nowChapter,newChaptersLists);
        novelItemAdapter.setNowChapter(nowChapter);
        novelItemAdapter.notifyDataSetChanged();
        RecyclerView.LayoutManager layoutManager = rcv_chapter.getLayoutManager();
        layoutManager.scrollToPosition(position);
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
        if (isDown){
            pos = pos>6?pos-6:0;
        }else {
            pos = 0;
        }
        return pos;
    }


    public interface ChaptersClick{
        void chapterItemClick(int position);
    }
}
