package com.rideeaze.views.activity;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.rideeaze.R;
import com.rideeaze.gcm.CommonUtilities;

public class DriverPhoneSettingActivity extends Activity implements OnClickListener, OnSeekBarChangeListener {

    TextView txt_minute;
    TextView txt_second;
    SeekBar time_seek;
    Button setting;
    Button ok;


    int interval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foreground_setting);


        initUI();

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
        interval = preference.getInt("interval", 60);

        displayTime(interval);
    }

    public void initUI() {
        txt_minute = (TextView) findViewById(R.id.minute);
        txt_second = (TextView) findViewById(R.id.second);

        time_seek = (SeekBar) findViewById(R.id.seek_time);
        time_seek.setOnSeekBarChangeListener(this);

        setting = (Button) findViewById(R.id.goToSetting);
        setting.setOnClickListener(this);

        ok = (Button) findViewById(R.id.ok);
        ok.setOnClickListener(this);
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == ok) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("interval", interval);
            editor.commit();

            Intent intent = new Intent(CommonUtilities.UPDATE_INTERVAL_ACTION);
            intent.putExtra("interval", interval);
            sendBroadcast(intent);

            finish();
        }

        if (v == setting) {
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        // TODO Auto-generated method stub
        if (seekBar == time_seek) {
            interval = 60 + progress;
            displayTime(interval);
        }
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    public void displayTime(int time) {
        int minute = (time == 120) ? 2 : 1;
        int second = (time == 120) ? 0 : time - 60;

        txt_minute.setText(String.valueOf(minute));
        txt_second.setText(String.valueOf(second));

    }

}
