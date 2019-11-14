package com.org.biquge.jsoup.novel;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.org.biquge.jsoup.JsoupGet;
import com.org.biquge.jsoup.MyPreference;
import com.org.biquge.jsoup.R;
import com.org.biquge.jsoup.novel.adapter.ScanViewAdapter;
import com.org.biquge.jsoup.novel.events.RefreshMyBooks;
import com.org.biquge.jsoup.novel.view.ScanView;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.org.biquge.jsoup.MyPreference.saveInfo;

public class NovelReadItem extends AppCompatActivity {

    @BindView(R.id.scanView)
    ScanView scanView;
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
            String content = (String) readItem.get("content");
            final List<String> cons = new ArrayList<>();
            int i=0;
            for (i = 0;i<content.length()/pageReadCount;i++){
                cons.add(content.substring(i*pageReadCount,(i+1)*pageReadCount));
            }
            cons.add(content.substring(i*pageReadCount,content.length()));
            if (pageStatus.equals("next")){
                currentIndex = 1;
            }else {
                currentIndex = cons.size();
            }
            if (first){
                first = false;
                currentIndex = (int) authorMap.get("lastPage");
            }
            scanViewAdapter = new ScanViewAdapter(context,cons, (String) readItem.get("name"));
            scanViewAdapter.setChapterClicker(new ScanViewAdapter.ChapterClicker() {
                @Override
                public void lastChapterListener() {
                    String lastUrl = (String) readItem.get("lastChapter");
                    String allUrl = (String) readItem.get("allChapter");
                    if (lastUrl.equals(allUrl))
                        return;
                    pageStatus = "next";
                    authorMap.put("chapter",lastUrl);
                    getData(lastUrl);
                }

                @Override
                public void nextChapterListener() {
                    pageStatus = "next";
                    authorMap.put("chapter",(String) readItem.get("nextChapter"));
                    getData((String) readItem.get("nextChapter"));
                }
            });
            scanView.setAdapter(scanViewAdapter, currentIndex);
            scanView.setPageListener(pageListener);
            scanView.setSavePageListener(savePageListener);
//            tvContent.setText((CharSequence) readItem.get("content"));
        }
    };
    private HashMap readItem;
    private HashMap authorMap;
    private int currentIndex=1;
    private String pageStatus="next";
    private ScanView.OnPageListener pageListener = new ScanView.OnPageListener() {
        @Override
        public void lastChapter() {
            String lastUrl = (String) readItem.get("lastChapter");
            String allUrl = (String) readItem.get("allChapter");
            if (lastUrl.equals(allUrl))
                return;
            pageStatus = "last";
            authorMap.put("chapter",lastUrl);
            getData(lastUrl);
        }

        @Override
        public void nextChapter() {
            pageStatus = "next";
            authorMap.put("chapter",(String) readItem.get("nextChapter"));
            getData((String) readItem.get("nextChapter"));
        }
    };
    private ScanView.SavePageListener savePageListener = new ScanView.SavePageListener() {
        @Override
        public void saveNowPage(int page) {
            authorMap.put("lastPage",page);
        }
    };
    private boolean first = false;
    private MyPreference myPreference;
    private int mapPosition=-1;
    private List<HashMap> myBooksLists;

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

        Bundle bundle = getIntent().getExtras();
        String href = getIntent().getExtras().getString("url");
        mapPosition = bundle.getInt("itemPosition");
        if (mapPosition!=-1 && myBooksLists!=null && myBooksLists.size()>0){
            authorMap = myBooksLists.get(mapPosition);
            href = (String) authorMap.get("chapter");
            first = true;
        }
        getData(href);
    }

    private void getData(final String href) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    readItem = jsoupGet.getReadItem(href);
                    handler.sendEmptyMessage(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myBooksLists.remove(mapPosition);
        myBooksLists.add(mapPosition,authorMap);
        myPreference.setObject(saveInfo,myBooksLists);
        EventBus.getDefault().post(new RefreshMyBooks());
        bind.unbind();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode==KeyEvent.KEYCODE_VOLUME_UP) {
            scanView.keyDown(keyCode);
            return true;
        }else
        return super.onKeyUp(keyCode,event);
    }

}
