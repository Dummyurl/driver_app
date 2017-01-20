package com.rideeaze.uc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.rideeaze.R;
import com.rideeaze.adapter.ArrayWheelAdapter;
import com.rideeaze.adapter.scrollingDone;
import com.rideeaze.widget.WheelView;
import com.util.Utils;

public class WheelDateActivity extends Activity implements scrollingDone {

	public Button btnDone, btnCancel;
	String[] monthArray = { "January", "February", "March", "April", "May",
			"June", "July", "August", "September", "October", "November",
			"December" };
	String[] dateArray = { "01", "02", "03", "04", "05", "06", "07", "08",
			"09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
			"20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
			"31" };
	int year = Calendar.getInstance().get(Calendar.YEAR);
	int nextYear = 0;
	String[] yearArray = null;
	private ArrayList<String> yearArrayList;

	String selectedMonth = null;
	String selectedDay = null;
	String selectedYear = null;
	private ArrayWheelAdapter<String> dateAdapter;
	private ArrayWheelAdapter<String> monthAdapter;
	private ArrayWheelAdapter<String> yearAdapter;

	int dPos = 0, mPos = 0, yPos = 0;
	int cdPos = 0, cmPos = 0, cyPos = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wheeldatetime);

		btnDone = (Button) findViewById(R.id.btn_Done);
		btnCancel = (Button) findViewById(R.id.btn_cancel);
		
		yearArrayList = new ArrayList<String>();
		for (int i = 0; i < 100; i++) {
			nextYear = year + i;
			yearArrayList.add(String.valueOf(nextYear));
		}
		yearArray = new String[yearArrayList.size()];
		yearArray =  yearArrayList.toArray(yearArray);

		Intent gotIntent = getIntent();

		selectedDay = gotIntent.getStringExtra("selected-date");
		selectedMonth = gotIntent.getStringExtra("selected-month");
		selectedYear = gotIntent.getStringExtra("selected-year");

		wheeler1();

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				setResults(RESULT_CANCELED, "", "", "");

			}

		});

		btnDone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Date date = null;
				String datePart[] = null;
				try {
				
						if (dateValidation(selectedDay, selectedMonth,
								selectedYear)) {
							date = Utils.convertStringToDate(
									selectedDay.concat("-").concat(
											selectedMonth.concat("-").concat(
													selectedYear)),
									"dd-MMMMM-yyyy");

							datePart = Utils.convertDateToString(date,
									"dd-MM-yyyy").split("-");
							System.out.println(date.toString());
							setResults(RESULT_OK, datePart[0], datePart[1],
									datePart[2]);
						} else {
							AlertDailogView.showAlert(WheelDateActivity.this,
									"Plaease Select proper Date..", "Ok")
									.show();
						}
					
				} catch (Exception e) {
					e.printStackTrace();
					AlertDailogView.showAlert(WheelDateActivity.this,
							"Please Select Valid Date", "Ok").show();

				}
			}
		});
	}

	private boolean dateValidation(String selecteddate, String month,
			String selectedyear) {

		boolean result = true;
		if (month != null && !month.equals("")) {
			{
				if (month.equals("February")) {
					if ((Integer.parseInt(selectedyear) % 4) == 0) {
						if ((Integer.parseInt(selecteddate) > 29)) {
							result = false;
						}
					} else {
						if ((Integer.parseInt(selecteddate) > 28)) {
							result = false;
						}
					}
				} else if (month.equals("April") || month.equals("June")
						|| month.equals("September")
						|| month.equals("November")) {
					if (Integer.parseInt(selecteddate) == 31) {
						result = false;
					}
				}
			}
		}
		return result;
	}

	

	private void setResults(int result, String selectedDay,
			String selectedMonth2, String selectedYear2) {
		Intent previous = new Intent();
		previous.putExtra("selected-date", selectedDay);
		previous.putExtra("selected-month", selectedMonth2);
		previous.putExtra("selected-year", selectedYear2);

		setResult(result, previous);
		finish();
	}

	private void wheeler1() {

		final WheelView wheel = (WheelView) findViewById(R.id.wheel1);
		wheel.setListner(1, this);
		dateAdapter = new ArrayWheelAdapter<String>(this, dateArray);
		dateAdapter.setItemResource(R.layout.wheel_text_date);
		dateAdapter.setItemTextResource(R.id.text);
		wheel.setViewAdapter(dateAdapter);
		wheel.setCyclic(false);
		wheel.setCurrentItem(Integer.parseInt(selectedDay) - 1);

		final WheelView wheel1 = (WheelView) findViewById(R.id.wheelDt);
		wheel1.setListner(2, this);
		monthAdapter = new ArrayWheelAdapter<String>(this, monthArray);
		monthAdapter.setItemResource(R.layout.wheel_text_time);
		monthAdapter.setItemTextResource(R.id.text);
		wheel1.setViewAdapter(monthAdapter);
		wheel1.setCyclic(false);
		wheel1.setCurrentItem(Integer.parseInt(selectedMonth) - 1);
		selectedMonth = monthArray[wheel1.getCurrentItem()];

		final WheelView wheel11 = (WheelView) findViewById(R.id.wheelyr);
		wheel11.setListner(3, this);
		yearAdapter = new ArrayWheelAdapter<String>(this, yearArray);
		yearAdapter.setItemResource(R.layout.wheel_text_year);
		yearAdapter.setItemTextResource(R.id.text);
		wheel11.setViewAdapter(yearAdapter);
		wheel11.setCyclic(false);
		wheel11.setCurrentItem(Utils
				.indexOfStringArray(yearArray, selectedYear));
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
			selectedDay = (String) dateAdapter.getItemText(position);
		} else if (tag == 2) {
			mPos = position;
			selectedMonth = (String) monthAdapter.getItemText(position);
		} else if (tag == 3) {
			yPos = position;
			selectedYear = (String) yearAdapter.getItemText(position);
		}
	}
}
