package com.org.biquge.jsoup.novel.popwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.org.biquge.jsoup.R;

public class FullScreenPop extends PopupWindow {
    Context mContext;
    LayoutInflater inflater;
    View popView;
    FullPopClick fullPopClick;

    public FullScreenPop(Context context) {
        super(context);
        this.mContext = mContext;
        this.inflater = LayoutInflater.from(context);

        init();
    }

    public void setFullPopClick(FullPopClick click){
        this.fullPopClick = click;
    }

    private void init() {
        popView = inflater.inflate(R.layout.full_screen_pop_layout,null);
        setContentView(popView);
        setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        setHeight(RelativeLayout.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0x30000000);
        setBackgroundDrawable(dw);

        initView();
    }

    private void initView(){
        RelativeLayout rl_full_pop = popView.findViewById(R.id.rl_full_pop);
        LinearLayout ll_books_chapters = popView.findViewById(R.id.ll_books_chapters);

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
    }
}
