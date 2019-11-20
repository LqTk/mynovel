package com.org.biquge.jsoup.novel.thread;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.org.biquge.jsoup.JsoupGet;
import com.org.biquge.jsoup.novel.NovelPublic;
import com.org.biquge.jsoup.novel.entities.DownLoadEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.HashMap;

import static com.org.biquge.jsoup.novel.NovelPublic.novelHomeUrl;

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

    public DownLoadThread(Context context, String title, String author, String savePath,
                          DownLoadEntity loadEntity, Handler mhandler, int position) {
        this.context = context;
        this.path = savePath;
        this.loadEntity = loadEntity;
        this.loadString = loadEntity.getCurrentPageUrl();
        this.handler = mhandler;
        this.title = title;
        this.position = position;
        this.author = author;
    }

    private void checkDir(){
        split = loadString.split("/");
        fileName = split[split.length-1].split("\\.")[0];
        saveFile = new File(path);
        if (!saveFile.exists()){
            saveFile.mkdirs();
        }
    }

    @Override
    public void run() {
        try {
            Intent intent = new Intent(NovelPublic.downLoadingUpdata);
            intent.putExtra("position",position);
            JsoupGet jsoupGet = new JsoupGet();
            HashMap downContent;
            if (!loadString.contains(novelHomeUrl))
                loadString = novelHomeUrl+loadString;
            downContent = jsoupGet.getReadItem(loadString);
            String content = (String) downContent.get("content");
            checkDir();
            File writeFile = new File(path,fileName+".txt");
            FileOutputStream outputStream = new FileOutputStream(writeFile);
            OutputStreamWriter writer=new OutputStreamWriter(outputStream, Charset.forName("gbk"));
            writer.write(content);
            writer.close();
            long old_length;
            do {
                old_length = writeFile.length();
            } while (old_length != writeFile.length());
            loadEntity.setLoadedPage(saveFile.list().length);
            intent.putExtra("loadEntity", JSON.toJSONString(loadEntity));
            if (System.currentTimeMillis()-time>1000) {
                context.sendBroadcast(intent);
                time = System.currentTimeMillis();
            }
            while ((!downContent.get("allChapter").equals(downContent.get("nextChapter"))) && loadEntity.getLoadingStatu()!=0 && loadEntity.getLoadingStatu()!=3){
                loadString = (String) downContent.get("nextChapter");
                downContent = jsoupGet.getReadItem(loadString);
                content = (String) downContent.get("content");
                checkDir();
                writeFile = new File(path,fileName+".txt");
                outputStream = new FileOutputStream(writeFile);
                writer=new OutputStreamWriter(outputStream, Charset.forName("gbk"));
                writer.write(content);
                writer.close();
                do {
                    old_length = writeFile.length();
                } while (old_length != writeFile.length());
                loadEntity.setLoadedPage(saveFile.list().length);
                intent.putExtra("loadEntity",JSON.toJSONString(loadEntity));
                if (System.currentTimeMillis()-time>1000) {
                    context.sendBroadcast(intent);
                    time = System.currentTimeMillis();
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
                handler.sendMessage(message);
                loadEntity.setLoadingStatu(2);
                loadEntity.setLoadedPage(saveFile.list().length);
                intent.putExtra("loadEntity", JSON.toJSONString(loadEntity));
                context.sendBroadcast(intent);
            }else if(loadEntity.getLoadingStatu()==0){
                message.obj = "《" + title + "》已暂停下载";
                handler.sendMessage(message);
                intent.putExtra("loadEntity", JSON.toJSONString(loadEntity));
                if (System.currentTimeMillis()-time>1000) {
                    context.sendBroadcast(intent);
                    time = System.currentTimeMillis();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

