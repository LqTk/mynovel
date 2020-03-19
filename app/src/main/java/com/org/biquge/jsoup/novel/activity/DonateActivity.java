package com.org.biquge.jsoup.novel.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import com.githang.statusbar.StatusBarCompat;
import com.org.biquge.jsoup.MyPreference;
import com.org.biquge.jsoup.R;
import com.org.biquge.jsoup.novel.events.RefreshTheme;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.org.biquge.jsoup.MyPreference.themeNum;

public class DonateActivity extends AppCompatActivity {

    @BindView(R.id.rl_top)
    RelativeLayout rlTop;
    private MyPreference myPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        myPreference = MyPreference.getInstance();
        myPreference.setPreference(this);
        refreshTheme(new RefreshTheme());
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
