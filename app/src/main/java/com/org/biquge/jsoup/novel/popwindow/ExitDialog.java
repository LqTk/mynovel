package com.org.biquge.jsoup.novel.popwindow;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import com.org.biquge.jsoup.R;

public class ExitDialog extends Dialog {
    Context context;
    View dialogView;

    public ExitDialog(@NonNull Context context) {
        this(context, R.style.dialog_exit);
    }

    public ExitDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        init(context);
    }

    private void init(Context context) {
        dialogView = LayoutInflater.from(context).inflate(R.layout.exit_dialog_layout,null);
        setContentView(dialogView);
    }
}
