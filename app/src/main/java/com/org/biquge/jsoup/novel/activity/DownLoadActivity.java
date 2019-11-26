package com.org.biquge.jsoup.novel.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.githang.statusbar.StatusBarCompat;
import com.org.biquge.jsoup.MyPreference;
import com.org.biquge.jsoup.R;
import com.org.biquge.jsoup.novel.NovelPublic;
import com.org.biquge.jsoup.novel.adapter.DownAdapter;
import com.org.biquge.jsoup.novel.broadcastReceiver.DownLoadingBroadcast;
import com.org.biquge.jsoup.novel.entities.DownLoadEntity;
import com.org.biquge.jsoup.novel.events.DeleteEvent;
import com.org.biquge.jsoup.novel.events.RefreshTheme;
import com.org.biquge.jsoup.novel.thread.DownLoadTask;
import com.org.biquge.jsoup.novel.thread.DownLoadThread;
import com.org.biquge.jsoup.novel.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.org.biquge.jsoup.MyPreference.saveInfo;
import static com.org.biquge.jsoup.MyPreference.themeNum;
import static com.org.biquge.jsoup.novel.NovelPublic.novelHomeUrl;
import static com.org.biquge.jsoup.novel.NovelPublic.novelSaveDirName;

public class DownLoadActivity extends AppCompatActivity {

    @BindView(R.id.rcv_down)
    RecyclerView rcvDown;
    @BindView(R.id.rl_top)
    RelativeLayout rlTop;
    private MyPreference myPreference;
    private Context context;
    private List<HashMap> myBooksLists;
    private DownAdapter downAdapter;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    ToastUtils.showShortMsg(context, (String) msg.obj);
                    break;
                case 1:
                    EventBus.getDefault().post(new DeleteEvent());
                    break;
            }
        }
    };
    private DownLoadingBroadcast downLoadingBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_load);
        ButterKnife.bind(this);
        context = this;
        EventBus.getDefault().register(this);
        myPreference = MyPreference.getInstance();
        myPreference.setPreference(context);
        refreshTheme(new RefreshTheme());

        initData();
        initRcv();
    }

    private void initData() {
        myBooksLists = myPreference.getListObject(saveInfo, HashMap.class);
        if (myBooksLists == null) {
            myBooksLists = new ArrayList<>();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(downLoadingBroadcast);
    }

    private void initRcv() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcvDown.setLayoutManager(linearLayoutManager);

        for (int i = 0; i < myBooksLists.size(); i++) {
            HashMap hashMap = myBooksLists.get(i);
            DownLoadEntity loadEntity = JSON.parseObject((String) hashMap.get("downLoadInfo"), DownLoadEntity.class);
            List<HashMap> chapters = JSON.parseArray((String) hashMap.get("chapters"), HashMap.class);

            String path = Environment.getExternalStorageDirectory() + novelSaveDirName + loadEntity.getHomeUrl().split(novelHomeUrl)[1];
            File savedFile = new File(path);
            if (savedFile.exists()) {
                int filesCount = savedFile.list().length;
                loadEntity.setLoadedPage(filesCount);
                if (chapters.size() == filesCount) {
                    loadEntity.setLoadingStatu(2);
                }
                if (filesCount == 0) {
                    loadEntity.setCurrentPageUrl((String) chapters.get(0).get("href"));
                } else {
                    loadEntity.setCurrentPageUrl((String) chapters.get(filesCount - 1).get("href"));
                }
                myBooksLists.get(i).put("downLoadInfo", JSON.toJSONString(loadEntity));
            }
        }

        downAdapter = new DownAdapter(R.layout.down_item_layout, myBooksLists);
        rcvDown.setAdapter(downAdapter);
        IntentFilter filter = new IntentFilter(NovelPublic.downLoadingUpdata);
        downLoadingBroadcast = new DownLoadingBroadcast(downAdapter);
        registerReceiver(downLoadingBroadcast, filter);

        if (DownLoadTask.threadList == null) {
            DownLoadTask.threadList = new ArrayList<>();
            for (int i = 0; i < myBooksLists.size(); i++) {
                HashMap hashMap = myBooksLists.get(i);
                DownLoadEntity loadEntity = JSON.parseObject((String) hashMap.get("downLoadInfo"), DownLoadEntity.class);
                List<HashMap> chapters = JSON.parseArray((String) hashMap.get("chapters"), HashMap.class);
                String path = Environment.getExternalStorageDirectory() + novelSaveDirName + loadEntity.getHomeUrl().split(novelHomeUrl)[1];
                Log.d("savepath", path);
                DownLoadTask.threadList.add(new DownLoadThread(context, (String) hashMap.get("title"),
                        (String) hashMap.get("author"), path, loadEntity, handler, i, chapters));
            }
        } else {
            List<DownLoadThread> loadThreads = new ArrayList<>();
            int thredid = 0;
            for (DownLoadThread loadThread : DownLoadTask.threadList) {
                boolean has = false;
                for (HashMap hashMap : myBooksLists) {
                    if (hashMap.get("title").equals(loadThread.title) && hashMap.get("author").equals(loadThread.author)) {
                        has = true;
                    }
                }
                if (!has) {
                    loadThreads.add(loadThread);
                }
                DownLoadTask.threadList.get(thredid).handler = handler;
                thredid++;
            }
            DownLoadTask.threadList.removeAll(loadThreads);
            for (int i = 0; i < myBooksLists.size(); i++) {
                HashMap hashMap = myBooksLists.get(i);
                boolean has = false;
                int isPosition = 0;
                for (DownLoadThread loadThread : DownLoadTask.threadList) {
                    if (hashMap.get("title").equals(loadThread.title) && hashMap.get("author").equals(loadThread.author)) {
                        has = true;
                        break;
                    }
                    isPosition++;
                }
                DownLoadEntity loadEntity = JSON.parseObject((String) hashMap.get("downLoadInfo"), DownLoadEntity.class);
                String path = Environment.getExternalStorageDirectory() + novelSaveDirName + loadEntity.getHomeUrl().split(novelHomeUrl)[1];
                String title = (String) hashMap.get("title");
                String author = (String) hashMap.get("author");
                List<HashMap> chapters = JSON.parseArray((String) hashMap.get("chapters"), HashMap.class);
                if (!has) {
                    DownLoadTask.threadList.add(new DownLoadThread(context, (String) hashMap.get("title"),
                            (String) hashMap.get("author"), path, loadEntity, handler, i, chapters));
                } else {
                    int loadStatu = DownLoadTask.threadList.get(isPosition).loadEntity.getLoadingStatu();
                    loadEntity.setLoadingStatu(loadStatu);
                    DownLoadTask.threadList.get(isPosition).title = title;
                    DownLoadTask.threadList.get(isPosition).author = author;
                    DownLoadTask.threadList.get(isPosition).path = path;
                    DownLoadTask.threadList.get(isPosition).loadEntity = loadEntity;
                    DownLoadTask.threadList.get(isPosition).position = i;
                }
            }
        }
        downAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                /*ToastUtils.showShortMsg(context,"开始暂停");
                HashMap hashMap = myBooksLists.get(position);
                DownLoadEntity loadEntity = JSON.parseObject((String) hashMap.get("downLoadInfo"),DownLoadEntity.class);

                String path = Environment.getExternalStorageDirectory()+novelSaveDirName+loadEntity.getHomeUrl().split(novelHomeUrl)[1];
                Log.d("savepath",path);
                new DownLoadThread(context,(String) hashMap.get("title"),path,loadEntity,handler,position).start();*/
                boolean isInTask = false;
                HashMap hashMap = myBooksLists.get(position);
                String title = (String) hashMap.get("title");
                String author = (String) hashMap.get("author");
                DownLoadThread downLoadThread = null;
                for (int i = 0; i < DownLoadTask.threadList.size(); i++) {
                    DownLoadThread loadThread = DownLoadTask.threadList.get(i);
                    if (loadThread.title.equals(title) && loadThread.author.equals(author)) {
                        downLoadThread = loadThread;
                        if (loadThread.loadEntity.getLoadingStatu() != 1) {
                            DownLoadTask.startDownLoad(position);
                        } else {
                            DownLoadTask.threadList.get(position).loadEntity.setLoadingStatu(0);
                        }
                        isInTask = true;
                        break;
                    }
                }
                if (!isInTask) {
                    DownLoadEntity loadEntity = JSON.parseObject((String) hashMap.get("downLoadInfo"), DownLoadEntity.class);
                    String path = Environment.getExternalStorageDirectory() + novelSaveDirName + loadEntity.getHomeUrl().split(novelHomeUrl)[1];
                    List<HashMap> chapters = JSON.parseArray((String) hashMap.get("chapters"), HashMap.class);
                    DownLoadTask.threadList.add(new DownLoadThread(context, (String) hashMap.get("title"),
                            (String) hashMap.get("author"), path, loadEntity, handler, position, chapters));
                    DownLoadTask.startDownLoad(position);
                }
                if (downLoadThread != null) {
                    Intent intent = new Intent(NovelPublic.downLoadingUpdata);
                    intent.putExtra("position", position);
                    intent.putExtra("loadEntity", JSON.toJSONString(downLoadThread.loadEntity));
                    sendBroadcast(intent);
                }
            }
        });
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

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
