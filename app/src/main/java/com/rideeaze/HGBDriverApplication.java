package com.rideeaze;

import android.app.Application;
import android.util.Log;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

//import com.crashlytics.android.Crashlytics;

public class HGBDriverApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        //Crashlytics.start(this);
        //Crashlytics.start(this);

        LocationLibrary.showDebugOutput(true);

        try {
            LocationLibrary.initialiseLibrary(getBaseContext(), 30 * 1000, 1 * 60 * 1000, "com.rideeaze");
            LocationLibrary.forceLocationUpdate(getBaseContext());
        }
        catch (UnsupportedOperationException ex) {
            Log.d("HGBDriverApplication", "UnsupportedOperationException thrown - the device doesn't have any location providers");
        }
    }
}