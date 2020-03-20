package com.org.biquge.jsoup.novel.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.githang.statusbar.StatusBarCompat;
import com.org.biquge.jsoup.MyPreference;
import com.org.biquge.jsoup.R;
import com.org.biquge.jsoup.novel.NovelPublic;
import com.org.biquge.jsoup.novel.events.RefreshTheme;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.org.biquge.jsoup.MyPreference.themeNum;

public class DonateActivity extends AppCompatActivity {

    @BindView(R.id.rl_top)
    RelativeLayout rlTop;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_donate)
    ImageView ivDonate;
    private MyPreference myPreference;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        ButterKnife.bind(this);
        context = this;
        EventBus.getDefault().register(this);
        myPreference = MyPreference.getInstance();
        myPreference.setPreference(this);
        refreshTheme(new RefreshTheme());
        initDonateIv();
    }

    private void initDonateIv() {
        Glide.with(context)
                .load("https://raw.githubusercontent.com/LqTk/mynovel/master/app/src/main/res/drawable/donate.png")
                .apply(new RequestOptions()
                        .centerInside()
                        .error(R.drawable.nobookpic)
                        .diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(ivDonate);
    }


    @Subscribe
    public void refreshTheme(RefreshTheme theme) {
        int themeId = myPreference.getInt(themeNum, 0);
        if (themeId == 0) {
            themeId = R.color.theme_blue;
            myPreference.setInt(themeNum, themeId);
        }
        rlTop.setBackgroundColor(getResources().getColor(themeId));
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(themeId));
    }

}
