package com.rideeaze.views.fragmentactivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rideeaze.R;
import com.rideeaze.services.network.NetworkApi;
import com.rideeaze.services.network.NetworkService;
import com.rideeaze.services.network.model.data.HttpFeecode;
import com.rideeaze.services.network.model.request.HttpSendInvoiceToPassengerRequest;
import com.rideeaze.services.network.model.response.HttpSendInvoiceToPassengerData;
import com.rideeaze.services.network.model.response.JsonResponse;
import com.rideeaze.uc.AlertDailogView;
import com.rideeaze.views.activity.DriverActivity;
import com.util.Const;
import com.util.DecimalTextWatcher;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EnterAmountActivity extends DriverActivity implements OnClickListener, AlertDailogView.OnCustPopUpDialogButoonClickListener {

	private TextView txtEntrAmntClose;
	private TextView txtEntrAmntDvrName;
	private TextView txtEntrAmntNext;
    private TextView txtQuotedAmntTitle;
	private EditText txtQuotedAmnt;
	private TextView txtAdjustAmnt;
	private TextView txtAdjustAmntTitle;
    private TextView txtSubTotalTitle;
	private TextView txtSubTotal;
    private CheckBox isSendToPassanger;
    Dialog waitingDialogView;

    private List<HttpFeecode> _feeCodeList;

	private ProgressDialog progressDialog = null;
	private Message msg;
	/*
	 * if fromFlag = 1 means collecting payment from Known Passenger if fromFlag
	 * = 2 means collecting payment from UnKnown Passenger
	 */
	private int fromFlag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        msg = new Message();

		setContentView(R.layout.enter_amount_activity);

		txtEntrAmntClose = (TextView) findViewById(R.id.txtEntrAmntClose);
		txtEntrAmntDvrName = (TextView) findViewById(R.id.txtEntrAmntDvrName);
		txtEntrAmntNext = (TextView) findViewById(R.id.txtEntrAmntNext);

        txtQuotedAmntTitle = (TextView) findViewById(R.id.textView2);
        isSendToPassanger = (CheckBox) findViewById(R.id.is_send_to_passanger);

        txtQuotedAmnt = (EditText) findViewById(R.id.txtQuotedAmnt);
		txtQuotedAmnt.addTextChangedListener(new DecimalTextWatcher(txtQuotedAmnt, 6, 2) {
			
			@Override
			public void onTextChanged(CharSequence s, int start,
					int before, int count) {
			
				Const.fare = (float) Double
						.parseDouble((txtQuotedAmnt.getText().toString().trim() != null && !txtQuotedAmnt
						.getText().toString().trim().equals("")) ? txtQuotedAmnt
						.getText().toString().trim()
						: "0.00");
				
				txtSubTotal.setText(new DecimalFormat("0.00").format((Const.fare + Double
                        .parseDouble(txtAdjustAmnt.getText().toString().trim()))));
			}
		});
        txtAdjustAmntTitle = (TextView) findViewById(R.id.txtAdjustAmntTitle);
		txtAdjustAmnt = (TextView) findViewById(R.id.txtAdjustAmnt);

        txtAdjustAmnt.setOnClickListener(this);
        txtAdjustAmntTitle.setOnClickListener(this);

        txtSubTotalTitle = (TextView) findViewById(R.id.txtSubTotalTitle);
		txtSubTotal = (TextView) findViewById(R.id.txtSubTotal);


		txtEntrAmntDvrName.setText(getIntent().getStringExtra("psngrName"));
		fromFlag = getIntent().getIntExtra("fromFlag", 0);

		txtEntrAmntNext.setOnClickListener(this);
		txtEntrAmntClose.setOnClickListener(this);


        txtEntrAmntNext.setEnabled(true);
        txtEntrAmntClose.setEnabled(true);
        txtQuotedAmntTitle.setText("Quoted Fare :");
        txtAdjustAmntTitle.setText("Adjustments :");
        txtSubTotalTitle.setText("Subtotal \nbefore tip :");

		txtAdjustAmntTitle.setPaintFlags(txtAdjustAmntTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        waitingDialogView = (Dialog)AlertDailogView.showAlert(EnterAmountActivity.this,
                "Waiting for passenger to approve", "Quit waiting",
                EnterAmountActivity.this);

        initValueOfPayment();
	}

    public void initValueOfPayment() {
        if(fromFlag == 2) {
            Const.fare = (float) 0.00;
        } else {

        }
    }
	
	/*public void getInvoiceDetailes() {
		if (!Utils.isOnline(EnterAmountActivity.this)) {
			return;
		}
		msg = new Message();
		DisplayProcessMessage("Please wait...");

		new Thread() {
			public void run() {
				JsonResponse<HttpGetInvoiceData> result = driverViewModel.getInvoiceDetals(Const.invoiceId);

				if (result.IsSuccess) {
					DisplayProcessMessage(true);
					msg.what = 1;
					msg.arg1 = 0;
					Const.paymentCustomerCode = result.Content.PaymentID;
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
	}*/
	
	private void showProcessingDialog(String msg) {
		if (progressDialog == null)
			progressDialog = ProgressDialog.show(this, "", msg, true, false);
	}

	public void DisplayProcessMessage(final String msg) {
		showProcessingDialog(msg);
	}

	private void hideProcessingDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	public void DisplayProcessMessage(final boolean hide) {

		hideProcessingDialog();

	}

    private final AlertDailogView.OnCustPopUpDialogButoonClickListener listenerDialog = new AlertDailogView.OnCustPopUpDialogButoonClickListener() {
        @Override
        public void OnButtonClick(int tag, int buttonIndex) {
            switch (tag) {
                case 1:
                    Const.paymentTotalAmnt = Double.parseDouble(new DecimalFormat("#####.00").format(Const.fare + Const.paymentTotalAdjstAmnt) + "");
                    startActivity(new Intent(EnterAmountActivity.this,
                            AddTipActivity.class).putExtra("fromPush",false));
                    break;
            }
        }
    };

	@Override
	protected void onResume() {
		super.onResume();
		txtQuotedAmnt.setText(Const.fare+"");
		txtQuotedAmnt.setSelectAllOnFocus(true);
		txtAdjustAmnt.requestFocus();
		txtAdjustAmnt.setText(new DecimalFormat("0.00").format(Const.paymentTotalAdjstAmnt)+"");
		txtSubTotal
				.setText(new DecimalFormat("0.00").format((Double
						.parseDouble(txtQuotedAmnt.getText().toString().trim()) + Double
						.parseDouble(txtAdjustAmnt.getText().toString().trim()))));
	}
	

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.txtEntrAmntClose:
			finish();
			break;

		case R.id.txtEntrAmntNext:

            /*if(isSendToPassanger.isChecked()) {
                SendInvoiceToPasngr();
            } else {
                startActivity(new Intent(EnterAmountActivity.this,
                        ApproveTipActivity.class).putExtra("optionValue", 1));
            }*/

			if (fromFlag == 1) {
				Const.fromUnknown = false;
                /*startActivity(new Intent(EnterAmountActivity.this,
                        SendInvoceActivity.class).putExtra("UpdateInvoiceOnly", isSendToPassanger.isChecked()));*/

/*                .show();*/

                SendInvoiceToPasngr();
                waitingDialogView.show();


			} else {
				Const.fromUnknown = true;
				startActivity(new Intent(EnterAmountActivity.this,
						CollectFronUnknownActivity.class));
			}
			break;

		case R.id.txtAdjustAmntTitle:
			startActivity(new Intent(EnterAmountActivity.this,
					AdjustAmountActivity.class));
			break;

		}

	}

    @Override
    public void OnButtonClick(int tag, int buttonIndex) {
        switch (tag) {
            case 1:
                waitingDialogView.hide();
                Const.paymentTotalAmnt = Double.parseDouble(txtSubTotal.getText().toString().trim());
                /*startActivity(new Intent(EnterAmountActivity.this,
                        AddTipActivity.class).putExtra("fromPush",false));*/
                startActivity(new Intent(EnterAmountActivity.this,
                        ApproveTipActivity.class));
                break;
        }
    }

    public void SendInvoiceToPasngr() {
        if (!isNetworkAvailable()) return;

        DisplayProcessMessage(getResources().getString(R.string.driver_common_please_wait));

        HttpSendInvoiceToPassengerRequest httpSendInvoiceToPassengerRequest = new HttpSendInvoiceToPassengerRequest();
        httpSendInvoiceToPassengerRequest.ReservationID = Const.paymentReservationId;
        httpSendInvoiceToPassengerRequest.Tip = false;
        httpSendInvoiceToPassengerRequest.LineItems = generateFeeCodeList();
        httpSendInvoiceToPassengerRequest.PassengerID = Const.paymentPassengerId;
        httpSendInvoiceToPassengerRequest.UpdateInvoiceOnly = isSendToPassanger.isChecked();

        NetworkApi api = (new NetworkService()).getApi();
        api.sendInvoiceToPassenger(httpSendInvoiceToPassengerRequest)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonResponse<HttpSendInvoiceToPassengerData>>() {
                    @Override
                    public void onCompleted() {
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        waitingDialogView.hide();
                        Toast.makeText(getBaseContext(), "sendInvoiceToPassenger FAILED", Toast.LENGTH_SHORT).show();
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onNext(JsonResponse<HttpSendInvoiceToPassengerData> httpSendInvoiceToPassengerDataJsonResponse) {
                        if(httpSendInvoiceToPassengerDataJsonResponse.IsSuccess) {
                            Const.paymentInvoiceId = httpSendInvoiceToPassengerDataJsonResponse.Content.InvoiceID;
                        } else {
                          onError(null);
                        }
                    }
                });
    }

    public static List<HttpFeecode> generateFeeCodeList() {
        ArrayList<HttpFeecode> _feeCodeList = new ArrayList<HttpFeecode>();
        for (HttpFeecode feeCodeBean : Const.feeCodeList) {
            if(feeCodeBean.Description.equals("Estimated Fare"))
            {
                feeCodeBean.Amount = Const.fare;
                _feeCodeList.add(feeCodeBean);
            }
            else if (feeCodeBean.Amount != 0.0) {
                _feeCodeList.add(feeCodeBean);
            }
        }
        if(Const.feeCodeList == null || Const.feeCodeList.size() == 0) {
            HttpFeecode estFare = new HttpFeecode();
            estFare.Amount = Const.fare;
            estFare.Description = "Estimated Fare";
            estFare.FeeCode = "EF";
            estFare.Id = 0;

            _feeCodeList.add(estFare);
        }
        return _feeCodeList;
    }

}
