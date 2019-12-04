package com.org.biquge.jsoup.novel.view;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearSmoothScroller;
import android.util.DisplayMetrics;
import android.util.Log;

public class SnapSmoothScroller extends LinearSmoothScroller {
    LinearLayoutScroll linearLayoutScroll;

    public SnapSmoothScroller(Context context, LinearLayoutScroll linearLayoutScroll) {
        super(context);
        this.linearLayoutScroll = linearLayoutScroll;
    }

    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        if (linearLayoutScroll!=null){
            return linearLayoutScroll.computeScrollVectorForPosition(targetPosition);
        }
        return null;
    }

    @Override
    protected int getVerticalSnapPreference() {
        return SNAP_TO_START;
    }

    @Override
    protected int calculateTimeForScrolling(int dx) {
        Log.d("scrolling dx",dx+",,,,,,,");
        dx = 550;
        return super.calculateTimeForScrolling(dx);
    }

}
