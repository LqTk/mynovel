package com.org.biquge.jsoup.novel.thread;

import android.os.Handler;
import android.os.Message;

import com.org.biquge.jsoup.novel.utils.ToastUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownLoadTask {
    public static List<DownLoadThread> threadList;
    public static ExecutorService sExe = Executors.newCachedThreadPool();//线程池

    public static void startDownLoad(int position){
        threadList.get(position).loadEntity.setLoadingStatu(1);
        sExe.execute(threadList.get(position));
    }

    public static void stopAll(){
        sExe.shutdownNow();
    }

    private static int getStartCount(){
        int count = 0;
        if (threadList!=null) {
            for (DownLoadThread loadThread : threadList) {
                if (loadThread.loadEntity.getLoadingStatu()==1){
                    count++;
                }
            }
        }
        return count;
    }
}
