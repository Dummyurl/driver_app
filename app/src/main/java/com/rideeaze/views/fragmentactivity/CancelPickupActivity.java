package com.rideeaze.views.fragmentactivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rideeaze.R;
import com.rideeaze.gcm.CommonUtilities;
import com.rideeaze.services.network.NetworkApi;
import com.rideeaze.services.network.NetworkService;
import com.rideeaze.services.network.model.request.HttpCancelPassengerRequest;
import com.rideeaze.services.network.model.response.JsonResponse;
import com.util.StorageDataHelper;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CancelPickupActivity extends DriverFragmentActivity implements
		OnClickListener {

	Button close;
	Button confirm;
	Button btn_done;
	EditText edit_reason;


	@Override
	public void onCreate(Bundle arg0) {
		setContentView(R.layout.cancel_call);
		super.onCreate(arg0);


		initUI();
		attachListener();

	}

	public void initUI() {
		close = (Button) findViewById(R.id.close_cancel);
		confirm = (Button) findViewById(R.id.confirmCancel);
		edit_reason = (EditText) findViewById(R.id.reason);
		btn_done = (Button) findViewById(R.id.btn_done);

	}

	public void attachListener() {
		close.setOnClickListener(this);
		confirm.setOnClickListener(this);
		btn_done.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == close) {
			finish();
		}

		if (v == confirm) {
			String reason = edit_reason.getText().toString();
			if (reason.equals("")) {
				Toast.makeText(this, "Please Input your Reason!",
						Toast.LENGTH_LONG).show();
				return;
			}

			cancelPickup(reason);

		}
		if(v == btn_done)
		{
			finish();
		}
	}

	public void cancelPickup(final String reason) {
		final String passengerId = CommonUtilities.pickups
				.get(CommonUtilities.pickups.size() - 1).PassengerID;

		if (!isNetworkAvailable()) return;

		DisplayProcessMessage(getResources().getString(R.string.driver_common_please_wait));


		HttpCancelPassengerRequest cancelRequest = new HttpCancelPassengerRequest();
		cancelRequest.DriverToken = StorageDataHelper.getInstance(getApplicationContext()).getAuthToken();
		cancelRequest.PassengerID = passengerId;
		cancelRequest.Reason = reason;
		cancelRequest.ReservationID = SplashActivityOld.reservationId;

		NetworkApi api = (new NetworkService()).getApi();
		api.sendCancelPassenger(cancelRequest)
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<JsonResponse<String>>() {
					@Override
					public void onCompleted() {
						DisplayProcessMessage(false);
					}

					@Override
					public void onError(Throwable e) {
						DisplayProcessMessage(false);
					}

					@Override
					public void onNext(JsonResponse<String> stringJsonResponse) {
						DisplayMessage(stringJsonResponse.Message);

						if (stringJsonResponse.IsSuccess) {
							finish();
						}
					}
				});

			}

}
