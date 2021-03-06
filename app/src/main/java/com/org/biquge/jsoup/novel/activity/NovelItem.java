package com.org.biquge.jsoup.novel.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.githang.statusbar.StatusBarCompat;
import com.org.biquge.jsoup.JsoupGet;
import com.org.biquge.jsoup.MyPreference;
import com.org.biquge.jsoup.R;
import com.org.biquge.jsoup.fastscroller.RecyclerFastScroller;
import com.org.biquge.jsoup.novel.NovelPublic;
import com.org.biquge.jsoup.novel.adapter.NovelItemAdapter;
import com.org.biquge.jsoup.novel.entities.DownLoadEntity;
import com.org.biquge.jsoup.novel.events.RefreshMyBooks;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.org.biquge.jsoup.MyPreference.saveInfo;

public class NovelItem extends AppCompatActivity {

    @BindView(R.id.iv_item)
    ImageView ivItem;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_author_name)
    TextView tvAuthorName;
    @BindView(R.id.rcv_item)
    RecyclerView rcvItem;
    @BindView(R.id.tv_recent_time)
    TextView tvRecentTime;
    @BindView(R.id.tv_recent_content)
    TextView tvRecentContent;
    @BindView(R.id.ll_pro)
    LinearLayout llPro;
    @BindView(R.id.fast_scroll)
    RecyclerFastScroller fastScroll;

    JsoupGet jsoupGet = new JsoupGet();
    Unbinder binder;
    Context context;
    NovelItemAdapter novelItemAdapter;
    MyPreference myPreference;

    List<HashMap> itemsList = new ArrayList<>();
    HashMap itemsMap = new HashMap();
    List<HashMap> infoList = new ArrayList<>();
    HashMap authorMap = new HashMap();

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    boolean isHave = false;
                    if (saveBookLists != null) {
                        for (int i = 0; i < saveBookLists.size(); i++) {
                            HashMap cMap = saveBookLists.get(i);
                            if (cMap.get("title").equals(authorMap.get("title"))
                                    && cMap.get("author").equals(authorMap.get("author"))) {
                                isHave = true;
                                tvAddbook.setTextColor(Color.parseColor("#999999"));
                                tvAddbook.setText("已加入图书");
                                tvAddbook.setEnabled(false);
                                break;
                            }
                        }
                    } else {
                        tvAddbook.setText("加入图书");
                    }
                    if (!isHave) {
                        tvAddbook.setText("加入图书");
                    }
                    if (authorMap.size() > 0) {
                        llPro.setVisibility(View.GONE);
                    }
                    Glide.with(context)
                            .load(authorMap.get("img"))
                            .apply(NovelPublic.errorOptions())
                            .into(ivItem);
                    tvName.setText((CharSequence) authorMap.get("title"));
                    tvAuthorName.setText((CharSequence) authorMap.get("author"));
                    tvRecentTime.setText((CharSequence) authorMap.get("time"));
                    tvRecentContent.setText((CharSequence) authorMap.get("recentString"));
                    novelItemAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    llAdd.setVisibility(View.GONE);
                    tvAddbook.setTextColor(Color.parseColor("#999999"));
                    tvAddbook.setText("已加入图书");
                    tvAddbook.setEnabled(false);
                    break;
            }

        }
    };

    @BindView(R.id.tv_addbook)
    TextView tvAddbook;
    @BindView(R.id.ll_add)
    LinearLayout llAdd;
    private List<HashMap> saveBookLists = new ArrayList<>();
    private String cataLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novel_item);
        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#00ffffff"));
        binder = ButterKnife.bind(this);
        context = this;
        myPreference = MyPreference.getInstance();
        myPreference.setPreference(context);

        saveBookLists = myPreference.getListObject(saveInfo, HashMap.class);
        cataLog = getIntent().getExtras().getString("url");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcvItem.setLayoutManager(linearLayoutManager);

        initView();
        getItemContent(cataLog);
    }

    private void initView(){

        novelItemAdapter = new NovelItemAdapter(R.layout.novel_item, itemsList);
        novelItemAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                Bundle bundle = new Bundle();
                bundle.putString("url", String.valueOf(itemsList.get(position).get("href")));
                bundle.putBoolean("isAdded", tvAddbook.getText().toString().equals("已加入图书"));
                bundle.putString("from", "noveitem");
                bundle.putString("cataLog", cataLog);
                bundle.putString("title", (String) authorMap.get("title"));
                bundle.putString("author", (String) authorMap.get("author"));

                Intent intent = new Intent(context, NovelReadItem.class);
                intent.putExtras(bundle);
                startActivity(intent);
                NovelItem.this.finish();
            }
        });
        rcvItem.setAdapter(novelItemAdapter);
        fastScroll.attachRecyclerView(rcvItem);

        fastScroll.setTouchTargetWidth(25);
        fastScroll.setMarginLeft(10);
        fastScroll.setBarColor(getResources().getColor(R.color.transparent));
        fastScroll.setHidingEnabled(false);
        fastScroll.touchIsDrawable(true);

    }

    private void getItemContent(final String href) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<List<HashMap>> itemContent = jsoupGet.getPageContent(href);
                    infoList = itemContent.get(0);
                    authorMap = infoList.get(0);
                    itemsList.addAll(itemContent.get(1));
                    itemsMap = itemsList.get(0);
                    mHandler.sendEmptyMessage(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binder.unbind();
    }

    @OnClick({R.id.tv_addbook, R.id.ll_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_addbook:llAdd.setVisibility(View.VISIBLE);
                if (saveBookLists == null)
                    saveBookLists = new ArrayList<>();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        authorMap.put("chapter", String.valueOf(itemsMap.get("href")));
                        authorMap.put("lastPage", 1);
                        authorMap.put("chapters", JSON.toJSONString(itemsList));
                        authorMap.put("cataLog", cataLog);
                        authorMap.put("hasNew", false);
                        DownLoadEntity downLoadEntity = new DownLoadEntity(0, itemsList.size(), 0,
                                cataLog, (String) itemsList.get(0).get("href"), 0);
                        authorMap.put("downLoadInfo", JSON.toJSONString(downLoadEntity));
                        saveBookLists.add(authorMap);
                        myPreference.setObject(saveInfo, saveBookLists);
                        mHandler.sendEmptyMessage(1);
                        EventBus.getDefault().post(new RefreshMyBooks());
                    }
                }).start();
                break;
            case R.id.ll_add:
                break;
        }
    }
}
