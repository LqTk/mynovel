package com.org.biquge.jsoup.novel.popwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.org.biquge.jsoup.R;

public class SavePop extends PopupWindow {
    Context mContext;
    View popView;
    SavePopClick savePopClick;
    LayoutInflater layoutInflater;

    public SavePop(Context context) {
        super(context);
        this.mContext = context;
        this.layoutInflater = LayoutInflater.from(context);

        init();
    }

    public void setSavePopClick(SavePopClick click){
        this.savePopClick = click;
    }

    private void init() {
        popView = layoutInflater.inflate(R.layout.save_layout,null);

        setContentView(popView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        setBackgroundDrawable(dw);

        initView();
    }

    private void initView() {
        TextView tv_saveall = popView.findViewById(R.id.tv_saveall);
        TextView tv_savenow = popView.findViewById(R.id.tv_savenow);
        TextView tv_savecancel = popView.findViewById(R.id.tv_savecancel);
        RelativeLayout rl_save = popView.findViewById(R.id.rl_save);
        tv_saveall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePopClick.saveAll();
                dismiss();
            }
        });
        tv_savenow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePopClick.saveNow();
                dismiss();
            }
        });
        tv_savecancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePopClick.cancel();
                dismiss();
            }
        });
        rl_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public interface SavePopClick{
        void cancel();
        void saveAll();
        void saveNow();
    }
}
