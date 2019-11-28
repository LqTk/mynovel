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
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.org.biquge.jsoup.JsoupGet;
import com.org.biquge.jsoup.R;
import com.org.biquge.jsoup.novel.NovelPublic;
import com.org.biquge.jsoup.novel.activity.NovelItem;
import com.org.biquge.jsoup.novel.adapter.NovelItemAdapter;
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

public class BooksPage7 extends Fragment {
    @BindView(R.id.rcv_all)
    RecyclerView rcvAll;
    @BindView(R.id.tv_refresh)
    TextView tvRefresh;
    @BindView(R.id.ll_pro)
    LinearLayout llPro;
    Unbinder unbinder;
    @BindView(R.id.srf)
    SmartRefreshLayout srf;

    JsoupGet jsoupGet = new JsoupGet();
    private Context context;
    private List<HashMap> noveList=new ArrayList<>();
    private NovelItemAdapter novelFullAdapter;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    if (noveList!=null) {
                        novelFullAdapter.notifyDataSetChanged();
                    }
                    llPro.setVisibility(View.GONE);
                    break;
                case 1:
                    noveList.clear();
                    novelFullAdapter.notifyDataSetChanged();
                    llPro.setVisibility(View.GONE);
                    tvRefresh.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.full_books, null);
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcvAll.setLayoutManager(layoutManager);

        srf.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getData();
            }
        });

        novelFullAdapter = new NovelItemAdapter(R.layout.novel_item, noveList);

        novelFullAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(context, NovelItem.class);
                intent.putExtra("url", String.valueOf(noveList.get(position).get("bookUrl")));
                startActivity(intent);
            }
        });

        rcvAll.setAdapter(novelFullAdapter);
    }

    private void getData() {
        llPro.setVisibility(View.VISIBLE);
        tvRefresh.setVisibility(View.GONE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<HashMap> getList = jsoupGet.getFullNovel(NovelPublic.getHomeUrl(3)+"/xiaoshuodaquan/");
                    if (getList!=null){
                        noveList.clear();
                        noveList.addAll(getList);
                    }
                    srf.finishRefresh();
                    handler.sendEmptyMessage(0);
                } catch (IOException e) {
                    handler.sendEmptyMessage(1);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
