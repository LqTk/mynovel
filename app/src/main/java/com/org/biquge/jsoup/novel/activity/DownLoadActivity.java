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

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.githang.statusbar.StatusBarCompat;
import com.org.biquge.jsoup.JsoupGet;
import com.org.biquge.jsoup.MyPreference;
import com.org.biquge.jsoup.R;
import com.org.biquge.jsoup.novel.NovelPublic;
import com.org.biquge.jsoup.novel.adapter.DownAdapter;
import com.org.biquge.jsoup.novel.broadcastReceiver.DownLoadingBroadcast;
import com.org.biquge.jsoup.novel.entities.DownLoadEntity;
import com.org.biquge.jsoup.novel.events.DeleteEvent;
import com.org.biquge.jsoup.novel.thread.DownLoadTask;
import com.org.biquge.jsoup.novel.thread.DownLoadThread;
import com.org.biquge.jsoup.novel.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.org.biquge.jsoup.MyPreference.saveInfo;
import static com.org.biquge.jsoup.novel.NovelPublic.novelHomeUrl;
import static com.org.biquge.jsoup.novel.NovelPublic.novelSaveDirName;

public class DownLoadActivity extends AppCompatActivity {

    @BindView(R.id.rcv_down)
    RecyclerView rcvDown;
    private MyPreference myPreference;
    private Context context;
    private List<HashMap> myBooksLists;
    private DownAdapter downAdapter;
    private ToastUtils mToastUtils = new ToastUtils();

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    mToastUtils.showShortMsg(context, (String) msg.obj);
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
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.blue_main));
        ButterKnife.bind(this);
        context = this;

        initData();
        initRcv();
    }

    private void initData() {
        myPreference = MyPreference.getInstance();
        myPreference.setPreference(context);
        myBooksLists = myPreference.getListObject(saveInfo, HashMap.class);
        if (myBooksLists==null){
            myBooksLists = new ArrayList<>();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(downLoadingBroadcast);
    }

    private void initRcv(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcvDown.setLayoutManager(linearLayoutManager);

        for (int i=0;i<myBooksLists.size();i++){
            HashMap hashMap = myBooksLists.get(i);
            DownLoadEntity loadEntity = JSON.parseObject((String) hashMap.get("downLoadInfo"),DownLoadEntity.class);
            List<HashMap> chapters = JSON.parseArray((String) hashMap.get("chapters"), HashMap.class);

            String path = Environment.getExternalStorageDirectory()+novelSaveDirName+loadEntity.getHomeUrl().split(novelHomeUrl)[1];
            File savedFile = new File(path);
            if (savedFile.exists()) {
                int filesCount = savedFile.list().length;
                loadEntity.setLoadedPage(filesCount);
                loadEntity.setCurrentPageUrl((String) chapters.get(filesCount - 1).get("href"));
                myBooksLists.get(i).put("downLoadInfo",JSON.toJSONString(loadEntity));
            }
        }

        downAdapter = new DownAdapter(R.layout.down_item_layout,myBooksLists);
        rcvDown.setAdapter(downAdapter);
        IntentFilter filter = new IntentFilter(NovelPublic.downLoadingUpdata);
        downLoadingBroadcast = new DownLoadingBroadcast(downAdapter);
        registerReceiver(downLoadingBroadcast, filter);

        if (DownLoadTask.threadList==null){
            DownLoadTask.threadList = new ArrayList<>();
            for (int i=0;i<myBooksLists.size();i++){
                HashMap hashMap = myBooksLists.get(i);
                DownLoadEntity loadEntity = JSON.parseObject((String) hashMap.get("downLoadInfo"),DownLoadEntity.class);

                String path = Environment.getExternalStorageDirectory()+novelSaveDirName+loadEntity.getHomeUrl().split(novelHomeUrl)[1];
                Log.d("savepath",path);
                DownLoadTask.threadList.add(new DownLoadThread(context,(String) hashMap.get("title"),(String) hashMap.get("author"),path,loadEntity,handler,i));
            }
        }else {
            List<DownLoadThread> loadThreads = new ArrayList<>();
            for (DownLoadThread loadThread:DownLoadTask.threadList){
                boolean has = false;
                for (HashMap hashMap:myBooksLists){
                    if (hashMap.get("title").equals(loadThread.title)&&hashMap.get("author").equals(loadThread.author)){
                        has = true;
                        break;
                    }
                }
                if (!has){
                    loadThreads.add(loadThread);
                }
            }
            DownLoadTask.threadList.removeAll(loadThreads);
            for (int i=0;i<myBooksLists.size();i++){
                HashMap hashMap = myBooksLists.get(i);
                boolean has = false;
                int isPosition = 0;
                for (DownLoadThread loadThread:DownLoadTask.threadList){
                    if (hashMap.get("title").equals(loadThread.title)&&hashMap.get("author").equals(loadThread.author)){
                        has = true;
                        break;
                    }
                    isPosition++;
                }
                DownLoadEntity loadEntity = JSON.parseObject((String) hashMap.get("downLoadInfo"),DownLoadEntity.class);
                String path = Environment.getExternalStorageDirectory()+novelSaveDirName+loadEntity.getHomeUrl().split(novelHomeUrl)[1];
                String title = (String) hashMap.get("title");
                String author = (String) hashMap.get("author");
                if (!has){
                    DownLoadTask.threadList.add(new DownLoadThread(context,(String) hashMap.get("title"),(String) hashMap.get("author"),path,loadEntity,handler,i));
                }else {
                    int loadStatu = DownLoadTask.threadList.get(isPosition).loadEntity.getLoadingStatu();
                    loadEntity.setLoadingStatu(loadStatu);
                    DownLoadTask.threadList.get(isPosition).title= title;
                    DownLoadTask.threadList.get(isPosition).author= author;
                    DownLoadTask.threadList.get(isPosition).path= path;
                    DownLoadTask.threadList.get(isPosition).loadEntity= loadEntity;
                    DownLoadTask.threadList.get(isPosition).position= i;
                }
            }
        }
        downAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                /*mToastUtils.showShortMsg(context,"开始暂停");
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
                for (int i=0;i<DownLoadTask.threadList.size();i++){
                    DownLoadThread loadThread = DownLoadTask.threadList.get(i);
                    if (loadThread.title.equals(title)&&loadThread.author.equals(author)){
                        downLoadThread = loadThread;
                        if (loadThread.loadEntity.getLoadingStatu()!=1) {
                            DownLoadTask.startDownLoad(position);
                        }else {
                            DownLoadTask.threadList.get(position).loadEntity.setLoadingStatu(0);
                        }
                        isInTask = true;
                        break;
                    }
                }
                if (!isInTask){
                    DownLoadEntity loadEntity = JSON.parseObject((String) hashMap.get("downLoadInfo"),DownLoadEntity.class);
                    String path = Environment.getExternalStorageDirectory()+novelSaveDirName+loadEntity.getHomeUrl().split(novelHomeUrl)[1];
                    DownLoadTask.threadList.add(new DownLoadThread(context,(String) hashMap.get("title"),(String) hashMap.get("author"),path,loadEntity,handler,position));
                    DownLoadTask.startDownLoad(position);
                }
                if (downLoadThread!=null) {
                    Intent intent = new Intent(NovelPublic.downLoadingUpdata);
                    intent.putExtra("position", position);
                    intent.putExtra("loadEntity", JSON.toJSONString(downLoadThread.loadEntity));
                    sendBroadcast(intent);
                }
            }
        });
    }

/*
    public void read() throws IOException {
        //创建一个带缓冲区的输出流
        String state= Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)) {
            File SDPath = Environment.getExternalStorageDirectory();//SD根目录
            File file = new File(SDPath, "data.txt");
            //创建一个带缓冲区的输入流
            FileInputStream bis = new FileInputStream(file);
            InputStreamReader reader=new InputStreamReader(bis,"utf-8");
            int len;
            char[] buffer = new char[bis.available()];
            while ((len = reader.read()) != -1) {
                reader.read(buffer);
            }
            tv_read.setText(new String(buffer));
            reader.close();
            bis.close();
        }
    }*/
}
