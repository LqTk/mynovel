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
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.org.biquge.jsoup.R;
import com.org.biquge.jsoup.novel.entities.ScanThemeBgEntity;
import com.org.biquge.jsoup.novel.adapter.ScanThemeAdapter;

import java.util.ArrayList;
import java.util.List;

public class FullScreenPop extends PopupWindow {
    Context mContext;
    LayoutInflater inflater;
    View popView;
    FullPopClick fullPopClick;
    List<ScanThemeBgEntity> themeData = new ArrayList<>();
    ScanThemeAdapter themeAdapter;
    private RecyclerView rcv_theme;
    private ImageView iv_scroll;
    private TextView tv_scroll;

    public FullScreenPop(Context context) {
        super(context);
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);

        themeAdapter = new ScanThemeAdapter(R.layout.theme_menu_layout,themeData);

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

    public void refreshData(List<ScanThemeBgEntity> themeBgEntities, int orientation){
        if (iv_scroll!=null){
            //orientation 1滑动 0横屏
            if (orientation==0){
                iv_scroll.setImageResource(R.drawable.scroll_up_down);
                tv_scroll.setText("滑动");
            }else {
                iv_scroll.setImageResource(R.drawable.scroll_left_right);
                tv_scroll.setText("翻页");
            }
        }
        themeData = themeBgEntities;
        themeAdapter.setNewData(themeBgEntities);
        themeAdapter.notifyDataSetChanged();
    }

    public void initView(){
        final RelativeLayout rl_full_pop = popView.findViewById(R.id.rl_full_pop);
        LinearLayout ll_books_chapters = popView.findViewById(R.id.ll_books_chapters);
        LinearLayout ll_save = popView.findViewById(R.id.ll_save);
        LinearLayout ll_scroll_orientation = popView.findViewById(R.id.ll_scroll_orientation);
        ImageView iv_theme_menu = popView.findViewById(R.id.iv_theme_menu);
        TextView tv_refresh = popView.findViewById(R.id.tv_refresh);
        rcv_theme = popView.findViewById(R.id.rcv_theme);
        iv_scroll = popView.findViewById(R.id.iv_scroll);
        tv_scroll = popView.findViewById(R.id.tv_scroll);

        ll_scroll_orientation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int orientation = 0;
                if (tv_scroll.getText().toString().equals("滑动")){
                    iv_scroll.setImageResource(R.drawable.scroll_left_right);
                    tv_scroll.setText("翻页");
                    orientation = 1;
                }else{
                    iv_scroll.setImageResource(R.drawable.scroll_up_down);
                    tv_scroll.setText("滑动");
                    orientation = 0;
                }
                fullPopClick.setOrientationClick(orientation);
            }
        });
        ll_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullPopClick.onSaveClick();
            }
        });
        tv_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullPopClick.refreshDataClick();
                dismiss();
            }
        });
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
                for (ScanThemeBgEntity bgEntity:themeData){
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
        void refreshDataClick();
        void onSaveClick();
        void setOrientationClick(int orientation);
    }
}
