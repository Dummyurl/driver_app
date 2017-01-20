package com.rideeaze.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;

import com.rideeaze.R;

/**
 * Created by adventis on 3/25/15.
 */
public class ProcessingDialog extends Dialog {
    public ProcessingDialog(Context context) {
        super(context);
        initElements();
    }

    public ProcessingDialog(Context context, int theme) {
        super(context, theme);
        initElements();
    }

    protected ProcessingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initElements();
    }

    private void initElements() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        this.setContentView(R.layout.popup_procesing_dialog);
        this.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        this.setCancelable(false);
    }
}
