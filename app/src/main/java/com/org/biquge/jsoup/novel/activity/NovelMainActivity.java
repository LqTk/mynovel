package com.org.biquge.jsoup.novel.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.githang.statusbar.StatusBarCompat;
import com.org.biquge.jsoup.JsoupGet;
import com.org.biquge.jsoup.R;
import com.org.biquge.jsoup.novel.NovelPublic;
import com.org.biquge.jsoup.novel.adapter.NovelItemAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class NovelMainActivity extends AppCompatActivity {

    JsoupGet jsoupGet = new JsoupGet();
    NovelItemAdapter novelItemAdapter;

    Handler getNetWorkHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    final List<HashMap> noveList = (List<HashMap>) msg.obj;
                    novelItemAdapter = new NovelItemAdapter(R.layout.novel_item, noveList);

                    novelItemAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                            Intent intent = new Intent(context, NovelItem.class);
                            intent.putExtra("url", String.valueOf(noveList.get(position).get("href")));
                            startActivity(intent);
                        }
                    });

                    rcvNovel.setAdapter(novelItemAdapter);
                    break;
            }
        }
    };

    @BindView(R.id.rcv_novel)
    RecyclerView rcvNovel;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.rl_search)
    RelativeLayout rlSearch;

    private List<HashMap> novelContent;
    private Context context;
    private Unbinder bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novel_main);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.blue_main));
        bind = ButterKnife.bind(this);
        context = this;

        initView();
        getData();
    }

    private void initView() {
        tvTitle.setText("所有小说");
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcvNovel.setLayoutManager(layoutManager);
    }

    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    novelContent = jsoupGet.getHtmlContent(NovelPublic.novelsContent);
                    Message message = Message.obtain();
                    message.what = 0;
                    message.obj = novelContent;
                    getNetWorkHandler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }

    @OnClick({R.id.iv_back, R.id.tv_title, R.id.iv_search,R.id.iv_delete_item, R.id.iv_search_item})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                break;
            case R.id.iv_search:
                rlSearch.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_delete_item:
                rlSearch.setVisibility(View.GONE);
                Message message = Message.obtain();
                message.what = 0;
                message.obj = novelContent;
                getNetWorkHandler.sendMessage(message);
                break;
            case R.id.iv_search_item:
                searchTitle();
                break;
        }
    }

    private void searchTitle() {
        String searchString = etSearch.getText().toString().trim();
        List<HashMap> searchList = new ArrayList<>();
        if (novelContent!=null && novelContent.size()>0) {
            for(int i=0;i<novelContent.size();i++){
                String title = (String) novelContent.get(i).get("name");
                if (title.contains(searchString)){
                    searchList.add(novelContent.get(i));
                }
            }
        }
        Message message = Message.obtain();
        message.what = 0;
        message.obj = searchList;
        getNetWorkHandler.sendMessage(message);
    }

}
