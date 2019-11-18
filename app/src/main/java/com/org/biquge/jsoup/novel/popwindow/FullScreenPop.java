package com.org.biquge.jsoup.novel.popwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.org.biquge.jsoup.R;
import com.org.biquge.jsoup.novel.entities.ThemeBgEntity;
import com.org.biquge.jsoup.novel.adapter.ThemeAdapter;

import java.util.ArrayList;
import java.util.List;

public class FullScreenPop extends PopupWindow {
    Context mContext;
    LayoutInflater inflater;
    View popView;
    FullPopClick fullPopClick;
    List<ThemeBgEntity> themeData = new ArrayList<>();
    ThemeAdapter themeAdapter;
    private RecyclerView rcv_theme;

    public FullScreenPop(Context context) {
        super(context);
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);

        themeAdapter = new ThemeAdapter(R.layout.theme_menu_layout,themeData);

        init();
    }

    public void setFullPopClick(FullPopClick click){
        this.fullPopClick = click;
    }

    private void init() {
        popView = inflater.inflate(R.layout.full_screen_pop_layout,null);
        setContentView(popView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0x30000000);
        setBackgroundDrawable(dw);

        initView();
    }

    public void refreshData(List<ThemeBgEntity> themeBgEntities){
        themeData = themeBgEntities;
        themeAdapter.setNewData(themeBgEntities);
        themeAdapter.notifyDataSetChanged();
    }

    public void initView(){
        final RelativeLayout rl_full_pop = popView.findViewById(R.id.rl_full_pop);
        LinearLayout ll_books_chapters = popView.findViewById(R.id.ll_books_chapters);
        ImageView iv_theme_menu = popView.findViewById(R.id.iv_theme_menu);
        rcv_theme = popView.findViewById(R.id.rcv_theme);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcv_theme.setLayoutManager(layoutManager);

        rcv_theme.setAdapter(themeAdapter);
        iv_theme_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rcv_theme.setVisibility(View.VISIBLE);
            }
        });
        themeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                fullPopClick.themeClick(position);
                for (ThemeBgEntity bgEntity:themeData){
                    bgEntity.setChecked(false);
                }
                themeData.get(position).setChecked(true);
                themeAdapter.setNewData(themeData);
                themeAdapter.notifyDataSetChanged();
                dismiss();
            }
        });
        ll_books_chapters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fullPopClick!=null){
                    fullPopClick.chaptersClick();
                }
                dismiss();
            }
        });

        rl_full_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public interface FullPopClick{
        void chaptersClick();
        void themeClick(int position);
    }
}
