package com.org.biquge.jsoup.novel.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.githang.statusbar.StatusBarCompat;
import com.org.biquge.jsoup.MyPreference;
import com.org.biquge.jsoup.R;
import com.org.biquge.jsoup.novel.adapter.ThemeAdapter;
import com.org.biquge.jsoup.novel.entities.ThemeBgEntity;
import com.org.biquge.jsoup.novel.events.RefreshTheme;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.org.biquge.jsoup.MyPreference.themeNum;

public class ThemeActivity extends AppCompatActivity {

    @BindView(R.id.rl_top)
    RelativeLayout rlTop;
    @BindView(R.id.gv_theme)
    GridView gvTheme;

    MyPreference myPreference;
    int[] themes = {R.color.theme_brown,R.color.theme_blue,R.color.theme_red,R.color.theme_black,
            R.color.theme_orange,R.color.theme_green,R.color.theme_purple,R.color.theme_pink,
    R.color.theme_cyan,R.color.theme_yellow};
    String[] names = {"棕","蓝","红","黑","橙","绿","紫","粉","青","黄"};
    List<ThemeBgEntity> themesList = new ArrayList<>();
    private Context context;
    private int configThemeNum = 0;
    private ThemeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);
        ButterKnife.bind(this);
        context = this;
        myPreference = MyPreference.getInstance();
        myPreference.setPreference(context);
        refreshTheme();
        configThemeNum = myPreference.getInt(themeNum,0);

        initData();
        setAdapter();
    }

    private void initData() {
        for (int i=0;i<themes.length;i++){
            ThemeBgEntity themeBgEntity = new ThemeBgEntity(themes[i],false,names[i]);
            if (themes[i]==configThemeNum || configThemeNum==0){
                themeBgEntity.setCheck(true);
            }
            themesList.add(themeBgEntity);
        }
    }

    private void setAdapter() {
        adapter = new ThemeAdapter(themesList, context);
        gvTheme.setAdapter(adapter);

        gvTheme.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clearAllCheck();
                themesList.get(position).setCheck(true);
                adapter.notifyDataSetChanged();
                configThemeNum = position;
                myPreference.setInt(themeNum,themesList.get(position).getId());
                refreshTheme();
                EventBus.getDefault().post(new RefreshTheme());
            }
        });
    }

    private void clearAllCheck(){
        for (int i=0;i<themesList.size();i++){
            themesList.get(i).setCheck(false);
        }
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }

    public void refreshTheme(){
        int themeId = myPreference.getInt(themeNum,0);
        if (themeId==0) {
            rlTop.setBackgroundColor(getResources().getColor(R.color.blue_main));
            StatusBarCompat.setStatusBarColor(ThemeActivity.this, getResources().getColor(R.color.blue_main));
        }else {
            rlTop.setBackgroundColor(getResources().getColor(themeId));
            StatusBarCompat.setStatusBarColor(ThemeActivity.this, getResources().getColor(themeId));
        }
    }
}
