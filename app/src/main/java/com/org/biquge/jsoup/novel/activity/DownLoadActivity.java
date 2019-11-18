package com.org.biquge.jsoup.novel.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.githang.statusbar.StatusBarCompat;
import com.org.biquge.jsoup.JsoupGet;
import com.org.biquge.jsoup.MyPreference;
import com.org.biquge.jsoup.R;
import com.org.biquge.jsoup.novel.adapter.DownAdapter;
import com.org.biquge.jsoup.novel.entities.DownLoadEntity;
import com.org.biquge.jsoup.novel.events.LoadingMsg;
import com.org.biquge.jsoup.novel.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
import static com.org.biquge.jsoup.novel.NovelUrl.novelHomeUrl;
import static com.org.biquge.jsoup.novel.NovelUrl.novelSaveDirName;
import static java.security.AccessController.getContext;

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
            super.handleMessage(msg);
            mToastUtils.showShortMsg(context, (String) msg.obj);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_load);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.blue_main));
        ButterKnife.bind(this);
        EventBus.getDefault().unregister(this);
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
        EventBus.getDefault().unregister(this);
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
        downAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, final View view, final int position) {
                final ImageView ivST = view.findViewById(R.id.iv_st);
                ivST.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mToastUtils.showShortMsg(context,"开始暂停");
                        HashMap hashMap = myBooksLists.get(position);
                        DownLoadEntity loadEntity = JSON.parseObject((String) hashMap.get("downLoadInfo"),DownLoadEntity.class);

                        String path = Environment.getExternalStorageDirectory()+novelSaveDirName+loadEntity.getHomeUrl().split(novelHomeUrl)[1];
                        Log.d("savepath",path);
                        new DownThread((String) hashMap.get("title"),path,loadEntity.getCurrentPageUrl(),handler).start();
                    }
                });
            }
        });
        rcvDown.setAdapter(downAdapter);
    }

    private String getFilePath(String currentPageUrl) {
        String[] split = currentPageUrl.split("/");
        String fileName = split[split.length-1].split("\\.")[0];
        return fileName+"txt";
    }

    class DownThread extends Thread{
        String loadString;
        String path;
        File saveFile;
        String[] split;
        String fileName;
        Handler handler;
        String title;

        public DownThread(String title,String savePath, String loadString, Handler mhandler) {
            this.path = savePath;
            this.loadString = loadString;
            this.handler = mhandler;
            this.title = title;
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
                JsoupGet jsoupGet = new JsoupGet();
                HashMap downContent;
                downContent = jsoupGet.getReadItem(novelHomeUrl+loadString);
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
                while (!downContent.get("allChapter").equals(downContent.get("nextChapter"))){
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
                }
                Message message = Message.obtain();
                message.what = 0;
                message.obj="《"+title+"》下载完成";
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isDestroyed() {
        return super.isDestroyed();
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
