package com.org.biquge.jsoup.novel.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.org.biquge.jsoup.novel.NovelPublic;
import com.org.biquge.jsoup.novel.adapter.ScanViewAdapter;
import com.org.biquge.jsoup.novel.broadcastReceiver.BattaryBroadcast;
import com.org.biquge.jsoup.novel.entities.DownLoadEntity;
import com.org.biquge.jsoup.novel.entities.ScanThemeBgEntity;
import com.org.biquge.jsoup.novel.events.RefreshMyBooks;
import com.org.biquge.jsoup.novel.popwindow.ChapterPop;
import com.org.biquge.jsoup.novel.popwindow.FullScreenPop;
import com.org.biquge.jsoup.novel.popwindow.SavePop;
import com.org.biquge.jsoup.novel.thread.DownLoadTask;
import com.org.biquge.jsoup.novel.thread.DownLoadThread;
import com.org.biquge.jsoup.novel.utils.ToastUtils;
import com.org.biquge.jsoup.novel.view.ScanView;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.org.biquge.jsoup.MyPreference.saveInfo;
import static com.org.biquge.jsoup.MyPreference.scanViewBgId;
import static com.org.biquge.jsoup.MyPreference.scanViewOrientation;
import static com.org.biquge.jsoup.MyPreference.scanViewScrollPo;
import static com.org.biquge.jsoup.novel.NovelPublic.novelHomeUrl;
import static com.org.biquge.jsoup.novel.NovelPublic.novelSaveDirName;

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
    private int pageReadCount = 280;
    ScanViewAdapter scanViewAdapter;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    ToastUtils.showShortMsg(context, (String) msg.obj);
                    break;
                case 1:
                    ToastUtils.showShortMsg(context, (String) msg.obj);
                    break;
                case 2:
                    if (readItem == null)
                        return;
                    tvChapterName.setText((String) readItem.get("name"));
                    String content = (String) readItem.get("content");
                    final List<String> cons = new ArrayList<>();
                    final List<String> consCopy = new ArrayList<>();
                    if (scrollOrientation==0) {
                        int i = 0;
                        for (i = 0; i < content.length() / pageReadCount; i++) {
                            cons.add(content.substring(i * pageReadCount, (i + 1) * pageReadCount));
                        }
                        cons.add(content.substring(i * pageReadCount, content.length()));
                    }else {
                        int i = 0;
                        for (i = 0; i < content.length() / pageReadCount; i++) {
                            consCopy.add(content.substring(i * pageReadCount, (i + 1) * pageReadCount));
                        }
                        consCopy.add(content.substring(i * pageReadCount, content.length()));
                        cons.add(content);
                    }
                    if (!first && !isChangeOrientation) {
                        if (pageStatus.equals("next")) {
                            currentIndex = 1;
                            authorMap.put("lastPage", currentIndex);
                            scrollPo = 0;
                        } else {
                            currentIndex = cons.size();
                            authorMap.put("lastPage", currentIndex);
                        }
                        if (scrollOrientation==0) {
                            currentIndex = (int) authorMap.get("lastPage");
                        }else {
                            currentIndex = 1;
                            if (pageStatus.equals("last")) {
                                scrollPosition = consCopy.size();
                            }else {
                                authorMap.put("lastPage", currentIndex);
                                scrollPosition = (int) authorMap.get("lastPage");
                            }
                        }
                    }
                    if (isChangeOrientation){
                        isChangeOrientation = false;
                        if (scrollOrientation==0) {
                            currentIndex = (int) authorMap.get("lastPage");
                        }else {
                            if (pageStatus.equals("last")) {
                                scrollPosition = consCopy.size();
                            }else {
                                scrollPosition = (int) authorMap.get("lastPage");
                            }
                            currentIndex = 1;
                        }
                    }
                    if (first) {
                        first = false;
                        currentIndex = (int) authorMap.get("lastPage");
                        scrollPosition = currentIndex;
                        if (scrollOrientation==1){
                            currentIndex = 1;
                        }
                    }
                    scanViewAdapter = new ScanViewAdapter(context, cons, (Integer) scanViewBgSetting.get("bgId"),scrollOrientation);
                    scanViewAdapter.setChapterClicker(new ScanViewAdapter.ChapterClicker() {
                        @Override
                        public void lastChapterListener() {
                            String lastUrl = (String) readItem.get("lastChapter");
                            String allUrl = (String) readItem.get("allChapter");
                            if (llPro.getVisibility() == View.VISIBLE) {
                                return;
                            }
                            if (lastUrl.equals(allUrl)) {
                                if (System.currentTimeMillis() - showLastTime > 1200) {
                                    showLastTime = System.currentTimeMillis();
                                    ToastUtils.showShortMsg(context, "这已经是第一章了哦~");
                                }
                                return;
                            }
                            pageStatus = "next";
                            getData(lastUrl);
                        }

                        @Override
                        public void nextChapterListener() {
                            String nextUrl = (String) readItem.get("nextChapter");
                            String allUrl = (String) readItem.get("allChapter");
                            if (llPro.getVisibility() == View.VISIBLE) {
                                return;
                            }
                            if (nextUrl==null || allUrl==null){
                                return;
                            }
                            if (nextUrl.equals(allUrl)) {
                                if (System.currentTimeMillis() - showLastTime > 1200) {
                                    showLastTime = System.currentTimeMillis();
                                    ToastUtils.showShortMsg(context, "没有下一章了，看看其它的吧");
                                }
                                return;
                            }
                            pageStatus = "next";
                            getData(nextUrl);
                        }
                    });
                    if (scanViewAdapter != null) {
                        scanView.setAdapter(scanViewAdapter, currentIndex,scrollPosition);
                        scanView.setPageListener(pageListener);
                        scanView.setScreenClick(screenClick);
                        scanView.setSavePageListener(savePageListener);
                    }
                    llPro.setVisibility(View.GONE);
                    break;
                case 3:
                    llPro.setVisibility(View.GONE);
                    ToastUtils.showShortMsg(context,"请重试");
                    break;
            }
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
    private int chapterPosition = 0;
    private String homeUrl = NovelPublic.getHomeUrl(3);

    List<ScanThemeBgEntity> themeBgEntities = new ArrayList<>();

    private ScanView.OnPageListener pageListener = new ScanView.OnPageListener() {
        @Override
        public void lastChapter() {
            String lastUrl = (String) readItem.get("lastChapter");
            String allUrl = (String) readItem.get("allChapter");
            if (llPro.getVisibility()==View.VISIBLE){
                return;
            }
            if (lastUrl.equals(allUrl)) {
                if (System.currentTimeMillis()-showLastTime>1200) {
                    showLastTime = System.currentTimeMillis();
                    ToastUtils.showShortMsg(context, "这已经是第一章了哦~");
                }
                return;
            }
            pageStatus = "last";
            getData(lastUrl);
        }

        @Override
        public void nextChapter() {
            String nextUrl = (String) readItem.get("nextChapter");
            String allUrl = (String) readItem.get("allChapter");
            if (llPro.getVisibility()==View.VISIBLE){
                return;
            }
            if (nextUrl==null || allUrl==null){
                return;
            }
            if (nextUrl.equals(allUrl)) {
                if (System.currentTimeMillis()-showLastTime>1200) {
                    showLastTime = System.currentTimeMillis();
                    ToastUtils.showShortMsg(context, "没有下一章了，看看其它的吧");
                }
                return;
            }
            pageStatus = "next";
            getData(nextUrl);
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
            fullScreenPop.refreshData(themeBgEntities,scrollOrientation);
            fullScreenPop.setFullPopClick(fullPopClick);
            fullScreenPop.setFocusable(true);
            fullScreenPop.setOutsideTouchable(false);
            fullScreenPop.showAtLocation(llMain, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }

        @Override
        public void onScrollListener(int scrollPosition, int scrollHeight) {
            scrollPo = scrollPosition;
            if (scrollPosition>scrollHeight){
                authorMap.put("lastPage", scrollPosition/scrollHeight+1);
            }else {
                authorMap.put("lastPage", 1);
            }
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

        @Override
        public void refreshDataClick() {
            refreshData();
        }

        @Override
        public void onSaveClick() {
            SavePop savePop = new SavePop(context);
            savePop.setSavePopClick(savePopClick);
            savePop.setFocusable(true);
            savePop.setOutsideTouchable(false);
            savePop.showAtLocation(llMain, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }

        @Override
        public void setOrientationClick(int orientation) {
            myPreference.setInt(scanViewOrientation,orientation);
            scrollOrientation = myPreference.getInt(scanViewOrientation,0);
            isChangeOrientation = true;
            handler.sendEmptyMessage(2);
        }
    };

    private ChapterPop.ChaptersClick chaptersClick = new ChapterPop.ChaptersClick() {
        @Override
        public void chapterItemClick(int position) {
            HashMap clickHashMap = chaptersLists.get(position);
            Toast.makeText(context, (CharSequence) clickHashMap.get("name"), Toast.LENGTH_SHORT).show();
            pageStatus = "next";
            getData((String) clickHashMap.get("href"));
        }
    };

    private SavePop.SavePopClick savePopClick = new SavePop.SavePopClick() {
        @Override
        public void cancel() {

        }

        @Override
        public void saveAll() {
            if (isAdded) {
                downLoad((String) chaptersLists.get(0).get("href"));
            }else {
                saveBook((String) chaptersLists.get(0).get("href"));
            }
        }

        @Override
        public void saveNow() {
            if (isAdded) {
                downLoad((String) authorMap.get("chapter"));
            }else {
                saveBook((String) authorMap.get("chapter"));
            }
        }
    };

    private BattaryBroadcast battaryBroadcast;
    private HashMap scanViewBgSetting;
    private long showLastTime = 0l;
    private String catalog="";
    private int scrollOrientation=0;
    private int scrollPo=0;
    private boolean isChangeOrientation = false;
    private int scrollVHeight=0;
    private int scrollPosition = 0;
    private String bookName;

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
        if (myBooksLists==null){
            myBooksLists=new ArrayList<>();
        }
        scanViewBgSetting = myPreference.getObject(scanViewBgId,HashMap.class);
        scrollOrientation = myPreference.getInt(scanViewOrientation,0);
        scrollPo = myPreference.getInt(scanViewScrollPo,0);
        if (scanViewBgSetting==null){
            scanViewBgSetting = new HashMap();
            setSetting(R.drawable.read_cover3,0);
        }

        llPro.setVisibility(View.VISIBLE);
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
        themeBgEntities.add(new ScanThemeBgEntity(true,R.drawable.read_cover3));
        themeBgEntities.add(new ScanThemeBgEntity(false,R.drawable.read_cover2));
        themeBgEntities.add(new ScanThemeBgEntity(false,R.drawable.read_cover));
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
//            chaptersLists = JSON.parseArray(bundle.getString("chapters"), HashMap.class);
            catalog = bundle.getString("cataLog");
            isAdded = bundle.getBoolean("isAdded");
            bookName = bundle.getString("title");
            for (int i = 0; i < myBooksLists.size(); i++) {
                HashMap cMap = myBooksLists.get(i);
                if (cMap.get("title").equals(bundle.getString("title"))
                        && cMap.get("author").equals(bundle.getString("author"))) {
                    mapPosition = i;
                    authorMap = myBooksLists.get(mapPosition);
                    isAdded = true;
                    break;
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

    private void getChacters(final String href){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<List<HashMap>> itemContent = null;
                try {
                    itemContent = jsoupGet.getPageContent(catalog);
                    chaptersLists = itemContent.get(1);
                    if (authorMap.size()<5){
                        authorMap = itemContent.get(0).get(0);
                    }
                    getData(href);
                } catch (IOException e) {
                    handler.sendEmptyMessage(3);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void getData(final String href) {
        llPro.setVisibility(View.VISIBLE);
        authorMap.put("chapter", href);

        if (chaptersLists==null){
            getChacters(href);
        }else {
            for (int i = 0; i < chaptersLists.size(); i++) {
                if ((chaptersLists.get(i).get("href")).equals(href)) {
                    chapterPosition = i;
                    break;
                }
            }

            if (!isAdded){
                authorMap.put("lastPage",chapterPosition);
            }
            String nowChapter = (String) chaptersLists.get(chapterPosition).get("href");
            String[] split = nowChapter.split(NovelPublic.getHomeUrl(3))[1].split(".html");
            nowChapter = split[0] + ".txt";
            String savePath = Environment.getExternalStorageDirectory() + novelSaveDirName + nowChapter;
            File saveFile = new File(savePath);
            if (saveFile.exists()) {
                readItem = read(saveFile);
                handler.sendEmptyMessageDelayed(2, 500);
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            readItem = jsoupGet.getReadItem(href);
                            saveContent(href,(String) readItem.get("content"),(String) readItem.get("allChapter"));
                            handler.sendEmptyMessageDelayed(2, 200);
                        } catch (Exception e) {
                            handler.sendEmptyMessage(3);
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
    }

    public HashMap read(File textFile){
        //创建一个带缓冲区的输出流
        HashMap hashMap = new HashMap();
        try {
            //创建一个带缓冲区的输入流
            FileInputStream bis = new FileInputStream(textFile);
            InputStreamReader reader=new InputStreamReader(bis,"gbk");
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            StringBuffer buffer=new StringBuffer();
            while ((line=bufferedReader.readLine())!=null){
                buffer.append(line+"\n");
            }
            reader.close();
            bis.close();
            String lastChapter = "";
            String nextChapter = "";
            if (chapterPosition==0){
                lastChapter = (String) authorMap.get("cataLog");
            }else {
                lastChapter = (String) chaptersLists.get(chapterPosition-1).get("href");
            }
            if (chapterPosition==chaptersLists.size()-1){
                nextChapter = (String) authorMap.get("cataLog");
            }else {
                nextChapter = (String) chaptersLists.get(chapterPosition+1).get("href");
            }
            hashMap.put("lastChapter",lastChapter);
            hashMap.put("allChapter",authorMap.get("cataLog"));
            hashMap.put("nextChapter",nextChapter);
            hashMap.put("content",buffer.toString());
            hashMap.put("name",chaptersLists.get(chapterPosition).get("name"));
            return hashMap;
        }catch (Exception e){
            e.printStackTrace();
            return hashMap;
        }
    }

    public void refreshData(){
        llPro.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    readItem = jsoupGet.getReadItem((String) authorMap.get("chapter"));
                    saveContent((String) authorMap.get("chapter"),(String) readItem.get("content"),(String) readItem.get("allChapter"));
                    handler.sendEmptyMessageDelayed(2,200);
                } catch (Exception e) {
                    handler.sendEmptyMessage(3);
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

    private void saveContent(final String loadString, final String content, final String homeUrl){
        if (!isAdded)
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] split = loadString.split("/");
                String fileName = split[split.length - 1].split("\\.")[0];
                String path = Environment.getExternalStorageDirectory()+novelSaveDirName+homeUrl.split(NovelReadItem.this.homeUrl)[1];
                File saveFile = new File(path);
                if (!saveFile.exists()){
                    saveFile.mkdirs();
                }
                try {
                    File writeFile = new File(path, fileName + ".txt");
                    FileOutputStream outputStream = new FileOutputStream(writeFile);
                    OutputStreamWriter writer = new OutputStreamWriter(outputStream, Charset.forName("gbk"));
                    writer.write(content);
                    writer.close();
                }catch (Exception e){
                    handler.sendEmptyMessage(3);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @OnClick(R.id.ll_pro)
    public void onClickViewed(View v){
        switch (v.getId()){
            case R.id.ll_pro:
                break;
        }
    }

    private void downLoad(String currentUrl){
        Message msg1 = Message.obtain();
        msg1.what=0;
        if (DownLoadTask.threadList == null) {
            DownLoadTask.threadList = new ArrayList<>();
            for (int i=0;i<myBooksLists.size();i++){
                HashMap hashMap = myBooksLists.get(i);
                DownLoadEntity loadEntity = JSON.parseObject((String) hashMap.get("downLoadInfo"), DownLoadEntity.class);
                loadEntity.setCurrentPageUrl(currentUrl);
                String path = Environment.getExternalStorageDirectory() + novelSaveDirName + loadEntity.getHomeUrl().split(homeUrl)[1];
                Log.d("savepath", path);
                List<HashMap> chapters = JSON.parseArray((String) hashMap.get("chapters"), HashMap.class);
                DownLoadTask.threadList.add(new DownLoadThread(context, (String) hashMap.get("title"),
                        (String) hashMap.get("author"), path, loadEntity, handler, i,chapters));
                if (hashMap.get("title").equals(authorMap.get("title"))&&hashMap.get("author").equals(authorMap.get("author"))) {
                    msg1.obj="开始下载《"+authorMap.get("title")+"》";
                    DownLoadTask.startDownLoad(i);
                    handler.sendMessage(msg1);
                }
            }
                /*DownLoadTask.threadList.add(new DownLoadThread(context,(String) hashMap.get("title"),(String) hashMap.get("author"),path,loadEntity,handler,0));
                DownLoadTask.startDownLoad(0);*/
        } else {
            int position=0;
            boolean isInTask = false;
            DownLoadThread downLoadThread = null;
            for (int i=0;i<DownLoadTask.threadList.size();i++){
                DownLoadThread loadThread = DownLoadTask.threadList.get(i);
                if (loadThread.title.equals(authorMap.get("title"))&&loadThread.author.equals(authorMap.get("author"))){
                    isInTask = true;
                    position = i;
                    break;
                }
            }
            if (!isInTask){
                HashMap hashMap = myBooksLists.get(myBooksLists.size()-1);
                DownLoadEntity loadEntity = JSON.parseObject((String) hashMap.get("downLoadInfo"),DownLoadEntity.class);
                String path = Environment.getExternalStorageDirectory()+novelSaveDirName+loadEntity.getHomeUrl().split(homeUrl)[1];
                List<HashMap> chapters = JSON.parseArray((String) hashMap.get("chapters"), HashMap.class);
                loadEntity.setCurrentPageUrl(currentUrl);
                DownLoadTask.threadList.add(new DownLoadThread(context,(String) hashMap.get("title"),
                        (String) hashMap.get("author"),path,loadEntity,handler,myBooksLists.size()-1,chapters));
                msg1.obj="开始下载《"+hashMap.get("title")+"》";
                DownLoadTask.startDownLoad(myBooksLists.size()-1);
                handler.sendMessage(msg1);
            }else {
                HashMap hashMap = myBooksLists.get(position);
                DownLoadThread loadThread = DownLoadTask.threadList.get(position);
                if (loadThread.loadEntity.getLoadingStatu()==1) {
                    DownLoadTask.threadList.get(position).loadEntity.setCurrentPageUrl(currentUrl);
                    msg1.obj="正在下载哦~";
                    handler.sendMessage(msg1);
                }else {
                    DownLoadTask.threadList.get(position).loadString = currentUrl;
                    DownLoadTask.threadList.get(position).initData();
                    msg1.obj="开始下载《"+hashMap.get("title")+"》";
                    DownLoadTask.startDownLoad(position);
                    handler.sendMessage(msg1);
                }
            }
        }
    }

    private void saveBook(final String saveUrl){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap saveMap = new HashMap();

                saveMap.put("img",authorMap.get("img"));
                saveMap.put("recentString","最新："+ (String) chaptersLists.get(chaptersLists.size()-1).get("name"));
                saveMap.put("recentHref", (String) chaptersLists.get(chaptersLists.size()-1).get("href"));
                saveMap.put("time",authorMap.get("time"));
                saveMap.put("author",authorMap.get("author"));
                saveMap.put("title",authorMap.get("title"));

                saveMap.put("chapter",String.valueOf(authorMap.get("chapter")));
                saveMap.put("lastPage",authorMap.get("lastPage"));
                saveMap.put("chapters", JSON.toJSONString(chaptersLists));
                saveMap.put("cataLog",catalog);
                saveMap.put("hasNew",false);
                DownLoadEntity downLoadEntity = new DownLoadEntity(0,chaptersLists.size(),0,
                        catalog, (String) chaptersLists.get(0).get("href"),0);
                saveMap.put("downLoadInfo",JSON.toJSONString(downLoadEntity));
                myBooksLists.add(saveMap);
                authorMap = saveMap;
                myPreference.setObject(saveInfo,myBooksLists);
                EventBus.getDefault().post(new RefreshMyBooks());
                isAdded = true;
                mapPosition = myBooksLists.size()-1;
                downLoad(saveUrl);
            }
        }).start();
    }
}
