package com.rideeaze.views.fragmentactivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.rideeaze.R;
import com.rideeaze.payment.service.DataServices;
import com.rideeaze.payment.service.JsonPaymentResponse;
import com.rideeaze.services.network.NetworkApi;
import com.rideeaze.services.network.NetworkService;
import com.rideeaze.services.network.model.data.HttpFeecode;
import com.rideeaze.services.network.model.request.HttpEmailReceipt;
import com.rideeaze.services.network.model.response.JsonResponse;
import com.rideeaze.uc.AlertDailogView;
import com.rideeaze.uc.WheelActivity;
import com.rideeaze.uc.WheelDateActivity;
import com.rideeaze.views.activity.DriverActivity;
import com.rideeaze.views.dialog.EnterEmailDialog;
import com.rideeaze.views.dialog.ProcessingDialog;
import com.util.Const;
import com.util.CreditCardValidator;
import com.util.StorageDataHelper;
import com.util.Utils;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HandKeyedTransactionActivity extends DriverActivity implements
		OnClickListener {

	private TextView txtTraCancel;
	private TextView txtTraTotal;
	private EditText edtTraCCnumber;
	private TextView txtTraExpirDate;
	private TextView edtTraCVCNo;
	private TextView txtTraCardType;
	private TextView txtTraDone;
	private TextView txtTraSubmit;
    private TextView txtCancelBtn;
	private Message msg;
	private List<HttpFeecode> _feeCodeList;
	private JsonPaymentResponse<String> status;
	private String handKeyTransactionId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.handkeyed_transaction_activity);
		txtTraCancel = (TextView) findViewById(R.id.txtTraCancel);
		txtTraTotal = (TextView) findViewById(R.id.txtTraTotal);
		edtTraCCnumber = (EditText) findViewById(R.id.edtTraCCnumber);
		txtTraExpirDate = (TextView) findViewById(R.id.txtTraExpirDate);
		edtTraCVCNo = (EditText) findViewById(R.id.edtTraCVCNo);
		txtTraCardType = (TextView) findViewById(R.id.txtTraCardType);
		txtTraDone = (TextView) findViewById(R.id.txtTraDone);
		txtTraSubmit = (TextView) findViewById(R.id.txtTraSubmit);
        txtCancelBtn = (TextView) findViewById(R.id.txtCancelBtn);

		txtTraTotal.setText("$"+getTransactionTotal());

		msg = new Message();

		txtTraCancel.setOnClickListener(this);
		txtTraDone.setOnClickListener(this);
		txtTraSubmit.setOnClickListener(this);
        txtCancelBtn.setOnClickListener(this);
		txtTraExpirDate.setOnClickListener(this);
		txtTraCardType.setOnClickListener(this);

        getActionBar().setDisplayShowHomeEnabled(false);

	}

    private String getTransactionTotal() {
        return String.format("%.2f",
                (float) Const.paymentTotalAmnt);
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txtTraCancel:
			finish();
			break;

        case R.id.txtCancelBtn:
           finish();
           break;

		case R.id.txtTraDone:

			break;

		case R.id.txtTraSubmit:

			String result = null;

			result = validateData();

			if (result != null) {
				AlertDailogView.showAlert(HandKeyedTransactionActivity.this,result).show();
			} else {

				performSearch(edtTraCCnumber.getText().toString().trim(),
						edtTraCVCNo.getText().toString().trim(),
						txtTraExpirDate.getText().toString().trim(),
						getTransactionTotal().trim(),
						Const.tip + "", txtTraCardType.getText().toString()
								.trim());
			}
			break;
		case R.id.txtTraExpirDate:
			String currentDate = Utils.millisToDate(System.currentTimeMillis(),
					"dd-MM-yyyy");

			String date[] = currentDate.split("-");

			Intent openWheel1 = new Intent(HandKeyedTransactionActivity.this,
					WheelDateActivity.class);
			openWheel1.putExtra("selected-date", date[0]);
			openWheel1.putExtra("selected-month", date[1]);
			openWheel1.putExtra("selected-year", date[2]);
			startActivityForResult(openWheel1, 2);
			break;
		case R.id.txtTraCardType:
			String[] cardType = { "Visa", "Master Card", "Discover",
					"American Express" };

			Intent openWheel = new Intent(HandKeyedTransactionActivity.this,
					WheelActivity.class);
			openWheel.putExtra("content_array", cardType);
			openWheel.putExtra("selected_value", txtTraCardType.getText()
					.toString());
			startActivityForResult(openWheel, 1);
			break;

		}

	}

    private ProcessingDialog authDialog = null;
    private void showAuthDialog() {
        if(authDialog == null) {
            authDialog = new ProcessingDialog(this);
            authDialog.show();
        }
    }

    private void hideAuthDialog() {
        if (authDialog != null) {
            authDialog.dismiss();
            authDialog = null;
        }
    }



	private JsonPaymentResponse<String> callWebService(String CardNumber, String CVV, String ExpDt,
			String Amount, String Tip, String CardType) {
		JsonPaymentResponse<String> transactionId = DataServices.getDataServiceObject(
				getBaseContext()).ProcessHandKeyPay(CardNumber, CVV, ExpDt,
				Amount, Tip, CardType);
		return transactionId;
	}

	private void performSearch(final String CardNumber, final String CVV,
			final String ExpDt, final String Amount, final String Tip,
			final String CardType) {

		if (!Utils.isOnline(HandKeyedTransactionActivity.this)) {
			AlertDailogView.showAlert(HandKeyedTransactionActivity.this,
					"Please check your internet connection.").show();
			return;
		}

        showAuthDialog();

		new Thread() {
			public void run() {

				status = callWebService(CardNumber, CVV, ExpDt, Amount, Tip,
						CardType);

				if (status != null && status.responseCode.equals("")
						&& status.responseCode.equals("0000")) {
					hideAuthDialog();
					handKeyTransactionId = status.content;
					msg.what = 1;
					msg.arg1 = 0;
					handlerWeb.sendMessage(msg);
					this.interrupt();
					return;
				} else {
					hideAuthDialog();
					msg.what = 1;
					msg.arg1 = 1;
					if (status != null)
					{
						msg.obj = status.message;
					}
					else{
						msg.obj = "Transaction failed!";
					}
					handlerWeb.sendMessage(msg);
					this.interrupt();
					return;
				}
			}

		}.start();

	}

	public static void showTransactionCompleteAlert(final Context context,
			String email) {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.trasaction_complete_dialog);
		dialog.setCancelable(true);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.getWindow().getAttributes().width = LayoutParams.FILL_PARENT;

		TextView txtTraCmpMessage = (TextView) dialog
				.findViewById(R.id.txtTraCmpMessage);
		TextView txtTraCmpDone = (TextView) dialog
				.findViewById(R.id.txtTraCmpDone);

		txtTraCmpMessage.setText("Receipt Emailed to\n" + email);

		txtTraCmpDone.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				dialog.dismiss();
				((Activity) context).finish();
			}
		});

		dialog.show();
	}

	private String validateData() {

		String result = null;

		if (txtTraCardType.getText().toString().trim() == null
				|| txtTraCardType.getText().toString().trim().equals(""))
			result = "Please provide your cardtype.";
		else if (edtTraCCnumber.getText().toString().trim() != null
				|| edtTraCCnumber.getText().toString().trim().equals("")) {
			if (txtTraCardType.getText().toString().trim().equals("Visa")) {
				if (!CreditCardValidator.validate(edtTraCCnumber.getText()
						.toString().trim(), CreditCardValidator.VISA)) {
					result = "Invalid Visa Card";
				}
			} else if (txtTraCardType.getText().toString().trim()
					.equals("Master Card")) {
				if (!CreditCardValidator.validate(edtTraCCnumber.getText()
						.toString().trim(), CreditCardValidator.MASTERCARD)) {
					result = "Invalid Master Card";
				}
			} else if (txtTraCardType.getText().toString().trim()
					.equals("Discover")) {
				if (!CreditCardValidator.validate(edtTraCCnumber.getText()
						.toString().trim(), CreditCardValidator.DISCOVER)) {
					result = "Invalid Discover Card";
				}
			} else if (txtTraCardType.getText().toString().trim()
					.equals("American Express")) {
				if (!CreditCardValidator.validate(edtTraCCnumber.getText()
						.toString().trim(), CreditCardValidator.AMEX)) {
					result = "Invalid  American Express Card";
				}
			} else {
				result = "Invalid Card Number";
			}

		}
		return result;
	}

	@SuppressLint("HandlerLeak")
	private Handler handlerWeb = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				if (msg.arg1 == 0) {
					if (Const.fromUnknown) {
                        showEnterEmailDialog();
					} else {
						/*showTransactionCompleteAlert(
								HandKeyedTransactionActivity.this,
								Const.paymentUserEmail);*/
                        showEnterEmailDialog();
					}
				} else {
					AlertDailogView.showAlert(HandKeyedTransactionActivity.this,
							((String)msg.obj)).show();
					
				}
			}
		}
	};

    public void showEnterEmailDialog() {
        final EnterEmailDialog emailDialog = new EnterEmailDialog(this);

        final EditText emailView = emailDialog.enterEmailEditText;
        emailView.setText(Const.paymentUserEmail);

        emailDialog.sendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String enteredEmail = emailView.getText().toString().trim();

				if (enteredEmail.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(enteredEmail).matches() == false) {
					AlertDailogView.showAlert(HandKeyedTransactionActivity.this, getString(R.string.procesing_invalid_email)).show();
				} else {
					Const.paymentUserEmail = enteredEmail;
					emailReceiptUnknownPassenger();
				}
			}
		});
        emailDialog.noReceiptButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                emailDialog.dismiss();
                finish();
            }
        });
        emailDialog.show();
    }

	public void emailReceiptUnknownPassenger() {
		if (!isNetworkAvailable()) return;

		DisplayProcessMessage(getResources().getString(R.string.driver_common_please_wait));

		HttpEmailReceipt emailReceipt = new HttpEmailReceipt();
		emailReceipt.DriverToken = StorageDataHelper.getInstance(getApplicationContext()).getAuthToken();
		emailReceipt.LocalTime = Utils.millisToSecond(System.currentTimeMillis());
		emailReceipt.LineItems = generateFeeCodeList();
		emailReceipt.PassengerEmail = Const.paymentUserEmail;
		emailReceipt.Signature = Const.signature;
		emailReceipt.TransactionID = handKeyTransactionId;

		NetworkApi api = (new NetworkService()).getApi();
		api.sendEmailReceiptPassenger(emailReceipt)
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
							showEnterEmailDialog();
						}
					}
				});
	}

	private List<HttpFeecode> generateFeeCodeList() {
		_feeCodeList = new ArrayList<HttpFeecode>();
		for (HttpFeecode feeCodeBean : Const.feeCodeList) {
			if (feeCodeBean.Amount != 0.0) {
				_feeCodeList.add(feeCodeBean);
			}
		}
		return _feeCodeList;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 2 && resultCode == RESULT_OK) { // success
			txtTraExpirDate.setText(data.getStringExtra("selected-month") + "/" + data.getStringExtra("selected-year").substring(2,4));
		} else if (requestCode == 1 && resultCode == RESULT_OK) { // success
			txtTraCardType.setText(data.getStringExtra("selected_value"));
		}
	}

}
