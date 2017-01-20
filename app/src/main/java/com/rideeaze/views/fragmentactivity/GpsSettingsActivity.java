package com.rideeaze.views.fragmentactivity;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.rideeaze.R;
import com.rideeaze.views.activity.DriverActivity;
import com.util.Const;
import com.util.Pref;
import com.util.Utils;

/**
 * Created by kvofreelance on 21.01.2015.
 */
public class GpsSettingsActivity extends DriverActivity {

    private ImageView imageViewSignal;
    private ToggleButton toggleButtonGps;
    private TextView txtsignalStrnt;
    private TextView toggleButtonGps_title;

    private Utils.GPSReception reception;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_gpssettings);

        //forbid showing icon on action bar
        getActionBar().setDisplayShowHomeEnabled(false);

        initUI();
        setInitValue();
    }

    public void initUI() {
        imageViewSignal = (ImageView) findViewById(R.id.signal_img);
        txtsignalStrnt = (TextView) findViewById(R.id.signal_tv);

        toggleButtonGps = (ToggleButton) findViewById(R.id.toggleButtonGps);
        toggleButtonGps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Pref.setValue(getApplicationContext(), Const.GPS_STATUS, "1");
                    Utils.GPSReception reception = Utils.getGPSReception(Float
                            .parseFloat(Pref.getValue(getApplicationContext(),
                                    Const.Accuracy, "0.0")));
                    txtsignalStrnt.setText(Utils.toSignalTextValue(reception));
                    // reception
                    Utils.turnGPSOn(getApplicationContext());
                    // String rec = toString(reception);
                    int res = Utils.toSignalImageResource(reception);
                    imageViewSignal.setImageResource(res);

                    toggleButtonGps_title.setText("Active");
                    //compoundButton.setBackgroundColor(Color.GREEN);
                } else {
                    Pref.setValue(getApplicationContext(), Const.GPS_STATUS, "0");
                    Utils.turnGPSOff(getApplicationContext());
                    imageViewSignal.setImageResource(R.drawable.setting_gpsno);
                    toggleButtonGps_title.setText("Inactive");
                    //compoundButton.setBackgroundColor(Color.GRAY);
                }
            }
        });

        toggleButtonGps_title = (TextView) findViewById(R.id.active_tv);
    }

    public void setInitValue() {
        if (Pref.getValue(getApplicationContext(), Const.GPS_STATUS, "1").equals(
                "1")) {
            reception = Utils.getGPSReception(Float.parseFloat(Pref.getValue(
                    getApplicationContext(), Const.Accuracy, "0.0")));

            // reception
            Utils.turnGPSOn(getApplicationContext());

            // String rec = toString(reception);
            int res = Utils.toSignalImageResource(reception);

            txtsignalStrnt.setText(Utils.toSignalTextValue(reception));
            imageViewSignal.setImageResource(res);
            toggleButtonGps.setChecked(true);
        }
    }

}
