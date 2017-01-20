package com.rideeaze.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.rideeaze.R;

/**
 * Created by adventis on 3/25/15.
 */
public class EnterEmailDialog extends Dialog {

    public EditText enterEmailEditText;
    public Button sendButton;
    public Button noReceiptButton;

    public EnterEmailDialog(Context context) {
        super(context);
        initElements();
    }

    public EnterEmailDialog(Context context, int theme) {
        super(context, theme);
        initElements();
    }

    protected EnterEmailDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initElements();
    }

    private void initElements() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        this.setContentView(R.layout.popup_enter_email_dialog);
        this.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        this.setCanceledOnTouchOutside(true);
        this.setCancelable(false);

        final EnterEmailDialog dialog = this;
        enterEmailEditText = (EditText) findViewById(R.id.email_edit_enter_email_dialog);
        sendButton = (Button) findViewById(R.id.send_btn_enter_email_dialog);
        noReceiptButton = (Button) findViewById(R.id.noreceipt_btn_enter_email_dialog);
    }
}
