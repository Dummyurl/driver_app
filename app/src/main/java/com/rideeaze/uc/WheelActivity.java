package com.rideeaze.uc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.rideeaze.R;
import com.rideeaze.adapter.ArrayWheelAdapter;
import com.rideeaze.adapter.scrollingDone;
import com.rideeaze.widget.WheelView;
import com.util.Utils;

public class WheelActivity extends Activity implements scrollingDone {

	public Button btnDone, btnCancel;

	String[] valueArray;

	private LinearLayout Datepicker;
	private LinearLayout Numberpicker;
	private LinearLayout yearpicker;

	String selectedValue = null;
	private ArrayWheelAdapter<String> dateAdapter;

	int dPos = 0, mPos = 0, yPos = 0;
	int cdPos = 0, cmPos = 0, cyPos = 0;
	
	private int tag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wheeldatetime);

		btnDone = (Button) findViewById(R.id.btn_Done);
		btnCancel = (Button) findViewById(R.id.btn_cancel);
		Datepicker = (LinearLayout) findViewById(R.id.Datepicker);
		Numberpicker = (LinearLayout) findViewById(R.id.Numberpicker);
		yearpicker = (LinearLayout) findViewById(R.id.yearpicker);
		
		Datepicker.setVisibility(View.INVISIBLE);
		yearpicker.setVisibility(View.INVISIBLE);
		
		Numberpicker.setGravity(Gravity.CENTER);


		valueArray = getIntent().getStringArrayExtra("content_array");
		selectedValue = getIntent().getStringExtra("selected_value");
		tag = getIntent().getIntExtra("tag",0);

		wheeler1();

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				setResults(RESULT_CANCELED, "");

			}

		});

		btnDone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				setResults(RESULT_OK, selectedValue);
			}

		});
	}

	private void setResults(int result, String selectedValue) {
		Intent previous = new Intent();
		previous.putExtra("selected_value", selectedValue);
		previous.putExtra("tag", tag);

		setResult(result, previous);
		finish();
	}

	private void wheeler1() {

		final WheelView wheel = (WheelView) findViewById(R.id.wheelDt);
		wheel.setListner(1, this);
		dateAdapter = new ArrayWheelAdapter<String>(this, valueArray);
		dateAdapter.setItemResource(R.layout.wheel_text_date);
		dateAdapter.setItemTextResource(R.id.text);
		wheel.setViewAdapter(dateAdapter);
		wheel.setCyclic(false);
		selectedValue = (String) dateAdapter.getItemText(0);
//		wheel.setCurrentItem(Integer.parseInt(selectedValue) - 1);
		wheel.setCurrentItem(Utils.indexOfStringArray(valueArray, selectedValue));
		

		//
		// //set Current item
		// wheel.setCurrentItem(dPos);
		// wheel1.setCurrentItem(mPos);
		// wheel11.setCurrentItem(yPos);
	}

	@Override
	public void OnScrollingDone(int tag, int position) {

		if (tag == 1) {
			dPos = position;
			selectedValue = (String) dateAdapter.getItemText(position);
		}
	}
}
