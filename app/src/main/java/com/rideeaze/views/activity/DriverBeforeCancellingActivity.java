package com.rideeaze.views.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.rideeaze.R;
import com.rideeaze.views.fragmentactivity.CancelPickupActivity;

public class DriverBeforeCancellingActivity extends Activity implements OnClickListener {

    Button confirm;
    Button close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_cancelling);
        initUI();
    }

    public void initUI() {
        confirm = (Button) findViewById(R.id.confirm_cancel);
        confirm.setOnClickListener(this);

        close = (Button) findViewById(R.id.close_before);
        close.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        if (v == confirm) {
            Intent intent = new Intent(DriverBeforeCancellingActivity.this, CancelPickupActivity.class);
            startActivity(intent);
            finish();

        }

        if (v == close) {
            finish();
        }
    }

}
