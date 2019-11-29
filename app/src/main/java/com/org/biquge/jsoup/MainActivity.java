package com.org.biquge.jsoup;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.githang.statusbar.StatusBarCompat;
import com.org.biquge.jsoup.novel.events.RefreshTheme;
import com.org.biquge.jsoup.novel.fragments.BooksHome;
import com.org.biquge.jsoup.novel.fragments.MyFragment;
import com.org.biquge.jsoup.novel.fragments.NovelsAllFragement;
import com.org.biquge.jsoup.novel.thread.DownLoadTask;
import com.org.biquge.jsoup.novel.utils.ToastUtils;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.org.biquge.jsoup.MyPreference.themeNum;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.fl_main)
    FrameLayout flMain;
    @BindView(R.id.bnv_main)
    BottomNavigationView bnvMain;
    private Unbinder bind;
    private MyFragment myFragment;
    private NovelsAllFragement novelsAllFragement;
    private BooksHome booksHome;
    private Fragment[] fragmentlist;
    private int lastFragment;
    private long exitTime=0l;
    Context context;
    MyPreference myPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                |WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_main);
        bind = ButterKnife.bind(this);
        context = this;
        myPreference = MyPreference.getInstance();
        myPreference.setPreference(context);
        refreshTheme(new RefreshTheme());

        initFragment();
        requestMyPermission();
    }

    private void requestMyPermission() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MainActivity.this,permissions,0);
            }
        }
    }

    private void initFragment() {
        bnvMain.setItemIconTintList(null);

        myFragment = new MyFragment();
        novelsAllFragement = new NovelsAllFragement();
        booksHome = new BooksHome();

        fragmentlist = new Fragment[]{myFragment, booksHome};

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

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis()-exitTime<1500){
            if (DownLoadTask.threadList!=null){
                DownLoadTask.stopAll();
            }
            super.onBackPressed();
        }else {
            exitTime = System.currentTimeMillis();
            ToastUtils.showShortMsg(context,"再按一次退出");
        }
    }

    @Subscribe
    public void refreshTheme(RefreshTheme theme){
        int themeId = myPreference.getInt(themeNum,0);
        if (themeId==0) {
            themeId=R.color.theme_blue;
            myPreference.setInt(themeNum,themeId);
        }
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(themeId));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 0:
                boolean permissionOk = true;
                for (Integer reslut:grantResults){
                    if (reslut!=PackageManager.PERMISSION_GRANTED){
                        permissionOk = false;
                        break;
                    }
                }
                if (!permissionOk){
                    ToastUtils.showShortMsg(context,"请先去设置页面允许权限哦~");
                }
                break;
        }
    }
}
