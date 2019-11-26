package com.org.biquge.jsoup.novel.fragments.booksfrag;

import android.content.Context;
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

import com.org.biquge.jsoup.JsoupGet;
import com.org.biquge.jsoup.R;
import com.org.biquge.jsoup.novel.NovelPublic;
import com.org.biquge.jsoup.novel.adapter.GvHotAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AllBooksFragment extends Fragment {
    @BindView(R.id.rcv_hot)
    RecyclerView rcvHot;
    @BindView(R.id.rcv_recent)
    RecyclerView rcvRecent;
    Unbinder unbinder;
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
                    break;
            }
        }
    };
    private List<HashMap> books3 =new ArrayList<>();
    private GvHotAdapter hotAdapter;

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
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcvHot.setLayoutManager(layoutManager);
        hotAdapter = new GvHotAdapter(R.layout.gv_hot,books3);
        rcvHot.setAdapter(hotAdapter);
    }

    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<List<HashMap>> getBooks3 = jsoupGet.getBooks3(NovelPublic.getHomeUrl(3));
                    if (getBooks3.get(0)!=null) {
                        books3.clear();
                        books3.addAll(getBooks3.get(0));
                    }
                    handler.sendEmptyMessage(0);
                } catch (IOException e) {
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
