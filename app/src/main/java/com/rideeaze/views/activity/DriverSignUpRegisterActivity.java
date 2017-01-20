package com.rideeaze.views.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.rideeaze.R;
import com.rideeaze.services.network.NetworkApi;
import com.rideeaze.services.network.NetworkService;
import com.rideeaze.services.network.model.data.AccountAuthDetails;
import com.rideeaze.services.network.model.data.AccountInfoDetails;
import com.rideeaze.services.network.model.response.HttpAccountDataResponse;
import com.rideeaze.services.network.model.response.JsonResponse;
import com.rideeaze.services.telephone.DriverTelephoneService;
import com.rideeaze.views.fragmentactivity.SplashActivityOld;
import com.util.Const;
import com.util.StorageDataHelper;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by kvofreelance on 20.12.2014.
 */
public class DriverSignUpRegisterActivity extends DriverActivity{

    @Bind(R.id.firstName)
    EditText firstName;

    @Bind(R.id.midName)
    EditText secondName;
    @Bind(R.id.lastName)
    EditText lastName;
    @Bind(R.id.mobile_number)
    EditText mobileNo;
    @Bind(R.id.merchant_id)
    EditText merchantId;
    @Bind(R.id.driver_code)
    EditText driverCode;
    @Bind(R.id.cardReaderList)
    Spinner credit_card_reader;

    @OnClick(R.id.help_merchant_id)
    public void showMerchantIdHint() {
        new AlertDialog.Builder(DriverSignUpRegisterActivity.this)
                .setTitle("Info")
                .setMessage("Get the merchant ID from your supervisor")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @OnClick(R.id.help_driver_code)
    public void showDriverCodeHint() {
        new AlertDialog.Builder(DriverSignUpRegisterActivity.this)
                .setTitle("Info")
                .setMessage("Obtain this code from your supervisor. This code is used by your back-office program to identify you when receiving pickups from the office.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @OnClick(R.id.driver_activity_signup_register)
    public void registerAccount() {
        if(credit_card_reader.getSelectedItemPosition() == credit_card_reader.getAdapter().getCount()) {
            Toast.makeText(getBaseContext(), getString(R.string.driver_error_non_selected_ccreader), Toast.LENGTH_SHORT).show();
            return;
        }
        if (firstName.getText().toString().length() == 0) {
            firstName.setError("Please Input your First Name");
            return;
        }

        if (lastName.getText().toString().length() == 0) {
            firstName.setError("Please Input your Last Name");
            return;
        }

        if ( mobileNo.getText().toString().length() == 0) {
            mobileNo.setError("Please Input your Mobile Number");
            return;
        }

        if (merchantId.getText().toString().length() == 0) {
            merchantId.setError("Please Input MerchantID");
            return;
        }

        if (driverCode.getText().toString().length() == 0) {
            driverCode.setError("Please Input your Driver Code");
        }

        createDriverAccount();
    }

    DriverTelephoneService telephoneService;
    String tmpEmail;
    String tmpPassword;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_activity_signup_register);

        ButterKnife.bind(this);

        //forbid showing icon on action bar
        getActionBar().setDisplayShowHomeEnabled(false);
        mobileNo.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        String[] ccreaderArray = getResources().getStringArray(R.array.credit_card_reader_list);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Arrays.asList(ccreaderArray)); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        credit_card_reader.setAdapter(spinnerArrayAdapter);

        telephoneService = new DriverTelephoneService(this.getBaseContext());

        tmpEmail = getIntent().getStringExtra(Const.REGISTER_TEMP_USERNAME);
        tmpPassword = getIntent().getStringExtra(Const.REGISTER_TEMP_PASSWORD);

        preFilledData();
    }

    public void showMainActivity() {
        Intent intent = new Intent(DriverSignUpRegisterActivity.this, SplashActivityOld.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void showErrorRegisterDialog() {
        new AlertDialog.Builder(DriverSignUpRegisterActivity.this)
                .setTitle("Error")
                .setMessage("This driver has already exists, or you entered incorrect data.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            dialogInterface.cancel();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .create().show();
    }

    private void createDriverAccount() {
        if (!isNetworkAvailable()) return;

        DisplayProcessMessage(getResources().getString(R.string.driver_common_please_wait));

        final AccountInfoDetails createAccReqParam = new AccountInfoDetails();
        createAccReqParam.MobilePhone = mobileNo.getText().toString();
        createAccReqParam.UniqueId = telephoneService.getDeviceId();
        createAccReqParam.FirstName = firstName.getText().toString();
        createAccReqParam.MiddleName = secondName.getText().toString();;
        createAccReqParam.LastName = lastName.getText().toString();
        createAccReqParam.MerchantId = merchantId.getText().toString();
        createAccReqParam.DriverCode = driverCode.getText().toString();
        createAccReqParam.InHouse = false;
        createAccReqParam.Email = tmpEmail;
        createAccReqParam.Password = tmpPassword;


        NetworkApi api = (new NetworkService()).getApi();
        api.createDriverAccount(createAccReqParam)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonResponse<HttpAccountDataResponse>>() {
                    @Override
                    public void onCompleted() {
                        DisplayProcessMessage(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        DisplayProcessMessage(false);
                        showErrorRegisterDialog();
                    }

                    @Override
                    public void onNext(JsonResponse<HttpAccountDataResponse> httpAccountDataResponseJsonResponse) {
                        if (httpAccountDataResponseJsonResponse.IsSuccess) {
                            StorageDataHelper.getInstance(getApplicationContext()).setAccountInfoDetails(createAccReqParam);

                            AccountAuthDetails accountAuthDetails = new AccountAuthDetails();
                            accountAuthDetails.username = tmpEmail;
                            accountAuthDetails.password = tmpPassword;
                            accountAuthDetails.token = httpAccountDataResponseJsonResponse.Content.AuthToken;
                            accountAuthDetails.paymentToken = httpAccountDataResponseJsonResponse.Content.GatewayToken;
                            accountAuthDetails.isLoggedIn = true;
                            accountAuthDetails.localCardReader = (String) credit_card_reader.getSelectedItem();

                            StorageDataHelper.getInstance(getApplicationContext()).setAccountAuthDetails(accountAuthDetails);

                            showMainActivity();
                        } else {
                            AccountAuthDetails accountAuthDetails = StorageDataHelper.getInstance(getApplicationContext()).getAccountAuthDetails();
                            accountAuthDetails.isLoggedIn = false;
                            StorageDataHelper.getInstance(getApplicationContext()).setAccountAuthDetails(accountAuthDetails);

                            showErrorRegisterDialog();

                            /*Intent intent = new Intent(DriverSignUpRegisterActivity.this, DriverSignUpActivity.class);
                            startActivity(intent);*/
                        }
                    }
                });
    }

    private void preFilledData() {
        AccountInfoDetails accountInfoDetails = StorageDataHelper.getInstance(getApplicationContext()).getAccountInfoDetails();
        firstName.setText(accountInfoDetails.FirstName);
        secondName.setText(accountInfoDetails.SecondName);
        lastName.setText(accountInfoDetails.LastName);
        mobileNo.setText(accountInfoDetails.MobilePhone);
        merchantId.setText(accountInfoDetails.MerchantId);
        driverCode.setText(accountInfoDetails.DriverCode);
    }
}
