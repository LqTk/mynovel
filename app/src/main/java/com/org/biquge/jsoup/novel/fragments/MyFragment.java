package com.org.biquge.jsoup.novel.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.githang.statusbar.StatusBarCompat;
import com.org.biquge.jsoup.JsoupGet;
import com.org.biquge.jsoup.MyPreference;
import com.org.biquge.jsoup.R;
import com.org.biquge.jsoup.novel.NovelPublic;
import com.org.biquge.jsoup.novel.activity.DownLoadActivity;
import com.org.biquge.jsoup.novel.activity.NovelReadItem;
import com.org.biquge.jsoup.novel.activity.ThemeActivity;
import com.org.biquge.jsoup.novel.adapter.MyBooksAdapter;
import com.org.biquge.jsoup.novel.entities.DownLoadEntity;
import com.org.biquge.jsoup.novel.events.DeleteEvent;
import com.org.biquge.jsoup.novel.events.RefreshMyBooks;
import com.org.biquge.jsoup.novel.events.RefreshTheme;
import com.org.biquge.jsoup.novel.thread.DownLoadTask;
import com.org.biquge.jsoup.novel.thread.DownLoadThread;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.org.biquge.jsoup.MyPreference.refreshLastTime;
import static com.org.biquge.jsoup.MyPreference.saveInfo;
import static com.org.biquge.jsoup.MyPreference.themeNum;
import static com.org.biquge.jsoup.novel.NovelPublic.novelSaveDirName;

public class MyFragment extends Fragment {
    @BindView(R.id.rcl_books)
    SwipeMenuRecyclerView rclBooks;
    @BindView(R.id.ll_pro)
    LinearLayout llPro;
    @BindView(R.id.srf)
    SmartRefreshLayout srf;

    Unbinder unbinder;
    MyPreference myPreference;
    MyBooksAdapter booksAdapter;
    View emptyView;
    @BindView(R.id.rl_top)
    RelativeLayout rlTop;
    @BindView(R.id.srf_header)
    ClassicsHeader srfHeader;
    private List<HashMap> myBooksLists = new ArrayList<>();
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                Toast.makeText(getContext(), (String) msg.obj, Toast.LENGTH_SHORT).show();
            } else if (msg.what == 1) {
                deleteItem(new DeleteEvent());
            } else if (msg.what == 2) {
                booksAdapter.notifyDataSetChanged();
            }
        }
    };
    private String deletePath = "";
    private int deletePosition = 0;
    List<DownLoadThread> loadThreads = new ArrayList<>();
    private long refreshTime = 0l;
    private String homeUrl = NovelPublic.getHomeUrl(3);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        emptyView = getLayoutInflater().inflate(R.layout.my_empty_layout, (ViewGroup) rclBooks.getParent(), false);
        myPreference = MyPreference.getInstance();
        myPreference.setPreference(getContext());

        refreshTheme(new RefreshTheme());
        refreshTime = myPreference.getLong(refreshLastTime, 0);

        initView();
        initData();
    }

    private void initView() {
        srf.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (myBooksLists != null && myBooksLists.size() > 0) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < myBooksLists.size(); i++) {
                                HashMap hashMap = myBooksLists.get(i);
                                JsoupGet jsoupGet = new JsoupGet();
                                try {
                                    List<List<HashMap>> cataLog = jsoupGet.getPageContent((String) hashMap.get("cataLog"));
                                    if (JSON.parseArray((String) hashMap.get("chapters"), HashMap.class).size() != cataLog.get(1).size()) {
                                        myBooksLists.get(i).put("chapters", JSON.toJSONString(cataLog.get(1)));
                                        myBooksLists.get(i).put("recentString", cataLog.get(0).get(0).get("recentString"));
                                        myBooksLists.get(i).put("recentHref", cataLog.get(0).get(0).get("recentHref"));
                                        myBooksLists.get(i).put("time", cataLog.get(0).get(0).get("time"));
                                        myBooksLists.get(i).put("hasNew", true);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            refreshTime = System.currentTimeMillis();
                            myPreference.setLong(refreshLastTime, refreshTime);
                            srf.finishRefresh();
                            handler.sendEmptyMessage(2);
                        }
                    }).start();
                }else {
                    srf.finishRefresh();
                }
            }
        });
        srfHeader.setTimeFormat(new SimpleDateFormat("上次更新 MM-dd HH:mm", Locale.CHINA));
        if (refreshTime==0){
            refreshTime = System.currentTimeMillis();
            srf.autoRefresh();
        }

    }

    private void initData() {
        myBooksLists = myPreference.getListObject(saveInfo, HashMap.class);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rclBooks.setLayoutManager(layoutManager);
        setRecyleMenu();
        setAdapter();
    }

    private void setRecyleMenu() {
        SwipeMenuCreator mSwipeMenuCreate = new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
                SwipeMenuItem menuItem = new SwipeMenuItem(getContext())
                        .setBackgroundColor(getResources().getColor(R.color.delete_pink))
                        .setText("删除")
                        .setTextColor(getResources().getColor(R.color.white))
                        .setTextSize(15)
                        .setWidth(160)
                        .setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
                swipeRightMenu.addMenuItem(menuItem);
            }
        };
        rclBooks.setSwipeMenuCreator(mSwipeMenuCreate);

        SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge) {
                menuBridge.closeMenu();//任何操作必须先关闭菜单，否则可能出现item菜单打开状态错乱

                int direction = menuBridge.getDirection();//左侧还是右侧菜单
                int adapterPosition = menuBridge.getAdapterPosition();
                int menuPosition = menuBridge.getPosition();
                if (menuPosition == 0) {
                    HashMap hashMap = myBooksLists.get(adapterPosition);
                    DownLoadEntity loadEntity = JSON.parseObject((String) hashMap.get("downLoadInfo"), DownLoadEntity.class);
                    String path = Environment.getExternalStorageDirectory() + novelSaveDirName + loadEntity.getHomeUrl().split(homeUrl)[1];
                    deletePath = path;
                    deletePosition = adapterPosition;
                    llPro.setVisibility(View.VISIBLE);
                    stopDown(hashMap);
                }
            }
        };
        rclBooks.setSwipeMenuItemClickListener(mMenuItemClickListener);
    }

    private void stopDown(HashMap hashMap) {
        if (DownLoadTask.threadList != null) {
            for (int i = 0; i < DownLoadTask.threadList.size(); i++) {
                DownLoadThread loadThread = DownLoadTask.threadList.get(i);
                if (hashMap.get("title").equals(loadThread.title) && hashMap.get("author").equals(loadThread.author)) {
                    loadThreads.add(loadThread);
                    if (DownLoadTask.threadList.get(i).loadEntity.getLoadingStatu() == 1) {
                        DownLoadTask.threadList.get(i).handler = handler;
                        DownLoadTask.threadList.get(i).loadEntity.setLoadingStatu(3);
                    }else {
                        deleteItem(new DeleteEvent());
                    }
                    break;
                }
            }
//            deleteItem(new DeleteEvent());
        } else {
            deleteItem(new DeleteEvent());
        }
    }

    @Subscribe
    public void deleteItem(DeleteEvent deleteEvent) {
        if (DownLoadTask.threadList != null) {
            DownLoadTask.threadList.removeAll(loadThreads);
        }
        delFolder(deletePath);
        myBooksLists.remove(deletePosition);
        myPreference.setObject(saveInfo, myBooksLists);
        Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
        llPro.setVisibility(View.GONE);
        getBooks(new RefreshMyBooks());
    }

    private void setAdapter() {
        booksAdapter = new MyBooksAdapter(R.layout.mybooks_item, myBooksLists);
        booksAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putInt("itemPosition", position);
                bundle.putBoolean("isAdded", true);
                bundle.putString("from", "myFragment");
                Intent intent = new Intent(getContext(), NovelReadItem.class);
                intent.putExtras(bundle);
                startActivity(intent);
                myBooksLists.get(position).put("hasNew", false);
                myPreference.setObject(saveInfo, myBooksLists);
            }
        });
        rclBooks.setAdapter(booksAdapter);

        if (myBooksLists == null || myBooksLists.size() == 0) {
            booksAdapter.setEmptyView(emptyView);
            booksAdapter.notifyDataSetChanged();
        }

        if (myBooksLists != null && myBooksLists.size() > 0 && System.currentTimeMillis() - refreshTime > 21600000) {
            refreshTime = System.currentTimeMillis();
            myPreference.setLong(refreshLastTime, refreshTime);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < myBooksLists.size(); i++) {
                        HashMap hashMap = myBooksLists.get(i);
                        JsoupGet jsoupGet = new JsoupGet();
                        try {
                            List<List<HashMap>> cataLog = jsoupGet.getPageContent((String) hashMap.get("cataLog"));
                            if (JSON.parseArray((String) hashMap.get("chapters"), HashMap.class).size() != cataLog.get(1).size()) {
                                myBooksLists.get(i).put("chapters", JSON.toJSONString(cataLog.get(1)));
                                myBooksLists.get(i).put("recentString", cataLog.get(0).get(0).get("recentString"));
                                myBooksLists.get(i).put("recentHref", cataLog.get(0).get(0).get("recentHref"));
                                myBooksLists.get(i).put("time", cataLog.get(0).get(0).get("time"));
                                myBooksLists.get(i).put("hasNew", true);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    handler.sendEmptyMessage(2);
                }
            }).start();
        }
    }

    public void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
            }
        }
    }

    //folderPath为文件路径
    public void delFolder(String folderPath) {
        try {
            String[] strings = folderPath.split(File.separator);
            StringBuffer pathbuffer = new StringBuffer();
            for (int i = 0; i < strings.length - 2; i++) {
                pathbuffer.append(strings[i] + File.separator);
            }
//            pathbuffer.append(File.separator);
            String path = pathbuffer.toString();
            File deleteFile = new File(path + File.separator + strings[strings.length - 2]);
            String[] fileList = deleteFile.list();
            if (fileList.length > 1) {
                for (int i = 0; i < fileList.length; i++) {
                    if (strings[strings.length - 1].equals(fileList[i])) {
                        delAllFile(path + strings[strings.length - 2] + File.separator + fileList[i] + File.separator); //删除完里面所有内容
                        String filePath = folderPath;
                        filePath = filePath.toString();
                        File myFilePath = new File(filePath);
                        myFilePath.delete();
                    }
                }
            } else {
                delAllFile(folderPath); //删除完里面所有内容
                String filePath = folderPath;
                filePath = filePath.toString();
                File myFilePath = new File(filePath);
                myFilePath.delete();
                filePath = path + strings[strings.length - 2];
                filePath = filePath.toString();
                myFilePath = new File(filePath);
                myFilePath.delete();
            }
            System.out.println(path);
        } catch (Exception e) {
            System.out.println("删除文件夹操作出错");
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void getBooks(RefreshMyBooks refreshMyBooks) {
        myBooksLists = myPreference.getListObject(saveInfo, HashMap.class);
        if (myBooksLists == null || myBooksLists.size() == 0) {
            booksAdapter.setEmptyView(emptyView);
        } else {
            booksAdapter.setNewData(myBooksLists);
        }
        booksAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myPreference.setObject(saveInfo, myBooksLists);
    }

    @Override
    public void onPause() {
        super.onPause();
        myPreference.setObject(saveInfo, myBooksLists);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DownLoadTask.threadList != null) {
            for (int i = 0; i < DownLoadTask.threadList.size(); i++) {
                DownLoadTask.threadList.get(i).handler = handler;
            }
        }
    }

    @OnClick({R.id.iv_down, R.id.iv_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_down:
                Intent intent = new Intent(getContext(), DownLoadActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_setting:
                startActivity(new Intent(getContext(), ThemeActivity.class));
                break;
        }
    }

    @Subscribe
    public void refreshTheme(RefreshTheme theme) {
        int themeId = myPreference.getInt(themeNum, 0);
        if (themeId == 0) {
            themeId = R.color.theme_blue;
            myPreference.setInt(themeNum, themeId);
        }
        rlTop.setBackgroundColor(getResources().getColor(themeId));
        StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(themeId));
    }
}
