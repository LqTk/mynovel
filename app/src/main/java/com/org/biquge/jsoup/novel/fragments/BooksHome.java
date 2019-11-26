package com.org.biquge.jsoup.novel.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.githang.statusbar.StatusBarCompat;
import com.org.biquge.jsoup.MyPreference;
import com.org.biquge.jsoup.R;
import com.org.biquge.jsoup.novel.adapter.RcvBooksPageAdapter;
import com.org.biquge.jsoup.novel.entities.BooksPageEntity;
import com.org.biquge.jsoup.novel.events.RefreshTheme;
import com.org.biquge.jsoup.novel.fragments.booksfrag.AllBooksFragment;
import com.org.biquge.jsoup.novel.fragments.booksfrag.BooksPage1;
import com.org.biquge.jsoup.novel.fragments.booksfrag.BooksPage2;
import com.org.biquge.jsoup.novel.fragments.booksfrag.BooksPage3;
import com.org.biquge.jsoup.novel.fragments.booksfrag.BooksPage4;
import com.org.biquge.jsoup.novel.fragments.booksfrag.BooksPage5;
import com.org.biquge.jsoup.novel.fragments.booksfrag.BooksPage6;
import com.org.biquge.jsoup.novel.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.org.biquge.jsoup.MyPreference.themeNum;

/**
 * A simple {@link Fragment} subclass.
 */
public class BooksHome extends Fragment {

    @BindView(R.id.rl_top)
    RelativeLayout rlTop;
    Unbinder unbinder;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.rcv_page)
    RecyclerView rcvPage;

    private MyPreference myPreference;
    private Context context;
    private List<BooksPageEntity> pageEntityList = new ArrayList<>();
    private RcvBooksPageAdapter pageAdapter;
    private Fragment[] fragments;
    private AllBooksFragment allBooksFragment;
    private BooksPage1 booksPage1;
    private BooksPage2 booksPage2;
    private BooksPage3 booksPage3;
    private BooksPage4 booksPage4;
    private BooksPage5 booksPage5;
    private BooksPage6 booksPage6;

    private String[] pageName = {"全部", "玄幻奇幻", "武侠仙侠", "都市言情", "历史军事", "科幻灵异", "完本小说"};
    private int lastFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.books_home_layout, container, false);
        EventBus.getDefault().register(this);
        context = getContext();
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        myPreference = MyPreference.getInstance();
        myPreference.setPreference(context);
        refreshTheme(new RefreshTheme());

        initData();
        initView();
        initFragment();
    }

    private void initFragment() {
        allBooksFragment = new AllBooksFragment();
        booksPage1 = new BooksPage1();
        booksPage2 = new BooksPage2();
        booksPage3 = new BooksPage3();
        booksPage4 = new BooksPage4();
        booksPage5 = new BooksPage5();
        booksPage6 = new BooksPage6();

        fragments = new Fragment[]{allBooksFragment,booksPage1,booksPage2,booksPage3,booksPage4,booksPage5,booksPage6};

        //此时标识首页
        //0表示首页，1依次推
        lastFragment = 0;

        //设置默认页面
        getChildFragmentManager().beginTransaction().replace(R.id.fl_main,allBooksFragment)
                .show(allBooksFragment).commit();
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcvPage.setLayoutManager(linearLayoutManager);
        setAdapter();
    }

    private void setAdapter() {
        pageAdapter = new RcvBooksPageAdapter(R.layout.rcv_page_adapter,pageEntityList);
        pageAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                clearAll();
                pageEntityList.get(position).setIscheck(true);
                pageAdapter.notifyDataSetChanged();
                ToastUtils.showShortMsg(context,"点击了："+pageEntityList.get(position).getName());
                switchFragment(position);
            }
        });

        rcvPage.setAdapter(pageAdapter);
    }

    private void initData() {
        for (int i = 0; i < pageName.length; i++) {
            if (i == 0) {
                pageEntityList.add(new BooksPageEntity(pageName[i], "", true));
            } else {
                pageEntityList.add(new BooksPageEntity(pageName[i], "", false));
            }
        }
    }

    private void clearAll(){
        for (int i=0;i<pageEntityList.size();i++){
            pageEntityList.get(i).setIscheck(false);
        }
    }

    private void switchFragment(int index) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        //隐藏上一个fragment
        transaction.hide(fragments[lastFragment]);

        //判断transaction中是否加载过index对应的页面，若没加载过则加载
        if (fragments[index].isAdded()==false){
            transaction.add(R.id.fl_main,fragments[index]);
        }
        //根据角标将fragment显示出来
        transaction.show(fragments[index]).commitAllowingStateLoss();

        lastFragment = index;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.iv_back, R.id.bt_search_item})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                break;
            case R.id.bt_search_item:
                break;
        }
    }
}
