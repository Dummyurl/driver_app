package com.rideeaze.views.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;

import com.rideeaze.R;
import com.rideeaze.services.network.NetworkApi;
import com.rideeaze.services.network.NetworkService;
import com.rideeaze.services.network.model.data.AccountInfoDetails;
import com.rideeaze.services.network.model.request.HttpRegisterNewAccountRequest;
import com.rideeaze.services.network.model.response.JsonResponse;
import com.rideeaze.uc.AlertDailogView;
import com.rideeaze.views.common.DriverAppCompatActivity;
import com.util.StorageDataHelper;
import com.util.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DriverUpdateProfileActivity extends DriverAppCompatActivity {

	EditText firstName;
	EditText midName;
	EditText lastName;
	EditText mobileNumber;
	EditText driverCode;
	EditText emailField;

	EditText merchantID;
	Button update;
	Button cancel;

	String oldEmail;

	@Bind(R.id.call_in_morning_switch)
	CheckBox callInTheMorningSwitch;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_updatemyprofile);

		ButterKnife.bind(this);

		initUI();

		if (isNetworkAvailable())  {
			GetDriverInfo();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ButterKnife.unbind(this);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.driver_modify_schedule_done, menu);
		for (int i = 0; i < menu.size(); i++) {
			if(menu.getItem(i) !=null)
				menu.getItem(i).setVisible(true);
		}
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_done:
				finish();
				break;
			default:
				break;
		}

		return true;
	}

	public void initUI() {
		firstName = (EditText) findViewById(R.id.firstname_field);

		midName = (EditText) findViewById(R.id.middle_field);

		lastName = (EditText) findViewById(R.id.lastname_field);

		mobileNumber = (EditText) findViewById(R.id.phone_field);

        merchantID = (EditText) findViewById(R.id.merchant_id_field);

		driverCode = (EditText) findViewById(R.id.driver_code_id_field);

		emailField = (EditText) findViewById(R.id.email_field);

		update = (Button) findViewById(R.id.button_update);
		update.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String result = null;

				result = validateData();
				if (result != null) {

					AlertDailogView.showAlert(DriverUpdateProfileActivity.this,
							result).show();
				} else {
					if (Utils.isOnline(DriverUpdateProfileActivity.this)) {
						UpdateDriverInfo();
					} else {
						Utils.showToastAlert(DriverUpdateProfileActivity.this,
								"Internet connection not available.");
					}
				}
			}
		});

		cancel = (Button) findViewById(R.id.button_cancel);
		cancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				clearDate();
			}
		});

	}

    public void clearDate() {
        firstName.setText("");
        midName.setText("");
        lastName.setText("");
        mobileNumber.setText("");
        merchantID.setText("");
		driverCode.setText("");
		emailField.setText("");
		callInTheMorningSwitch.setChecked(false);

    }

	public void GetDriverInfo() {
		if (!isNetworkAvailable()) return;

		DisplayProcessMessage(getResources().getString(R.string.driver_common_please_wait));

		NetworkApi api = (new NetworkService()).getApi();
		api.getUserInfo(StorageDataHelper.getInstance(getBaseContext()).getAuthToken())
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<JsonResponse<AccountInfoDetails>>() {
					@Override
					public void onCompleted() {
						DisplayProcessMessage(false);
					}

					@Override
					public void onError(Throwable e) {
						DisplayProcessMessage(false);
					}

					@Override
					public void onNext(JsonResponse<AccountInfoDetails> accountInfoDetailsJsonResponse) {
						if(accountInfoDetailsJsonResponse.IsSuccess) {
							DisplayStringMessage("Get Driver Info Success!");

							AccountInfoDetails accountInfoDetails = accountInfoDetailsJsonResponse.Content;
							firstName.setText(accountInfoDetails.FirstName);
							midName.setText(accountInfoDetails.MiddleName);
							lastName.setText(accountInfoDetails.LastName);
							merchantID.setText(accountInfoDetails.MerchantId);
							mobileNumber.setText(accountInfoDetails.MobilePhone);
							driverCode.setText(accountInfoDetails.DriverCode);
							emailField.setText(accountInfoDetails.Email);
							try {
								callInTheMorningSwitch.setChecked(accountInfoDetails.UseWakeUpCall);
							} catch (Exception e) {

							}

							StorageDataHelper.getInstance(getBaseContext()).setAccountInfoDetails(accountInfoDetails);
						} else {
							DisplayStringMessage("GetDriver Info failed");
						}
					}
				});
	}

	private String validateData() {

		String result = null;
		if (firstName.getText().toString().trim() == null
				|| firstName.getText().toString().trim().equals(""))
			result = "Please provide your firstname";
		else if (lastName.getText().toString().trim() == null
				|| lastName.getText().toString().trim().equals(""))
			result = "Please provide your lastname";
		else if (mobileNumber.getText().toString().trim() == null
				|| mobileNumber.getText().toString().trim().equals(""))
			result = "Please provide your mobile no.";
		else if (merchantID.getText().toString().trim() == null
				|| merchantID.getText().toString().trim().equals(""))
			result = "Please provide your merchantId";
		else if (driverCode.getText().toString().trim() == null
				|| driverCode.getText().toString().trim().equals(""))
			result = "Please provide your driver code";
		else if (emailField.getText().toString().trim() == null
				|| emailField.getText().toString().trim().equals("")
				|| !emailField.getText().toString().contains("@")
				|| !emailField.getText().toString().contains("."))
			result = "Please provide your email (or check it)";

		return result;
	}

	public void UpdateDriverInfo() {
		if (!isNetworkAvailable()) return;

		final AccountInfoDetails updatedData = new AccountInfoDetails();
		updatedData.MobilePhone=mobileNumber.getText().toString().trim();
		updatedData.FirstName = firstName.getText().toString().trim();
		updatedData.MiddleName = midName.getText().toString().trim();
		updatedData.LastName = lastName.getText().toString().trim();
		updatedData.MerchantId = merchantID.getText().toString().trim();
		updatedData.DriverCode = driverCode.getText().toString().trim();
		updatedData.UseWakeUpCall = callInTheMorningSwitch.isChecked();
		String _email = emailField.getText().toString().trim();
		if (!_email.equals(oldEmail)) {
			updatedData.Email = _email;
		}

		DisplayProcessMessage(getResources().getString(R.string.driver_common_please_wait));

		NetworkApi api = (new NetworkService()).getApi();
		api.updateUserInfo(StorageDataHelper.getInstance(getBaseContext()).getAuthToken(),updatedData)
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
						if (stringJsonResponse.IsSuccess) {
							DisplayStringMessage("Update Driver Info Success!");
							StorageDataHelper.getInstance(getApplicationContext()).setAccountInfoDetails(updatedData);
						} else {
							DisplayStringMessage("UpdateDriver Info failed");
						}

						finish();
					}
				});
	}
}
