package com.rideeaze.services;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.rideeaze.services.network.model.data.HttpLocation;
import com.rideeaze.services.network.NetworkApi;
import com.rideeaze.services.network.NetworkService;
import com.rideeaze.services.network.model.data.AccountAuthDetails;
import com.rideeaze.services.network.model.request.HttpUpdatePositionRequest;
import com.rideeaze.services.network.model.response.JsonResponse;
import com.rideeaze.views.fragmentactivity.SplashActivityOld;
import com.util.StorageDataHelper;
import com.util.Utils;

import java.util.Timer;
import java.util.TimerTask;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UpdatePositionService extends Service {

    Timer timer;

    @Override
    public void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        timer = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {

                AccountAuthDetails accountAuthDetails = StorageDataHelper.getInstance(getApplicationContext()).getAccountAuthDetails();

                if (SplashActivityOld.current_location != null && !accountAuthDetails.token.equals("") && Utils.isOnline(getApplicationContext())) {
                	System.out.println("==============TOKEN:UpdateLocation::::::::::"+ accountAuthDetails.token);
                	System.out.println("==============TOKEN:UpdateLocation LAtitude::::::::::"+ SplashActivityOld.current_location.getLatitude());
                	System.out.println("==============TOKEN:UpdateLocation Longitude::::::::::"+ SplashActivityOld.current_location.getLongitude());

                    HttpUpdatePositionRequest updatePositionRequest = new HttpUpdatePositionRequest();
                    updatePositionRequest.DriverToken = accountAuthDetails.token;
                    updatePositionRequest.Location = new HttpLocation();
                    updatePositionRequest.Location.latitude = SplashActivityOld.current_location.getLatitude();
                    updatePositionRequest.Location.longitude = SplashActivityOld.current_location.getLongitude();


                    NetworkApi api = (new NetworkService()).getApi();
                    api.sendUpdatedPosition(updatePositionRequest)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<JsonResponse<String>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(JsonResponse<String> stringJsonResponse) {
                            if (stringJsonResponse.ResponseCode == 500) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                                builder.setTitle("Error");
                                builder.setMessage("Please describe your vehicle in settings");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    });


                    Log.e("CurrentLocation", String.valueOf(SplashActivityOld.current_location.getLatitude()) + "," + String.valueOf(SplashActivityOld.current_location.getLongitude()));
                }
            }
        };

        timer.schedule(task, 0, 30000);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
