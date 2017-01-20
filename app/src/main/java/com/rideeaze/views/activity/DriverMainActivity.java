package com.rideeaze.views.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.rideeaze.R;
import com.rideeaze.gcm.CommonUtilities;
import com.rideeaze.services.network.model.data.AccountInfoDetails;
import com.rideeaze.services.network.model.request.HttpLoginRequest;
import com.rideeaze.services.BootService;
import com.rideeaze.services.network.NetworkApi;
import com.rideeaze.services.network.NetworkService;
import com.rideeaze.services.network.model.data.AccountAuthDetails;
import com.rideeaze.services.network.model.response.HttpAccountDataResponse;
import com.rideeaze.services.network.model.response.JsonResponse;
import com.rideeaze.views.fragmentactivity.DriverFragmentActivity;
import com.rideeaze.views.fragmentactivity.SplashActivityOld;
import com.util.Const;
import com.util.StorageDataHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DriverMainActivity extends DriverFragmentActivity {// implements OnClickListener {
	RegReceiver receiver;
    String selectedCreditCardReader;
    public static Context ctx;

    public DriverMainActivity() {
    }

    @Bind(R.id.et_email)
    EditText et_email;
    @Bind(R.id.et_password)
    EditText et_password;
    @Bind(R.id.tvVersion)
    TextView tvVersion;



    @OnClick(R.id.driver_main_account_next_button_id)
    public void clickLoginlBtn() {
        String mail = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        //String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (mail.length() == 0) {
            et_email.setError(getString(R.string.driver_error_invalid_email));
            return;
        }
//        if (!mail.matches(emailPattern)) {
//            et_email.setError(getString(R.string.driver_error_invalid_validate_email));
//            return;
//        }

        if (password.length() == 0) {
            et_password.setError(getString(R.string.driver_error_invalid_password));
            return;
        }

        AccountAuthDetails accountAuthDetails = StorageDataHelper.getInstance(getApplicationContext()).getAccountAuthDetails();
        selectedCreditCardReader = accountAuthDetails.localCardReader;

        loginDriver(getBaseContext(), mail, password);
    }

    @OnClick(R.id.sign_up_tv_relative_layout)
    public void signUpShow() {
        Intent intent = new Intent(DriverMainActivity.this, DriverSignUpActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.forgot_textview)
    public void forgotViewpShow() {
        Intent intent = new Intent(DriverMainActivity.this, DriverForgotPasswordActivity.class);
        startActivity(intent);
    }



    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.driver_main_view);

        ButterKnife.bind(this);

        getActionBar().hide();

        ctx = getApplicationContext();

		Intent service = new Intent(DriverMainActivity.this, BootService.class);
		startService(service);

        AccountAuthDetails accountAuthDetails = StorageDataHelper.getInstance(getApplicationContext()).getAccountAuthDetails();
        et_email.setText(accountAuthDetails.username);
        et_password.setText(accountAuthDetails.password);

        et_email.requestFocus();

        if(accountAuthDetails.isLoggedIn) {
            registerDriver(true);
        }

        try {
            tvVersion.setText(String.format("Version %s",
                    getPackageManager().getPackageInfo(getPackageName(), 0).versionName));
        } catch (PackageManager.NameNotFoundException ex) {
            ex.printStackTrace();
        }
	}

    private void loginDriver(final Context context, final String username, final String password) {
        if (!isNetworkAvailable()) return;

        DisplayProcessMessage(getResources().getString(R.string.driver_common_please_wait));
        HttpLoginRequest httpLoginRequest = new HttpLoginRequest();
        httpLoginRequest.Email = username;
        httpLoginRequest.Password = password;

        NetworkApi api = (new NetworkService()).getApi();
        api.loginDriver(httpLoginRequest)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonResponse<HttpAccountDataResponse>>() {
                    @Override
                    public void onCompleted() {
                        HideProccessMessage();
                    }

                    @Override
                    public void onError(Throwable e) {
                        HideProccessMessage();
                    }

                    @Override
                    public void onNext(JsonResponse<HttpAccountDataResponse> httpAccountDataResponseJsonResponse) {
                        if (httpAccountDataResponseJsonResponse.IsSuccess) {
                            AccountAuthDetails accountAuthDetails = new AccountAuthDetails();
                            accountAuthDetails.username = username;
                            accountAuthDetails.password = password;
                            accountAuthDetails.token = httpAccountDataResponseJsonResponse.Content.AuthToken;
                            accountAuthDetails.paymentToken = httpAccountDataResponseJsonResponse.Content.GatewayToken;
                            accountAuthDetails.isLoggedIn = true;
                            accountAuthDetails.localCardReader = selectedCreditCardReader;
                            accountAuthDetails.CompanyPhoneNumber = httpAccountDataResponseJsonResponse.Content.CompanyPhoneNumber;

                            StorageDataHelper.getInstance(getApplicationContext()).setAccountAuthDetails(accountAuthDetails);

                            registerDriver(false);
                        }
                    }
                });
    }

    public void registerDriver(final boolean isLoginBefore) {
        Intent intent = new Intent(DriverMainActivity.this, SplashActivityOld.class);
        if(isLoginBefore) {
            intent.putExtra(Const.EXTRA_INTENT_LOGIN_BEFORE, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        } else {
            GetDriverInfo();
            final Intent _intent = intent;
            AlertDialog.Builder builder = new AlertDialog.Builder(DriverMainActivity.this);
            builder.setTitle("Warning");
            builder.setMessage(getResources().getString(R.string.select_your_vehicle_in_setting));
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                    startActivity(_intent);
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
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

                            StorageDataHelper.getInstance(getBaseContext()).setAccountInfoDetails(accountInfoDetails);
                        } else {
                            DisplayStringMessage("GetDriver Info failed");
                        }
                    }
                });
    }

	@Override
	public void onPause() {
		super.onPause();
		/*try {
			unregisterReceiver(lftBroadcastReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}*/

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
        ButterKnife.unbind(this);
		try {
            if (receiver != null)
			    unregisterReceiver(receiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class RegReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
					CommonUtilities.REGISTER_DIALOG_ACTION)) {
                Intent mIntent = new Intent(DriverMainActivity.this, DriverSignUpRegisterActivity.class);
                startActivity(mIntent);
			}
		}
	}
}
