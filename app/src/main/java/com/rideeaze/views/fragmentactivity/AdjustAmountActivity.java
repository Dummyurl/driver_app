package com.rideeaze.views.fragmentactivity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rideeaze.R;
import com.rideeaze.services.network.NetworkApi;
import com.rideeaze.services.network.NetworkService;
import com.rideeaze.services.network.model.data.HttpFeecode;
import com.rideeaze.services.network.model.request.HttpDriverTokenRequest;
import com.rideeaze.services.network.model.response.JsonResponse;
import com.rideeaze.views.activity.DriverActivity;
import com.util.Const;
import com.util.DecimalTextWatcher;
import com.util.StorageDataHelper;

import java.text.DecimalFormat;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AdjustAmountActivity extends DriverActivity implements OnClickListener {

	private TextView txtAdjAmntClose;
	private TextView txtAdjAmntDone;

	private TextView txtTotalAdjst;

	private LinearLayout llAdjustment;

	// For Row File
	private TextView txtAdjustDesc;
	private EditText edtAdjstRowAmnt;
	private RelativeLayout rlRowMain;
	private EditText adjstTemp;

	private ProgressDialog progressDialog = null;
	private Message msg;

	private float tempEstimateFare;
	private double tempCNSNAmnt;
	private boolean tempFlag = false;
	private EditText lastchangedEdt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.adjust_amount_activity);
		msg = new Message();
		txtAdjAmntClose = (TextView) findViewById(R.id.txtAdjAmntClose);
		txtAdjAmntDone = (TextView) findViewById(R.id.txtAdjAmntDone);
		txtTotalAdjst = (TextView) findViewById(R.id.txtTotalAdjst);
		llAdjustment = (LinearLayout) findViewById(R.id.llAdjustment);

		tempEstimateFare = Const.fare;

		getFeedCode();

		txtAdjAmntClose.setOnClickListener(this);
		txtAdjAmntDone.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.txtAdjAmntClose:
			finish();
			break;
		case R.id.txtAdjAmntDone:
			Const.paymentTotalAdjstAmnt = Double.parseDouble(txtTotalAdjst.getText().toString().trim()!=null && !txtTotalAdjst.getText().toString().trim().equals("")?txtTotalAdjst.getText().toString().trim():"0.0");
			finish();
			break;

		}
	}

	private void generateFeecodeList(List<HttpFeecode> feeCodeList) {
		llAdjustment.removeAllViews();
		int i = 0;
		for (HttpFeecode httpFeecode : feeCodeList) {

			View view = LayoutInflater.from(AdjustAmountActivity.this).inflate(
					R.layout.adjustment_row, null);

			LinearLayout llRow = (LinearLayout) view.findViewById(R.id.llAdjstRow);
			txtAdjustDesc = (TextView) view.findViewById(R.id.txtAdjustDesc);
			edtAdjstRowAmnt = (EditText) view.findViewById(R.id.edtAdjstRowAmnt);

            View viewRow = (View) view.findViewById(R.id.viewRow);
			edtAdjstRowAmnt.setSelectAllOnFocus(true);
			txtAdjustDesc.setText(httpFeecode.Description);
			edtAdjstRowAmnt.setText(httpFeecode.Amount + "");

			if(httpFeecode.Description.equals("Estimated Fare")){
				llRow.setVisibility(View.GONE);
			}else {
				llRow.setVisibility(View.VISIBLE);
			}


			if (i == (feeCodeList.size() - 1)) {
				viewRow.setVisibility(View.VISIBLE);
			} else {
				viewRow.setVisibility(View.GONE);
			}

			if(httpFeecode.FeeCode.equals("CR"))
			{
				edtAdjstRowAmnt.setBackgroundColor(Color.parseColor("#cf2a27"));
				edtAdjstRowAmnt.setTextColor(Color.parseColor("#ffffff"));
			}

			edtAdjstRowAmnt.addTextChangedListener(new DecimalTextWatcher(edtAdjstRowAmnt,6,2) {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					if(tempFlag)
					{
						txtTotalAdjst.setText(tempCNSNAmnt + "");
						tempFlag = false;
					}
					else{
						txtTotalAdjst.setText(getAllAdjustMentTotal() + "");
					}
				}
			});

			llAdjustment.addView(view);
			i++;
		}


	}

	private String getAllAdjustMentTotal() {
		double adjustTotal = 0;
		for (int i = 0; i < llAdjustment.getChildCount(); i++) {

			if(!Const.feeCodeList.get(i).Description
					.equals("Estimated Fare")) {


				adjstTemp = (EditText) llAdjustment.getChildAt(i).findViewById(
						R.id.edtAdjstRowAmnt);


				Const.feeCodeList.get(i).Amount = (float) Double
						.parseDouble((adjstTemp.getText().toString().trim() != null && !adjstTemp
						.getText().toString().trim().equals("")) ? adjstTemp
								.getText().toString().trim()
								: "0.0");
				if(Const.feeCodeList.get(i).FeeCode.equals("CR"))
				{
					adjustTotal = (adjustTotal - Const.feeCodeList.get(i).Amount);
				}
				else{
					adjustTotal = (Const.feeCodeList.get(i).Amount + adjustTotal);
				}

				if(Const.feeCodeList.get(i).FeeCode.equals("CN"))
				{

					tempCNSNAmnt = Const.feeCodeList.get(i).Amount;
					if(tempCNSNAmnt > 0)
					{
						lastchangedEdt = adjstTemp;
						setZeroOut("CN");
						Const.fare = (float)0.0;
					}
					else{
						Const.fare = tempEstimateFare;
					}
				}
				else if(Const.feeCodeList.get(i).FeeCode.equals("NS"))
				{

					tempCNSNAmnt = Const.feeCodeList.get(i).Amount;
					if(tempCNSNAmnt > 0)
					{
						lastchangedEdt = adjstTemp;
						setZeroOut("NS");
						Const.fare = (float)0.0;
					}
					else{
						Const.fare = tempEstimateFare;
					}
				}

			}



		}
		return new DecimalFormat("0.00").format(adjustTotal);

	}

	private void setZeroOut(String feeCode)
	{
		for (int i = 0; i < llAdjustment.getChildCount(); i++) {
			System.out.println("====OUT SIDE::::FEECODE:"+Const.feeCodeList.get(i).FeeCode);
			if(!Const.feeCodeList.get(i).FeeCode.equals(feeCode))
			{
				System.out.println("====INSIDE SIDE::::FEECODE:"+Const.feeCodeList.get(i).FeeCode);
				Const.feeCodeList.get(i).Amount = 0;
				tempFlag = true;
				((EditText) llAdjustment.getChildAt(i).findViewById(
						R.id.edtAdjstRowAmnt)).setText("0.0");

			}
		}
	}

	public void getFeedCode() {
		if (!isNetworkAvailable()) return;

		DisplayProcessMessage(getResources().getString(R.string.driver_common_please_wait));

		HttpDriverTokenRequest httpDriverTokenRequest = new HttpDriverTokenRequest();
		httpDriverTokenRequest.DriverToken = StorageDataHelper.getInstance(getApplicationContext()).getAuthToken();

		if(Const.feeCodeList.size() > 0){
			generateFeecodeList(Const.feeCodeList);
			txtTotalAdjst.setText(getAllAdjustMentTotal() + "");
			return;
		}


		NetworkApi api = (new NetworkService()).getApi();
		api.getFeeCodeList(httpDriverTokenRequest)
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<JsonResponse<List<HttpFeecode>>>() {
					@Override
					public void onCompleted() {
						DisplayProcessMessage(false);
					}

					@Override
					public void onError(Throwable e) {
						DisplayProcessMessage(false);
					}

					@Override
					public void onNext(JsonResponse<List<HttpFeecode>> listJsonResponse) {
						if(listJsonResponse.IsSuccess) {
							Const.feeCodeList = listJsonResponse.Content;
							generateFeecodeList(listJsonResponse.Content);
						}
					}
				});

	}
}
