package com.org.biquge.jsoup.novel.view;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.org.biquge.jsoup.R;
import com.org.biquge.jsoup.novel.adapter.PageAdapter;
import com.org.biquge.jsoup.novel.adapter.ScanViewAdapter;

/**
 * @author chenjing
 */

public class ScanView extends RelativeLayout {

    public static final String TAG = "ScanView";
    private boolean isInit = true;
    // 滑动的时候存在两页可滑动，要判断是哪一页在滑动
    private boolean isPreMoving = true, isCurrMoving = true;
    // 当前是第几页
    private int index;
    private float lastX;
    private float lastY;
    // 前一页，当前页，下一页的左边位置
    private int prePageLeft = 0, currPageLeft = 0, nextPageLeft = 0;
    // 三张页面
    private View prePage, currPage, nextPage;
    // 页面状态
    private static final int STATE_MOVE = 0;
    private static final int STATE_STOP = 1;
    // 滑动的页面，只有前一页和当前页可滑
    private static final int PRE = 2;
    private static final int CURR = 3;
    private int state = STATE_STOP;
    // 正在滑动的页面右边位置，用于绘制阴影
    private float right;
    // 手指滑动的距离
    private float moveLenght;
    // 页面宽高
    private int mWidth, mHeight;
    // 获取滑动速度
    private VelocityTracker vt;
    // 防止抖动
    private float speed_shake = 30;
    // 当前滑动速度
    private float speed;
    private Timer timer;
    private MyTimerTask mTask;
    // 滑动动画的移动速度
    public static final int MOVE_SPEED = 15;
    // 页面适配器
    private PageAdapter adapter;
    /**
     * 过滤多点触碰的控制变量
     */
    private int mEvents;

    private OnPageListener pageListener;
    private SavePageListener savePageListener;
    private ScreenClick screenClick;

    private long downTime=0l;
    private long upTime=0l;
    private int scrollPosition;

    public void setScreenClick(ScreenClick click){
        this.screenClick = click;
    }

    public void setPageListener(OnPageListener listener){
        this.pageListener = listener;
    }

    public void setSavePageListener(SavePageListener listener){
        this.savePageListener = listener;
    }

    public void setAdapter(ScanViewAdapter adapter, int currentIndex, int scrollPosition) {
        removeAllViews();
        index = currentIndex;
        this.adapter = adapter;
        this.scrollPosition = scrollPosition;
        prePage = adapter.getView();
        addView(prePage, 0, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        adapter.addContent(prePage, index - 1);
        currPage = adapter.getView();
        addView(currPage, 0, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        setScrollTo();
        adapter.addContent(currPage, index);
        nextPage = adapter.getView();
        addView(nextPage, 0, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        adapter.addContent(nextPage, index + 1);
    }

    private void setScrollTo(){
        if (adapter.getOrientation()==1){
            final MyScrollView scrollView = currPage.findViewById(R.id.slv_content);
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.scrollTo(0,(scrollPosition-1)*scrollView.getHeight());
                }
            });
            scrollView.setOnMyScrollChangeListener(new MyScrollView.OnMyScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    screenClick.onScrollListener(scrollY,scrollView.getHeight());
                }
            });
        }
    }

    /**
     * 向左滑。注意可以滑动的页面只有当前页和前一页
     *
     * @param which
     */
    private void moveLeft(int which) {
        switch (which) {
            case PRE:
                prePageLeft -= MOVE_SPEED;
                if (prePageLeft < -mWidth)
                    prePageLeft = -mWidth;
                right = mWidth + prePageLeft;
                break;
            case CURR:
                currPageLeft -= MOVE_SPEED;
                if (currPageLeft < -mWidth)
                    currPageLeft = -mWidth;
                right = mWidth + currPageLeft;
                break;
        }
    }

    /**
     * 向右滑。注意可以滑动的页面只有当前页和前一页
     *
     * @param which
     */
    private void moveRight(int which) {
        switch (which) {
            case PRE:
                prePageLeft += MOVE_SPEED;
                if (prePageLeft > 0)
                    prePageLeft = 0;
                right = mWidth + prePageLeft;
                break;
            case CURR:
                currPageLeft += MOVE_SPEED;
                if (currPageLeft > 0)
                    currPageLeft = 0;
                right = mWidth + currPageLeft;
                break;
        }
    }
    /**
     * 当往回翻过一页时添加前一页在最左边
     */
    private void addPrePage() {
        removeView(nextPage);
        addView(nextPage, -1, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        // 从适配器获取前一页内容
        adapter.addContent(nextPage, index - 1);
        savePageListener.saveNowPage(index);
        // 交换顺序
        View temp = nextPage;
        nextPage = currPage;
        currPage = prePage;
        prePage = temp;
        prePageLeft = -mWidth;
    }

    /**
     * 当往前翻过一页时，添加一页在最底下
     */
    private void addNextPage() {
        removeView(prePage);
        addView(prePage, 0, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        // 从适配器获取后一页内容
        adapter.addContent(prePage, index + 1);
        savePageListener.saveNowPage(index);
        // 交换顺序
        View temp = currPage;
        currPage = nextPage;
        nextPage = prePage;
        prePage = temp;
        currPageLeft = 0;
    }
    Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (state != STATE_MOVE)
                return;
            // 移动页面
            // 翻回，先判断当前哪一页处于未返回状态
            if (prePageLeft > -mWidth && speed <= 0) {
                // 前一页处于未返回状态
                moveLeft(PRE);
            } else if (currPageLeft < 0 && speed >= 0) {
                // 当前页处于未返回状态
                moveRight(CURR);
            } else if (speed < 0 && index < adapter.getCount()) {
                // 向左翻，翻动的是当前页
                moveLeft(CURR);
                if (currPageLeft == (-mWidth)) {
                    index++;
                    // 翻过一页，在底下添加一页，把最上层页面移除
                    addNextPage();
                }
            } else if (speed > 0 && index > 1) {
                // 向右翻，翻动的是前一页
                moveRight(PRE);
                if (prePageLeft == 0) {
                    index--;
                    // 翻回一页，添加一页在最上层，隐藏在最左边
                    addPrePage();
                }
            }
            if (right == 0 || right == mWidth) {
                releaseMoving();
                state = STATE_STOP;
                quitMove();
            }
            ScanView.this.requestLayout();
        }
    };

    public ScanView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ScanView(Context context) {
        super(context);
        init();
    }

    public ScanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 退出动画翻页
     */
    public void quitMove() {
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
    }

    private void init() {
        index = 1;
        timer = new Timer();
        mTask = new MyTimerTask(updateHandler);
    }

    /**
     * 释放动作，不限制手滑动方向
     */
    private void releaseMoving() {
        isPreMoving = true;
        isCurrMoving = true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (adapter != null)
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = event.getX();
                    lastY = event.getY();

                    WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
                    int screenWidth = wm.getDefaultDisplay().getWidth();
                    int screenHeight = wm.getDefaultDisplay().getHeight();
                    if (lastX>screenWidth/3&&lastX<screenWidth/3*2) {
                        downTime = System.currentTimeMillis();
                    }
                    try {
                        if (vt == null) {
                            vt = VelocityTracker.obtain();
                        } else {
                            vt.clear();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    vt.addMovement(event);
                    mEvents = 0;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                case MotionEvent.ACTION_POINTER_UP:
                    mEvents = -1;
                    break;
                case MotionEvent.ACTION_MOVE:
                    // 取消动画
                    quitMove();
                    Log.d("index", "mEvents = " + mEvents + ", isPreMoving = "
                            + isPreMoving + ", isCurrMoving = " + isCurrMoving);
                    vt.addMovement(event);
                    vt.computeCurrentVelocity(500);
                    speed = vt.getXVelocity();
                    if (adapter.getOrientation()==0) {
                        moveLenght = event.getX() - lastX;
                    }else {
                        moveLenght = event.getY() - lastY;
                    }
                    if ((moveLenght > 0 || !isCurrMoving) && isPreMoving
                            && mEvents == 0) {
                        isPreMoving = true;
                        isCurrMoving = false;
                        lastPage();
                    } else if ((moveLenght < 0 || !isPreMoving) && isCurrMoving
                            && mEvents == 0) {
                        isPreMoving = false;
                        isCurrMoving = true;
                        nextPage();
                    } else
                        mEvents = 0;
                    lastX = event.getX();
                    requestLayout();
                    break;
                case MotionEvent.ACTION_UP:
                    upTime = System.currentTimeMillis();
                    if (upTime-downTime<70){
                        if (screenClick!=null){
                            screenClick.onClick();
                        }
                    }
                    actionUp();
                    break;
                default:
                    break;
            }
        try {
            super.dispatchTouchEvent(event);
        }catch (IllegalStateException e){
            e.printStackTrace();
        }
        return true;
    }
    /*
     * （非 Javadoc） 在这里绘制翻页阴影效果
     * @see android.view.ViewGroup#dispatchDraw(android.graphics.Canvas)
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (right == 0 || right == mWidth)
            return;
        RectF rectF = new RectF(right, 0, mWidth, mHeight);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        LinearGradient linearGradient = new LinearGradient(right, 0,
                right + 36, 0, 0xffbbbbbb, 0x00bbbbbb, TileMode.CLAMP);
        paint.setShader(linearGradient);
        paint.setStyle(Style.FILL);
        canvas.drawRect(rectF, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        if (isInit) {
            // 初始状态，一页放在左边隐藏起来，两页叠在一块
            prePageLeft = -mWidth;
            currPageLeft = 0;
            nextPageLeft = 0;
            isInit = false;
        }
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (adapter == null)
            return;
        prePage.layout(prePageLeft, 0,
                prePageLeft + prePage.getMeasuredWidth(),
                prePage.getMeasuredHeight());
        currPage.layout(currPageLeft, 0,
                currPageLeft + currPage.getMeasuredWidth(),
                currPage.getMeasuredHeight());
        nextPage.layout(nextPageLeft, 0,
                nextPageLeft + nextPage.getMeasuredWidth(),
                nextPage.getMeasuredHeight());
        invalidate();
    }

    class MyTimerTask extends TimerTask {
        Handler handler;
        public MyTimerTask(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            handler.sendMessage(handler.obtainMessage());
        }
    }

    private void lastPage(){
        if (index == 1) {
            // 第一页不能再往右翻，跳转到前一个activity
            state = STATE_MOVE;
            releaseMoving();

            MyScrollView scrollView = currPage.findViewById(R.id.slv_content);
            float sy = scrollView.getScrollY();
            if (sy==0) {
                if (pageListener!=null)
                    pageListener.lastChapter();
            }
        } else {
            // 非第一页
            prePageLeft += (int) moveLenght;
            // 防止滑过边界
            if (prePageLeft > 0)
                prePageLeft = 0;
            else if (prePageLeft < -mWidth) {
                // 边界判断，释放动作，防止来回滑动导致滑动前一页时当前页无法滑动
                prePageLeft = -mWidth;
                releaseMoving();
            }
            right = mWidth + prePageLeft;
            state = STATE_MOVE;
        }
    }

    private void nextPage(){
        if (index == adapter.getCount()) {
            // 最后一页不能再往左翻
            state = STATE_STOP;
            releaseMoving();
            MyScrollView scrollView = currPage.findViewById(R.id.slv_content);
            View view = scrollView.getChildAt(0);
            int vh = view.getMeasuredHeight();
            float sh1 = scrollView.getHeight();
            float sh2 = scrollView.getScrollY();
            float sy = sh1+sh2;
            if (sh2!=0&&vh<=sy) {
                if (pageListener!=null)
                    pageListener.nextChapter();
            }
        } else {
            currPageLeft += (int) moveLenght;
            // 防止滑过边界
            if (currPageLeft < -mWidth)
                currPageLeft = -mWidth;
            else if (currPageLeft > 0) {
                // 边界判断，释放动作，防止来回滑动导致滑动当前页是前一页无法滑动
                currPageLeft = 0;
                releaseMoving();
            }
            right = mWidth + currPageLeft;
            state = STATE_MOVE;
        }
//        actionUp();
    }

    private void actionUp(){
        if (Math.abs(speed) < speed_shake)
            speed = 0;
        quitMove();
        mTask = new MyTimerTask(updateHandler);
        timer.schedule(mTask, 0, 5);
        try {
            vt.clear();
            vt.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void keyDown(int code) {
        Log.d("keyDown","code="+code);
        switch (code){
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                nextPage();
                if (index != adapter.getCount()) {
                    index++;
                    // 翻过一页，在底下添加一页，把最上层页面移除
                    addNextPage();
                }
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (index == 1) {
                    // 第一页不能再往右翻，跳转到前一个activity
                    state = STATE_MOVE;
                    releaseMoving();
                    if (pageListener!=null)
                        pageListener.lastChapter();
                }else {
                    lastPage();
                    index--;
                    addPrePage();
                }
                break;
        }
    }

    public interface OnPageListener{
        void lastChapter();
        void nextChapter();
    }

    public interface SavePageListener {
        void saveNowPage(int page);
    }

    public interface ScreenClick{
        void onClick();
        void onScrollListener(int scrollPosition,int scrollHeight);
    }

    public void resetBg(int bgId){
        if (prePage!=null){
            prePage.findViewById(R.id.rl_scanView).setBackground(getResources().getDrawable(bgId));
        }
        if (currPage!=null){
            currPage.findViewById(R.id.rl_scanView).setBackground(getResources().getDrawable(bgId));
        }
        if (nextPage!=null){
            nextPage.findViewById(R.id.rl_scanView).setBackground(getResources().getDrawable(bgId));
        }
    }
}
