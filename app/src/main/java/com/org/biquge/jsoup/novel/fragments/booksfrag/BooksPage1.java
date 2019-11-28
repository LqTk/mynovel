package com.org.biquge.jsoup.novel.fragments.booksfrag;

import android.content.Context;
import android.content.Intent;
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

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.org.biquge.jsoup.JsoupGet;
import com.org.biquge.jsoup.R;
import com.org.biquge.jsoup.novel.NovelPublic;
import com.org.biquge.jsoup.novel.activity.NovelItem;
import com.org.biquge.jsoup.novel.activity.NovelReadItem;
import com.org.biquge.jsoup.novel.adapter.AllBookRecentAdapter;
import com.org.biquge.jsoup.novel.adapter.GvHotAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BooksPage1 extends Fragment {
    @BindView(R.id.rcv_hot)
    RecyclerView rcvHot;
    @BindView(R.id.rcv_recent)
    RecyclerView rcvRecent;
    Unbinder unbinder;
    @BindView(R.id.ll_pro)
    LinearLayout llPro;
    @BindView(R.id.rl_failed)
    RelativeLayout rlFailed;
    @BindView(R.id.srf)
    SmartRefreshLayout srf;
    private Context context;
    JsoupGet jsoupGet = new JsoupGet();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (books3 != null) {
                        hotAdapter.notifyDataSetChanged();
                    }
                    if (recentNew != null) {
                        recentAdapter.notifyDataSetChanged();
                    }
                    llPro.setVisibility(View.GONE);
                    break;
                case 1:
                    llPro.setVisibility(View.GONE);
                    if (books3.size()==0) {
                        rlFailed.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    };
    private List<HashMap> books3 = new ArrayList<>();
    private List<HashMap> recentNew = new ArrayList<>();
    private GvHotAdapter hotAdapter;
    private AllBookRecentAdapter recentAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_books_fragment, null);
        context = getContext();
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView();
        getData();
    }

    private void initView() {
        srf.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getData();
            }
        });
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(context);
        layoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcvHot.setLayoutManager(layoutManager1);
        hotAdapter = new GvHotAdapter(R.layout.gv_hot, books3);
        rcvHot.setAdapter(hotAdapter);

        hotAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(context, NovelItem.class);
                intent.putExtra("url", String.valueOf(books3.get(position).get("bookUrl")));
                startActivity(intent);
            }
        });

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(context);
        layoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        rcvRecent.setLayoutManager(layoutManager2);
        recentAdapter = new AllBookRecentAdapter(R.layout.all_book_recent, recentNew);
        rcvRecent.setAdapter(recentAdapter);
        recentAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()){
                    case R.id.tv_book_name:
                        Intent intent = new Intent(context, NovelItem.class);
                        intent.putExtra("url", String.valueOf(recentNew.get(position).get("nameurl")));
                        startActivity(intent);
                        break;
                    case R.id.tv_recent_chapter:
                        Bundle bundle = new Bundle();
                        bundle.putString("url", NovelPublic.getHomeUrl(3) + recentNew.get(position).get("chapterUrl"));
                        bundle.putBoolean("isAdded", false);
                        bundle.putString("from", "noveitem");
                        bundle.putString("cataLog",String.valueOf(recentNew.get(position).get("nameurl")));
                        bundle.putString("bookName", (String) recentNew.get(position).get("name"));
                        bundle.putString("title", (String) recentNew.get(position).get("name"));
                        bundle.putString("author",(String) recentNew.get(position).get("author"));

                        Intent intent1 = new Intent(context, NovelReadItem.class);
                        intent1.putExtras(bundle);
                        startActivity(intent1);
                        break;
                }
            }
        });
    }

    private void getData() {
        rlFailed.setVisibility(View.GONE);
        llPro.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<List<HashMap>> getBooks3 = jsoupGet.getBooksPage(NovelPublic.getHomeUrl(3)+"/xuanhuanxiaoshuo/");
                    if (getBooks3.get(0) != null) {
                        books3.clear();
                        books3.addAll(getBooks3.get(0));
                    }
                    if (getBooks3.get(1) != null) {
                        recentNew.clear();
                        recentNew.addAll(getBooks3.get(1));
                    }
                    handler.sendEmptyMessage(0);
                } catch (IOException e) {
                    handler.sendEmptyMessage(1);
                    e.printStackTrace();
                }
                srf.finishRefresh();
            }
        }).start();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
