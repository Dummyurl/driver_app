package com.rideeaze.views.fragmentactivity;

import android.app.Activity;
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
import com.util.DecimalTextWatcher;

import java.text.DecimalFormat;
import java.util.List;

public class CollectFronUnknownActivity extends Activity implements
		OnClickListener, OnCheckedChangeListener {

	private TextView txtColctUnClose;
	private TextView txtColctUnEdit;
	private TextView txtColctUnNext;
	private EditText edtColctUnQuotedAmnt;
	private LinearLayout llColctUnAdjstContainer;
	private TextView txtColctUnSubTotal;
	private TextView txtColctUnQuotSubTitle;
	private TextView txtColctUnQuotTitle;
	private TextView txtColctUnDone;
	private RadioButton rbClctUnPassAddTip;
	private RadioButton rbClctUnAddStdrdTip;
	private RadioButton rbClctUnTipAlAd;
	
	// For Row File
		private TextView txtAdjustDesc;
		private EditText edtAdjstRowAmnt;
		private RelativeLayout rlRowMain;
		private EditText adjstTemp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.collect_from_unknown_activity);

		txtColctUnClose = (TextView) findViewById(R.id.txtColctUnClose);
		txtColctUnEdit = (TextView) findViewById(R.id.txtColctUnBack);
		txtColctUnNext = (TextView) findViewById(R.id.txtColctUnNext);
		edtColctUnQuotedAmnt = (EditText) findViewById(R.id.edtColctUnQuotedAmnt);
		llColctUnAdjstContainer = (LinearLayout) findViewById(R.id.llColctUnAdjstContainer);
		txtColctUnSubTotal = (TextView) findViewById(R.id.txtColctUnSubTotal);
		txtColctUnQuotSubTitle = (TextView) findViewById(R.id.txtColctUnQuotSubTitle);
		txtColctUnQuotTitle = (TextView) findViewById(R.id.txtColctUnQuotTitle);
		txtColctUnDone = (TextView) findViewById(R.id.txtColctUnDone);
		rbClctUnPassAddTip = (RadioButton) findViewById(R.id.rbClctUnPassAddTip);
		rbClctUnAddStdrdTip = (RadioButton) findViewById(R.id.rbClctUnAddStdrdTip);
        rbClctUnAddStdrdTip.setChecked(true);
		rbClctUnTipAlAd = (RadioButton) findViewById(R.id.rbClctUnTipAlAd);
		
		edtColctUnQuotedAmnt.setText(new DecimalFormat("#####.00").format(Const.fare)+"");
		txtColctUnSubTotal.setText(new DecimalFormat("#####.00")
						.format(Const.paymentTotalAdjstAmnt)+"");
		
		if (Const.feeCodeList != null && Const.feeCodeList.size() > 0) {
			generateFeecodeListUi(Const.feeCodeList);
		} else {
			llColctUnAdjstContainer.setVisibility(View.GONE);
		}

		edtColctUnQuotedAmnt.addTextChangedListener(
				new DecimalTextWatcher(edtColctUnQuotedAmnt,6,2) {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Const.fare = (float)Double.parseDouble(edtColctUnQuotedAmnt.getText().toString().trim()!= null 
						&& !edtColctUnQuotedAmnt.getText().toString().trim().equals("")?
								edtColctUnQuotedAmnt.getText().toString().trim():"0.0");
				calculateTotalFare(Const.fare + Const.paymentTotalAdjstAmnt);
			}
		});

		txtColctUnClose.setOnClickListener(this);
		txtColctUnEdit.setOnClickListener(this);
		txtColctUnNext.setOnClickListener(this);

		rbClctUnPassAddTip.setOnCheckedChangeListener(this);
		rbClctUnAddStdrdTip.setOnCheckedChangeListener(this);
		rbClctUnTipAlAd.setOnCheckedChangeListener(this);
	}

	private void generateFeecodeListUi(List<HttpFeecode> feeCodeList) {
		llColctUnAdjstContainer.removeAllViews();
		int i = 0;
		for (HttpFeecode httpFeecode : feeCodeList) {
			if(httpFeecode.Amount > 0 && !httpFeecode.Description.equals("Estimated Fare"))
			{
				View view = LayoutInflater.from(CollectFronUnknownActivity.this).inflate(
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
						Const.paymentTotalAdjstAmnt  = getAllAdjustMentTotal();
						calculateTotalFare(Const.fare + Const.paymentTotalAdjstAmnt);
					}
	
					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
	
					}
	
					@Override
					public void afterTextChanged(Editable s) {
	
					}
				});
	
				llColctUnAdjstContainer.addView(view);
				
			}else if(i == (feeCodeList.size() - 1)){
				View view = LayoutInflater.from(CollectFronUnknownActivity.this).inflate(
						R.layout.adjustment_row, null);
				
				View viewRow = (View) view.findViewById(R.id.viewRow);
				viewRow.setVisibility(View.VISIBLE);
				llColctUnAdjstContainer.addView(view);
			}
			i++;
		}

	}
	
	private double getAllAdjustMentTotal() {
		double adjustTotal = 0;
		for (int i = 0; i < llColctUnAdjstContainer.getChildCount(); i++) {
			adjstTemp = (EditText) llColctUnAdjstContainer.getChildAt(i)
					.findViewById(R.id.edtAdjstRowAmnt);
			if(!Const.feeCodeList.get(i).Description.equals("Estimated Fare")){
				Const.feeCodeList.get(i).Amount = (float) Double
							.parseDouble((adjstTemp.getText().toString().trim() != null && !adjstTemp
							.getText().toString().trim().equals("")) ? adjstTemp
							.getText().toString().trim()
							: "0.0");
				adjustTotal = (Const.feeCodeList.get(i).Amount + adjustTotal);
			}
		}
		
		return adjustTotal;

	}
	private void calculateTotalFare(double quoted) {
		Const.paymentSubTotalAmnt = quoted;
		txtColctUnSubTotal.setText(new DecimalFormat("#####.00").format((quoted))+ "");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txtColctUnClose:
			startActivity(new Intent(CollectFronUnknownActivity.this,
					PassengerActivity.class));
			
			break;
		case R.id.txtColctUnBack:
			finish();
			break;
		case R.id.txtColctUnNext:
			Const.fare = (float)Double.parseDouble(edtColctUnQuotedAmnt.getText()
					.toString().trim()!= null && !edtColctUnQuotedAmnt.getText()
					.toString().trim().equals("")?edtColctUnQuotedAmnt.getText().toString().trim():"0.0");
			Const.paymentSubTotalAmnt = Const.fare + Const.paymentTotalAdjstAmnt;
			if (rbClctUnPassAddTip.isChecked()) {
				startActivity(new Intent(CollectFronUnknownActivity.this,
						ApproveTipActivity.class).putExtra("optionValue", 0));
			}
			else if(rbClctUnAddStdrdTip.isChecked())
			{
				startActivity(new Intent(CollectFronUnknownActivity.this,
						ApproveTipActivity.class).putExtra("optionValue", 1));
			}
			else if(rbClctUnTipAlAd.isChecked())
			{
				startActivity(new Intent(CollectFronUnknownActivity.this,
						ApproveTipActivity.class).putExtra("optionValue", 2));
			}
			break;

		}

	}

	@Override
	public void onCheckedChanged(CompoundButton v, boolean isChecked) {
		switch (v.getId()) {
		case R.id.rbClctUnPassAddTip:

			break;
		case R.id.rbClctUnAddStdrdTip:

			break;
		case R.id.rbClctUnTipAlAd:

			break;

		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		edtColctUnQuotedAmnt.setText(Const.fare+"");
		Const.paymentTotalAdjstAmnt  = getAllAdjustMentTotal();
		calculateTotalFare(Const.fare + Const.paymentTotalAdjstAmnt);
		
	}
}
