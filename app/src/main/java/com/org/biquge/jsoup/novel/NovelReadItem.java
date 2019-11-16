package com.org.biquge.jsoup.novel;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.org.biquge.jsoup.JsoupGet;
import com.org.biquge.jsoup.MyPreference;
import com.org.biquge.jsoup.R;
import com.org.biquge.jsoup.novel.adapter.NovelItemAdapter;
import com.org.biquge.jsoup.novel.adapter.ScanViewAdapter;
import com.org.biquge.jsoup.novel.adapter.ThemeAdapter;
import com.org.biquge.jsoup.novel.broadcastReceiver.BattaryBroadcast;
import com.org.biquge.jsoup.novel.events.RefreshMyBooks;
import com.org.biquge.jsoup.novel.popwindow.ChapterPop;
import com.org.biquge.jsoup.novel.popwindow.FullScreenPop;
import com.org.biquge.jsoup.novel.view.ScanView;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.org.biquge.jsoup.MyPreference.saveInfo;
import static com.org.biquge.jsoup.MyPreference.scanViewBgId;
import static com.org.biquge.jsoup.novel.NovelUrl.novelHomeUrl;

public class NovelReadItem extends AppCompatActivity {

    @BindView(R.id.scanView)
    ScanView scanView;
    @BindView(R.id.ll_main)
    RelativeLayout llMain;
    @BindView(R.id.rl_top)
    RelativeLayout rlTop;
    @BindView(R.id.tv_chapter_name)
    TextView tvChapterName;
    @BindView(R.id.tv_now_battary)
    TextView tvNowBattary;
    @BindView(R.id.iv_battary)
    ImageView ivBattary;
    @BindView(R.id.ll_pro)
    LinearLayout llPro;
    /*@BindView(R.id.tv_content)
        TextView tvContent;*/
    private Context context;
    private Unbinder bind;
    private JsoupGet jsoupGet = new JsoupGet();
    private int pageReadCount = 300;
    ScanViewAdapter scanViewAdapter;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (readItem == null)
                return;
            tvChapterName.setText((String) readItem.get("name"));
            String content = (String) readItem.get("content");
            final List<String> cons = new ArrayList<>();
            int i = 0;
            for (i = 0; i < content.length() / pageReadCount; i++) {
                cons.add(content.substring(i * pageReadCount, (i + 1) * pageReadCount));
            }
            cons.add(content.substring(i * pageReadCount, content.length()));
            if (!first) {
                if (pageStatus.equals("next")) {
                    currentIndex = 1;
                    authorMap.put("lastPage", currentIndex);
                } else {
                    currentIndex = cons.size();
                    authorMap.put("lastPage", currentIndex);
                }
            }
            if (first) {
                first = false;
                currentIndex = (int) authorMap.get("lastPage");
            }
            scanViewAdapter = new ScanViewAdapter(context, cons, (Integer) scanViewBgSetting.get("bgId"));
            scanViewAdapter.setChapterClicker(new ScanViewAdapter.ChapterClicker() {
                @Override
                public void lastChapterListener() {
                    String lastUrl = (String) readItem.get("lastChapter");
                    String allUrl = (String) readItem.get("allChapter");
                    if (lastUrl.equals(allUrl))
                        return;
                    pageStatus = "next";
                    getData(lastUrl);
                }

                @Override
                public void nextChapterListener() {
                    pageStatus = "next";
                    getData((String) readItem.get("nextChapter"));
                }
            });
            if (scanViewAdapter!=null) {
                scanView.setAdapter(scanViewAdapter, currentIndex);
                scanView.setPageListener(pageListener);
                scanView.setScreenClick(screenClick);
                scanView.setSavePageListener(savePageListener);
            }
            llPro.setVisibility(View.GONE);
//            tvContent.setText((CharSequence) readItem.get("content"));
        }
    };
    private HashMap readItem;
    private HashMap authorMap = new HashMap();
    private int currentIndex = 1;
    private String pageStatus = "next";
    private boolean first = false;
    private MyPreference myPreference;
    private int mapPosition = -1;
    private List<HashMap> myBooksLists;
    private List<HashMap> chaptersLists;
    private boolean isAdded = false;

    List<ThemeBgEntity> themeBgEntities = new ArrayList<>();

    private ScanView.OnPageListener pageListener = new ScanView.OnPageListener() {
        @Override
        public void lastChapter() {
            String lastUrl = (String) readItem.get("lastChapter");
            String allUrl = (String) readItem.get("allChapter");
            if (lastUrl.equals(allUrl))
                return;
            pageStatus = "last";
            getData(lastUrl);
        }

        @Override
        public void nextChapter() {
            pageStatus = "next";
            getData((String) readItem.get("nextChapter"));
        }
    };
    private ScanView.SavePageListener savePageListener = new ScanView.SavePageListener() {
        @Override
        public void saveNowPage(int page) {
            authorMap.put("lastPage", page);
        }
    };
    private ScanView.ScreenClick screenClick = new ScanView.ScreenClick() {
        @Override
        public void onClick() {
            FullScreenPop fullScreenPop = new FullScreenPop(context);
            fullScreenPop.refreshData(themeBgEntities);
            fullScreenPop.setFullPopClick(fullPopClick);
            fullScreenPop.setFocusable(true);
            fullScreenPop.setOutsideTouchable(false);
            fullScreenPop.showAtLocation(llMain, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    };

    private FullScreenPop.FullPopClick fullPopClick = new FullScreenPop.FullPopClick() {
        @Override
        public void chaptersClick() {
            ChapterPop chapterPop = new ChapterPop(context);
            chapterPop.setData(chaptersLists,bookName,tvChapterName.getText().toString());
            chapterPop.setChaptersClick(chaptersClick);
            chapterPop.setFocusable(true);
            chapterPop.setOutsideTouchable(false);
            chapterPop.showAtLocation(llMain, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }

        @Override
        public void themeClick(int position) {
            if (scanViewAdapter!=null)
                scanViewAdapter.setScanViewBg(themeBgEntities.get(position).getBgId());
            llMain.setBackground(getResources().getDrawable(themeBgEntities.get(position).getBgId()));
            scanView.resetBg(themeBgEntities.get(position).getBgId());
            setSetting(themeBgEntities.get(position).getBgId(),position);
        }
    };

    private ChapterPop.ChaptersClick chaptersClick = new ChapterPop.ChaptersClick() {
        @Override
        public void chapterItemClick(int position) {
            HashMap clickHashMap = chaptersLists.get(position);
            Toast.makeText(context, (CharSequence) clickHashMap.get("name"), Toast.LENGTH_SHORT).show();
            pageStatus = "next";
            getData(novelHomeUrl+(String) clickHashMap.get("href"));
        }
    };
    private String bookName="";
    private BattaryBroadcast battaryBroadcast;
    private HashMap scanViewBgSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_novel_read_item);
        bind = ButterKnife.bind(this);
        context = this;
        myPreference = MyPreference.getInstance();
        myPreference.setPreference(context);
        myBooksLists = myPreference.getListObject(saveInfo, HashMap.class);
        scanViewBgSetting = myPreference.getObject(scanViewBgId,HashMap.class);
        if (scanViewBgSetting==null){
            scanViewBgSetting = new HashMap();
            setSetting(R.drawable.read_cover3,0);
        }

        initThemeData();
        getData(initData());
    }

    private void setSetting(int bgId,int which){
        scanViewBgSetting.put("bgId",bgId);
        scanViewBgSetting.put("which",which);
        myPreference.setObject(scanViewBgId,scanViewBgSetting);
    }

    private void initThemeData() {
        int which = (int) scanViewBgSetting.get("which");
        themeBgEntities.add(new ThemeBgEntity(true,R.drawable.read_cover3));
        themeBgEntities.add(new ThemeBgEntity(false,R.drawable.read_cover2));
        themeBgEntities.add(new ThemeBgEntity(false,R.drawable.read_cover));
        for (int i=0;i<themeBgEntities.size();i++){
            themeBgEntities.get(i).setChecked(false);
            if (which == i){
                themeBgEntities.get(i).setChecked(true);
            }
        }

        llMain.setBackground(getResources().getDrawable((Integer) scanViewBgSetting.get("bgId")));
    }

    private String initData() {
        String href = "";
        Bundle bundle = getIntent().getExtras();
        if (bundle.getString("from").equals("noveitem")) {
            href = bundle.getString("url");
            bookName = bundle.getString("bookName");
            chaptersLists = JSON.parseArray(bundle.getString("chapters"), HashMap.class);
            isAdded = bundle.getBoolean("isAdded");
            if (isAdded) {
                for (int i = 0; i < myBooksLists.size(); i++) {
                    HashMap cMap = myBooksLists.get(i);
                    if (cMap.get("title").equals(bundle.getString("title"))
                            && cMap.get("author").equals(bundle.getString("author"))) {
                        mapPosition = i;
                        authorMap = myBooksLists.get(mapPosition);
                        /*href = (String) authorMap.get("chapter");
                        first = true;*/
                        break;
                    }
                }
            }
        } else {
            isAdded = true;
            mapPosition = bundle.getInt("itemPosition");
            if (myBooksLists != null && myBooksLists.size() > 0) {
                authorMap = myBooksLists.get(mapPosition);
                href = (String) authorMap.get("chapter");
                chaptersLists = JSON.parseArray((String) authorMap.get("chapters"),HashMap.class);
                bookName = (String) authorMap.get("title");
                first = true;
            }
        }

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        battaryBroadcast = new BattaryBroadcast(tvNowBattary, ivBattary);
        registerReceiver(battaryBroadcast, filter);

        return href;
    }

    private void getData(final String href) {
        authorMap.put("chapter", href);
        llPro.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    readItem = jsoupGet.getReadItem(href);
                    handler.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onSaveBooks();
        bind.unbind();
        unregisterReceiver(battaryBroadcast);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void onSaveBooks() {
        if (isAdded) {
            myBooksLists.remove(mapPosition);
            myBooksLists.add(mapPosition, authorMap);
            myPreference.setObject(saveInfo, myBooksLists);
        }
        EventBus.getDefault().post(new RefreshMyBooks());
    }

    @Override
    protected void onStop() {
        super.onStop();
        onSaveBooks();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (llPro.getVisibility()== View.GONE)
            scanView.keyDown(keyCode);
            return true;
        } else {
            onSaveBooks();
            return super.onKeyDown(keyCode, event);
        }
    }

    @OnClick(R.id.ll_pro)
    public void onClickViewed(View v){
        switch (v.getId()){
            case R.id.ll_pro:
                break;
        }
    }

}
