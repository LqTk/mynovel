package com.org.biquge.jsoup.novel.thread;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.org.biquge.jsoup.JsoupGet;
import com.org.biquge.jsoup.novel.NovelPublic;
import com.org.biquge.jsoup.novel.entities.DownLoadEntity;
import com.org.biquge.jsoup.novel.events.RefreshLoadingEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

public class DownLoadThread extends Thread{
    public String loadString;
    public String path;
    File saveFile;
    String[] split;
    String fileName;
    public Handler handler;
    public String title;
    Context context;
    public int position;
    public DownLoadEntity loadEntity;
    public String author;
    private long time=0l;
    private List<HashMap> chaptersLists;
    JsoupGet jsoupGet;
    private int nowChapterPosition;
    private String homeUrl = NovelPublic.getHomeUrl(3);

    public DownLoadThread(Context context, String title, String author, String savePath,
                          final DownLoadEntity loadEntity, Handler mhandler, int position, List<HashMap> chapters) {
        this.context = context;
        this.path = savePath;
        this.loadEntity = loadEntity;
        this.loadString = loadEntity.getCurrentPageUrl();
        this.handler = mhandler;
        this.title = title;
        this.position = position;
        this.author = author;
        this.jsoupGet = new JsoupGet();
        this.chaptersLists = chapters;
        initData();
    }

    public void initData() {
        saveFile = new File(path);
        if (!saveFile.exists()){
            saveFile.mkdirs();
        }
        for (int i=0;i<chaptersLists.size();i++){
            if (loadString.contains((String) chaptersLists.get(i).get("href"))){
                nowChapterPosition = i;
                break;
            }
        }
    }

    private void checkDir(){
        split = loadString.split("/");
        fileName = split[split.length-1].split("\\.")[0];
    }

    @Override
    public void run() {
        try {
            loadEntity.setLoadingStatu(1);
            EventBus.getDefault().post(new RefreshLoadingEvent("iv",position,JSON.toJSONString(loadEntity)));
            HashMap downContent = new HashMap<>();
            String content;
            FileOutputStream outputStream;
            OutputStreamWriter writer;
            long old_length;
            if (!loadString.contains(homeUrl))
                loadString = homeUrl +loadString;
            checkDir();
            File writeFile = new File(path,fileName+".txt");
            if (writeFile.exists()){
                nowChapterPosition++;
                downContent.put("allChapter",loadEntity.getHomeUrl());
                if (nowChapterPosition>=chaptersLists.size()){
                    downContent.put("nextChapter",loadEntity.getHomeUrl());
                }else {
                    downContent.put("nextChapter",chaptersLists.get(nowChapterPosition).get("href"));
                }
            }else {
                downContent = jsoupGet.getReadItem(loadString);
                content = (String) downContent.get("content");
                outputStream = new FileOutputStream(writeFile);
                writer = new OutputStreamWriter(outputStream, Charset.forName("gbk"));
                writer.write(content);
                writer.close();
                do {
                    old_length = writeFile.length();
                } while (old_length != writeFile.length());
                loadEntity.setLoadedPage(saveFile.list().length);
                EventBus.getDefault().post(new RefreshLoadingEvent("pro",position,JSON.toJSONString(loadEntity)));
            }
            while ((!downContent.get("allChapter").equals(downContent.get("nextChapter"))) && loadEntity.getLoadingStatu()!=0 && loadEntity.getLoadingStatu()!=3){
                loadString = (String) downContent.get("nextChapter");
                checkDir();
                writeFile = new File(path, fileName + ".txt");
                if (writeFile.exists()){
                    nowChapterPosition++;
                    downContent.put("allChapter",loadEntity.getHomeUrl());
                    if (nowChapterPosition>=chaptersLists.size()){
                        downContent.put("nextChapter",loadEntity.getHomeUrl());
                    }else {
                        downContent.put("nextChapter", chaptersLists.get(nowChapterPosition).get("href"));
                    }
                }else {
                    Thread.sleep(300);
                    downContent = jsoupGet.getReadItem(loadString);
                    content = (String) downContent.get("content");
                    outputStream = new FileOutputStream(writeFile);
                    writer = new OutputStreamWriter(outputStream, Charset.forName("gbk"));
                    writer.write(content);
                    writer.close();
                    do {
                        old_length = writeFile.length();
                    } while (old_length != writeFile.length());
                    loadEntity.setLoadedPage(saveFile.list().length);
                    EventBus.getDefault().post(new RefreshLoadingEvent("pro",position,JSON.toJSONString(loadEntity)));
                }
            }
            Message message = Message.obtain();
            message.what = 0;
            if (loadEntity.getLoadingStatu()==3){
                //删除事件
                message.what=1;
                handler.sendMessage(message);
            }else if (loadEntity.getLoadingStatu()==1) {
                message.obj = "《" + title + "》下载完成";
                handler.sendMessageDelayed(message,200);
                loadEntity.setLoadingStatu(2);
                loadEntity.setLoadedPage(saveFile.list().length);
                EventBus.getDefault().post(new RefreshLoadingEvent("iv",position,JSON.toJSONString(loadEntity)));
            }else if(loadEntity.getLoadingStatu()==0){
                message.obj = "《" + title + "》已暂停下载";
                handler.sendMessage(message);
                EventBus.getDefault().post(new RefreshLoadingEvent("iv",position,JSON.toJSONString(loadEntity)));
            }
        } catch (Exception e) {
            Message message = Message.obtain();
            message.what = 0;
            message.obj = "下载出现错误《" + title + "》已暂停下载";
            handler.sendMessage(message);
            loadEntity.setLoadingStatu(0);
            EventBus.getDefault().post(new RefreshLoadingEvent("iv",position,JSON.toJSONString(loadEntity)));
            e.printStackTrace();
        }
    }
}

