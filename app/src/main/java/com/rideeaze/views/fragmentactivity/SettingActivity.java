package com.rideeaze.views.fragmentactivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.rideeaze.R;
import com.rideeaze.services.network.model.data.AccountAuthDetails;
import com.rideeaze.views.activity.DriverActivity;
import com.rideeaze.views.activity.DriverMainActivity;
import com.rideeaze.views.activity.DriverUpdateProfileActivity;
import com.util.Const;
import com.util.StorageDataHelper;

/**
 * Created by kvofreelance on 21.01.2015.
 */
public class SettingActivity extends DriverActivity {
    RelativeLayout profile_relative_layout, gps_relative_layout, help_relative_layout, vehicle_relative_layout;
    Button logout_btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        boolean isLogOut = getIntent().getBooleanExtra(Const.LOG_OUT_FROM_ACCOUNT, false);

        if(isLogOut) {
            logOut();
            return;
        }


        //forbid showing icon on action bar
        getActionBar().setDisplayShowHomeEnabled(false);

        profile_relative_layout = (RelativeLayout) findViewById(R.id.profile_relative_layout);
        profile_relative_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, DriverUpdateProfileActivity.class);
                startActivity(intent);
            }
        });

        vehicle_relative_layout = (RelativeLayout) findViewById(R.id.vehicle_relative_layout);
        vehicle_relative_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, VehicleActivityNew.class);
                startActivity(intent);
            }
        });

        gps_relative_layout = (RelativeLayout) findViewById(R.id.gps_relative_layout);
        gps_relative_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, GpsSettingsActivity.class);
                startActivity(intent);
            }
        });

        help_relative_layout = (RelativeLayout) findViewById(R.id.help_relative_layout);
        help_relative_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, HelpSupportActivity.class);
                startActivity(intent);
            }
        });

        logout_btn = (Button) findViewById(R.id.logout_btn);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });
    }

    private void logOut() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.commit();
        AccountAuthDetails accountAuthDetails = StorageDataHelper.getInstance(getApplicationContext()).getAccountAuthDetails();
        accountAuthDetails.isLoggedIn = false;
        StorageDataHelper.getInstance(getApplicationContext()).setAccountAuthDetails(accountAuthDetails);
        startActivity(new Intent(SettingActivity.this,
                DriverMainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }
}
