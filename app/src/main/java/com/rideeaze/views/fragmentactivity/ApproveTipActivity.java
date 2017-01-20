package com.rideeaze.views.fragmentactivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rideeaze.R;
import com.rideeaze.services.network.model.data.HttpFeecode;
import com.util.Const;

import java.text.DecimalFormat;
import java.util.List;

public class ApproveTipActivity extends Activity implements OnClickListener,
OnCheckedChangeListener {

	private TextView txtAprvTipQuotedAmnt;
	private LinearLayout llAprvTipAd;
	private TextView txtAprvTipSubTotal;
	private TextView txtAprvTip;
	private TextView txtAprvTipTotal;
	private RadioButton rbTrtyPer;
	private RadioButton rbTwntyPer;
	private RadioButton rbTenPer;
	private RadioButton rbNoTip;
	private TextView txtIapprove;
	private TextView txtIdecline;
	private LinearLayout llaprvTipSubTotal;
	private RelativeLayout rlAprvTipContnr;
	private TextView txtAprvTipTitle;

	private int optionValue;

	// For Row File
	private TextView txtAdjustDesc;
	private EditText edtAdjstRowAmnt;
	private RelativeLayout rlRowMain;
	private EditText adjstTemp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.approve_tip_activity);

		txtAprvTipQuotedAmnt = (TextView) findViewById(R.id.txtAprvTipQuotedAmnt);
		llAprvTipAd = (LinearLayout) findViewById(R.id.llAprvTipAd);
		txtAprvTipSubTotal = (TextView) findViewById(R.id.txtAprvTipSubTotal);
		txtAprvTip = (TextView) findViewById(R.id.txtAprvTip);
		txtAprvTipTotal = (TextView) findViewById(R.id.txtAprvTipTotal);
		rbTrtyPer = (RadioButton) findViewById(R.id.rbTrtyPer);
		rbTwntyPer = (RadioButton) findViewById(R.id.rbTwntyPer);
		rbTenPer = (RadioButton) findViewById(R.id.rbTenPer);
		rbNoTip = (RadioButton) findViewById(R.id.rbNoTip);
		txtIapprove = (TextView) findViewById(R.id.txtIapprove);
		txtIdecline = (TextView) findViewById(R.id.txtIdecline);
		llaprvTipSubTotal = (LinearLayout) findViewById(R.id.llaprvTipSubTotal);

		rlAprvTipContnr = (RelativeLayout) findViewById(R.id.rlAprvTipContnr);
		txtAprvTipTitle = (TextView) findViewById(R.id.txtAprvTipTitle);

		optionValue = getIntent().getIntExtra("optionValue", 3);
		if (optionValue == 0) {
			rbTrtyPer.setChecked(false);
			rbTwntyPer.setChecked(false);
			rbTenPer.setChecked(false);
			rbNoTip.setChecked(false);
		} else if (optionValue == 1) {
			rbTwntyPer.setChecked(true);
			rbTrtyPer.setChecked(false);
			rbTenPer.setChecked(false);
			rbNoTip.setChecked(false);
			txtAprvTip.setText(calculatePercentageAmnt(
					Const.paymentSubTotalAmnt, 20) + "");
		} else if (optionValue == 2) {
			llaprvTipSubTotal.setVisibility(View.GONE);
			rlAprvTipContnr.setVisibility(View.GONE);
			txtAprvTipTitle.setVisibility(View.GONE);
		} else if (optionValue == 3) {
            rbNoTip.setChecked(true);
        }

		txtAprvTipQuotedAmnt.setText(Const.fare + "");


		if (Const.feeCodeList != null && Const.feeCodeList.size() > 0) {
			generateFeecodeListUi(Const.feeCodeList);
		} else {
			llAprvTipAd.setVisibility(View.GONE);

		}

		txtIdecline.setOnClickListener(this);
		txtIapprove.setOnClickListener(this);
		rbTrtyPer.setOnCheckedChangeListener(this);
		rbTwntyPer.setOnCheckedChangeListener(this);
		rbTenPer.setOnCheckedChangeListener(this);
		rbNoTip.setOnCheckedChangeListener(this);

        calculateSubTotalFare(Const.fare + getAllAdjustMentTotal());
        //calculateTotalFare();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txtIdecline:
            AlertDialog dialog=new AlertDialog.Builder(ApproveTipActivity.this).create();
            dialog.setTitle(getString(R.string.app_name));
            dialog.setMessage(getString(R.string.passangerDeclined));
            dialog.setButton("OK",
                    new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int whichButton)
                        {
                            dialog.dismiss();
                            startActivity(new Intent(ApproveTipActivity.this,
                                    PassengerActivity.class));
                            finish();
                        }
                    });
            dialog.show();
			break;
		case R.id.txtIapprove:
			startActivity(new Intent(ApproveTipActivity.this,
					SignConfirmActivity.class));
			break;

		}

	}

	private void generateFeecodeListUi(List<HttpFeecode> feeCodeList) {
		llAprvTipAd.removeAllViews();
		int i = 0;
	
		for (HttpFeecode httpFeecode : feeCodeList) {
			
			if(httpFeecode.Amount > 0 && !httpFeecode.Description.equals("Estimated Fare")) {
				
				View view = LayoutInflater.from(ApproveTipActivity.this).inflate(
						R.layout.adjustment_row, null);
				LinearLayout llRow = (LinearLayout) view.findViewById(R.id.llAdjstRow);
				txtAdjustDesc = (TextView) view.findViewById(R.id.txtAdjustDesc);
				edtAdjstRowAmnt = (EditText) view
						.findViewById(R.id.edtAdjstRowAmnt);
				View viewRow = (View) view.findViewById(R.id.viewRow);

				txtAdjustDesc.setText(httpFeecode.Description);
				edtAdjstRowAmnt.setText(httpFeecode.Amount + "");
				llRow.setVisibility(View.VISIBLE);
				if (i == (feeCodeList.size() - 1)) {
					viewRow.setVisibility(View.VISIBLE);
				} else {
					viewRow.setVisibility(View.GONE);
				}
				edtAdjstRowAmnt.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {

						calculateSubTotalFare(Const.fare + getAllAdjustMentTotal());
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {

					}

					@Override
					public void afterTextChanged(Editable s) {

					}
				});

				llAprvTipAd.addView(view);
			}else if(i == (feeCodeList.size() - 1)){
				
				View view = LayoutInflater.from(ApproveTipActivity.this).inflate(
						R.layout.adjustment_row, null);
				
				View viewRow = (View) view.findViewById(R.id.viewRow);
				viewRow.setVisibility(View.VISIBLE);
				llAprvTipAd.addView(view);
			}
			i++;
		}

	}

	private double getAllAdjustMentTotal() {
		double adjustTotal = 0;
		for (int i = 0; i < llAprvTipAd.getChildCount(); i++) {
			adjstTemp = (EditText) llAprvTipAd.getChildAt(i).findViewById(
					R.id.edtAdjstRowAmnt);
			if(!Const.feeCodeList.get(i).Description.equals("Estimated Fare")){
				Const.feeCodeList.get(i).Amount = (float) Double
					.parseDouble(adjstTemp.getText().toString().trim()!=null && !adjstTemp.getText().toString().trim().equals("")?adjstTemp.getText().toString().trim():"0.0");
				adjustTotal = (Const.feeCodeList.get(i).Amount + adjustTotal);
			}
		}
		
		return adjustTotal;

	}

	private void calculateSubTotalFare(double fare) {
		Const.paymentSubTotalAmnt = fare;
		txtAprvTipSubTotal.setText(new DecimalFormat("0.00").format(fare)
				+ "");
        calculateTotalFare();


	}

	private void calculateTotalFare() {
		Const.paymentTotalAmnt = Const.paymentSubTotalAmnt + Const.tip;
		txtAprvTipTotal.setText(new DecimalFormat("0.00")
		.format((Const.paymentSubTotalAmnt + Const.tip)) + "");
	}

	private String calculatePercentageAmnt(double subTotal, int prcrnt) {
		 Const.tip = (float)((subTotal * prcrnt) / 100);
		 return new DecimalFormat("0.00").format(Const.tip);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.rbTrtyPer:
			if (isChecked) {
				txtAprvTip.setText(calculatePercentageAmnt(
						Double.parseDouble(txtAprvTipSubTotal.getText()
								.toString().trim()), 30)
								+ "");
				calculateTotalFare();
			}
			break;
		case R.id.rbTwntyPer:
			if (isChecked) {
				txtAprvTip.setText(calculatePercentageAmnt(
						Double.parseDouble(txtAprvTipSubTotal.getText()
								.toString().trim()), 20)
								+ "");
				calculateTotalFare();
			}
			break;
		case R.id.rbTenPer:
			if (isChecked) {
				txtAprvTip.setText(calculatePercentageAmnt(
						Double.parseDouble(txtAprvTipSubTotal.getText()
								.toString().trim()), 10)
								+ "");
				calculateTotalFare();
			}
			break;
		case R.id.rbNoTip:
			if (isChecked) {
				txtAprvTip.setText(0.0 + "");
				Const.tip = (float)0.0;
				calculateTotalFare();
			}
			break;

		}
	}
}
