package com.rideeaze.views.fragmentactivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.util.Utils;

import roboguice.activity.RoboFragmentActivity;

public class DriverFragmentActivity extends RoboFragmentActivity {

    private ProgressDialog progressDialog = null;
    protected Handler handler = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        handler = new Handler();

        super.onCreate(savedInstanceState);
    }

    public void DisplayProcessMessage(final String message) {
        /*handler.post(new Runnable() {
            public void run() {*/
                try {
                    if (progressDialog == null)
                        progressDialog = ProgressDialog.show(DriverFragmentActivity.this, "", message, true, false);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
        /*    }
        });*/
    }

    public void HideProccessMessage() {
        /*handler.post(new Runnable() {
            public void run() {*/
                try {
                    if (progressDialog != null)
                    {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            /*}
        });*/
    }

    public void DisplayMessage(final String messsage) {
        handler.post(new Runnable() {
            public void run() {
                try {
                    int duration = Toast.LENGTH_LONG;
                    Toast.makeText(DriverFragmentActivity.this, messsage, duration).show();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public boolean isNetworkAvailable() {
        if (!Utils.isOnline(getApplicationContext())) {
            Toast.makeText(
                    this,
                    "There is no Internet Connection. Please try after sometimes",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void DisplayProcessMessage(final boolean hide) {

        hideProcessingDialog();

    }

    public void showProcessingDialog(String msg) {
        if (progressDialog == null)
            progressDialog = ProgressDialog.show(this, "", msg, true, false);
    }

    public void hideProcessingDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public void DisplayStringMessage(final String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}
