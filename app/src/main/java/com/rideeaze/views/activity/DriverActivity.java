package com.rideeaze.views.activity;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.util.Utils;

import roboguice.activity.RoboActivity;

public class DriverActivity extends RoboActivity {

    private static ProgressDialog blockingDialog;
    private static String currentMessage;

    protected boolean initialized, paused;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public final void blockUI(String message) {
        currentMessage = message;
        blockingDialog = new ProgressDialog(this);
        blockingDialog.setCancelable(false);
        blockingDialog.setIndeterminate(true);
        blockingDialog.setMessage(message);
        blockingDialog.show();
    }

    public final void unblockUI() {
        if (blockingDialog != null)
            blockingDialog.dismiss();
        blockingDialog = null;
        currentMessage = null;
    }




    public boolean isNetworkAvailable() {
        if (!Utils.isOnline(DriverActivity.this)) {
        Toast.makeText(
                this,
                "There is no Internet Connection. Please try after sometimes",
                Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void DisplayProcessMessage(final String msg) {
        showProcessingDialog(msg);
    }

    public void DisplayProcessMessage(final boolean hide) {

        hideProcessingDialog();

    }

    private ProgressDialog progressDialog = null;

    private void showProcessingDialog(String msg) {
        if (progressDialog == null)
            progressDialog = ProgressDialog.show(this, "", msg, true, false);
    }

    private void hideProcessingDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void onPause() {
        paused = true;
        super.onPause();
    }

    public void DisplayStringMessage(final String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}
