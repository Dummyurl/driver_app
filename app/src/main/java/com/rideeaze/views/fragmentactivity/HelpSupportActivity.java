package com.rideeaze.views.fragmentactivity;

import android.os.Bundle;

import com.rideeaze.R;
import com.rideeaze.views.activity.DriverActivity;

/**
 * Created by kvofreelance on 21.01.2015.
 */
public class HelpSupportActivity extends DriverActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_helpsupport);

        //forbid showing icon on action bar
        getActionBar().setDisplayShowHomeEnabled(false);
    }
}

