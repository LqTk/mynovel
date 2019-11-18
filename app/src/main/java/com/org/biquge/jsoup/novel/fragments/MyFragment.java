package com.org.biquge.jsoup.novel.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.org.biquge.jsoup.novel.NovelReadItem;
import com.org.biquge.jsoup.novel.adapter.MyBooksAdapter;
import com.org.biquge.jsoup.novel.events.RefreshMyBooks;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.org.biquge.jsoup.MyPreference.saveInfo;
import static com.org.biquge.jsoup.novel.NovelUrl.novelHomeUrl;

public class MyFragment extends Fragment {
    @BindView(R.id.rcl_books)
    SwipeMenuRecyclerView rclBooks;
    Unbinder unbinder;
    @BindView(R.id.ll_pro)
    LinearLayout llPro;

    MyPreference myPreference;
    MyBooksAdapter booksAdapter;
    View emptyView;
    private List<HashMap> myBooksLists=new ArrayList<>();
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            booksAdapter.notifyDataSetChanged();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        StatusBarCompat.setStatusBarColor(getActivity(),getResources().getColor(R.color.blue_main));
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        emptyView = getLayoutInflater().inflate(R.layout.my_empty_layout, (ViewGroup) rclBooks.getParent(),false);
        myPreference = MyPreference.getInstance();
        myPreference.setPreference(getContext());

        initData();
    }

    private void initData() {
        myBooksLists = myPreference.getListObject(saveInfo, HashMap.class);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rclBooks.setLayoutManager(layoutManager);
        setRecyleMenu();
        setAdapter();
    }

    private void setRecyleMenu(){
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
                if (menuPosition == 0){
                    myBooksLists.remove(adapterPosition);
                    myPreference.setObject(saveInfo,myBooksLists);
                    Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                    getBooks(new RefreshMyBooks());
                }
            }
        };
        rclBooks.setSwipeMenuItemClickListener(mMenuItemClickListener);
    }

    private void setAdapter() {
        booksAdapter = new MyBooksAdapter(R.layout.mybooks_item,myBooksLists);
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
                myBooksLists.get(position).put("hasNew",false);
                myPreference.setObject(saveInfo,myBooksLists);
            }
        });
        rclBooks.setAdapter(booksAdapter);

        if (myBooksLists==null||myBooksLists.size()==0){
            booksAdapter.setEmptyView(emptyView);
            booksAdapter.notifyDataSetChanged();
        }

        if (myBooksLists!=null && myBooksLists.size()>0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < myBooksLists.size(); i++) {
                        HashMap hashMap = myBooksLists.get(i);
                        JsoupGet jsoupGet = new JsoupGet();
                        try {
                            List<List<HashMap>> cataLog = jsoupGet.getItemContent((String) hashMap.get("cataLog"));
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
                    handler.sendEmptyMessage(0);
                }
            }).start();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void getBooks(RefreshMyBooks refreshMyBooks){
        myBooksLists = myPreference.getListObject(saveInfo, HashMap.class);
        if (myBooksLists==null || myBooksLists.size()==0){
            booksAdapter.setEmptyView(emptyView);
        }else {
            booksAdapter.setNewData(myBooksLists);
        }
        booksAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myPreference.setObject(saveInfo,myBooksLists);
    }

    @Override
    public void onPause() {
        super.onPause();
        myPreference.setObject(saveInfo,myBooksLists);
    }
}
