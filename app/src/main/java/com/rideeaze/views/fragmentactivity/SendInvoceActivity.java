package com.rideeaze.views.fragmentactivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.rideeaze.uc.AlertDailogView.OnCustPopUpDialogButoonClickListener;
import com.rideeaze.views.activity.DriverActivity;
import com.util.Const;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SendInvoceActivity extends DriverActivity implements OnClickListener,
		OnCustPopUpDialogButoonClickListener {

	private TextView txtSndInvAmntClose;
	private TextView txtSndInvBack;
	private TextView txtSndInvNext;
	private TextView txtSndInvQuotedAmnt;
	private LinearLayout llSndInvAdjstContainer;
	private TextView txtSndInvSubTotal;
	private TextView txtSneVoiceQuotSubTitle;
	private TextView txtSneVoiceQuotTitle;
	private TextView txtSndInvDone;

	private ProgressDialog progressDialog = null;
	private Message msg;
	private List<HttpFeecode> _feeCodeList;

	// For Row File
	private TextView txtAdjustDesc;
	private EditText edtAdjstRowAmnt;
	private RelativeLayout rlRowMain;
	private EditText adjstTemp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.send_invoice_activity);
		msg = new Message();
		txtSndInvAmntClose = (TextView) findViewById(R.id.txtSndInvAmntClose);
		txtSndInvBack = (TextView) findViewById(R.id.txtSndInvBack);
		txtSndInvNext = (TextView) findViewById(R.id.txtSndInvNext);
		txtSndInvQuotedAmnt = (TextView) findViewById(R.id.txtSndInvQuotedAmnt);
		llSndInvAdjstContainer = (LinearLayout) findViewById(R.id.llSndInvAdjstContainer);
		txtSndInvSubTotal = (TextView) findViewById(R.id.txtSndInvSubTotal);
		txtSneVoiceQuotSubTitle = (TextView) findViewById(R.id.txtSneVoiceQuotSubTitle);
		txtSneVoiceQuotTitle = (TextView) findViewById(R.id.txtSneVoiceQuotTitle);
		txtSndInvDone = (TextView) findViewById(R.id.txtSndInvDone);
		
		

		txtSndInvQuotedAmnt.setText(new DecimalFormat("#####.00").format(Const.fare) + "");
		txtSndInvSubTotal
				.setText(new DecimalFormat("#####.00").format(Const.fare + Const.paymentTotalAdjstAmnt) + "");

		if (Const.feeCodeList != null && Const.feeCodeList.size() > 0) {
			generateFeecodeListUi(Const.feeCodeList);
		} else {
			llSndInvAdjstContainer.setVisibility(View.GONE);
		}

		txtSndInvAmntClose.setOnClickListener(this);
		txtSndInvBack.setOnClickListener(this);
		txtSndInvNext.setOnClickListener(this);
		txtSndInvDone.setOnClickListener(this);

        txtSndInvBack.setVisibility(View.INVISIBLE);
        txtSneVoiceQuotSubTitle.setText("Grand Total ");
        txtSneVoiceQuotTitle.setText("Total Fare :");
        txtSndInvDone.setVisibility(View.VISIBLE);
        txtSndInvNext.setVisibility(View.GONE);
        txtSndInvQuotedAmnt.setText(txtSndInvSubTotal.getText().toString().trim());
        txtSndInvSubTotal.setText(txtSndInvSubTotal.getText().toString().trim());
        AlertDailogView.showAlert(SendInvoceActivity.this,
                "Waiting for passenger to approve", "Quit waiting",
                SendInvoceActivity.this).show();

        SendInvoiceToPasngr();
	}

	private void generateFeecodeListUi(List<HttpFeecode> feeCodeList) {
		llSndInvAdjstContainer.removeAllViews();
		int i = 0;
		for (HttpFeecode httpFeecode : feeCodeList) {
			if(httpFeecode.Amount > 0 && !httpFeecode.Description.equals("Estimated Fare")){
				View view = LayoutInflater.from(SendInvoceActivity.this).inflate(
						R.layout.adjustment_row, null);
				LinearLayout llRow = (LinearLayout) view.findViewById(R.id.llAdjstRow);
				txtAdjustDesc = (TextView) view.findViewById(R.id.txtAdjustDesc);
				edtAdjstRowAmnt = (EditText) view
						.findViewById(R.id.edtAdjstRowAmnt);
				llRow.setVisibility(View.VISIBLE);
				View viewRow = (View) view.findViewById(R.id.viewRow);
	
				txtAdjustDesc.setText(httpFeecode.Description);
				edtAdjstRowAmnt.setText(httpFeecode.Amount + "");
	
				if (i == (feeCodeList.size() - 1)) {
					viewRow.setVisibility(View.VISIBLE);
				} else {
					viewRow.setVisibility(View.GONE);
				}
				edtAdjstRowAmnt.addTextChangedListener(new TextWatcher() {
	
					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						txtSndInvSubTotal
								.setText(new DecimalFormat("#####.00").format((Const.fare + getAllAdjustMentTotal())) + "");
						
						
					}
	
					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
	
					}
	
					@Override
					public void afterTextChanged(Editable s) {
	
					}
				});
	
				llSndInvAdjstContainer.addView(view);
				
			}else if(i == (feeCodeList.size() - 1)){
				View view = LayoutInflater.from(SendInvoceActivity.this).inflate(
						R.layout.adjustment_row, null);
				
				View viewRow = (View) view.findViewById(R.id.viewRow);
				viewRow.setVisibility(View.VISIBLE);
				llSndInvAdjstContainer.addView(view);
			}
			i++;
		}

	}

	private double getAllAdjustMentTotal() {
		double adjustTotal = 0;
		for (int i = 0; i < llSndInvAdjstContainer.getChildCount(); i++) {
			adjstTemp = (EditText) llSndInvAdjstContainer.getChildAt(i)
					.findViewById(R.id.edtAdjstRowAmnt);
			if(!Const.feeCodeList.get(i).Description.equals("Estimated Fare")){
				Const.feeCodeList.get(i).Amount = (float) Double
						.parseDouble(adjstTemp.getText().toString().trim());
				adjustTotal = (Double.parseDouble(adjstTemp.getText().toString()
						.trim()) + adjustTotal);
			}
		}
		
		return adjustTotal;

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txtSndInvAmntClose:
			startActivity(new Intent(SendInvoceActivity.this,
					PassengerActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			finish();
			
			break;
		case R.id.txtSndInvBack:
			finish();
			break;
		case R.id.txtSndInvNext:
			SendInvoiceToPasngr();
			break;
		case R.id.txtSndInvDone:
			break;
			
		}

	}


	private List<HttpFeecode> generateFeeCodeList() {
		_feeCodeList = new ArrayList<HttpFeecode>();
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
		return _feeCodeList;
	}

	public void SendInvoiceToPasngr() {
		if (!isNetworkAvailable()) return;

		DisplayProcessMessage(getResources().getString(R.string.driver_common_please_wait));

		HttpSendInvoiceToPassengerRequest httpSendInvoiceToPassengerRequest = new HttpSendInvoiceToPassengerRequest();
		httpSendInvoiceToPassengerRequest.ReservationID = Const.paymentReservationId;
		httpSendInvoiceToPassengerRequest.Tip = false;
		httpSendInvoiceToPassengerRequest.LineItems = generateFeeCodeList();
		httpSendInvoiceToPassengerRequest.PassengerID = Const.paymentPassengerId;

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
						Toast.makeText(getBaseContext(), "sendInvoiceToPassenger FAILED", Toast.LENGTH_SHORT).show();
						DisplayProcessMessage(false);
					}

					@Override
					public void onNext(JsonResponse<HttpSendInvoiceToPassengerData> httpSendInvoiceToPassengerDataJsonResponse) {
						if(httpSendInvoiceToPassengerDataJsonResponse.IsSuccess) {
							Const.paymentInvoiceId = httpSendInvoiceToPassengerDataJsonResponse.Content.InvoiceID;
							txtSndInvBack.setVisibility(View.INVISIBLE);
							txtSneVoiceQuotSubTitle.setText("Grand Total ");
							txtSneVoiceQuotTitle.setText("Total Fare :");
							txtSndInvDone.setVisibility(View.VISIBLE);
							txtSndInvNext.setVisibility(View.GONE);
							txtSndInvQuotedAmnt.setText(txtSndInvSubTotal.getText().toString().trim());
							txtSndInvSubTotal.setText(txtSndInvSubTotal.getText().toString().trim());
							AlertDailogView.showAlert(SendInvoceActivity.this,
									"Waiting for passenger to approve", "Quit waiting",
									SendInvoceActivity.this).show();
						} else {
							onError(null);
						}
					}
				});
	}

	@Override
	public void OnButtonClick(int tag, int buttonIndex) {
		switch (tag) {
		case 1:
			Const.paymentTotalAmnt = Double.parseDouble(txtSndInvSubTotal.getText().toString().trim());
			startActivity(new Intent(SendInvoceActivity.this,
					AddTipActivity.class).putExtra("fromPush",false));
			break;
		}
	}
}
