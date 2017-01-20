package com.rideeaze.views.common;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adventis on 1/20/16.
 */
public class DriverAppCompatActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        List<Fragment> fragments = new ArrayList<Fragment>();
        fragments = fragmentManager.getFragments();
        for(Fragment fragment : fragments){
            if(fragment != null && fragment.isVisible())
                return fragment;
        }
        return null;
    }

    public boolean isNetworkAvailable() {
        if (!Utils.isOnline(DriverAppCompatActivity.this)) {
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

    public void DisplayStringMessage(final String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}
