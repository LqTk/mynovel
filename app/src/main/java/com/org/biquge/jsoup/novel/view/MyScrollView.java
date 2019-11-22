package com.org.biquge.jsoup.novel.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {

    private OnMyScrollChangeListener scrollChangeListener;

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("NewApi")
    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollChangeListener!=null) {
            scrollChangeListener.onScrollChange(this, l, t, oldl, oldt);
        }
    }

    public void setOnMyScrollChangeListener(OnMyScrollChangeListener listener){
        this.scrollChangeListener = listener;
    }

    public interface OnMyScrollChangeListener {
        void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY);
    }
}
