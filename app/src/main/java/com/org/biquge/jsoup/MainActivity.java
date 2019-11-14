package com.org.biquge.jsoup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.org.biquge.jsoup.novel.fragments.MyFragment;
import com.org.biquge.jsoup.novel.fragments.NovelsAllFragement;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.fl_main)
    FrameLayout flMain;
    @BindView(R.id.bnv_main)
    BottomNavigationView bnvMain;
    private Unbinder bind;
    private MyFragment myFragment;
    private NovelsAllFragement novelsAllFragement;
    private Fragment[] fragmentlist;
    private int lastFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bind = ButterKnife.bind(this);

        initFragment();
    }

    private void initFragment() {
        bnvMain.setItemIconTintList(null);

        myFragment = new MyFragment();
        novelsAllFragement = new NovelsAllFragement();

        fragmentlist = new Fragment[]{myFragment, novelsAllFragement};

        //此时标识首页
        //0表示首页，1依次推
        lastFragment = 0;

        bnvMain.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //设置默认页面
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_main,myFragment)
                .show(myFragment).commit();
        bnvMain.setSelectedItemId(R.id.navigation_my);
    }

    //给bottomnavigationview添加点击事件
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            //每次点击后都将所有图标重置到默认不选中图片
            resetTodefaultIcon();
            switch (item.getItemId()){
                case R.id.navigation_my:
                    if (lastFragment!=0){
                        switchFragment(0);
                    }
                    item.setIcon(R.drawable.my_books);
                    return true;
                case R.id.navigation_allnovels:
                    if (lastFragment!=1){
                        switchFragment(1);
                    }
                    item.setIcon(R.drawable.books);
                    return true;
            }
            return false;
        }
    };

    private void switchFragment(int index) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //隐藏上一个fragment
        transaction.hide(fragmentlist[lastFragment]);

        //判断transaction中是否加载过index对应的页面，若没加载过则加载
        if (fragmentlist[index].isAdded()==false){
            transaction.add(R.id.fl_main,fragmentlist[index]);
        }
        //根据角标将fragment显示出来
        transaction.show(fragmentlist[index]).commitAllowingStateLoss();

        lastFragment = index;
    }

    private void resetTodefaultIcon(){
        bnvMain.getMenu().findItem(R.id.navigation_my).setIcon(R.drawable.my_books_gray);
        bnvMain.getMenu().findItem(R.id.navigation_allnovels).setIcon(R.drawable.books_gray);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }
}
