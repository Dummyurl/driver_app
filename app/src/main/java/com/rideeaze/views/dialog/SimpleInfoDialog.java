package com.rideeaze.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.rideeaze.R;

/**
 * Created by adventis on 3/24/15.
 */
public class SimpleInfoDialog  extends Dialog {
    private final String TAG = "SimpleInfoDialog";

    private ImageView indicatorImageSimpleDlg;
    private TextView statusTextView;
    private TextView instructionsTextView;

    public static int SUCCESS_DIALOG = 0;
    public static int DECLINE_DIALOG = 1;
    public static int FAIL_DIALOG = 2;

    public int currentDialog = SUCCESS_DIALOG;

    private void writeLog(String msg) {
        Log.d(TAG, msg);
    }

    public SimpleInfoDialog(Context context) {
        super(context);
        writeLog("SimpleInfoDialog(Context context)");
        initDialog();

    }

    public SimpleInfoDialog(Context context, int theme) {
        super(context, theme);
        writeLog("SimpleInfoDialog(Context context, int theme)");
        currentDialog = theme;
        initDialog();
    }

    protected SimpleInfoDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        writeLog("SimpleInfoDialog(Context context, boolean cancelable, OnCancelListener cancelListener)");
        initDialog();
    }

    private void initDialog() {
        writeLog("initDialog()");
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        this.setContentView(R.layout.popup_simple_info_dialog);
        this.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        this.setCanceledOnTouchOutside(true);
        this.setCancelable(true);


        indicatorImageSimpleDlg = (ImageView) findViewById(R.id.indicator_image_simple_dlg);
        statusTextView = (TextView)findViewById(R.id.status_textview_simple_dlg);
        instructionsTextView = (TextView)findViewById(R.id.instructions_textview_simple_dlg);

        setStyleDialog(SUCCESS_DIALOG);
    }

    public void setStyleDialog(int style) {
        currentDialog = style;
        if(currentDialog == SUCCESS_DIALOG) {
            indicatorImageSimpleDlg.setImageResource(R.drawable.success_image_indicator_dialog);
            statusTextView.setText(R.string.success_status_text);
            instructionsTextView.setText(R.string.success_instructions_text);
        } else if(currentDialog == DECLINE_DIALOG) {
            indicatorImageSimpleDlg.setImageResource(R.drawable.dismiss_image_indicator_dialog);
            statusTextView.setText(R.string.decline_status_text);
            instructionsTextView.setText(R.string.decline_instructions_text);
        } else if(currentDialog == FAIL_DIALOG) {
            indicatorImageSimpleDlg.setImageResource(R.drawable.unknown_image_indicator_dialog);
            statusTextView.setText(R.string.fail_status_text);
            instructionsTextView.setText(R.string.fail_instructions_text);
        }
    }
}
