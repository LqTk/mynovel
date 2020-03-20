package com.org.biquge.jsoup.novel.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.org.biquge.jsoup.R;
import com.org.biquge.jsoup.novel.broadcastReceiver.BattaryBroadcast;

public class ScanViewAdapter extends PageAdapter{
    Context context;
    List<String> items = new ArrayList<>();
    AssetManager am;
    ChapterClicker chapterClicker;
    private View scanView;
    private RelativeLayout rl_scanView;
    private int scanViewBg;
    private int orientation;
    private String stringText;
    private GetPages pagesListener;

    public void setChapterClicker(ChapterClicker clicker){
        this.chapterClicker = clicker;
    }

    public ScanViewAdapter() {
    }

    public void setData(Context context, String stringText, int bgId, int orientation)
    {
        this.scanViewBg = bgId;
        this.context = context;
//        this.items = items;
        this.stringText = stringText;
        this.orientation = orientation;
        am = context.getAssets();
        items.clear();
        setItems();
    }

    public void setPagesListener(GetPages pagesListener) {
        this.pagesListener = pagesListener;
    }

    private void setItems() {
        List<String> itemsReslut = new ArrayList<>();
        TextView content = (TextView) this.getView().findViewById(R.id.content);
        TextPaint textPaint = content.getPaint();
        float textWidth = textPaint.measureText("宽")/2;
        int lineHeight = content.getLineHeight()/2;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int heigth = dm.heightPixels/2;
        int width = (int) (dm.widthPixels/2-textValueToDp(20));
        int textHeight = (int) (heigth-textValueToDp(15)-textValueToDp(23f));
        Paint pFont = new Paint();
        Rect rect = new Rect();
        int lineWidth = 0;
        int textOneHeight = 0;
        StringBuffer stringBuffer = new StringBuffer();
        pFont.getTextBounds("宽", 0, 1, rect);
        for (int i=0;i<stringText.length();i++){
            String oneChar = String.valueOf(stringText.charAt(i));
            if (textOneHeight==0 && oneChar.equals("\n") && TextUtils.isEmpty(stringBuffer.toString())){
            }else {
//                lineWidth += rect.width() + textValueToDp(5f);
                lineWidth += textWidth;
                stringBuffer.append(oneChar);
                if (lineWidth >= width || oneChar.equals("\n")) {
                    lineWidth = 0;
                    textOneHeight += lineHeight;
                    Log.d("textOneHeight", "textOneHeight=" + textOneHeight);
                    if ((textOneHeight + lineHeight) > textHeight) {
                        String lineText = stringBuffer.toString();
                        if (lineText.endsWith("\n")) {
                            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                        }
                        itemsReslut.add(stringBuffer.toString());
                        stringBuffer.setLength(0);
                        lineWidth = 0;
                        textOneHeight = 0;
                    }
                }
                if (i==stringText.length()-1 && !TextUtils.isEmpty(stringBuffer.toString())){
                    String lineText = stringBuffer.toString();
                    if (lineText.endsWith("\n")) {
                        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                    }
                    itemsReslut.add(stringBuffer.toString());
                    stringBuffer.setLength(0);
                    lineWidth = 0;
                    textOneHeight = 0;
                }
            }
        }
        if (orientation==0){
            items = itemsReslut;
        }else {
            items.add(stringText);
        }
        if (pagesListener!=null){
            pagesListener.allPagesCount(stringText,itemsReslut);
        }
    }

    public void addContent(View view, int position)
    {
        TextView content = (TextView) view.findViewById(R.id.content);
        TextView tv = (TextView) view.findViewById(R.id.index);
        if ((position - 1) < 0 || (position - 1) >= getCount())
            return;
        content.setText(items.get(position - 1));
        tv.setText("第"+position+"/"+items.size()+"页");
    }

    public int getCount()
    {
        return items.size();
    }

    public View getView()
    {
            scanView = LayoutInflater.from(context).inflate(R.layout.pagelayout,
                    null);
            rl_scanView = scanView.findViewById(R.id.rl_scanView);
            rl_scanView.setBackground(context.getResources().getDrawable(scanViewBg));
            TextView lastchapter = scanView.findViewById(R.id.tv_last_chapter);
            TextView nextchapter = scanView.findViewById(R.id.tv_next_chapter);
            lastchapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (chapterClicker != null) {
                        chapterClicker.lastChapterListener();
                    }
                }
            });
            nextchapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (chapterClicker != null) {
                        chapterClicker.nextChapterListener();
                    }
                }
            });

        return scanView;
    }

    public void setScanViewBg(int bgId){
        this.scanViewBg = bgId;
    }

    public int getOrientation(){
        return orientation;
    }

    public interface ChapterClicker {
        void lastChapterListener();
        void nextChapterListener();
    }

    public interface GetPages{
        void allPagesCount(String stringText, List<String> items);
    }

    private float textValueToDp(float value){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,value,context.getResources().getDisplayMetrics());
    }

}
