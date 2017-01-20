package com.rideeaze.views.fragmentactivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TextView;

import com.rideeaze.R;
import com.rideeaze.payment.service.DataServices;
import com.rideeaze.services.network.NetworkApi;
import com.rideeaze.services.network.NetworkService;
import com.rideeaze.services.network.model.data.HttpFeecode;
import com.rideeaze.services.network.model.request.HttpEmailPassengerRequest;
import com.rideeaze.services.network.model.request.HttpGetInvoiceRequest;
import com.rideeaze.services.network.model.response.HttpGetInvoiceData;
import com.rideeaze.services.network.model.response.JsonResponse;
import com.rideeaze.uc.AlertDailogView;
import com.rideeaze.uc.AlertDailogView.OnCustPopUpDialogButoonClickListener;
import com.rideeaze.views.activity.DriverActivity;
import com.util.Const;
import com.util.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AddTipActivity extends DriverActivity implements OnClickListener,
		OnCheckedChangeListener, OnCustPopUpDialogButoonClickListener {

	private TextView txtAdTipQuotedAmnt;
	private TextView txtAdTipGrndTotal;
	private TextView txtAdTipAmnt;
	private TextView txtAdTipNext;
	private TextView txtAdTipDone;
	private TextView txtAdTipClose;
	private RadioButton rbCrgPass;
	private RadioButton rbScanNewCard;
	private RadioButton rbPaycash;

	private ProgressDialog progressDialog = null;
	private Message msg;

	private String paymentMethod;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.add_tip_activity);

		txtAdTipQuotedAmnt = (TextView) findViewById(R.id.txtAdTipQuotedAmnt);
		txtAdTipGrndTotal = (TextView) findViewById(R.id.txtAdTipGrndTotal);
		txtAdTipAmnt = (TextView) findViewById(R.id.txtAdTipAmnt);
		txtAdTipNext = (TextView) findViewById(R.id.txtAdTipNext);
		txtAdTipClose = (TextView) findViewById(R.id.txtAdTipClose);
		txtAdTipDone = (TextView) findViewById(R.id.txtAdTipDone);
		rbCrgPass = (RadioButton) findViewById(R.id.rbCrgPass);
		rbScanNewCard = (RadioButton) findViewById(R.id.rbScanNewCard);
		rbPaycash = (RadioButton) findViewById(R.id.rbPaycash);
		Const.IS_KNOWN_PASSENGER = 1;
		if (getIntent().getBooleanExtra("fromPush", false)) {
			if (getIntent().getExtras().getString("approved")
					.equalsIgnoreCase("True")) {
				Const.tip = (float) Double.parseDouble(getIntent().getExtras()
                        .getString("tip_amount"));
				Const.paymentTotalAmnt = Double.parseDouble(getIntent()
						.getExtras().getString("fare_amount"));
				Const.invoiceId = Integer.parseInt(getIntent().getExtras()
						.getString("invoice_id"));
				Const.MerchantId = getIntent().getExtras().getString(
						"MerchantId");
				Const.IsCaptive = getIntent().getExtras()
						.getString("IsCaptive").equalsIgnoreCase("true") ? true
						: false;
				Const.paymentReservationId = Integer.parseInt(getIntent()
						.getExtras().getString("reservation_id"));
			} else {
				AlertDailogView.showAlert(AddTipActivity.this, "Alert",
						getIntent().getExtras().getString("message"), "Ok",
						this).show();
			}

            getInvoiceDetailes();
		} else {
            startActivity(new Intent(AddTipActivity.this,
                    PassengerActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }



		txtAdTipQuotedAmnt.setText(getFormatedStringValueForTextEdit(Const.paymentTotalAmnt+""));
		txtAdTipAmnt.setText(getFormatedStringValueForTextEdit(Const.tip + ""));
		txtAdTipGrndTotal.setText(getFormatedStringValueForTextEdit(Const.paymentTotalAmnt + Const.tip + ""));

		txtAdTipNext.setOnClickListener(this);
		txtAdTipClose.setOnClickListener(this);
		txtAdTipDone.setOnClickListener(this);

		rbCrgPass.setOnCheckedChangeListener(this);
		rbScanNewCard.setOnCheckedChangeListener(this);
		rbPaycash.setOnCheckedChangeListener(this);

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        String ccReader = preferences.getString(Const.SELECTED_CC_READER,"");

        //if(!ccReader.equalsIgnoreCase("BTMag")) {
            rbCrgPass.setVisibility(View.GONE);
            rbScanNewCard.setVisibility(View.GONE);
            rbPaycash.setVisibility(View.GONE);
        //}

	}

    private String getFormatedStringValueForTextEdit(String doubleString) {
        return new DecimalFormat("0.00").format(Double.parseDouble(doubleString));
    }

	@SuppressLint("HandlerLeak")
	private Handler handlerWeb = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				if (msg.arg1 == 0) {
					sendEmailToPassenger(((Integer) msg.obj) + "");
				}
			} else if (msg.what == 3) {
				if (msg.arg1 == 0) {

					setPaymentMethod();
				}
			}
		}
	};

	private void setPaymentMethod() {
		if (paymentMethod != null && !paymentMethod.equals("")) {
			if (paymentMethod.equalsIgnoreCase("Cash")) {
				rbPaycash.setChecked(true);
				rbScanNewCard.setEnabled(false);
				rbCrgPass.setEnabled(false);
			} else if (paymentMethod.equalsIgnoreCase("Scanned")) {
				rbScanNewCard.setChecked(true);
				rbCrgPass.setEnabled(false);
				rbPaycash.setEnabled(false);
			} else if (paymentMethod.equalsIgnoreCase("Stored")) {
				rbCrgPass.setChecked(true);
				rbScanNewCard.setEnabled(false);
				rbPaycash.setEnabled(false);
			}
		}
	}

	public void getInvoiceDetailes() {

		if (!isNetworkAvailable()) return;

		DisplayProcessMessage(getResources().getString(R.string.driver_common_please_wait));

		HttpGetInvoiceRequest requestData = new HttpGetInvoiceRequest();
		requestData.InvoiceID = Const.invoiceId;

		NetworkApi api = (new NetworkService()).getApi();
		api.getInvoiceDetails(requestData)
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<JsonResponse<HttpGetInvoiceData>>() {
					@Override
					public void onCompleted() {
						DisplayProcessMessage(true);
					}

					@Override
					public void onError(Throwable e) {
						DisplayProcessMessage(false);
					}

					@Override
					public void onNext(JsonResponse<HttpGetInvoiceData> httpGetInvoiceDataJsonResponse) {
						if (httpGetInvoiceDataJsonResponse.IsSuccess) {
							paymentMethod = httpGetInvoiceDataJsonResponse.Content.PaymentMethod;
							Const.paymentCustomerCode = httpGetInvoiceDataJsonResponse.Content.PaymentID;
							setPaymentMethod();
						}
					}
				});
	}

	public void sendEmailToPassenger(final String transactionid) {
		if (!isNetworkAvailable()) return;

		DisplayProcessMessage(getResources().getString(R.string.driver_common_please_wait));

		HttpEmailPassengerRequest httpEmailPassengerRequest = new HttpEmailPassengerRequest();
		httpEmailPassengerRequest.LocalTime = Utils.millisToSecond(System.currentTimeMillis());
		httpEmailPassengerRequest.InvoiceID = Const.paymentInvoiceId;
		httpEmailPassengerRequest.TransactionID = transactionid;
		httpEmailPassengerRequest.ReservationID = Const.paymentReservationId;

		NetworkApi api = (new NetworkService()).getApi();
		api.sendEmailToPassenger(httpEmailPassengerRequest)
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
							startActivity(new Intent(AddTipActivity.this,PassengerActivity.class)
									.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
									.putExtra("fromPaymentProcess",2));
							finish();
						}
					}
				});
	}

	private JSONArray generateFeeCodeList() {
		JSONArray list = new JSONArray();
		JSONObject objList = null;

		try {

			for (HttpFeecode feeCodeBean : Const.feeCodeList) {
				if (feeCodeBean.Amount != 0.0) {

					objList = new JSONObject();
					objList.put("paymentType", feeCodeBean.FeeCode);
					objList.put("amount", feeCodeBean.Amount);

					list.put(objList);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public void processPay() {
		if (!Utils.isOnline(AddTipActivity.this)) {
			return;
		}
		msg = new Message();
		DisplayProcessMessage("Please wait...");

		new Thread() {
			public void run() {

				int content = DataServices.getDataServiceObject(
						AddTipActivity.this).processPayEnrolledCustomer(
						((Const.paymentTotalAmnt + 0.00) * 100) + "",
						Const.tip + "", generateFeeCodeList());

				if (content != 0) {
					DisplayProcessMessage(true);
					msg.what = 1;
					msg.arg1 = 0;
					msg.obj = content;
					handlerWeb.sendMessage(msg);
					// finish();
					this.interrupt();
					return;
				}

				else {

					DisplayProcessMessage(true);
					msg.what = 1;
					msg.arg1 = 1;
					handlerWeb.sendMessage(msg);
					this.interrupt();
					return;

				}

			}

		}.start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txtAdTipNext:
			startActivity(new Intent(AddTipActivity.this,
					PaymentActivityBT.class));
			break;
		case R.id.txtAdTipClose:
			finish();
			break;
		case R.id.txtAdTipDone:

			processPay();

			break;

		}

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.rbCrgPass:

			break;
		case R.id.rbScanNewCard:
			if (isChecked) {
				txtAdTipDone.setVisibility(View.GONE);
				txtAdTipNext.setVisibility(View.VISIBLE);
			} else {
				txtAdTipDone.setVisibility(View.VISIBLE);
				txtAdTipNext.setVisibility(View.GONE);
			}
			break;
		case R.id.rbPaycash:

			break;

		}
	}

	@Override
	public void OnButtonClick(int tag, int buttonIndex) {
		switch (tag) {
		case 1:
			startActivity(new Intent(AddTipActivity.this, PassengerActivity.class));
			finish();
			break;

		}

	}
}
