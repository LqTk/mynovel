package com.org.biquge.jsoup.novel.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyRecycleViewBar extends android.support.v7.widget.AppCompatImageView {

    private float lastX = 0;
    private float lastY = 0;

    private float dx;
    private float dy;
    private float movex = 0;
    private float movey = 0;

    private int screenWidth;
    private int screenHeight;

    public MyRecycleViewBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSize(int width,int height){
        screenWidth = width;
        screenHeight = height;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastY = event.getRawY();
                lastX = event.getRawX();
                movey = lastY;
                movex = lastX;
                break;
            case MotionEvent.ACTION_MOVE:
                dy = event.getRawY() - lastY;
                dx = event.getRawX() - lastX;

                int left = (int) (getLeft()+dx);
                int right = (int) (getRight()+dy);
                int top = (int) (getTop() + dy);
                int bottom = (int) (getBottom() + dy);
                if (top<0){
                    top = 0;
                    bottom = top + getHeight();
                }
                if (bottom>screenHeight){
                    bottom = screenHeight;
                    top = bottom - getHeight();
                }
                if (left<0){
                    left = 0;
                    right = left + getWidth();
                }
                if (right > screenWidth){
                    right = screenWidth;
                    left = right - getWidth();
                }
                layout(left,top,right,bottom);
                lastX = event.getRawX();
                lastY = event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                if ((int) (event.getRawX()-movex)!=0
                        || (int) (event.getRawY() - movey) != 0){
                    return true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}
