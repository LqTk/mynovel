package com.org.biquge.jsoup.novel.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class LinearLayoutScroll extends LinearLayoutManager {

    public LinearLayoutScroll(Context context) {
        super(context);
    }

    public LinearLayoutScroll(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public LinearLayoutScroll(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        SnapSmoothScroller snapSmoothScroller = new SnapSmoothScroller(recyclerView.getContext(),this);
        snapSmoothScroller.setTargetPosition(position);
        startSmoothScroll(snapSmoothScroller);
    }

}
