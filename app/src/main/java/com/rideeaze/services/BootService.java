package com.rideeaze.services;

import java.util.List;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.rideeaze.gcm.CommonUtilities;

public class BootService extends Service {

    WakeAppReceiver receiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub

        receiver = new WakeAppReceiver();
        registerReceiver(receiver, new IntentFilter(CommonUtilities.WAKE_APP_ACTION));

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    public void FindDetectorProcess() {

        boolean bExist = false;

        String processName = getCurrentTopActivity(getBaseContext());

        if (processName.startsWith("com.rideeaze")) {
            bExist = true;
            CommonUtilities.appBackgroung = false;
        }
        else{
        	CommonUtilities.appBackgroung = true;
        }

        if (!bExist) {
        	
            Intent telecare1 = new Intent(Intent.ACTION_VIEW);
            telecare1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            telecare1.setClassName("com.rideeaze", "com.rideeaze.views.fragmentactivity.SplashActivityOld");
            telecare1.putExtra("GetPickUp", true);

            Log.v("telecare1", "MainActivity Creating");
            getApplicationContext().startActivity(telecare1);
        }
    }

    public static String getCurrentTopActivity(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> RunningTask = mActivityManager.getRunningTasks(1);
        ActivityManager.RunningTaskInfo ar = RunningTask.get(0);
        return ar.topActivity.getClassName().toString();
    }

    public class WakeAppReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(CommonUtilities.WAKE_APP_ACTION)) {
                FindDetectorProcess();
            }
        }

    }


}
