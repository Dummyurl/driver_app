package com.rideeaze.views.fragmentactivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.rideeaze.R;
import com.rideeaze.gcm.CommonUtilities;
import com.rideeaze.services.network.NetworkApi;
import com.rideeaze.services.network.NetworkService;
import com.rideeaze.services.network.model.request.HttpReviewPassengerRequest;
import com.rideeaze.services.network.model.response.JsonResponse;
import com.util.ReviewObject;
import com.util.StorageDataHelper;
import com.util.Utils;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PassengerRatingActivity extends DriverFragmentActivity implements
		OnClickListener {

	Button close;
	Button confirm;

	TextView passengerName;
	RatingBar passengerRating;
	EditText comment;

    ReviewObject reviewObject;

	@Override
	public void onCreate(Bundle arg0) {
		setContentView(R.layout.activity_rating);

        String review = getIntent().getStringExtra("reviewObject");
        if(review != null && !review.contentEquals("")){
            reviewObject = new ReviewObject();
            reviewObject.convertFromString(review);
        }

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		initUI();
		attachListener();
		super.onCreate(arg0);
	}

	public void initUI() {
		close = (Button) findViewById(R.id.close_rating);
		confirm = (Button) findViewById(R.id.ok);

		passengerName = (TextView) findViewById(R.id.passengerName);
		passengerRating = (RatingBar) findViewById(R.id.passengerRating);
		comment = (EditText) findViewById(R.id.comment);

		comment.setOnEditorActionListener(new DoneOnEditorActionListener());

		if (CommonUtilities.pickups != null) {
			String userName = CommonUtilities.pickups
					.get(CommonUtilities.pickups.size() - 1).UserName;
			passengerName.setText(userName);
		}

        if(reviewObject!=null) {
            comment.setText(reviewObject.comment);
            passengerRating.setNumStars(reviewObject.stars);
            passengerName.setText(reviewObject.name);
            confirm.setSelected(false);
        }

	}

	public void attachListener() {
		close.setOnClickListener(this);
		confirm.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == close) {
			finish();
		}

		if (v == confirm) {

			String passengerComment = comment.getText().toString();
			int rate = (int) passengerRating.getRating();

			GiveReview(rate, passengerComment);
		}
	}

	class DoneOnEditorActionListener implements OnEditorActionListener {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			// TODO Auto-generated method stub
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				try {
					InputMethodManager imm = (InputMethodManager) v.getContext()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					return true;
				} catch (Exception ex) {
					try {
						final TextView _view = v;
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								InputMethodManager imm = (InputMethodManager) _view.getContext()
										.getSystemService(Context.INPUT_METHOD_SERVICE);
								imm.hideSoftInputFromWindow(_view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				}
			}
			return true;
		}
	}

	protected Handler handler = new Handler();

	public void GiveReview(final int rate, final String passengerComment) {

		if (CommonUtilities.pickups != null) {

			if (!isNetworkAvailable()) return;

			DisplayProcessMessage(getResources().getString(R.string.driver_common_please_wait));

			HttpReviewPassengerRequest reviewRequest = new HttpReviewPassengerRequest();
			reviewRequest.DriverID = StorageDataHelper.getInstance(getApplicationContext()).getAuthToken();
			reviewRequest.Title = "";
			reviewRequest.Comments = passengerComment;
			reviewRequest.Rating = rate;
			reviewRequest.ReservationID = Integer.parseInt(SplashActivityOld.reservationId);

			NetworkApi api = (new NetworkService()).getApi();
			api.giveReviewToPassenger(reviewRequest)
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
							if(stringJsonResponse.IsSuccess) {
								DisplayMessage("Give Review Success!");
								Utils.saveReviewToLocal(new ReviewObject(passengerName.getText().toString(), passengerComment, rate, SplashActivityOld.reservationId), getApplicationContext());
							} else {
								DisplayMessage("Give Review failed");
							}

							finish();
						}
					});
		}
	}


}
